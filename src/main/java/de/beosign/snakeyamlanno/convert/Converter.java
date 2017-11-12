package de.beosign.snakeyamlanno.convert;

import org.yaml.snakeyaml.nodes.Node;

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
     * @param yamlNode the node that is to be converted.
     * @return converted value
     * @throws ConverterException if conversion failed
     */
    T convertToModel(Node yamlNode);
}
