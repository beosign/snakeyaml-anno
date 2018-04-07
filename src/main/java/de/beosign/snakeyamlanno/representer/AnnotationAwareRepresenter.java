package de.beosign.snakeyamlanno.representer;

import java.util.ArrayList;
import java.util.Comparator;
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
import de.beosign.snakeyamlanno.skip.SkipAtDumpPredicate;

/**
 * Representer that is aware of annotations.
 * 
 * @author florian
 */
public class AnnotationAwareRepresenter extends Representer {
    /**
     * Returns a comparator that orders the properties according to their order value.
     */
    public static final Comparator<org.yaml.snakeyaml.introspector.Property> ORDER_COMPARATOR = new Comparator<org.yaml.snakeyaml.introspector.Property>() {

        @Override
        public int compare(org.yaml.snakeyaml.introspector.Property property1, org.yaml.snakeyaml.introspector.Property property2) {
            int order1 = 0;
            int order2 = 0;

            de.beosign.snakeyamlanno.annotation.Property propertyAnnotation1 = property1.getAnnotation(de.beosign.snakeyamlanno.annotation.Property.class);
            de.beosign.snakeyamlanno.annotation.Property propertyAnnotation2 = property2.getAnnotation(de.beosign.snakeyamlanno.annotation.Property.class);
            if (propertyAnnotation1 != null) {
                order1 = propertyAnnotation1.order();
            }
            if (propertyAnnotation2 != null) {
                order2 = propertyAnnotation2.order();
            }

            return order2 - order1;
        }
    };

    /**
     * Sets the {@link AnnotationAwarePropertyUtils} into this representer.
     */
    public AnnotationAwareRepresenter() {
        setPropertyUtils(new AnnotationAwarePropertyUtils());
    }

    @Override
    protected Set<Property> getProperties(Class<? extends Object> type) {
        Set<Property> propertySet = super.getProperties(type);

        // order properties
        List<Property> orderedList = new ArrayList<>(propertySet);
        orderedList.sort(ORDER_COMPARATOR);
        Set<Property> orderedProperties = new LinkedHashSet<>(orderedList);
        orderedProperties.addAll(propertySet);

        return orderedProperties;
    }

    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        de.beosign.snakeyamlanno.annotation.Property propertyAnnotation = property.getAnnotation(de.beosign.snakeyamlanno.annotation.Property.class);
        if (propertyAnnotation != null) {
            if (propertyAnnotation.skipAtDump()) {
                return null;
            }

            if (propertyAnnotation.skipAtDumpIf() != SkipAtDumpPredicate.class) {
                try {
                    SkipAtDumpPredicate skipAtDumpPredicate = propertyAnnotation.skipAtDumpIf().newInstance();
                    if (skipAtDumpPredicate.skip(javaBean, property, propertyValue, customTag)) {
                        return null;
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new YAMLException("Cannot create an instance of " + propertyAnnotation.skipAtDumpIf().getName(), e);
                }

            }
        }

        NodeTuple nodeTuple = super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);

        if (propertyAnnotation != null) {
            if (StringUtils.isNotBlank(propertyAnnotation.key())) {
                ScalarNode keyNode = (ScalarNode) representData(propertyAnnotation.key());
                return new NodeTuple(keyNode, nodeTuple.getValueNode());
            }
        }
        return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
    }

}
