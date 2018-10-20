package de.beosign.snakeyamlanno.constructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import de.beosign.snakeyamlanno.convert.Converter;
import de.beosign.snakeyamlanno.convert.ConverterException;
import de.beosign.snakeyamlanno.util.NodeUtil;

// CHECKSTYLE:OFF - test classes
public class Person {
    private String name;
    private Skill skill;
    private List<Color> favoriteColors = new ArrayList<>();
    private List<Animal> pets = new ArrayList<>();
    private Animal firstPet;
    private Animal secondPet;
    private Class<?> type;
    private String notConstructable;
    private Integer notSettable;

    public Person() {
    }

    @ConstructBy(AbstractCustomConstructor.class)
    public String getNotConstructable() {
        return notConstructable;
    }

    public void setNotConstructable(String notConstructable) {
        this.notConstructable = notConstructable;
    }

    @ConstructBy(WrongTypeCustomConstructor.class)
    public Integer getNotSettable() {
        return notSettable;
    }

    public void setNotSettable(Integer notSettable) {
        this.notSettable = notSettable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    @ConstructBy(ClassConstructor.class)
    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    @ConstructBy(DogByYearConstructor.class)
    public Animal getFirstPet() {
        return firstPet;
    }

    public void setFirstPet(Animal firstPet) {
        this.firstPet = firstPet;
    }

    public Animal getSecondPet() {
        return secondPet;
    }

    public void setSecondPet(Animal secondPet) {
        this.secondPet = secondPet;
    }

    public List<Animal> getPets() {
        return pets;
    }

    public void setPets(List<Animal> pets) {
        this.pets = pets;
    }

    public List<Color> getFavoriteColors() {
        return favoriteColors;
    }

    public void setFavoriteColors(List<Color> favoriteColors) {
        this.favoriteColors = favoriteColors;
    }

    @Override
    public String toString() {
        return "Person [name=" + name + ", skill=" + skill + ", favoriteColors=" + favoriteColors + ", pets=" + pets + ", firstPet=" + firstPet + ", type=" + type + "]";
    }

    @ConstructBy(SkillConstructor.class)
    public static enum Skill {
        PRO(1), AMATEUR(2), BEGINNER(3);

        private final int level;

        Skill(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    public static enum Color {
        RED, BLUE, GREEN, YELLOW;
    }

    @ConstructBy(AnimalConstructor.class)
    public abstract static class Animal {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @ConstructBy(DogConstructor.class)
    public static class Dog extends Animal {
        private int nrBarksPerDay;

        public Dog() {
        }

        public Dog(String str) {

        }

        public int getNrBarksPerDay() {
            return nrBarksPerDay;
        }

        public void setNrBarksPerDay(int nrBarksPerDay) {
            this.nrBarksPerDay = nrBarksPerDay;
        }

        /**
         * Overridden => disables converter.
         */
        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public String toString() {
            return "Dog [nrBarksPerDay=" + nrBarksPerDay + ", getName()=" + getName() + ", getAge()=" + getAge() + "]";
        }

    }

    @ConstructBy(DefaultCustomConstructor.class)
    public static class Cat extends Animal {
        private int nrMiceCaught;

        public int getNrMiceCaught() {
            return nrMiceCaught;
        }

        public void setNrMiceCaught(int nrMiceCaught) {
            this.nrMiceCaught = nrMiceCaught;
        }

        @Override
        public String toString() {
            return "Cat [nrMiceCaught=" + nrMiceCaught + ", getName()=" + getName() + ", getAge()=" + getAge() + "]";
        }

    }

    public static class ReverseStringConverter implements Converter<String> {

        @Override
        public String convertToYaml(String modelValue) {
            return StringUtils.reverse(modelValue);
        }

        @Override
        public String convertToModel(String value) {
            return StringUtils.reverse(value);
        }

    }

    public static class CharCountIntegerConverter implements Converter<Integer> {

        @Override
        public String convertToYaml(Integer modelValue) {
            return StringUtils.repeat("+", modelValue);
        }

        @Override
        public Integer convertToModel(String value) {
            if (value == null)
                return 0;

            return value.length();
        }

    }

    public static class WrongTypeCustomConstructor implements CustomConstructor<String> {
        @Override
        public String construct(Node node, Function<? super Node, ? extends String> defaultConstructor) throws YAMLException {
            return "a string";
        }

    }

    public static class SkillConstructor implements CustomConstructor<Skill> {

        @Override
        public Skill construct(Node node, Function<? super Node, ? extends Skill> defaultConstructor) throws ConstructorException {
            String val = (String) NodeUtil.getValue(node);

            if (StringUtils.isNumeric(val)) {
                for (Skill skill : Skill.values()) {
                    if (skill.getLevel() == Integer.parseInt(val)) {
                        return skill;
                    }
                }
            } else {
                for (Skill skill : Skill.values()) {
                    if (skill.name().equalsIgnoreCase(val)) {
                        return skill;
                    }
                }
            }
            throw new ConverterException("Cannot find Skill enum value for " + val);
        }

    }

    public abstract static class AbstractCustomConstructor implements CustomConstructor<Object> {
    }

    public static class AnimalConstructor implements CustomConstructor<Animal> {

        @Override
        public Animal construct(Node node, Function<? super Node, ? extends Animal> defaultConstructor) throws YAMLException {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public static class DogConstructor implements CustomConstructor<Dog> {
        @Override
        public Dog construct(Node node, Function<? super Node, ? extends Dog> defaultConstructor) throws ConstructorException {
            if (node instanceof MappingNode) {
                MappingNode mappingNode = (MappingNode) node;

                Integer age = null;
                for (NodeTuple nt : mappingNode.getValue()) {
                    ScalarNode sn = (ScalarNode) nt.getKeyNode();
                    if (sn.getValue().equals("years")) {
                        age = Integer.valueOf(((ScalarNode) nt.getValueNode()).getValue());
                        break;
                    }
                }
                mappingNode.getValue().removeIf(nt -> ((ScalarNode) nt.getKeyNode()).getValue().equals("years"));
                Dog dog = defaultConstructor.apply(mappingNode);
                if (age != null) {
                    dog.setAge(age);
                }
                return dog;
            } else if (node instanceof ScalarNode) {
                String value = (String) NodeUtil.getValue(node);
                String[] parts = value.split("~");
                Dog dog = new Dog();
                dog.setName(parts[0]);
                dog.setAge(Integer.parseInt(parts[1]));
                return dog;
            } else {
                return defaultConstructor.apply(node);
            }
        }
    }

    public static class DogByYearConstructor implements CustomConstructor<Dog> {

        @Override
        public Dog construct(Node node, Function<? super Node, ? extends Dog> defaultConstructor) throws ConstructorException {
            if (node instanceof MappingNode) {
                MappingNode mappingNode = (MappingNode) node;

                Integer age = null;
                for (NodeTuple nt : mappingNode.getValue()) {
                    ScalarNode sn = (ScalarNode) nt.getKeyNode();
                    if (sn.getValue().equals("dogYears")) {
                        age = Integer.parseInt(((ScalarNode) nt.getValueNode()).getValue()) / 7;
                        break;
                    }
                }
                mappingNode.getValue().removeIf(nt -> ((ScalarNode) nt.getKeyNode()).getValue().equals("dogYears"));
                Dog dog = defaultConstructor.apply(mappingNode);
                if (age != null) {
                    dog.setAge(age);
                }
                return dog;
            } else {
                return defaultConstructor.apply(node);
            }
        }

    }

    public static class ClassConstructor implements CustomConstructor<Class<?>> {

        @Override
        public Class<?> construct(Node node, Function<? super Node, ? extends Class<?>> defaultConstructor) throws YAMLException {
            if (node instanceof ScalarNode) {
                String className = (String) NodeUtil.getValue(node);
                try {
                    return Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new YAMLException(e);
                }
            } else {
                return defaultConstructor.apply(node);
            }
        }

    }

}
