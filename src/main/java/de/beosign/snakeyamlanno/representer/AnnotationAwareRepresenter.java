package de.beosign.snakeyamlanno.representer;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import de.beosign.snakeyamlanno.AnnotationAwarePropertyUtils;
import de.beosign.snakeyamlanno.property.YamlProperty;
import de.beosign.snakeyamlanno.skip.SkipAtDumpPredicate;
import de.beosign.snakeyamlanno.skip.SkipIfEmpty;
import de.beosign.snakeyamlanno.skip.SkipIfNull;

/**
 * Representer that is aware of annotations. Implements the features "order properties" and "skip properties to dump".
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

            YamlProperty propertyAnnotation1 = property1.getAnnotation(YamlProperty.class);
            YamlProperty propertyAnnotation2 = property2.getAnnotation(YamlProperty.class);
            if (propertyAnnotation1 != null) {
                order1 = propertyAnnotation1.order();
            }
            if (propertyAnnotation2 != null) {
                order2 = propertyAnnotation2.order();
            }

            if (order2 != order1) {
                return order2 - order1;
            }

            return property1.compareTo(property2); // default comparison
        }
    };

    private boolean skipEmpty;

    /**
     * Sets the {@link AnnotationAwarePropertyUtils} into this representer. Skips all empty properties.
     */
    public AnnotationAwareRepresenter() {
        this(true);
    }

    /**
     * Sets the {@link AnnotationAwarePropertyUtils} into this representer and skips all empty properties if flag is set.
     * If flag is set, this overrides any "skipAtDumpIf" properties.
     * 
     * @param skipEmpty if true, empty properties are skipped.
     */
    public AnnotationAwareRepresenter(boolean skipEmpty) {
        setPropertyUtils(new AnnotationAwarePropertyUtils());
        this.skipEmpty = skipEmpty;
    }

    @Override
    protected Set<Property> getProperties(Class<? extends Object> type) {
        Set<Property> propertySet = super.getProperties(type);

        // order properties
        TreeSet<Property> orderedProperties = new TreeSet<>(ORDER_COMPARATOR);
        orderedProperties.addAll(propertySet);

        return orderedProperties;
    }

    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        if (skipEmpty && SkipIfEmpty.getInstance().skip(javaBean, property, propertyValue, customTag)) {
            return null;
        }
        if (skipEmpty && SkipIfNull.getInstance().skip(javaBean, property, propertyValue, customTag)) {
            return null;
        }

        YamlProperty propertyAnnotation = property.getAnnotation(YamlProperty.class);
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

        return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
    }

}
