package de.beosign.snakeyamlanno.convert;

import org.yaml.snakeyaml.nodes.Node;

public interface Converter<T> {
    String convertToYaml(T modelValue);

    T convertToModel(Node yamlNode);
}
