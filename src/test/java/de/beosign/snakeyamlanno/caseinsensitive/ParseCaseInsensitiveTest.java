package de.beosign.snakeyamlanno.caseinsensitive;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Objects;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;
import de.beosign.snakeyamlanno.property.YamlProperty;

/**
 * Tests the ignore errors functionality.
 * 
 * @author florian
 */
public class ParseCaseInsensitiveTest {
    private static final Logger log = LoggerFactory.getLogger(ParseCaseInsensitiveTest.class);

    /**
     * Tests that the the yaml to parse can contain properties in any case.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void parseAnyCase() throws Exception {
        String yamlString = "";
        yamlString += "!!de.beosign.snakeyamlanno.caseinsensitive.ParseCaseInsensitiveTest.Person\n";
        yamlString += "nAmE: homer\n";
        yamlString += "age: 38\n";
        yamlString += "aLIAs: jay\n";

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Person.class, true);
        Yaml yaml = new Yaml(constructor);

        Person parseResult = yaml.loadAs(yamlString, Person.class);
        log.debug("Parsed YAML file:\n{}", parseResult);

        assertThat(parseResult.getName(), Is.is("homer"));
        assertThat(parseResult.getAliasProperty(), Is.is("jay"));
        assertThat(parseResult.getAge(), Is.is(38));
    }

    /**
     * Tests duplicate properties. Result is undetermined.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void parseAnyCaseDuplicateProperties() throws Exception {
        String yamlString = "";
        yamlString += "nAmE: homer\n";

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(PersonDuplicateProperties.class, true);
        Yaml yaml = new Yaml(constructor);

        PersonDuplicateProperties parseResult = yaml.loadAs(yamlString, PersonDuplicateProperties.class);
        log.debug("Parsed YAML file:\n{}", parseResult);

        // it is undetermined which property will be used in case of duplicates.
        assertThat(Objects.toString(parseResult.getName(), "") + Objects.toString(parseResult.getNAME(), ""), Is.is("homer"));
    }

    /**
     * Tests that the the yaml to parse can contain properties in any case.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void parseAnyCaseFieldAnnotated() throws Exception {
        String yamlString = "";
        yamlString += "!!de.beosign.snakeyamlanno.caseinsensitive.ParseCaseInsensitiveTest.PersonField\n";
        yamlString += "nAmE: homer\n";
        yamlString += "age: 38\n";
        yamlString += "aLIAs: jay\n";

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(PersonField.class, true);
        constructor.getPropertyUtils().setBeanAccess(BeanAccess.FIELD);
        Yaml yaml = new Yaml(constructor);

        PersonField parseResult = yaml.loadAs(yamlString, PersonField.class);
        log.debug("Parsed YAML file:\n{}", parseResult);

        assertThat(parseResult.getName(), Is.is("homer"));
        assertThat(parseResult.getAliasProperty(), Is.is("jay"));
        assertThat(parseResult.getAge(), Is.is(38));

    }

    /**
     * Test class.
     */
    public static class Person {
        private String name;
        private int age;
        private String aliasProperty;

        @YamlProperty(key = "AliaS")
        public String getAliasProperty() {
            return aliasProperty;
        }

        public void setAliasProperty(String aliasProperty) {
            this.aliasProperty = aliasProperty;
        }

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

        @Override
        public String toString() {
            return "Person [name=" + name + ", age=" + age + ", aliasProperty=" + aliasProperty + "]";
        }

    }

    /**
     * Test class.
     */
    public static class PersonDuplicateProperties {
        private String name;
        private String name2;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNAME() {
            return name2;
        }

        public void setNAME(String name2) {
            this.name2 = name2;
        }

        @Override
        public String toString() {
            return "PersonDuplicateProperties [name=" + name + ", name2=" + name2 + "]";
        }
    }

    /**
     * Test class.
     */
    public static class PersonField {
        private String name;
        private int age;

        @YamlProperty(key = "AliaS")
        private String aliasProperty;

        public String getAliasProperty() {
            return aliasProperty;
        }

        public void setAliasProperty(String aliasProperty) {
            this.aliasProperty = aliasProperty;
        }

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

        @Override
        public String toString() {
            return "Person [name=" + name + ", age=" + age + ", aliasProperty=" + aliasProperty + "]";
        }

    }

}
