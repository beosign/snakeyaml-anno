package de.beosign.snakeyamlanno.property;

// CHECKSTYLE:OFF
public abstract class Animal {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Dog extends Animal {
        private int loudness;
        private String aliasedProperty;

        public int getLoudness() {
            return loudness;
        }

        public void setLoudness(int loudness) {
            this.loudness = loudness;
        }

        @Override
        public String toString() {
            return "Dog [loudness=" + loudness + ", aliasedProperty=" + aliasedProperty + ", getName()=" + getName() + "]";
        }

        @YamlProperty(key = "alias")
        public String getAliasedProperty() {
            return aliasedProperty;
        }

        public void setAliasedProperty(String aliasedProperty) {
            this.aliasedProperty = aliasedProperty;
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

    @Override
    public String toString() {
        return "Animal [name=" + name + "]";
    }
}
