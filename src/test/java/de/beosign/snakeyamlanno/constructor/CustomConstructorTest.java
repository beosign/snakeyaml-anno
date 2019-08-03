package de.beosign.snakeyamlanno.constructor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;

import de.beosign.snakeyamlanno.constructor.Person.Dog;
import de.beosign.snakeyamlanno.constructor.Person.Skill;
import de.beosign.snakeyamlanno.util.NodeUtil;

/**
 * Tests the constructor functionality.
 * 
 * @author florian
 */
public class CustomConstructorTest {
    private static final Logger log = LoggerFactory.getLogger(CustomConstructorTest.class);

    private AnnotationAwareConstructor annotationAwareConstructor;

    @BeforeEach
    public void before() {
        annotationAwareConstructor = new AnnotationAwareConstructor(Person.class);
        annotationAwareConstructor.getConstructByMap().put(Enum.class, YamlConstructBy.Factory.of(EnumConstructor.class));
    }

    /**
     * Tests that the constructor is taken from the annotation at property level instead of the annotation at class level.
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

    /**
     * Tests converter in combination with a custom constructor on property level. First, the custom constructor is used to create the object,
     * then a converter is applied on the created object.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void parseWithCustomConstructorOverriddenAtPropertyLevelAndPropertyAnnotation() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            yamlString += "\nthirdPet: dog33";

            log.debug("YAML:\n{}", yamlString);

            Yaml yaml = new Yaml(annotationAwareConstructor);

            Person person = yaml.loadAs(yamlString, Person.class);
            log.debug("Parsed YAML file:\n{}", person);

            assertThat(person, notNullValue());
            assertThat(person.getThirdPet().getName(), is("dog33"));
        }
    }

    /**
     * Test case insensitive enum constructor that is programmatically applied for the Java <code>Enum</code> type.
     * 
     * @throws Exception on any exception
     */
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
     * Test a constructor that creates the correctly typed instance by looking at the property names of the mapping node.
     * So instead of the former auto type detection feature that tries one substitution type after the other and checks what does not fail, the idea here is to
     * preselect the correct type.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void parseWithTypeSubstitutingConstructor() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("order.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(annotationAwareConstructor);

            Order order = yaml.loadAs(yamlString, Order.class);
            log.debug("Parsed YAML file:\n{}", order);

            assertThat(order, notNullValue());
            assertThat(order.getItems().size(), is(2));
            assertThat(order.getItems().get(0), instanceOf(PeriodicItem.class));
            assertThat(order.getItems().get(1), instanceOf(Item.class));
            assertThat(order.getItems().get(1), not(instanceOf(PeriodicItem.class)));

            assertThat(order.getName(), is("Order1"));
            assertThat(order.getId(), is(1));

            PeriodicItem periodicItem = (PeriodicItem) order.getItems().get(0);
            assertThat(periodicItem.getInterval(), is(2));
            assertThat(periodicItem.getQuantity(), is(3));
            assertThat(periodicItem.getName(), is("Soap"));

            Item item = (Item) order.getItems().get(1);
            assertThat(item.getQuantity(), is(1));
            assertThat(item.getName(), is("Laptop"));

        }
    }

    /**
     * Test that a custom constructor applied on a concrete enum (a subclass of Java's <code>Enum</code>) overrides the custom constructor for
     * <code>Enum</code>.
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

            annotationAwareConstructor.getConstructByMap().put(Dog.class, YamlConstructBy.Factory.of(PrivateCustomConstructor.class));
            Yaml yaml = new Yaml(annotationAwareConstructor);
            try {
                yaml.loadAs(yamlString, Person.class);
                fail("Expected exception");
            } catch (Exception e) {
                assertThat(e, instanceOf(YAMLException.class));
                assertThat(e.getCause(), instanceOf(YAMLException.class));
                assertThat(e.getCause().getCause(), instanceOf(IllegalAccessException.class));
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

            annotationAwareConstructor.registerCustomConstructor(Dog.class, AbstractCustomConstructor.class);
            Yaml yaml = new Yaml(annotationAwareConstructor);

            try {
                yaml.loadAs(yamlString, Person.class);
                fail("Expected exception");
            } catch (Exception e) {
                assertThat(e, instanceOf(YAMLException.class));
                assertThat(e.getCause(), instanceOf(YAMLException.class));
                assertThat(e.getCause().getCause(), instanceOf(InstantiationException.class));
            }
        }
    }

    /**
     * Fails because the DogConstructor that handles 'dogYears' property is overridden by the DefaultConstructor.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void parseWithDefaultCustomConstructor() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("personWithDogAsSecondPet.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            annotationAwareConstructor.registerDefaultConstructor(Dog.class); // Override annotation on type "Dog"
            Yaml yaml = new Yaml(annotationAwareConstructor);

            try {
                yaml.loadAs(yamlString, Person.class);
                fail("Expected exception");
            } catch (Exception e) {
                assertThat(e, instanceOf(YAMLException.class));
                assertThat(e.getCause(), instanceOf(YAMLException.class));
                assertThat(e.getCause().getCause(), instanceOf(YAMLException.class));
                assertThat(e.getCause().getCause().getMessage(), containsString("Unable to find property 'dogYears'"));
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
                fail("Expected exception");
            } catch (Exception e) {
                assertThat(e, instanceOf(YAMLException.class));
                assertThat(e.getCause(), instanceOf(YAMLException.class));
                assertThat(e.getCause().getMessage(), StringContains.containsString("Custom constructor"));
                assertThat(e.getCause().getMessage(), StringContains.containsString("Person::notConstructable"));
                assertThat(e.getCause().getMessage(), StringContains.containsString("cannot be created"));
                assertThat(e.getCause().getCause(), instanceOf(InstantiationException.class));
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
                fail("Expected exception");
            } catch (Exception e) {
                assertThat(e, instanceOf(YAMLException.class));
                assertThat(e.getCause(), instanceOf(YAMLException.class));
                assertThat(e.getCause().getMessage(), StringContains.containsString("Cannot set value of type java.lang.String"));
                assertThat(e.getCause().getMessage(), StringContains.containsString("Person::notSettable"));
                assertThat(e.getCause().getMessage(), StringContains.containsString("of class java.lang.Integer"));
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

            annotationAwareConstructor.getConstructByMap().put(Skill.class, YamlConstructBy.Factory.of(WrongObjectConstructor.class));
            Yaml yaml = new Yaml(annotationAwareConstructor);

            try {
                yaml.loadAs(yamlString, Person.class);
                fail("Expected exception");
            } catch (Exception e) {
                assertThat(e, instanceOf(YAMLException.class));
                assertThat(e.getCause(), instanceOf(IllegalArgumentException.class));
            }
        }
    }

    // CHECKSTYLE:OFF - test classes
    protected static class WrongObjectConstructor implements CustomConstructor<String> {
        @Override
        public String construct(Node node, Function<? super Node, ? extends String> defaultConstructor) throws YAMLException {
            return "Wrong object type";
        }
    }

    protected abstract static class AbstractCustomConstructor implements CustomConstructor<Dog> {
    }

    private abstract static class PrivateCustomConstructor implements CustomConstructor<String> {
    }

    public static class Order {
        private int id;
        private String name;
        private List<AbstractItem> items;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<AbstractItem> getItems() {
            return items;
        }

        public void setItems(List<AbstractItem> items) {
            this.items = items;
        }

        @Override
        public String toString() {
            return "Order [id=" + id + ", name=" + name + ", items=" + items + "]";
        }
    }

    @YamlConstructBy(AbstractItemConstructor.class)
    public abstract static class AbstractItem {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "AbstractItem [name=" + name + "]";
        }
    }

    public static class Item extends AbstractItem {
        private int quantity;

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "Item [quantity=" + quantity + ", getName()=" + getName() + "]";
        }
    }

    public static class PeriodicItem extends Item {
        private int interval;

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        @Override
        public String toString() {
            return "PeriodicItem [interval=" + interval + ", getName()=" + getName() + ", getQuantity()=" + getQuantity() + "]";
        }
    }

    public static class AbstractItemConstructor implements CustomConstructor<AbstractItem> {

        @Override
        public AbstractItem construct(Node node, Function<? super Node, ? extends AbstractItem> defaultConstructor) throws YAMLException {
            MappingNode mappingNode = (MappingNode) node;
            if (NodeUtil.getPropertyToValueMap(mappingNode).containsKey("interval")) {
                mappingNode.setType(PeriodicItem.class);
            } else {
                mappingNode.setType(Item.class);
            }
            return defaultConstructor.apply(node);
        }

    }
}
