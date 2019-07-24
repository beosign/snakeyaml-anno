package de.beosign.snakeyamlanno.convert;

import de.beosign.snakeyamlanno.property.YamlProperty;

//CHECKSTYLE:OFF
public class PersonWithConverter2 {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @YamlProperty(converter = ExceptionThrowingConverter.class)
    private Integer length;

    public static class ExceptionThrowingConverter implements Converter<Integer> {
        @Override
        public String convertToYaml(Integer modelValue) {
            return modelValue.getClass().getSimpleName();
        }

        @Override
        public Integer convertToModel(Object yamlValue) {
            throw new ConverterException("Cannot convert", new IllegalArgumentException("Cause"));
        }
    }

}
