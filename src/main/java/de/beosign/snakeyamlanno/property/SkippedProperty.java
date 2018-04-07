package de.beosign.snakeyamlanno.property;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import org.yaml.snakeyaml.introspector.Property;

/**
 * This special property type is used to indicate that a value must be skipped and <b>not</b> set into the model class, so a call to "set" does nothing.
 * 
 * @author florian
 */
public class SkippedProperty extends Property {
    private final de.beosign.snakeyamlanno.annotation.Property propertyAnnotation;

    /**
     * New instance.
     * 
     * @param name name of property
     * @param propertyAnnotation {@link de.beosign.snakeyamlanno.annotation.Property} annotation
     */
    public SkippedProperty(String name, de.beosign.snakeyamlanno.annotation.Property propertyAnnotation) {
        super(name, Object.class);
        this.propertyAnnotation = propertyAnnotation;
    }

    @Override
    public Class<?>[] getActualTypeArguments() {
        return new Class[0];
    }

    /**
     * Setter does nothing.
     */
    @Override
    public void set(Object object, Object value) throws Exception {
    }

    /**
     * Gettern returns null.
     */
    @Override
    public Object get(Object object) {
        return null;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return Collections.singletonList(propertyAnnotation);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        if (annotationType.isAssignableFrom(propertyAnnotation.getClass())) {
            return annotationType.cast(propertyAnnotation);
        }

        return null;
    }

}
