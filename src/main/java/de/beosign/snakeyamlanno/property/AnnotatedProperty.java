package de.beosign.snakeyamlanno.property;

import java.lang.annotation.Annotation;
import java.util.List;

import org.yaml.snakeyaml.introspector.Property;

/**
 * <p>
 * All annotated properties should have this class as base class.
 * </p>
 * <p>
 * The delegation seems to be unnecessary at first glance, but it has the advantage that we do not have decide whether we extend from FieldProperty or
 * MethodProperty. Or we could extend from GenericProperty, but then we would have to deal with the generic type detection on our own.
 * </p>
 * 
 * @author florian
 */
public class AnnotatedProperty extends Property {
    private Property targetProperty;

    public AnnotatedProperty(Property targetProperty) {
        this(targetProperty.getName(), targetProperty);
    }

    public AnnotatedProperty(String name, Property targetProperty) {
        this(name, targetProperty.getType(), targetProperty);
    }

    public AnnotatedProperty(String name, Class<?> type, Property targetProperty) {
        super(name, type);
        this.targetProperty = targetProperty;
    }

    public Property getTargetProperty() {
        return targetProperty;
    }

    @Override
    public Class<?>[] getActualTypeArguments() {
        return targetProperty.getActualTypeArguments();
    }

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

    /**
     * Overridden so this property is retrieved from the delegate instead of returning just <code>true</code>.
     */
    @Override
    public boolean isReadable() {
        return targetProperty.isWritable();
    }

    /**
     * Overridden so this property is retrieved from the delegate instead of returning just <code>true</code>.
     */
    @Override
    public boolean isWritable() {
        return targetProperty.isWritable();
    }

}
