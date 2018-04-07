package de.beosign.snakeyamlanno.property;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

import de.beosign.snakeyamlanno.convert.Converter;

/**
 * This special property type is used to indicate that a value must be converted when being set into the model class.
 * 
 * @author florian
 */
public class ConvertedProperty extends Property {
    private final Converter<?> converter;
    private Property originalProperty;

    /**
     * New instance.
     * 
     * @param originalProperty property that was discovered and is now used as a delegate for this property
     * @param converterClass converter class
     * @throws IllegalAccessException if converter class cannot be accessed
     * @throws InstantiationException if converter class cannot be instantiated
     */
    public ConvertedProperty(Property originalProperty, Class<? extends Converter<?>> converterClass) throws InstantiationException, IllegalAccessException {
        super(originalProperty.getName(), Object.class);
        this.converter = converterClass.newInstance();
        this.originalProperty = originalProperty;
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
        Object convertedValue = converter.convertToModel(new ScalarNode(Tag.STR, Objects.toString(value), null, null, null));
        originalProperty.set(object, convertedValue);
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
