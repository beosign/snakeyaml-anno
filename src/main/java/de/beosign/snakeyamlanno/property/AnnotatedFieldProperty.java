package de.beosign.snakeyamlanno.property;

import java.lang.reflect.Field;

import org.yaml.snakeyaml.introspector.FieldProperty;

import de.beosign.snakeyamlanno.annotation.Property;

/**
 * Represents a field that has a {@link Property} annotation.
 * 
 * @author florian
 */
public class AnnotatedFieldProperty extends FieldProperty implements AnnotatedProperty {
    private Field field;

    /**
     * Constructor.
     * 
     * @param field field
     */
    public AnnotatedFieldProperty(Field field) {
        super(field);
        this.field = field;
    }

    @Override
    public Property getPropertyAnnotation() {
        return field.getAnnotation(Property.class);
    }

    /**
     * The annotated field.
     * 
     * @return annotated field
     */
    public Field getField() {
        return field;
    }

}
