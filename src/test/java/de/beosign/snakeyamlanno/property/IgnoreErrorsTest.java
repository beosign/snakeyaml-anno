package de.beosign.snakeyamlanno.property;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;
import de.beosign.snakeyamlanno.property.Person.Gender;
import de.beosign.snakeyamlanno.type.Animal.Dog;

/**
 * Tests the ignore errors functionality.
 * 
 * @author florian
 */
public class IgnoreErrorsTest {
    private static final Logger log = LoggerFactory.getLogger(IgnoreErrorsTest.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Tests if errors are ignored if annotated accordingly.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void ignoreErrorsInvalid() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("ignoreErrorsInvalid.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(Person.class));

            Person parseResult = yaml.loadAs(yamlString, Person.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.getName(), is("Homer"));
            assertThat(parseResult.getHeight(), is(185));
            assertThat(parseResult.getGender(), is(Gender.MALE));
            assertThat(parseResult.getAnimal(), nullValue());
        }
    }

    /**
     * Tests if everything works if ignoreErrors is set but there is no error.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void ignoreErrorsValid() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("ignoreErrorsValid.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(Person.class));

            Person parseResult = yaml.loadAs(yamlString, Person.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.getName(), is("Homer"));
            assertThat(parseResult.getHeight(), is(185));
            assertThat(parseResult.getGender(), is(Gender.MALE));
            Assert.assertTrue(parseResult.getAnimal() instanceof Dog);
            Assert.assertThat(((Dog) parseResult.getAnimal()).getLoudness(), is(3));
        }
    }

    /**
     * Tests if everything works if ignoreErrors is set but there is no error.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void noIgnoreErrorsInvalid() throws Exception {
        thrown.expect(YAMLException.class);
        thrown.expectMessage("name2");
        try (InputStream is = ClassLoader.getSystemResourceAsStream("noIgnoreErrorsInvalid.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(Person.class));

            yaml.loadAs(yamlString, Person.class);
        }
    }

}
