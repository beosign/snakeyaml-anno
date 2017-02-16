package de.beosign.snakeyamlanno.convert;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import de.beosign.snakeyamlanno.property.Person.Gender;

public class GenderConverter implements Converter<Gender> {

    @Override
    public String convertToYaml(Gender modelValue) {
        return modelValue.getAbbr();
    }

    @Override
    public Gender convertToModel(Node yamlNode) {
        if (yamlNode instanceof ScalarNode) {

            for (Gender g : Gender.values()) {
                if (g.getAbbr().equals(((ScalarNode) yamlNode).getValue())) {
                    return g;
                }
            }
        }
        return null;
    }
}
