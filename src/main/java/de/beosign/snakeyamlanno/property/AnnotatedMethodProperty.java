package de.beosign.snakeyamlanno.property;

import java.beans.PropertyDescriptor;

import org.yaml.snakeyaml.introspector.MethodProperty;

import de.beosign.snakeyamlanno.annotation.Property;

/**
 * Represents a getter method that has a {@link Property} annotation.
 * 
 * @author florian
 */
public class AnnotatedMethodProperty extends MethodProperty implements AnnotatedProperty {
    private PropertyDescriptor propertyDescriptor;

    /**
     * Constructor.
     * 
     * @param property property
     */
    public AnnotatedMethodProperty(PropertyDescriptor property) {
        super(property);
        this.propertyDescriptor = property;
    }

    /**
     * The annotated property descriptor.
     * 
     * @return annotated property descriptor.
     */
    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    @Override
    public Property getPropertyAnnotation() {
        return propertyDescriptor.getReadMethod().getAnnotation(Property.class);
    }

}
