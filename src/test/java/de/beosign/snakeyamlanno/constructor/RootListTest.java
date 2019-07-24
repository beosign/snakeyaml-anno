package de.beosign.snakeyamlanno.constructor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @BeforeEach
    public void before() {
        annotationAwareConstructor = new AnnotationAwareConstructor(Person.class);
        annotationAwareConstructor.getConstructByMap().put(Enum.class, YamlConstructBy.Factory.of(EnumConstructor.class));
    }

    /**
     * Tests loading of a list at the root using a certain item type.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testMultiple() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("persons.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            annotationAwareConstructor = new AnnotationAwareListConstructor(Person.class, false);
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

    /**
     * Tests loading of a list at the root using default mechanisms leads to HashMaps instead of a typed object.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testMultipleUntypedByDefault() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("persons.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            annotationAwareConstructor = new AnnotationAwareConstructor(List.class, false);
            Yaml yaml = new Yaml(annotationAwareConstructor);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> persons = yaml.loadAs(yamlString, List.class);
            log.debug("Parsed YAML file:\n{}", persons);

            assertThat(persons, notNullValue());
            assertThat(persons.size(), is(2));
            assertThat(persons.get(0), isA(Map.class));
            assertThat(persons.get(1), isA(Map.class));
        }
    }

    /**
     * Tests loading of a list at the root works by default if items are implicitly typed.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testMultipleWithDefaultConstructorWithImplicitType() throws Exception {
        String yamlString = "- 3\n- 4"; // list of ints
        log.debug("Loaded YAML file:\n{}", yamlString);

        annotationAwareConstructor = new AnnotationAwareConstructor(Integer.class);
        Yaml yaml = new Yaml(annotationAwareConstructor);

        @SuppressWarnings("unchecked")
        List<Integer> ints = yaml.loadAs(yamlString, List.class);
        assertThat(ints.size(), is(2));
        assertThat(ints.get(0), is(3));
        assertThat(ints.get(1), is(4));
    }

    /**
     * Tests loading of a list at the root works by default if items are implicitly typed.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testMultipleWithDefaultConstructorWithExplicitType() throws Exception {
        String yamlString = "- 3\n- 4"; // list of ints
        log.debug("Loaded YAML file:\n{}", yamlString);

        annotationAwareConstructor = new AnnotationAwareListConstructor(Integer.class);
        Yaml yaml = new Yaml(annotationAwareConstructor);

        @SuppressWarnings("unchecked")
        List<Integer> ints = yaml.loadAs(yamlString, List.class);
        assertThat(ints.size(), is(2));
        assertThat(ints.get(0), is(3));
        assertThat(ints.get(1), is(4));
    }

    /**
     * Tests that passing <code>null</code> as collection item type causes a {@link NullPointerException}.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testNullArgument() throws Exception {
        assertThrows(NullPointerException.class, () -> new AnnotationAwareListConstructor(null));
    }

    /**
     * Tests that passing <code>null</code> as collection item type causes a {@link NullPointerException}.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testNullArgument2() throws Exception {
        assertThrows(NullPointerException.class, () -> new AnnotationAwareListConstructor(null, false));
    }

}
