package de.beosign.snakeyamlanno.convert;

import java.util.Objects;

/**
 * Test converter.
 * 
 * @author florian
 */
public class LengthToCmConverter implements Converter<Integer> {
    @Override
    public String convertToYaml(Integer modelValue) {
        return modelValue + "cm";
    }

    @Override
    public Integer convertToModel(Object value) {
        String[] parts = Objects.toString(value, "").split("\\s");
        if (parts == null || parts.length == 1) {
            return Integer.parseInt(Objects.toString(value, "0"));
        }
        String unit = parts[1];
        String val = parts[0];

        if ("m".equals(unit)) {
            return (int) (Double.parseDouble(val) * 100.0);
        }

        return null;
    }
}
