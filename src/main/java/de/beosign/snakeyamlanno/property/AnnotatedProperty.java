package de.beosign.snakeyamlanno.property;

import java.lang.annotation.Annotation;
import java.util.List;

import org.yaml.snakeyaml.introspector.Property;

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

    public de.beosign.snakeyamlanno.annotation.Property getPropertyAnnotation() {
        return getAnnotation(de.beosign.snakeyamlanno.annotation.Property.class);
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

}
