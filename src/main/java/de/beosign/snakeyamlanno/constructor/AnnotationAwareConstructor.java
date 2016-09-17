package de.beosign.snakeyamlanno.constructor;

import java.beans.IntrospectionException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import de.beosign.snakeyamlanno.property.AnnotatedFieldProperty;
import de.beosign.snakeyamlanno.property.AnnotatedMethodProperty;

public class AnnotationAwareConstructor extends Constructor {
    private static final Logger log = LoggerFactory.getLogger(AnnotationAwareConstructor.class);

    public AnnotationAwareConstructor(Class<? extends Object> theRoot) {
        super(theRoot);
        setPropertyUtils(new AnnotationAwarePropertyUtils());
        yamlClassConstructors.put(NodeId.mapping, new AnnotationAwareMappingConstructor());
    }

    protected class AnnotationAwareMappingConstructor extends ConstructMapping {
        @Override
        protected Object constructJavaBean2ndStep(MappingNode node, Object object) {
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
                    if (property instanceof AnnotatedFieldProperty) {
                        AnnotatedFieldProperty fp = (AnnotatedFieldProperty) property;
                        if (fp.getPropertyAnnotation().converter() != NoConverter.class) {
                            fp.set(object, fp.getPropertyAnnotation().converter().newInstance().convertToModel(valueNode));
                        }
                    } else if (property instanceof AnnotatedMethodProperty) {
                        AnnotatedMethodProperty mp = (AnnotatedMethodProperty) property;

                        if (mp.getMethodPropertyAnnotation() != null && mp.getMethodPropertyAnnotation().converter() != NoConverter.class) {
                            mp.set(object, mp.getMethodPropertyAnnotation().converter().newInstance().convertToModel(valueNode));
                        }
                    }
                } catch (Exception e) {
                    throw new YAMLException("Cannot create property=" + key
                            + " for JavaBean=" + object, e);
                }
            }
            return super.constructJavaBean2ndStep(node, object);
        }

        @Override
        protected Property getProperty(Class<? extends Object> type, String name) throws IntrospectionException {
            log.debug("type = " + type.getName() + ", name = " + name);

            Property property = super.getProperty(type, name);
            if (property instanceof AnnotatedFieldProperty) {
                AnnotatedFieldProperty fp = (AnnotatedFieldProperty) property;
                if (fp.getPropertyAnnotation().converter() != NoConverter.class) {
                    // already set above
                    return new MissingProperty(name);
                }
            } else if (property instanceof AnnotatedMethodProperty) {
                AnnotatedMethodProperty mp = (AnnotatedMethodProperty) property;

                if (mp.getMethodPropertyAnnotation() != null && mp.getMethodPropertyAnnotation().converter() != NoConverter.class) {
                    // already set above
                    return new MissingProperty(name);
                }
            }
            return super.getProperty(type, name);
        }

    }
}
