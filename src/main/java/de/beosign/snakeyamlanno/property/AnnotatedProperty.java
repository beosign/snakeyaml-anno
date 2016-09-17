package de.beosign.snakeyamlanno.property;

import de.beosign.snakeyamlanno.annotation.Property;

/**
 * Common interface for annotated properties.
 * 
 * @author florian
 */
public interface AnnotatedProperty {
    /**
     * Returns the annotation on the field / method.
     * 
     * @return the annotation on the field / method
     */
    Property getPropertyAnnotation();

}
