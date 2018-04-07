package de.beosign.snakeyamlanno.property;

import java.lang.annotation.Annotation;
import java.util.List;

import org.yaml.snakeyaml.introspector.Property;

/**
 * This special property type is used to indicate that a property is being accessed by a different name.
 * 
 * @author florian
 */
public class AliasedProperty extends Property {
    private Property targetProperty;

    /**
     * New instance.
     * 
     * @param targetProperty property that was discovered and is now registered by an alias
     * @param alias the alias under which the property is now accessible
     */
    public AliasedProperty(Property targetProperty, String alias) {
        super(alias, Object.class);
        this.targetProperty = targetProperty;
    }

    @Override
    public Class<?>[] getActualTypeArguments() {
        return targetProperty.getActualTypeArguments();
    }

    /**
     * Setter does nothing.
     */
    @Override
    public void set(Object object, Object value) throws Exception {
        targetProperty.set(object, value);
    }

    @Override
    public Object get(Object object) {
        return targetProperty.get(object);
    }

    @Override
    public List<Annotation> getAnnotations() {
        return targetProperty.getAnnotations();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return targetProperty.getAnnotation(annotationType);
    }

}
