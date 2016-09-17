package de.beosign.snakeyamlanno.property;

import java.beans.PropertyDescriptor;

import org.yaml.snakeyaml.introspector.MethodProperty;

import de.beosign.snakeyamlanno.annotation.Property;

public class AnnotatedMethodProperty extends MethodProperty implements AnnotatedProperty {
    private PropertyDescriptor propertyDescriptor;

    public AnnotatedMethodProperty(PropertyDescriptor property) {
        super(property);
        this.propertyDescriptor = property;
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    @Override
    public Property getPropertyAnnotation() {
        return propertyDescriptor.getReadMethod().getAnnotation(Property.class);
    }

}
