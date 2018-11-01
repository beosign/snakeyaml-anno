package de.beosign.snakeyamlanno.convert;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;
import de.beosign.snakeyamlanno.property.Person;
import de.beosign.snakeyamlanno.property.Person.Gender;
import de.beosign.snakeyamlanno.representer.AnnotationAwareRepresenter;

/**
 * Tests the converter functionality.
 * 
 * @author florian
 */
public class ConverterTest {
    private static final Logger log = LoggerFactory.getLogger(ConverterTest.class);

    /**
     * Test enum converter.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void parseWithEnumConverter() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("person1.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(List.class));

            @SuppressWarnings("unchecked")
            List<Person> parseResult = yaml.loadAs(yamlString, List.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.size(), is(2));
            assertThat(parseResult.get(0).getName(), is("Homer"));
            assertThat(parseResult.get(0).getGender(), is(Gender.MALE));
            assertThat(parseResult.get(1).getName(), is("Marge"));
            assertThat(parseResult.get(1).getGender(), is(Gender.FEMALE));
        }
    }

    /**
     * Test string/integer converter.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void parseWithStringToIntConverter() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("person2.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(List.class));

            @SuppressWarnings("unchecked")
            List<Person> parseResult = yaml.loadAs(yamlString, List.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.size(), is(2));
            assertThat(parseResult.get(0).getName(), is("Homer"));
            assertThat(parseResult.get(0).getHeight(), is(185));
        }
    }

    @Test
    public void parseWithConverterThrowingException() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("personWithConverter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(PersonWithConverter.class));
            yaml.setBeanAccess(BeanAccess.FIELD);

            try {
                yaml.loadAs(yamlString, PersonWithConverter.class);
                fail("Expected exception");
            } catch (YAMLException e) {
                ConverterException cause = (ConverterException) e.getCause();
                assertNotNull(cause);
            }
        }
    }

    @Test
    public void parseWithConverterThrowingException2() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("personWithConverter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(PersonWithConverter2.class));
            yaml.setBeanAccess(BeanAccess.FIELD);

            try {
                yaml.loadAs(yamlString, PersonWithConverter2.class);
                fail("Expected exception");
            } catch (YAMLException e) {
                ConverterException cause = (ConverterException) e.getCause();
                assertNotNull(cause);
            }
        }
    }

    @Test
    public void parseWithConverterThrowingException3() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("personWithConverter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(PersonWithConverter3.class));
            yaml.setBeanAccess(BeanAccess.FIELD);

            try {
                yaml.loadAs(yamlString, PersonWithConverter3.class);
                fail("Expected exception");
            } catch (ConstructorException e) {
                InstantiationException causeCause = (InstantiationException) e.getCause().getCause();
                assertNotNull(causeCause);
            }
        }
    }

    @Test
    public void parseWithConverterThrowingException4() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("personWithConverter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(PersonWithConverter4.class));
            yaml.setBeanAccess(BeanAccess.FIELD);

            try {
                yaml.loadAs(yamlString, PersonWithConverter4.class);
                fail("Expected exception");
            } catch (ConstructorException e) {
                IllegalAccessException causeCause = (IllegalAccessException) e.getCause().getCause();
                assertNotNull(causeCause);
            }
        }
    }

    @Test
    public void dumpWithConverter() throws Exception {
        Person homer = new Person();
        homer.setName("Homer");
        homer.setGender(Gender.MALE);
        homer.setHeight(185);

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter());
        String dumped = yaml.dumpAsMap(homer);

        System.out.println(dumped);

        assertThat(dumped, dumped, StringContains.containsString("name: Homer"));
        assertThat(dumped, dumped, StringContains.containsString("height: 185cm"));
        assertThat(dumped, dumped, StringContains.containsString("gender: m"));

    }
}
