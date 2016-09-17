package de.beosign.snakeyamlanno.convert;

import org.yaml.snakeyaml.nodes.Node;

/**
 * Class that is used as the default value in the Property annotation.
 * 
 * @author florian
 */
public class NoConverter implements Converter<Object> {

    @Override
    public String convertToYaml(Object modelValue) {
        throw new IllegalStateException("Must not be called!");
    }

    @Override
    public Object convertToModel(Node yamlNode) {
        throw new IllegalStateException("Must not be called!");
    }

}
