package de.beosign.snakeyamlanno.constructor;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import de.beosign.snakeyamlanno.convert.NoConverter;
import de.beosign.snakeyamlanno.property.AnnotatedProperty;
import de.beosign.snakeyamlanno.property.ConvertedProperty;
import de.beosign.snakeyamlanno.property.SkippedProperty;
import de.beosign.snakeyamlanno.type.NoSubstitutionTypeSelector;
import de.beosign.snakeyamlanno.type.SubstitutionTypeSelector;

/**
 * When parsing, this constructor must be used in order to honor annotations on the target bean class.
 * 
 * @author florian
 */
public class AnnotationAwareConstructor extends Constructor {
    private static final Logger log = LoggerFactory.getLogger(AnnotationAwareConstructor.class);

    private Map<Class<?>, Type> typesMap = new HashMap<>();

    /**
     * Creates constructor.
     * 
     * @param theRoot root class - you can cast the result of the parsing process to this class
     */
    public AnnotationAwareConstructor(Class<? extends Object> theRoot) {
        super(theRoot);
        setPropertyUtils(new AnnotationAwarePropertyUtils());
        yamlClassConstructors.put(NodeId.mapping, new AnnotationAwareMappingConstructor());
    }

    /**
     * This constructor checks for converter information on annotated properties and calls the converter's methods.
     * 
     * @author florian
     */
    protected class AnnotationAwareMappingConstructor extends ConstructMapping {
        @Override
        protected Object createEmptyJavaBean(MappingNode node) {
            Class<?> type = node.getType();
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
                            validSubstitutionTypes = getValidSubstitutionTypes(type, typeAnnotation, node.getValue());
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new YAMLException("Cannot instantiate substitutionTypeSelector of type " + typeAnnotation.substitutionTypeSelector().getName(),
                                e);
                    }
                } else {
                    validSubstitutionTypes = getValidSubstitutionTypes(type, typeAnnotation, node.getValue());
                }

                if (substitutionTypeSelector != null) {
                    node.setType(substitutionTypeSelector.getSelectedType(node, validSubstitutionTypes));
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
            return super.createEmptyJavaBean(node);
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

        /**
         * Returns all <b>valid</b> substitution types from the list given by the {@link Type#substitutionTypes()} method.
         * 
         * @param clazz type
         * @param typeAnnotation the {@link Type} annotation to use for the given class
         * @param nodeValue node values
         */
        private List<Class<?>> getValidSubstitutionTypes(Class<?> clazz, Type typeAnnotation, List<NodeTuple> nodeValue) {
            List<Class<?>> validSubstitutionTypes = new ArrayList<>();

            /*
             *  For each possible substitution type, check if all YAML properties match a Bean property.
             *  If this is the case, this subtype is a valid substitution
             */
            for (Class<?> substitutionType : typeAnnotation.substitutionTypes()) {
                boolean isValidType = true;
                for (NodeTuple tuple : nodeValue) {
                    String key = null;
                    try {
                        ScalarNode keyNode;
                        if (tuple.getKeyNode() instanceof ScalarNode) {
                            keyNode = (ScalarNode) tuple.getKeyNode();
                        } else {
                            throw new YAMLException("Keys must be scalars but found: " + tuple.getKeyNode());
                        }
                        key = (String) AnnotationAwareConstructor.this.constructObject(keyNode);
                        getProperty(substitutionType, key);
                    } catch (YAMLException | IntrospectionException e) {
                        log.debug("Evaluating subsitution of type {}: Could not construct property {}.{}: {}", clazz, substitutionType.getName(), key,
                                e.getMessage());
                        isValidType = false;
                        break;
                    }
                }
                if (isValidType) {
                    validSubstitutionTypes.add(substitutionType);
                }

            }

            log.trace("Type = {}, found valid substitution types: {}", clazz, validSubstitutionTypes);
            return validSubstitutionTypes;
        }

        @Override
        protected Object constructJavaBean2ndStep(MappingNode node, Object object) {
            List<NodeTuple> unconstructableNodeTuples = new ArrayList<>();

            Class<? extends Object> beanType = node.getType();
            List<NodeTuple> nodeValue = node.getValue();
            for (NodeTuple tuple : nodeValue) {
                ScalarNode keyNode;
                if (tuple.getKeyNode() instanceof ScalarNode) {
                    keyNode = (ScalarNode) tuple.getKeyNode();
                } else {
                    throw new YAMLException("Keys must be scalars but found: " + tuple.getKeyNode());
                }
                Node valueNode = tuple.getValueNode();

                keyNode.setType(String.class);
                String key = (String) AnnotationAwareConstructor.this.constructObject(keyNode);
                try {
                    Property property = super.getProperty(beanType, key);
                    if (property instanceof AnnotatedProperty) {
                        AnnotatedProperty annotatedProperty = (AnnotatedProperty) property;
                        if (annotatedProperty.getPropertyAnnotation().converter() != NoConverter.class) {
                            property.set(object, annotatedProperty.getPropertyAnnotation().converter().newInstance().convertToModel(valueNode));
                        } else {
                            /* 
                             * No converter present, so let YAML set the value.
                             */
                            if (annotatedProperty.getPropertyAnnotation().ignoreExceptions()) {
                                try {
                                    Construct constructor = getConstructor(valueNode);
                                    constructor.construct(valueNode);
                                } catch (Exception e) {
                                    log.debug("Ignore: Could not construct property {}.{}: {}", beanType, key, e.getMessage());
                                    unconstructableNodeTuples.add(tuple);
                                }
                            }

                        }

                    }

                } catch (Exception e) {
                    throw new YAMLException("Cannot create property=" + key
                            + " for JavaBean=" + object, e);
                }
            }

            // Remove nodes that are unconstructable
            unconstructableNodeTuples.forEach(nt -> node.getValue().remove(nt));

            return super.constructJavaBean2ndStep(node, object);
        }

        @Override
        protected Property getProperty(Class<? extends Object> type, String name) throws IntrospectionException {
            log.debug("type = " + type.getName() + ", name = " + name);

            Property property = super.getProperty(type, name);
            if (property instanceof AnnotatedProperty) {
                AnnotatedProperty annotatedProperty = (AnnotatedProperty) property;
                if (annotatedProperty.getPropertyAnnotation().skipAtLoad()) {
                    // value must not be set
                    return new SkippedProperty(name);
                }
                if (annotatedProperty.getPropertyAnnotation().converter() != NoConverter.class) {
                    // value has already set above in constructJavaBean2ndStep
                    return new ConvertedProperty(name);
                }
            }
            return super.getProperty(type, name);
        }

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
}
