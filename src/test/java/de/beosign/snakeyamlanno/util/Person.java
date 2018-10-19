package de.beosign.snakeyamlanno.util;

import java.util.ArrayList;
import java.util.List;

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

    public String getNotConstructable() {
        return notConstructable;
    }

    public void setNotConstructable(String notConstructable) {
        this.notConstructable = notConstructable;
    }

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

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

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

}
