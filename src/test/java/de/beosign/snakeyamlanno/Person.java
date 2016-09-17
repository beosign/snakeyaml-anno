package de.beosign.snakeyamlanno;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import de.beosign.snakeyamlanno.annotation.Property;
import de.beosign.snakeyamlanno.convert.Converter;

public class Person {
    private String name;
    private int height; // cm
    private Gender gender;
    private Animal animal;

    public enum Gender {
        MALE("m"),
        FEMALE("f");

        private String abbr;

        private Gender(String abbr) {
            this.abbr = abbr;
        }

        public String getAbbr() {
            return abbr;
        }
    }

    public static class GenderConverter implements Converter<Gender> {

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

    public static class LengthToCmConverter implements Converter<Integer> {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Property(converter = GenderConverter.class)
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Person [name=" + name + ", height=" + height + ", gender=" + gender + ", animal=" + animal + "]";
    }

    public static abstract class Animal {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Animal [name=" + name + "]";
        }
    }

    public static class Dog extends Animal {
        private int loudness;

        public int getLoudness() {
            return loudness;
        }

        public void setLoudness(int loudness) {
            this.loudness = loudness;
        }

        @Override
        public String toString() {
            return "Dog [loudness=" + loudness + ", getName()=" + getName() + "]";
        }

    }

    public static class Cat extends Animal {
        private int length;

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        @Override
        public String toString() {
            return "Cat [length=" + length + ", getName()=" + getName() + "]";
        }

    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    @Property(converter = LengthToCmConverter.class)
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
