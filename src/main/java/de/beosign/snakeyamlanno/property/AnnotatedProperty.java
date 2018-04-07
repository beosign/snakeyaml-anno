package de.beosign.snakeyamlanno.property;

import java.util.Comparator;

import de.beosign.snakeyamlanno.annotation.Property;
import de.beosign.snakeyamlanno.convert.NoConverter;

/**
 * Common interface for annotated properties.
 * 
 * @author florian
 */
public interface AnnotatedProperty {
    /**
     * Returns a comparator that orders the properties according to their order value.
     */
    Comparator<org.yaml.snakeyaml.introspector.Property> ORDER_COMPARATOR = new Comparator<org.yaml.snakeyaml.introspector.Property>() {

        @Override
        public int compare(org.yaml.snakeyaml.introspector.Property property1, org.yaml.snakeyaml.introspector.Property property2) {
            int order1 = 0;
            int order2 = 0;

            Property propertyAnnotation1 = property1.getAnnotation(Property.class);
            Property propertyAnnotation2 = property2.getAnnotation(Property.class);
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
     * @return the annotation on the field / method.
     */
    Property getPropertyAnnotation();

    /**
     * Returns true if a converter is present.
     * 
     * @return true if a converter is present
     */
    default boolean isConverterPresent() {
        return getPropertyAnnotation() != null && !getPropertyAnnotation().converter().equals(NoConverter.class);
    }
}
