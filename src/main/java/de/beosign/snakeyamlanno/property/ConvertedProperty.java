package de.beosign.snakeyamlanno.property;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

import org.yaml.snakeyaml.introspector.Property;

import de.beosign.snakeyamlanno.convert.Converter;

/**
 * This special property type is used to indicate that a value must be converted when being set into the model class.
 * 
 * @author florian
 */
public class ConvertedProperty extends Property {
    private final Converter<Object> converter;
    private final Property originalProperty;

    /**
     * New instance.
     * 
     * @param originalProperty property that was discovered and is now used as a delegate for this property
     * @param converterClass converter class
     * @throws IllegalAccessException if converter class cannot be accessed
     * @throws InstantiationException if converter class cannot be instantiated
     */
    @SuppressWarnings("unchecked")
    public ConvertedProperty(Property originalProperty, Class<? extends Converter<?>> converterClass) throws InstantiationException, IllegalAccessException {
        super(originalProperty.getName(), Object.class);
        this.converter = (Converter<Object>) converterClass.newInstance();
        this.originalProperty = originalProperty;
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
        Object convertedValue = converter.convertToModel(Objects.toString(value));
        originalProperty.set(object, convertedValue);
    }

    @Override
    public Object get(Object object) {
        Object value = originalProperty.get(object);
        String convertedValue = converter.convertToYaml(value);
        return convertedValue;
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
