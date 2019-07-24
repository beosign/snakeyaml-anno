package de.beosign.snakeyamlanno.convert;

/**
 * Converter that converts between a YAML node and its Java representation.
 * 
 * @author florian
 * @param <T> type of Java bean property
 */
public interface Converter<T> {
    /**
     * Returns a string from the given model value.
     * 
     * @param modelValue value form java bean property
     * @return string
     * @throws ConverterException if conversion failed
     */
    String convertToYaml(T modelValue);

    /**
     * Converts to the Java bean property.
     * 
     * @param value that is to be converted.
     * @return converted value
     * @throws ConverterException if conversion failed
     */
    T convertToModel(Object value);

    /**
     * <b>This interface is for internal purposes only.</b><br>
     * Default value for the converter property; needed because one cannot define a type token with wildcards ({@code Class<? extends Converter<?>>}) from a
     * generic type ({@code Converter<T>}).
     */
    interface NoConverter extends Converter<Object> {
    }
}
