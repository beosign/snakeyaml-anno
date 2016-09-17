package de.beosign.snakeyamlanno.property;

import java.lang.reflect.Field;

import org.yaml.snakeyaml.introspector.FieldProperty;

import de.beosign.snakeyamlanno.annotation.Property;

public class AnnotatedFieldProperty extends FieldProperty implements AnnotatedProperty {
    private Field field;

    public AnnotatedFieldProperty(Field field) {
        super(field);
        this.field = field;
    }

    @Override
    public Property getPropertyAnnotation() {
        return field.getAnnotation(Property.class);
    }

    public Field getField() {
        return field;
    }

}
