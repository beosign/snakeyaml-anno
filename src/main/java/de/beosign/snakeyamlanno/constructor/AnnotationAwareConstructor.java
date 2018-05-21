package de.beosign.snakeyamlanno.constructor;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Arrays;
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
import de.beosign.snakeyamlanno.annotation.Type;
import de.beosign.snakeyamlanno.type.NoSubstitutionTypeSelector;
import de.beosign.snakeyamlanno.type.SubstitutionTypeSelector;

/**
 * Needed for implementing the auto type detection feature.
 * 
 * @author florian
 */
public class AnnotationAwareConstructor extends Constructor {
    private static final Logger log = LoggerFactory.getLogger(AnnotationAwareConstructor.class);

    private Map<Class<?>, Type> typesMap = new HashMap<>();
    private Map<Class<?>, ConstructBy> constructByMap = new HashMap<>();
    private IdentityHashMap<Node, Property> nodeToPropertyMap = new IdentityHashMap<>();

    /**
     * Creates constructor.
     * 
     * @param theRoot root class - you can cast the result of the parsing process to this class
     */
    public AnnotationAwareConstructor(Class<? extends Object> theRoot) {
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
     * Can be modified to manually define types for a given type. This is the programmatic counterpart to the {@link Type} annotation.
     * It can only be used to add additional types. If you want to override / disable a {@link Type} annotation that is already set on a class, override
     * the {@link AnnotationAwareMappingConstructor#getTypeForClass(Class)} method and return a restricted map.
     * 
     * @return map from a type to its substitution types
     */
    public Map<Class<?>, Type> getTypesMap() {
        return typesMap;
    }

    /**
     * Overridden to implement the "auto type detection" feature.
     */
    @Override
    protected Object newInstance(Class<?> ancestor, Node node, boolean tryDefault) throws InstantiationException {
        if (node instanceof MappingNode) {
            MappingNode mappingNode = (MappingNode) node;
            Class<?> type = mappingNode.getType();
            Type typeAnnotation = getTypeForClass(type);

            if (typeAnnotation != null && typeAnnotation.substitutionTypes().length > 0) {
                // One or more substitution types have been defined
                List<Class<?>> validSubstitutionTypes = new ArrayList<>();
                SubstitutionTypeSelector substitutionTypeSelector = null;

                if (typeAnnotation.substitutionTypeSelector() != NoSubstitutionTypeSelector.class) {
                    try {
                        // check if default detection algorithm is to be applied
                        substitutionTypeSelector = typeAnnotation.substitutionTypeSelector().newInstance();
                        if (!substitutionTypeSelector.disableDefaultAlgorithm()) {
                            validSubstitutionTypes = getValidSubstitutionTypes(type, typeAnnotation, mappingNode.getValue());
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new YAMLException("Cannot instantiate substitutionTypeSelector of type " + typeAnnotation.substitutionTypeSelector().getName(),
                                e);
                    }
                } else {
                    validSubstitutionTypes = getValidSubstitutionTypes(type, typeAnnotation, mappingNode.getValue());
                }

                if (substitutionTypeSelector != null) {
                    node.setType(substitutionTypeSelector.getSelectedType(mappingNode, validSubstitutionTypes));
                    log.debug("Type = {}, using substitution type {} calculated by SubstitutionTypeSelector {}", type, node.getType(),
                            typeAnnotation.substitutionTypeSelector().getName());
                } else {
                    if (validSubstitutionTypes.size() == 0) {
                        log.warn("Type = {}, NO possible substitution types found, using default YAML algorithm", type);
                    } else {
                        if (validSubstitutionTypes.size() > 1) {
                            log.debug("Type = {}, using substitution types = {}, choosing first", type, validSubstitutionTypes);
                        } else {
                            log.trace("Type = {}, using substitution type = {}", type, validSubstitutionTypes.get(0));
                        }
                        node.setType(validSubstitutionTypes.get(0));
                    }
                }

            }
        }
        return super.newInstance(ancestor, node, tryDefault);
    }

    /**
     * Returns the {@link Type} that is registered for the given class. If a type has been manually registered, this is returned. Otherwise, the
     * {@link Type}
     * annotation on the given class is returned.
     * 
     * @param clazz class
     * @return {@link Type} for given type
     */
    protected Type getTypeForClass(Class<?> clazz) {
        Type typeAnnotation = clazz.getAnnotation(Type.class);

        return typesMap.getOrDefault(clazz, typeAnnotation);
    }

    protected List<?> constructNodeAsList(Node node, Function<Node, Object> defaultConstructor) {
        Class<?> origType = node.getType();
        Property propertyOfNode = nodeToPropertyMap.get(node);
        if (propertyOfNode != null && propertyOfNode.getActualTypeArguments() != null && propertyOfNode.getActualTypeArguments().length > 0) {
            node.setType(propertyOfNode.getActualTypeArguments()[0]);
        }
        Object singleObject = defaultConstructor.apply(node);
        node.setType(origType);
        if (singleObject == null) {
            return null;
        } else {
            return Collections.singletonList(singleObject);
        }
    }

    /**
     * Returns all <b>valid</b> substitution types from the list given by the {@link Type#substitutionTypes()} method. This method
     * helps implementing the "auto type detection" feature.
     * 
     * @param type type
     * @param typeAnnotation the {@link Type} annotation to use for the given class
     * @param nodeValue node values
     */
    private List<Class<?>> getValidSubstitutionTypes(Class<?> type, Type typeAnnotation, List<NodeTuple> nodeValue) {
        List<Class<?>> validSubstitutionTypes = new ArrayList<>();
        List<? extends Class<?>> substitutionTypeList = Arrays.asList(typeAnnotation.substitutionTypes());
        /*
         *  For each possible substitution type, check if all YAML properties match a Bean property.
         *  If this is the case, this subtype is a valid substitution
         */
        for (Class<?> substitutionType : substitutionTypeList) {
            boolean isValidType = true;
            for (NodeTuple tuple : nodeValue) {
                String key = null;
                try {
                    ScalarNode keyNode = getKeyNode(tuple);
                    key = (String) AnnotationAwareConstructor.this.constructObject(keyNode);
                    final String propName = key;

                    boolean found = Arrays.stream(Introspector.getBeanInfo(substitutionType).getPropertyDescriptors())
                            .anyMatch(pd -> pd.getName().equals(propName));
                    if (!found) { // search in aliases
                        found = getPropertyUtils().getProperties(substitutionType).stream()
                                .map(p -> p.getAnnotation(de.beosign.snakeyamlanno.annotation.Property.class))
                                .filter(anno -> anno != null)
                                .anyMatch(anno -> propName.equals(anno.key()));

                    }
                    if (!found) {
                        throw new YAMLException("Cannot find a property named " + propName + " in type " + substitutionType.getTypeName());
                    }

                } catch (YAMLException | IntrospectionException e) {
                    log.debug("Evaluating subsitution of type {}: Could not construct property {}.{}: {}", type, substitutionType.getName(), key,
                            e.getMessage());
                    isValidType = false;
                    break;
                }
            }
            if (isValidType) {
                validSubstitutionTypes.add(substitutionType);
            }

        }

        log.trace("Type = {}, found valid substitution types: {}", type, validSubstitutionTypes);
        return validSubstitutionTypes;
    }

    /**
     * This constructor implements the features "automatic type detection", "ignore error", "constructBy at property-level" and "singleton list parsing" feature.
     * 
     * @author florian
     */
    protected class AnnotationAwareMappingConstructor extends ConstructMapping {

        @Override
        public Object construct(Node node) {
            if (Collection.class.isAssignableFrom(node.getType())) {
                return constructNodeAsList(node, super::construct);
            } else {
                return super.construct(node);
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


                if (property.getAnnotation(ConstructBy.class) != null) {
                    Object value = null;
                    try {
                        @SuppressWarnings("unchecked")
                        CustomConstructor<Object> cc = (CustomConstructor<Object>) property.getAnnotation(ConstructBy.class).value().newInstance();
                        Construct constructor = getConstructor(valueNode);
                        value = cc.construct(valueNode, constructor::construct);
                        property.set(object, value);
                        nodeTuplesToBeRemoved.add(tuple);
                    } catch (YAMLException e) {
                        throw e;
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new YAMLException(
                                "Custom constructor " + property.getAnnotation(ConstructBy.class).value() + //
                                        " on property " + object.getClass().getTypeName() + "::" + property + " cannot be created",
                                e);
                    } catch (Exception e) {
                        throw new YAMLException(
                                "Cannot set value of type " + (value != null ? value.getClass().getTypeName() : "null") + //
                                        " into property " + object.getClass().getTypeName() + "::" + property,
                                e);
                    }
                } else {
                    de.beosign.snakeyamlanno.annotation.Property propertyAnnotation = property.getAnnotation(de.beosign.snakeyamlanno.annotation.Property.class);
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

        @Override
        public Object construct(Node node) {
            return constructObject(node, super::construct);
        }
    }

    /**
     * Enables creating a complex object from a scalar. Useful if object to be created cannot be modified soa
     * adding a single argument constructor is not possible, e.g. enums
     * 
     * @author florian
     */
    protected class AnnotationAwareScalarConstructor extends ConstructScalar {
        @Override
        public Object construct(Node node) {
            return constructObject(node, super::construct);
        }
    }

    /**
     * Enables creating a complex object from a scalar. Useful if object to be created cannot be modified so
     * adding a single argument constructor is not possible, e.g. enums
     * 
     * @author florian
     */
    protected class AnnotationAwareScalarConstructor extends ConstructScalar {
        @Override
        public Object construct(Node nnode) {
            if (Collection.class.isAssignableFrom(nnode.getType())) {
                return constructNodeAsList(nnode, super::construct);
            }
            return constructObject(node, super::construct);
        }
    }

     * @return all programmatically registered class-to-constructBy associations.
     */
    public Map<Class<?>, ConstructBy> getConstructByMap() {
        return constructByMap;
    }

    /**
     * Programmatically registers a {@link CustomConstructor} for a given type. This is a convenience method for putting something into
     * {@link #getConstructByMap()}.
     * 
     * @param forType type for which a CustomConverter is to be registered
     * @param customConstructorClass {@link CustomConstructor} type
     */
    public void registerCustomConstructor(Class<?> forType, Class<? extends CustomConstructor<?>> customConstructorClass) {
        constructByMap.put(forType, ConstructByFactory.of(customConstructorClass));
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
        ConstructBy constructBy = getConstructBy(node.getType());
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
     * Returns a matching {@link ConstructBy} annotation by using the following rules, given the node is of type <code>T</code>:<br>
     * Walk the superclass hierarchy of <code>T</code>, then all interfaces of <code>T</code>.<br>
     * For each superclass/interface <code>S super T</code>, check first if there is an entry in the {@link #getConstructByMap()} for <code>S</code>
     * and if so, return the ConstructBy from the map;
     * if not, check if <code>S</code> is annotated with ConstructBy, and if so, return the ConstructBy from the annotation.<br>
     * If there is no match for <code>S</code>, proceed with the next superclass/interface.
     * if no match was found after walking the whole hierarchy, <code>null</code> is returned.
     * 
     * @param type type
     * @return {@link ConstructBy} or <code>null</code> if no matching ConstructBy found
     */
    protected ConstructBy getConstructBy(Class<?> type) {
        ConstructBy constructByFoundInMap = null;
        ConstructBy constructByAnnotation = null;

        List<Class<?>> typesInHierarchy = new ArrayList<>();
        typesInHierarchy.add(type);
        typesInHierarchy.addAll(ClassUtils.getAllSuperclasses(type));
        typesInHierarchy.addAll(ClassUtils.getAllInterfaces(type));

        for (Class<?> typeToFindInMap : typesInHierarchy) {
            constructByFoundInMap = constructByMap.get(typeToFindInMap);
            if (constructByFoundInMap != null) {
                return constructByFoundInMap;
            }
            constructByAnnotation = typeToFindInMap.getDeclaredAnnotation(ConstructBy.class);
            if (constructByAnnotation != null) {
                return constructByAnnotation;
            }
        }

        return null;
    }

    private static ScalarNode getKeyNode(NodeTuple tuple) {
        if (tuple.getKeyNode() instanceof ScalarNode) {
            return (ScalarNode) tuple.getKeyNode();
        } else {
            throw new YAMLException("Keys must be scalars but found: " + tuple.getKeyNode());
        }
    }

}
