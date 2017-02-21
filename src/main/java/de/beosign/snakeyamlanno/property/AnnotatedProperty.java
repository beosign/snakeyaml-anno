package de.beosign.snakeyamlanno.property;

import java.util.Comparator;

import de.beosign.snakeyamlanno.annotation.Property;

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

            if (property1 instanceof AnnotatedProperty) {
                AnnotatedProperty anno1 = (AnnotatedProperty) property1;
                order1 = anno1.getPropertyAnnotation().order();
            }
            if (property2 instanceof AnnotatedProperty) {
                AnnotatedProperty anno2 = (AnnotatedProperty) property2;
                order2 = anno2.getPropertyAnnotation().order();
            }

            return order2 - order1;
        }
    };

    /**
     * Returns the annotation on the field / method.
     */
    Property getPropertyAnnotation();

}
