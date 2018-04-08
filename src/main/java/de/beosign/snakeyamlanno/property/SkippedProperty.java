package de.beosign.snakeyamlanno.property;

import java.lang.annotation.Annotation;
import java.util.List;

import org.yaml.snakeyaml.introspector.Property;

/**
 * This special property type is used to indicate that a value must be skipped and <b>not</b> set into the model class, so a call to "set" does nothing.
 * 
 * @author florian
 */
public class SkippedProperty extends Property {
    private final de.beosign.snakeyamlanno.annotation.Property propertyAnnotation;
    private final Property defaultProperty;

    /**
     * New instance.
     * 
     * @param defaultProperty property that would have been used normally
     * @param propertyAnnotation {@link de.beosign.snakeyamlanno.annotation.Property} annotation
     */
    public SkippedProperty(Property defaultProperty, de.beosign.snakeyamlanno.annotation.Property propertyAnnotation) {
        super(defaultProperty.getName(), defaultProperty.getType());
        this.defaultProperty = defaultProperty;
        this.propertyAnnotation = propertyAnnotation;
    }

    @Override
    public Class<?>[] getActualTypeArguments() {
        return defaultProperty.getActualTypeArguments();
    }

    /**
     * Setter does nothing.
     */
    @Override
    public void set(Object object, Object value) throws Exception {
        if (!propertyAnnotation.skipAtLoad()) {
            defaultProperty.set(object, value);
        }
    }

    /**
     * Gettern returns null.
     */
    @Override
    public Object get(Object object) {
        return defaultProperty.get(object);
    }

    @Override
    public List<Annotation> getAnnotations() {
        return defaultProperty.getAnnotations();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return defaultProperty.getAnnotation(annotationType);
    }

}
