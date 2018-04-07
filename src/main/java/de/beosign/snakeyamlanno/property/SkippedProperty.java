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
    /**
     * New instance.
     * 
     * @param name name of property
     */
    public SkippedProperty(String name) {
        super(name, Object.class);
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

    @Override
    public Object get(Object object) {
        return object;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return Collections.emptyList();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null;
    }

}
