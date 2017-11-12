package de.beosign.snakeyamlanno.convert;

import org.yaml.snakeyaml.nodes.Node;

import de.beosign.snakeyamlanno.annotation.Property;

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

    @Property(converter = ExceptionThrowingConverter.class)
    private Integer length;

    public static class ExceptionThrowingConverter implements Converter<Integer> {
        @Override
        public String convertToYaml(Integer modelValue) {
            return modelValue.getClass().getSimpleName();
        }

        @Override
        public Integer convertToModel(Node yamlValue) {
            throw new ConverterException("Cannot convert", new IllegalArgumentException("Cause"));
        }
    }

}
