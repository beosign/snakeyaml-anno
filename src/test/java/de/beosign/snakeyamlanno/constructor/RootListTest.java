package de.beosign.snakeyamlanno.constructor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsNull.notNullValue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * Tests parsing of an untyped list of items at the root.
 * 
 * @author florian
 */
public class RootListTest {
    private static final Logger log = LoggerFactory.getLogger(CustomConstructorTest.class);

    private AnnotationAwareConstructor annotationAwareConstructor;

    @Before
    public void before() {
        annotationAwareConstructor = new AnnotationAwareConstructor(Person.class);
        annotationAwareConstructor.getConstructByMap().put(Enum.class, ConstructByFactory.of(EnumConstructor.class));
    }

    /**
     * Test enum converter.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testMultiple() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("persons.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            annotationAwareConstructor = new AnnotationAwareConstructor(List.class, Person.class, false);
            Yaml yaml = new Yaml(annotationAwareConstructor);

            @SuppressWarnings("unchecked")
            List<Person> persons = yaml.loadAs(yamlString, List.class);
            log.debug("Parsed YAML file:\n{}", persons);

            assertThat(persons, notNullValue());
            assertThat(persons.size(), is(2));
            assertThat(persons.get(0), isA(Person.class));
            assertThat(persons.get(1), isA(Person.class));

            assertThat(persons.get(0).getName(), is("Homer"));
            assertThat(persons.get(1).getName(), is("Marge"));
        }
    }

}
