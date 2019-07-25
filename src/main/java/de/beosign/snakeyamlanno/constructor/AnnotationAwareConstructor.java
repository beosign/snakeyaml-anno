package de.beosign.snakeyamlanno.constructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import de.beosign.snakeyamlanno.AnnotationAwarePropertyUtils;
import de.beosign.snakeyamlanno.instantiator.DefaultInstantiator;
import de.beosign.snakeyamlanno.instantiator.Instantiator;
import de.beosign.snakeyamlanno.instantiator.YamlInstantiateBy;
import de.beosign.snakeyamlanno.property.YamlProperty;

/**
 * Constructor that takes care of annotations.
 * 
 * @author florian
 */
public class AnnotationAwareConstructor extends Constructor {
    private static final Logger log = LoggerFactory.getLogger(AnnotationAwareConstructor.class);

    private Map<Class<?>, YamlConstructBy> constructByMap = new HashMap<>();
    private Map<Class<?>, YamlInstantiateBy> instantiateByMap = new HashMap<>();
    private IdentityHashMap<Node, Property> nodeToPropertyMap = new IdentityHashMap<>();
    private Instantiator<?> globalInstantiator;

    /**
     * Creates constructor.
     * 
     * @param theRoot root class - you can cast the result of the parsing process to this class
     */
    public AnnotationAwareConstructor(Class<?> theRoot) {
        this(theRoot, false);
    }

    /**
     * Creates constructor.
     * 
     * @param theRoot root class - you can cast the result of the parsing process to this class
     * @param caseInsensitive true if parsing should be independent of case of keys
     */
    public AnnotationAwareConstructor(Class<? extends Object> theRoot, boolean caseInsensitive) {
        super(theRoot);
        setPropertyUtils(new AnnotationAwarePropertyUtils(caseInsensitive));
        yamlClassConstructors.put(NodeId.mapping, new AnnotationAwareMappingConstructor());
        yamlClassConstructors.put(NodeId.scalar, new AnnotationAwareScalarConstructor());
    }

    /**
     * Sets a <i>global</i> instantiator that can be used to create instance for all types that the given instantiator wishes to consider. If the instantiator
     * wants to apply the default instantiation logic of SnakeYaml, it can return <code>null</code>.<br>
     * In order to apply an {@link Instantiator} for a given type only, or if you want to override the behaviour for a particular type, use the
     * {@link YamlInstantiateBy} annotation or use the programmatic way (e.g. {@link #registerInstantiator(Class, Class)}.
     * 
     * @param globalInstantiator instantiator instance that is global to this Constructor.
     * @since 0.9.0
     */
    public void setGlobalInstantiator(Instantiator<?> globalInstantiator) {
        this.globalInstantiator = globalInstantiator;
    }

    /**
     * Overridden to implement the "instantiator" feature.
     */
    @Override
    protected Object newInstance(Class<?> ancestor, Node node, boolean tryDefault) throws InstantiationException {
        /*
         *  Create an instance using the following order:
         *  1. check node type for an instantiator registration and create one if present
         *  2. call instantiator; if return value is null, check global instantiator within this constructor if present
         *  3. call global instantiator; if return value is null, call super (default instantiation logic)
         */
        Instantiator<Object> defaultInstantiator = (nodeType, n, tryDef, anc, def) -> super.newInstance(anc, n, tryDef);
        Object instance = null;
        YamlInstantiateBy instantiateBy = getInstantiateBy(node.getType());
        if (instantiateBy != null && !instantiateBy.value().equals(Instantiator.NoInstantiator.class)) {
            try {
                instance = instantiateBy.value().newInstance().createInstance(node.getType(), node, tryDefault, ancestor, defaultInstantiator);
            } catch (IllegalAccessException e) {
                throw new InstantiationException("Cannot access constructor of " + instantiateBy.value() + ": " + e.getMessage());
            }
        }

        if (instance == null && globalInstantiator != null) {
            instance = globalInstantiator.createInstance(node.getType(), node, tryDefault, ancestor, defaultInstantiator);
        }

        if (instance == null) {
            instance = super.newInstance(ancestor, node, tryDefault);
        }
        return instance;
    }

    /**
     * Constructs a singleton list from the constructed object unless the constructed object is <code>null</code>, in which case <code>null</code> is returned.
     * 
     * @param node node - a {@link MappingNode} or a {@link ScalarNode} that is to assigned to a collection property
     * @param defaultConstructor default constructor
     * @return a singleton list or <code>null</code>
     */
    protected List<?> constructNodeAsList(Node node, Function<Node, Object> defaultConstructor) {
        Class<?> origType = node.getType();
        Property propertyOfNode = nodeToPropertyMap.get(node);
        if (propertyOfNode.getActualTypeArguments() != null && propertyOfNode.getActualTypeArguments().length > 0) {
            node.setType(propertyOfNode.getActualTypeArguments()[0]);
        }
        Object singleObject = constructObject(node, defaultConstructor);
        node.setType(origType);
        if (singleObject == null) {
            return null;
        } else {
            return Collections.singletonList(singleObject);
        }
    }

    /**
     * This constructor implements the features "ignore error", "constructBy at property-level" and "singleton list parsing"
     * feature.
     * 
     * @author florian
     */
    protected class AnnotationAwareMappingConstructor extends ConstructMapping {
        @Override
        public Object construct(Node node) {
            if (Collection.class.isAssignableFrom(node.getType())) {
                return constructNodeAsList(node, super::construct);
            } else {
                return constructObject(node, super::construct);
            }
        }

        @Override
        protected Object constructJavaBean2ndStep(MappingNode node, Object object) {
            List<NodeTuple> nodeTuplesToBeRemoved = new ArrayList<>();

            Class<? extends Object> beanType = node.getType();
            List<NodeTuple> nodeValue = node.getValue();
            for (NodeTuple tuple : nodeValue) {
                ScalarNode keyNode = getKeyNode(tuple);

                keyNode.setType(String.class);
                String key = (String) AnnotationAwareConstructor.this.constructObject(keyNode);

                TypeDescription memberDescription = typeDefinitions.get(beanType);
                Property property = memberDescription == null ? getProperty(beanType, key) : memberDescription.getProperty(key);
                Node valueNode = tuple.getValueNode();

                nodeToPropertyMap.put(valueNode, property);

                if (property.getAnnotation(YamlConstructBy.class) != null) {
                    Object value = null;
                    try {
                        @SuppressWarnings("unchecked")
                        CustomConstructor<Object> cc = (CustomConstructor<Object>) property.getAnnotation(YamlConstructBy.class).value().newInstance();
                        Construct constructor = getConstructor(valueNode);
                        value = cc.construct(valueNode, constructor::construct);
                        property.set(object, value);
                        nodeTuplesToBeRemoved.add(tuple);
                    } catch (YAMLException e) {
                        throw e;
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new YAMLException(
                                "Custom constructor " + property.getAnnotation(YamlConstructBy.class).value() + //
                                        " on property " + object.getClass().getTypeName() + "::" + property + " cannot be created",
                                e);
                    } catch (Exception e) {
                        throw new YAMLException(
                                "Cannot set value of type " + (value != null ? value.getClass().getTypeName() : "null") + //
                                        " into property " + object.getClass().getTypeName() + "::" + property,
                                e);
                    }
                } else {
                    YamlProperty propertyAnnotation = property.getAnnotation(YamlProperty.class);
                    boolean ignoreExceptions = (propertyAnnotation != null && propertyAnnotation.ignoreExceptions());

                    if (ignoreExceptions) {
                        /* 
                         * Let YAML set the value.
                         */
                        try {
                            Construct constructor = getConstructor(valueNode);
                            constructor.construct(valueNode);
                        } catch (Exception e) {
                            log.debug("Ignore: Could not construct property {}.{}: {}", beanType, key, e.getMessage());
                            nodeTuplesToBeRemoved.add(tuple);
                        }
                    }
                }
            }

            // Remove nodes that are unconstructable or already created
            nodeTuplesToBeRemoved.forEach(nt -> node.getValue().remove(nt));
            return super.constructJavaBean2ndStep(node, object);
        }

    }

    /**
     * Enables creating a complex object from a scalar. Useful if object to be created cannot be modified so
     * adding a single argument constructor is not possible, e.g. enums.
     * Also checks if the node's type is a collection and in that case, converts it to a singleton list.
     * 
     * @author florian
     */
    protected class AnnotationAwareScalarConstructor extends ConstructScalar {
        @Override
        public Object construct(Node node) {
            if (Collection.class.isAssignableFrom(node.getType())) {
                return constructNodeAsList(node, super::construct);
            }
            return constructObject(node, super::construct);
        }
    }

    /**
     * @return all programmatically registered class-to-constructBy associations.
     */
    public Map<Class<?>, YamlConstructBy> getConstructByMap() {
        return constructByMap;
    }

    // TODO add method "unregisterCustomConstructor"
    /**
     * Programmatically registers a {@link CustomConstructor} for a given type. This is a convenience method for putting something into
     * {@link #getConstructByMap()}.
     * 
     * @param forType type for which a CustomConverter is to be registered
     * @param customConstructorClass {@link CustomConstructor} type
     * @param <T> type for which a {@link CustomConstructor} is registered
     */
    public <T> void registerCustomConstructor(Class<T> forType, Class<? extends CustomConstructor<? extends T>> customConstructorClass) {
        constructByMap.put(forType, YamlConstructBy.Factory.of(customConstructorClass));
    }

    /**
     * Programmatically registers an {@link Instantiator} for a given type.
     * 
     * @param forType type for which an {@link Instantiator} is to be registered
     * @param instantiator {@link Instantiator} type
     * @param <T> type for which an {@link Instantiator} is registered and thus the (sub-)type that the instantiator creates
     */
    public <T> void registerInstantiator(Class<T> forType, Class<? extends Instantiator<? extends T>> instantiator) {
        instantiateByMap.put(forType, YamlInstantiateBy.Factory.of(instantiator));
    }

    /**
     * Programmatically unregisters an {@link Instantiator} for the given type.
     * 
     * @param forType type for which an {@link Instantiator} is to be unregistered
     * @param ignoreGlobalInstantiator if <code>true</code>, a registered global instantiator will be ignored, so the instantiation logic is the default
     *            SnakeYaml logic; if <code>false</code>, a global instantiator is still considered.
     */
    public void unregisterInstantiator(Class<?> forType, boolean ignoreGlobalInstantiator) {
        if (ignoreGlobalInstantiator) {
            instantiateByMap.put(forType, YamlInstantiateBy.Factory.of(DefaultInstantiator.class));
        } else {
            instantiateByMap.put(forType, YamlInstantiateBy.Factory.of(Instantiator.NoInstantiator.class));
        }
    }

    /**
     * Constructs an object by using either the default constructor (usually the SnakeYaml way) or - if registered - the custom constructor if there is one
     * defined for the given node type.
     * 
     * @param node node
     * @param defaultConstructor default constructor
     * @param <T> object type
     * @return constructed object
     */
    private <T> T constructObject(Node node, Function<Node, T> defaultConstructor) {
        YamlConstructBy constructBy = getConstructBy(node.getType());
        if (constructBy != null) {
            try {
                @SuppressWarnings("unchecked")
                CustomConstructor<T> constructor = (CustomConstructor<T>) constructBy.value().newInstance();
                return constructor.construct(node, defaultConstructor);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new YAMLException("Cannot create custom constructor " + constructBy.value().getName(), e);
            }
        }
        return defaultConstructor.apply(node);
    }

    /**
     * Returns a matching {@link YamlConstructBy} annotation by using the following rules, given the node is of type <code>T</code>:<br>
     * Walk the superclass hierarchy of <code>T</code>, then all interfaces of <code>T</code>.<br>
     * For each superclass/interface <code>S super T</code>, check first if there is an entry in the {@link #getConstructByMap()} for <code>S</code>
     * and if so, return the {@code YamlConstructBy} from the map;
     * if not, check if <code>S</code> is annotated with {@code YamlConstructBy}, and if so, return the {@code YamlConstructBy} from the annotation.<br>
     * If there is no match for <code>S</code>, proceed with the next superclass/interface.
     * if no match was found after walking the whole hierarchy, <code>null</code> is returned.
     * 
     * @param type type
     * @return {@link YamlConstructBy} or <code>null</code> if no matching {@code YamlConstructBy} found
     */
    protected YamlConstructBy getConstructBy(Class<?> type) {
        YamlConstructBy constructByFoundInMap = null;
        YamlConstructBy constructByAnnotation = null;

        List<Class<?>> typesInHierarchy = new ArrayList<>();
        typesInHierarchy.add(type);
        typesInHierarchy.addAll(ClassUtils.getAllSuperclasses(type));
        typesInHierarchy.addAll(ClassUtils.getAllInterfaces(type));

        for (Class<?> typeToFindInMap : typesInHierarchy) {
            constructByFoundInMap = constructByMap.get(typeToFindInMap);
            if (constructByFoundInMap != null) {
                return constructByFoundInMap;
            }
            constructByAnnotation = typeToFindInMap.getDeclaredAnnotation(YamlConstructBy.class);
            if (constructByAnnotation != null) {
                return constructByAnnotation;
            }
        }

        return null;
    }

    /**
     * Returns a matching {@link YamlInstantiateBy} annotation by using the following rule:
     * <ol>
     * <li>If there is an entry in the {@link #instantiateByMap} for <code>type</code> return the {@link YamlInstantiateBy} from the map</li>
     * <li>If <code>type</code> is annotated with {@link YamlInstantiateBy}, return the annotation.</li>
     * </ol>
     * If no match was found, <code>null</code> is returned.
     * 
     * @param type type
     * @return {@link YamlInstantiateBy} or <code>null</code> if no matching {@link YamlInstantiateBy} found
     */
    protected YamlInstantiateBy getInstantiateBy(Class<?> type) {
        return instantiateByMap.getOrDefault(type, type.getAnnotation(YamlInstantiateBy.class));
    }

    private static ScalarNode getKeyNode(NodeTuple tuple) {
        if (tuple.getKeyNode() instanceof ScalarNode) {
            return (ScalarNode) tuple.getKeyNode();
        } else {
            throw new YAMLException("Keys must be scalars but found: " + tuple.getKeyNode());
        }
    }

}
