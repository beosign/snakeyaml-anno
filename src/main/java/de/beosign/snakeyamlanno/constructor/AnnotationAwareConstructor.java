package de.beosign.snakeyamlanno.constructor;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.MissingProperty;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import de.beosign.snakeyamlanno.AnnotationAwarePropertyUtils;
import de.beosign.snakeyamlanno.convert.NoConverter;
import de.beosign.snakeyamlanno.property.AnnotatedProperty;

/**
 * When parsing, this constructor must be used in order to honor annotations on the target bean class.
 * 
 * @author florian
 */
public class AnnotationAwareConstructor extends Constructor {
    private static final Logger log = LoggerFactory.getLogger(AnnotationAwareConstructor.class);

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
                                    log.info("Could not construct property {}.{}: {}", beanType, key, e.getMessage());
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
                if (annotatedProperty.getPropertyAnnotation().converter() != NoConverter.class) {
                    // value has already set above in constructJavaBean2ndStep
                    return new MissingProperty(name);
                }
            }
            return super.getProperty(type, name);
        }

    }
}
