package de.beosign.snakeyamlanno.type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.ConstructorException;

import de.beosign.snakeyamlanno.annotation.Type;
import de.beosign.snakeyamlanno.annotation.TypeImpl;
import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;
import de.beosign.snakeyamlanno.property.Company;
import de.beosign.snakeyamlanno.property.Person;
import de.beosign.snakeyamlanno.property.Person.Gender;
import de.beosign.snakeyamlanno.type.Animal.Dog;
import de.beosign.snakeyamlanno.type.WorkingPerson.Employee;
import de.beosign.snakeyamlanno.type.WorkingPerson.Employer;

/**
 * Tests the ignore errors functionality.
 * 
 * @author florian
 */
public class TypeDetectionTest {
    private static final Logger log = LoggerFactory.getLogger(TypeDetectionTest.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Tests that the type detection works if there is only a single possibility.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionSingleResult() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("typeDetection1.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Person.class);
            Yaml yaml = new Yaml(constructor);

            Person parseResult = yaml.loadAs(yamlString, Person.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.getName(), is("Homer"));
            assertThat(parseResult.getGender(), is(Gender.MALE));
            Assert.assertTrue(parseResult.getAnimal() instanceof Dog);
            assertThat(((Dog) parseResult.getAnimal()).getLoudness(), is(5));

            assertThat(new TypeImpl(null).annotationType().getTypeName(), Is.is(Type.class.getName()));
        }
    }

    /**
     * Tests that the type detection works if there is an alias defined on a subtype.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionSingleResultWithMapping() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("typeDetection2.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Person.class);
            Yaml yaml = new Yaml(constructor);

            Person parseResult = yaml.loadAs(yamlString, Person.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.getName(), is("Homer"));
            assertThat(parseResult.getGender(), is(Gender.MALE));
            Assert.assertTrue(parseResult.getAnimal() instanceof Dog);
            assertThat(((Dog) parseResult.getAnimal()).getLoudness(), is(5));
            assertThat(((Dog) parseResult.getAnimal()).getAliasedProperty(), is("aliased"));
        }
    }

    /**
     * Tests that the type detection works if there are more than valid subtypes - the first type must be chosen.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionMultipleSubclassesFirstChosen() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("typeDetection3.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(WorkingPerson.class);
            Yaml yaml = new Yaml(constructor);

            Person parseResult = yaml.loadAs(yamlString, WorkingPerson.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            Assert.assertTrue(parseResult.getClass().equals(Employee.class));
            assertThat(parseResult.getName(), is("Homer"));
            assertThat(parseResult.getGender(), is(Gender.MALE));

        }
    }

    /**
     * Tests that the type detection works if there are more than one valid subtypes and a type selector is given.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionMultipleSubclassesTypeSelector() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("typeDetection3.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(WorkingPerson2.class);
            Yaml yaml = new Yaml(constructor);

            Person parseResult = yaml.loadAs(yamlString, WorkingPerson2.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            Assert.assertTrue(parseResult.getClass().equals(WorkingPerson2.Employer.class));
            assertThat(parseResult.getName(), is("Homer"));
            assertThat(parseResult.getGender(), is(Gender.MALE));

        }
    }

    /**
     * Tests that the type detection works if there are different types in a list, e.g. the list contains Employee, Employer,...
     * 
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionMultipleSubclasses() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("typeDetection4.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Company.class);
            Yaml yaml = new Yaml(constructor);

            Company parseResult = yaml.loadAs(yamlString, Company.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.getWorkingPersons().size(), is(2));
            assertThat(parseResult.getWorkingPersons().get(0), IsInstanceOf.instanceOf(Employee.class));
            assertThat(parseResult.getWorkingPersons().get(1), IsInstanceOf.instanceOf(Employer.class));

            Employee homer = (Employee) parseResult.getWorkingPersons().get(0);
            assertThat(homer.getName(), is("Homer"));
            assertThat(homer.getGender(), is(Gender.MALE));
            assertThat(homer.getSalary(), is(1000));

            Employer monty = (Employer) parseResult.getWorkingPersons().get(1);
            assertThat(monty.getName(), is("Monty"));
            assertThat(monty.getGender(), is(Gender.MALE));
            assertThat(monty.getNrEmployees(), is(100));

        }
    }

    /**
     * Tests that the type detection works if there are different types in a list, e.g. the list contains Employee, Employer,...
     * 
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionDisableDefault() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("typeDetection5.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(WorkingPerson3.class);
            Yaml yaml = new Yaml(constructor);

            Person parseResult = yaml.loadAs(yamlString, WorkingPerson3.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            Assert.assertTrue(parseResult.getClass().equals(WorkingPerson3.Employee.class));
            assertThat(parseResult.getName(), is("Homer"));
            assertThat(parseResult.getGender(), is(Gender.MALE));

        }
    }

    /**
     * Tests that the type detection works if there are different types in a list, e.g. the list contains Employee, Employer,...
     * 
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionNoSubstitutionTypesFound() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("typeDetection6.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(WorkingPerson4.class);
            Yaml yaml = new Yaml(constructor);

            thrown.expect(ConstructorException.class);
            yaml.loadAs(yamlString, WorkingPerson4.class);

        }
    }

    /**
     * Tests that the type detection works if there are different types in a list, e.g. the list contains Employee, Employer,...
     * 
     * @throws Exception on any exception
     */
    @Test
    public void typeDetectionSubstitutionTypeNotInstantiable() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("typeDetection7.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(WorkingPerson5.class);
            Yaml yaml = new Yaml(constructor);

            thrown.expect(ConstructorException.class);
            thrown.expectMessage("Cannot instantiate substitutionTypeSelector of type");
            yaml.loadAs(yamlString, WorkingPerson5.class);

        }
    }

}
