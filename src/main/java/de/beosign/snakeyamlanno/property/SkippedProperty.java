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
    private final Property originalProperty;

    /**
     * New instance.
     * 
     * @param originalProperty property that was discovered and is now used as a delegate for this property
     * @param propertyAnnotation {@link de.beosign.snakeyamlanno.annotation.Property} annotation
     */
    public SkippedProperty(Property originalProperty, de.beosign.snakeyamlanno.annotation.Property propertyAnnotation) {
        super(originalProperty.getName(), originalProperty.getType());
        this.originalProperty = originalProperty;
        this.propertyAnnotation = propertyAnnotation;
    }

    @Override
    public Class<?>[] getActualTypeArguments() {
        return originalProperty.getActualTypeArguments();
    }

    /**
     * Setter does nothing.
     */
    @Override
    public void set(Object object, Object value) throws Exception {
        if (!propertyAnnotation.skipAtLoad()) {
            originalProperty.set(object, value);
        }
    }

    /**
     * Gettern returns null.
     */
    @Override
    public Object get(Object object) {
        return originalProperty.get(object);
    }

    @Override
    public List<Annotation> getAnnotations() {
        return originalProperty.getAnnotations();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return originalProperty.getAnnotation(annotationType);
    }

}
