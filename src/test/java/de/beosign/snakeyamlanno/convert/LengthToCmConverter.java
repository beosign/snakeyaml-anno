package de.beosign.snakeyamlanno.convert;

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
    public Integer convertToModel(String value) {
        String[] parts = value.split("\\s");
        if (parts == null || parts.length == 1) {
            return Integer.parseInt(value);
        }
        String unit = parts[1];
        String val = parts[0];

        if ("m".equals(unit)) {
            return (int) (Double.parseDouble(val) * 100.0);
        }

        return null;
    }
}
