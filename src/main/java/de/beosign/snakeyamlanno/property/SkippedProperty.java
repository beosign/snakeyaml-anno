package de.beosign.snakeyamlanno.property;

import org.yaml.snakeyaml.introspector.Property;

/**
 * This special property type is used to indicate that a value must be skipped and <b>not</b> set into the model class, so a call to "set" does nothing.
 * 
 * @author florian
 */
public class SkippedProperty extends AnnotatedProperty {

    /**
     * New instance.
     * 
     * @param originalProperty property that was discovered and is now used as a delegate for this property
     */
    public SkippedProperty(Property originalProperty) {
        super(originalProperty);
    }

    /**
     * Setter does nothing.
     */
    @Override
    public void set(Object object, Object value) throws Exception {
    }

}
