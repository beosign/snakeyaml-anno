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
public class IgnoreErrorsTest {
    private static final Logger log = LoggerFactory.getLogger(IgnoreErrorsTest.class);

    /**
     * Tests if errors are ignored if annotated accordnigly.
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

    // private static class PartialPersonConstructor extends AnnotationAwareConstructor {
    //
    // public PartialPersonConstructor() {
    // super(Person.class);
    // yamlClassConstructors.put(NodeId.mapping, new PartialPersonMappingConstructor());
    // }
    //
    // @Override
    // protected Object constructObject(Node node) {
    // // TODO Auto-generated method stub
    // return super.constructObject(node);
    // }
    //
    // protected class PartialPersonMappingConstructor extends AnnotationAwareMappingConstructor {
    //
    // @Override
    // protected Property getProperty(Class<? extends Object> type, String name) throws IntrospectionException {
    //
    // return super.getProperty(type, name);
    //
    // }
    //
    // }
    // }

    // private static class AnimalConstructor extends AnnotationAwareConstructor {
    //
    // public AnimalConstructor() {
    // super(Animal.class);
    // }
    // }

}
