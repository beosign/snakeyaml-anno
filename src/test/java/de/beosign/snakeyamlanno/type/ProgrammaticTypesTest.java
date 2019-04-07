package de.beosign.snakeyamlanno.type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;

import de.beosign.snakeyamlanno.annotation.Property;
import de.beosign.snakeyamlanno.annotation.Type;
import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;
import de.beosign.snakeyamlanno.type.Animal.Cat;
import de.beosign.snakeyamlanno.type.ProgrammaticTypesTest.Animal.Dog;
import de.beosign.snakeyamlanno.type.ProgrammaticTypesTest.AnnotatedAnimal.AnnotatedCat;
import de.beosign.snakeyamlanno.type.ProgrammaticTypesTest.AnnotatedAnimal.AnnotatedDog;
import de.beosign.snakeyamlanno.type.ProgrammaticTypesTest.WorkingPerson.Employee;
import de.beosign.snakeyamlanno.type.ProgrammaticTypesTest.WorkingPerson.Employer;

/**
 * Tests that substitution types can also be defined in a programmatic way.
 * 
 * @author florian
 */
public class ProgrammaticTypesTest {
    private static final Logger log = LoggerFactory.getLogger(ProgrammaticTypesTest.class);

    /**
     * Tests that the type detection works if there is only a single possibility.
     *
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionSingleResult() throws Exception {
        String yamlString = "!!de.beosign.snakeyamlanno.type.ProgrammaticTypesTest.Person\n";
        yamlString += "name: Homer\n";
        yamlString += "animal:\n";
        yamlString += " loudness: 5";

        log.debug("YAML:\n{}", yamlString);

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Person.class);
        constructor.registerSubstitutionTypes(Animal.class, Dog.class, Cat.class);
        Yaml yaml = new Yaml(constructor);

        Person parseResult = yaml.loadAs(yamlString, Person.class);
        log.debug("Parsed YAML file:\n{}", parseResult);

        assertThat(parseResult, notNullValue());
        assertThat(parseResult.getName(), is("Homer"));
        assertTrue(parseResult.getAnimal() instanceof Dog);
        assertThat(((Dog) parseResult.getAnimal()).getLoudness(), is(5));
    }

    /**
     * Tests that the type detection works if there is an alias defined on a subtype.
     *
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionSingleResultWithMapping() throws Exception {
        String yamlString = "!!de.beosign.snakeyamlanno.type.ProgrammaticTypesTest.Person\n";
        yamlString += "name: Homer\n";
        yamlString += "animal:\n";
        yamlString += " loudness: 5\n";
        yamlString += " alias: aliased";

        log.debug("Loaded YAML file:\n{}", yamlString);

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Person.class);
        Yaml yaml = new Yaml(constructor);
        constructor.registerSubstitutionTypes(Animal.class, Dog.class, Cat.class);
        constructor.registerSubstitutionTypes(WorkingPerson.class, WorkingPerson.Employee.class, WorkingPerson.Employer.class);

        Person parseResult = yaml.loadAs(yamlString, Person.class);
        log.debug("Parsed YAML file:\n{}", parseResult);

        assertThat(parseResult, notNullValue());
        assertThat(parseResult.getName(), is("Homer"));
        assertTrue(parseResult.getAnimal() instanceof Dog);
        assertThat(((Dog) parseResult.getAnimal()).getLoudness(), is(5));
        assertThat(((Dog) parseResult.getAnimal()).getAliasedProperty(), is("aliased"));
    }

    /**
     * Tests that the type detection works if there are more than valid subtypes - the first type must be chosen.
     *
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionMultipleSubclassesFirstChosen() throws Exception {
        String yamlString = "!!de.beosign.snakeyamlanno.type.ProgrammaticTypesTest.WorkingPerson\n";
        yamlString += "name: Homer\n";
        yamlString += "animal:\n";
        yamlString += " loudness: 5\n";
        yamlString += " alias: aliased";

        log.debug("Loaded YAML file:\n{}", yamlString);

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(WorkingPerson.class);
        constructor.registerSubstitutionTypes(Animal.class, Dog.class, Cat.class);
        constructor.registerSubstitutionTypes(WorkingPerson.class, WorkingPerson.Employee.class, WorkingPerson.Employer.class);
        Yaml yaml = new Yaml(constructor);

        Person parseResult = yaml.loadAs(yamlString, WorkingPerson.class);
        log.debug("Parsed YAML file:\n{}", parseResult);

        assertThat(parseResult, notNullValue());
        assertTrue(parseResult.getClass().equals(Employee.class));
        assertThat(parseResult.getName(), is("Homer"));
    }

    /**
     * Tests that the type detection works if there are more than one valid subtypes and a type selector is given.
     *
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionMultipleSubclassesTypeSelector() throws Exception {
        String yamlString = "name: Homer\n";
        yamlString += "animal:\n";
        yamlString += " loudness: 5\n";
        yamlString += " alias: aliased";
        log.debug("Loaded YAML file:\n{}", yamlString);

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(WorkingPerson2.class);
        Yaml yaml = new Yaml(constructor);
        constructor.registerSubstitutionTypes(Animal.class, Dog.class, Cat.class);
        constructor.registerType(WorkingPerson2.class,
                Type.Factory.of(null, WorkingPerson2.WorkingPerson2TypeSelector.class, WorkingPerson2.Employee.class, WorkingPerson2.Employer.class));

        Person parseResult = yaml.loadAs(yamlString, WorkingPerson2.class);
        log.debug("Parsed YAML file:\n{}", parseResult);

        assertThat(parseResult, notNullValue());
        assertTrue(parseResult.getClass().equals(WorkingPerson2.Employer.class));
        assertThat(parseResult.getName(), is("Homer"));

    }

    /**
     * Tests that the type detection works if there are different types in a list, e.g. the list contains Employee, Employer,...
     *
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionMultipleSubclasses() throws Exception {
        String yamlString = "!!de.beosign.snakeyamlanno.type.ProgrammaticTypesTest.Company\n";
        yamlString += "workingPersons:\n";
        yamlString += "- name: Homer\n";
        yamlString += "  salary: 1000\n";
        yamlString += "- name: Monty\n";
        yamlString += "  nrEmployees: 100\n";
        log.debug("Loaded YAML file:\n{}", yamlString);

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Company.class);
        Yaml yaml = new Yaml(constructor);
        constructor.registerSubstitutionTypes(WorkingPerson.class, WorkingPerson.Employee.class, WorkingPerson.Employer.class);

        Company parseResult = yaml.loadAs(yamlString, Company.class);
        log.debug("Parsed YAML file:\n{}", parseResult);

        assertThat(parseResult, notNullValue());
        assertThat(parseResult.getWorkingPersons().size(), is(2));
        assertThat(parseResult.getWorkingPersons().get(0), IsInstanceOf.instanceOf(Employee.class));
        assertThat(parseResult.getWorkingPersons().get(1), IsInstanceOf.instanceOf(Employer.class));

        Employee homer = (Employee) parseResult.getWorkingPersons().get(0);
        assertThat(homer.getName(), is("Homer"));
        assertThat(homer.getSalary(), is(1000));

        Employer monty = (Employer) parseResult.getWorkingPersons().get(1);
        assertThat(monty.getName(), is("Monty"));
        assertThat(monty.getNrEmployees(), is(100));

    }

    /**
     * Tests that the an annotated type definition can be overridden programmatically.
     *
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionAnnotationOverride() throws Exception {
        String yamlString = "!!de.beosign.snakeyamlanno.type.ProgrammaticTypesTest.AnnotatedAnimalsHolder\n";
        yamlString += "animals:\n";
        yamlString += "- loudness: 5\n";
        yamlString += "  alias: aliased\n";
        yamlString += "- length: 5\n";

        log.debug("Loaded YAML file:\n{}", yamlString);

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(AnnotatedAnimal.class);

        // override given annotation
        constructor.unregisterTypes();
        constructor.registerSubstitutionTypes(AnnotatedAnimal.class, AnnotatedDog.class, AnnotatedCat.class);

        Yaml yaml = new Yaml(constructor);

        AnnotatedAnimalsHolder parseResult = yaml.loadAs(yamlString, AnnotatedAnimalsHolder.class);
        log.debug("Parsed YAML file:\n{}", parseResult);

        assertThat(parseResult, notNullValue());

        assertThat(parseResult.getAnimals().get(0), IsInstanceOf.instanceOf(AnnotatedDog.class));
        AnnotatedDog dog = (AnnotatedDog) parseResult.getAnimals().get(0);
        assertThat(dog.getAliasedProperty(), is("aliased"));
        assertThat(dog.getLoudness(), is(5));

        assertThat(parseResult.getAnimals().get(1), IsInstanceOf.instanceOf(AnnotatedCat.class));
        AnnotatedCat cat = (AnnotatedCat) parseResult.getAnimals().get(1);
        assertThat(cat.getLength(), is(5));

    }

    // CHECKSTYLE:OFF
    public static class Person {
        private String name;
        private Animal animal;
        private int height; // cm

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Animal getAnimal() {
            return animal;
        }

        public void setAnimal(Animal animal) {
            this.animal = animal;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

    }

    public static abstract class Animal {
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

            @Property(key = "alias")
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

    public static class WorkingPerson extends Person {

        public static class Employee extends WorkingPerson {
            private int salary;

            public int getSalary() {
                return salary;
            }

            public void setSalary(int salary) {
                this.salary = salary;
            }

            @Override
            public String toString() {
                return "Employee [salary=" + salary + ", getName()=" + getName() + "]";
            }

        }

        public static class Employer extends WorkingPerson {
            private int nrEmployees;

            public int getNrEmployees() {
                return nrEmployees;
            }

            public void setNrEmployees(int nrEmployees) {
                this.nrEmployees = nrEmployees;
            }

            @Override
            public String toString() {
                return "Employer [nrEmployees=" + nrEmployees + ", getName()=" + getName() + ", getAnimal()=" + getAnimal()
                        + ", getHeight()=" + getHeight() + "]";
            }

        }
    }

    public static class WorkingPerson2 extends Person {

        public static class WorkingPerson2TypeSelector implements SubstitutionTypeSelector {
            private List<? extends Class<?>> possibleTypes;

            @Override
            public Class<?> getSelectedType(MappingNode node, List<? extends Class<?>> possibleTypes) {
                // store for unit tests
                this.possibleTypes = possibleTypes;
                return possibleTypes.get(1);
            }

            public List<? extends Class<?>> getPossibleTypes() {
                return possibleTypes;
            }
        }

        public static class Employee extends WorkingPerson2 {
            private int salary;

            public int getSalary() {
                return salary;
            }

            public void setSalary(int salary) {
                this.salary = salary;
            }

            @Override
            public String toString() {
                return "Employee [salary=" + salary + ", getName()=" + getName() + "]";
            }

        }

        public static class Employer extends WorkingPerson2 {
            private int nrEmployees;

            public int getNrEmployees() {
                return nrEmployees;
            }

            public void setNrEmployees(int nrEmployees) {
                this.nrEmployees = nrEmployees;
            }

            @Override
            public String toString() {
                return "Employer [nrEmployees=" + nrEmployees + ", getName()=" + getName() + ", getAnimal()=" + getAnimal()
                        + ", getHeight()=" + getHeight() + "]";
            }

        }
    }

    public static class Company {
        private List<WorkingPerson> workingPersons;

        public List<WorkingPerson> getWorkingPersons() {
            return workingPersons;
        }

        public void setWorkingPersons(List<WorkingPerson> workingPersons) {
            this.workingPersons = workingPersons;
        }

        @Override
        public String toString() {
            return "Company [workingPersons=" + workingPersons + "]";
        }

    }

    public static class AnnotatedAnimalsHolder {
        private List<AnnotatedAnimal> animals;

        public List<AnnotatedAnimal> getAnimals() {
            return animals;
        }

        public void setAnimals(List<AnnotatedAnimal> animals) {
            this.animals = animals;
        }
    }

    @Type(substitutionTypes = { Animal.Cat.class, Animal.Dog.class })
    public static abstract class AnnotatedAnimal {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static class AnnotatedDog extends AnnotatedAnimal {
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

            @Property(key = "alias")
            public String getAliasedProperty() {
                return aliasedProperty;
            }

            public void setAliasedProperty(String aliasedProperty) {
                this.aliasedProperty = aliasedProperty;
            }

        }

        public static class AnnotatedCat extends AnnotatedAnimal {
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
}
