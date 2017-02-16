package de.beosign.snakeyamlanno.convert;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class LengthToCmConverter implements Converter<Integer> {
    @Override
    public String convertToYaml(Integer modelValue) {
        return modelValue.getClass().getSimpleName();
    }

    @Override
    public Integer convertToModel(Node yamlValue) {
        if (yamlValue instanceof ScalarNode) {
            ScalarNode node = (ScalarNode) yamlValue;
            String[] parts = node.getValue().split("\\s");
            if (parts == null || parts.length == 1) {
                return Integer.parseInt(node.getValue());
            }
            String unit = parts[1];
            String value = parts[0];

            if ("m".equals(unit)) {
                return (int) (Double.parseDouble(value) * 100.0);
            }

        }
        return null;
    }
}
