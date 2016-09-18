package de.beosign.snakeyamlanno;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNull.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import de.beosign.snakeyamlanno.Person.Dog;
import de.beosign.snakeyamlanno.Person.Gender;
import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;

/**
 * Tests the ignore errors functionality.
 * 
 * @author florian
 */
public class TypeDetectionTest {
    private static final Logger log = LoggerFactory.getLogger(TypeDetectionTest.class);

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

}
