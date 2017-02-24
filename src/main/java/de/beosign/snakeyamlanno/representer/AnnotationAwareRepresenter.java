package de.beosign.snakeyamlanno.representer;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import de.beosign.snakeyamlanno.AnnotationAwarePropertyUtils;
import de.beosign.snakeyamlanno.property.AnnotatedProperty;
import de.beosign.snakeyamlanno.skip.SkipAtDumpPredicate;

/**
 * Representer that is aware of annotations.
 * 
 * @author florian
 */
public class AnnotationAwareRepresenter extends Representer {

    /**
     * Sets the {@link AnnotationAwarePropertyUtils} into this representer.
     */
    public AnnotationAwareRepresenter() {
        setPropertyUtils(new AnnotationAwarePropertyUtils());
    }

    @Override
    protected Set<Property> getProperties(Class<? extends Object> type) throws IntrospectionException {
        Set<Property> propertySet = super.getProperties(type);

        // order properties
        List<Property> orderedList = new ArrayList<>(propertySet);
        orderedList.sort(AnnotatedProperty.ORDER_COMPARATOR);
        Set<Property> orderedProperties = new LinkedHashSet<>(orderedList);
        orderedProperties.addAll(propertySet);

        return orderedProperties;
    }

    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        if (property instanceof AnnotatedProperty) {
            AnnotatedProperty annotatedProperty = (AnnotatedProperty) property;
            if (annotatedProperty.getPropertyAnnotation().skipAtDump()) {
                return null;
            }

            if (annotatedProperty.getPropertyAnnotation().skipAtDumpIf() != SkipAtDumpPredicate.class) {
                try {
                    SkipAtDumpPredicate skipAtDumpPredicate = annotatedProperty.getPropertyAnnotation().skipAtDumpIf().newInstance();
                    if (skipAtDumpPredicate.skip(javaBean, property, propertyValue, customTag)) {
                        return null;
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new YAMLException("Cannot create an instance of " + annotatedProperty.getPropertyAnnotation().skipAtDumpIf().getName(), e);
                }

            }
        }

        NodeTuple nodeTuple = super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);

        if (property instanceof AnnotatedProperty) {
            AnnotatedProperty annotatedProperty = (AnnotatedProperty) property;
            if (StringUtils.isNotBlank(annotatedProperty.getPropertyAnnotation().key())) {
                ScalarNode keyNode = (ScalarNode) representData(annotatedProperty.getPropertyAnnotation().key());
                return new NodeTuple(keyNode, nodeTuple.getValueNode());
            }
        }
        return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
    }

}
