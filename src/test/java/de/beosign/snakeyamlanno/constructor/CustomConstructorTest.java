package de.beosign.snakeyamlanno.constructor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

import de.beosign.snakeyamlanno.constructor.Person.Dog;
import de.beosign.snakeyamlanno.constructor.Person.Skill;

/**
 * Tests the constructor functionality.
 * 
 * @author florian
 */
public class CustomConstructorTest {
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
    public void parseWithCustomConstructorOverriddenAtPropertyLevel() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(annotationAwareConstructor);

            Person person = yaml.loadAs(yamlString, Person.class);
            log.debug("Parsed YAML file:\n{}", person);

            assertThat(person, notNullValue());
            assertThat(person.getFirstPet().getName(), is("dog1"));
            assertThat(person.getFirstPet().getAge(), is(6));
            assertThat(person.getPets().size(), is(3));

        }
    }

    @Test
    public void parseWithCaseInsensitiveEnum() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(annotationAwareConstructor);

            Person person = yaml.loadAs(yamlString, Person.class);
            log.debug("Parsed YAML file:\n{}", person);

            assertThat(person, notNullValue());
            assertThat(person.getFavoriteColors().size(), is(2));
            assertThat(person.getFavoriteColors().get(0), is(Person.Color.BLUE));
            assertThat(person.getFavoriteColors().get(1), is(Person.Color.RED));

        }
    }

    /**
     * Test enum converter.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void parseWithCustomOverridingEnumConstructor() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(annotationAwareConstructor);

            Person person = yaml.loadAs(yamlString, Person.class);
            log.debug("Parsed YAML file:\n{}", person);

            assertThat(person, notNullValue());
            assertThat(person.getSkill(), is(Skill.PRO));
        }
    }

    /**
     * Test custom constructor for unknown property "years".
     * 
     * @throws Exception on any exception
     */
    @Test
    public void parseWithCustomConstructorInList() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(annotationAwareConstructor);

            Person person = yaml.loadAs(yamlString, Person.class);
            log.debug("Parsed YAML file:\n{}", person);

            assertThat(person, notNullValue());

            assertThat(person.getPets().get(0), instanceOf(Dog.class));
            assertThat(person.getPets().get(0).getName(), is("dog2"));
            assertThat(person.getPets().get(0).getAge(), is(4));

        }
    }

    /**
     * @throws Exception on any exception
     */
    @Test
    public void parseWithNonAccessibleCustomConstructor() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            annotationAwareConstructor.getConstructByMap().put(Dog.class, ConstructByFactory.of(PrivateCustomConverter.class));
            Yaml yaml = new Yaml(annotationAwareConstructor);
            try {
                yaml.loadAs(yamlString, Person.class);
                Assert.fail("Expected exception");
            } catch (Exception e) {
                Assert.assertThat(e, instanceOf(YAMLException.class));
                Assert.assertThat(e.getCause(), instanceOf(YAMLException.class));
                Assert.assertThat(e.getCause().getCause(), instanceOf(IllegalAccessException.class));
            }
        }
    }

    /**
     * @throws Exception on any exception
     */
    @Test
    public void parseWithAbstractCustomConstructor() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            annotationAwareConstructor.getConstructByMap().put(Dog.class, ConstructByFactory.of(AbstractCustomConverter.class));
            Yaml yaml = new Yaml(annotationAwareConstructor);

            try {
                yaml.loadAs(yamlString, Person.class);
                Assert.fail("Expected exception");
            } catch (Exception e) {
                Assert.assertThat(e, instanceOf(YAMLException.class));
                Assert.assertThat(e.getCause(), instanceOf(YAMLException.class));
                Assert.assertThat(e.getCause().getCause(), instanceOf(InstantiationException.class));
            }
        }
    }

    /**
     * @throws Exception on any exception
     */
    @Test
    public void parseWithAbstractCustomConstructorOnProperty() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("personBad2.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);
            Yaml yaml = new Yaml(annotationAwareConstructor);

            try {
                yaml.loadAs(yamlString, Person.class);
                Assert.fail("Expected exception");
            } catch (Exception e) {
                Assert.assertThat(e, instanceOf(YAMLException.class));
                Assert.assertThat(e.getCause(), instanceOf(YAMLException.class));
                Assert.assertThat(e.getCause().getMessage(), StringContains.containsString("Custom constructor"));
                Assert.assertThat(e.getCause().getMessage(), StringContains.containsString("Person::notConstructable"));
                Assert.assertThat(e.getCause().getMessage(), StringContains.containsString("cannot be created"));
                Assert.assertThat(e.getCause().getCause(), instanceOf(InstantiationException.class));
            }
        }
    }

    /**
     * @throws Exception on any exception
     */
    @Test
    public void parseWithNotSettableCustomConstructorOnProperty() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("personBad.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(annotationAwareConstructor);

            try {
                yaml.loadAs(yamlString, Person.class);
                Assert.fail("Expected exception");
            } catch (Exception e) {
                Assert.assertThat(e, instanceOf(YAMLException.class));
                Assert.assertThat(e.getCause(), instanceOf(YAMLException.class));
                Assert.assertThat(e.getCause().getMessage(), StringContains.containsString("Cannot set value of type java.lang.String"));
                Assert.assertThat(e.getCause().getMessage(), StringContains.containsString("Person::notSettable"));
                Assert.assertThat(e.getCause().getMessage(), StringContains.containsString("of class java.lang.Integer"));
            }
        }
    }

    /**
     * @throws Exception on any exception
     */
    @Test
    public void parseWithWrongObjectCustomConstructor() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            annotationAwareConstructor.getConstructByMap().put(Skill.class, ConstructByFactory.of(WrongObjectConverter.class));
            Yaml yaml = new Yaml(annotationAwareConstructor);

            try {
                yaml.loadAs(yamlString, Person.class);
                Assert.fail("Expected exception");
            } catch (Exception e) {
                Assert.assertThat(e, instanceOf(YAMLException.class));
                Assert.assertThat(e.getCause(), instanceOf(IllegalArgumentException.class));
            }
        }
    }

    // CHECKSTYLE:OFF - test classes
    protected static class WrongObjectConverter implements CustomConstructor<String> {
        @Override
        public String construct(Node node, Function<? super Node, ? extends String> defaultConstructor) throws YAMLException {
            return "Wrong object type";
        }
    }

    protected abstract static class AbstractCustomConverter implements CustomConstructor<String> {
    }

    private abstract static class PrivateCustomConverter implements CustomConstructor<String> {
    }

}
