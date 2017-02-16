package de.beosign.snakeyamlanno.convert;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNull.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;
import de.beosign.snakeyamlanno.property.Person;
import de.beosign.snakeyamlanno.property.Person.Gender;

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
}
