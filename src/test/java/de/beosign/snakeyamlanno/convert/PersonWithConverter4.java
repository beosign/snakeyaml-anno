package de.beosign.snakeyamlanno.convert;

import de.beosign.snakeyamlanno.annotation.Property;

//CHECKSTYLE:OFF
public class PersonWithConverter4 {
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

    @Property(converter = NotInstantiableConverter.class)
    private Integer length;

    public static class NotInstantiableConverter implements Converter<Integer> {
        private NotInstantiableConverter() {
        }

        @Override
        public String convertToYaml(Integer modelValue) {
            return "";
        }

        @Override
        public Integer convertToModel(Object yamlValue) {
            return 0;
        }
    }
}
