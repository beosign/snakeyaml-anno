package de.beosign.snakeyamlanno.instantiator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;
import de.beosign.snakeyamlanno.constructor.Person;
import de.beosign.snakeyamlanno.util.NodeUtil;

/**
 * Tests the instantiator functionality.
 * 
 * @author florian
 */
public class InstantiatorTest {
    private static final Logger log = LoggerFactory.getLogger(InstantiatorTest.class);

    private AnnotationAwareConstructor annotationAwareConstructor;

    @BeforeEach
    public void before() {
        annotationAwareConstructor = new AnnotationAwareConstructor(Person.class);
        annotationAwareConstructor.setGlobalInstantiator(new PersonBeanInstantiator() {
            @Override
            protected int getAge() {
                return 66;
            }
        });
    }

    /**
     * Tests parsing using a constructor-wide instantiator and an instantiator on a type.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testValid() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("personbean.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(annotationAwareConstructor);

            PersonBean homerPersonBean = yaml.loadAs(yamlString, PersonBean.class);
            assertHomerPersonBean(homerPersonBean, 66);

        }
    }

    /**
     * Tests parsing using an instantiator on a type using the programmatic approach.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testValidNoInstantiatorInConstructor() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("personbean.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            // manually register instantiator
            annotationAwareConstructor.registerCustomInstantiator(PersonBean.class, PersonBeanInstantiator.class);
            Yaml yaml = new Yaml(annotationAwareConstructor);

            PersonBean homerPersonBean = yaml.loadAs(yamlString, PersonBean.class);
            assertHomerPersonBean(homerPersonBean, 77);
        }
    }

    /**
     * Tests the following setup.
     * <ul>
     * <li>A global instantiator is present in constructor</li>
     * <li>An instantiator is unregistered (programmatically) for a type, and a global instantiator is ignored</li>
     * </ul>
     * <b>Expectation:</b> The instance is created as if no (global) instantiator were present at all
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testValidInstantiatorOverriddenUsingFallback() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person1a.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            // manually register instantiator
            annotationAwareConstructor.setGlobalInstantiator(new AnimalInstantiator());
            annotationAwareConstructor.registerGlobalInstantiator(Animal.class);
            Yaml yaml = new Yaml(annotationAwareConstructor);

            Person1 person1 = yaml.loadAs(yamlString, Person1.class);

            assertThat(person1.getName(), is("Homer"));

            // although the name is "mydog", which should usually let the AnimalConstructor create a Dog instance, an "Animal" is created
            assertThat(person1.getAnimal().getClass().getName(), is(Animal.class.getName()));
            assertThat(person1.getAnimal().getName(), is("mydog"));

            assertThat(YamlInstantiateBy.Factory.of(DefaultCustomInstantiator.class).annotationType().getName(), is(YamlInstantiateBy.class.getName()));
        }
    }

    /**
     * Test an instantiator using the default instantiator to return the result.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testValidInstantiatorUsingDefaultInstantiator() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person1c.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            // manually register instantiator
            annotationAwareConstructor.setGlobalInstantiator(new AnimalInstantiator());
            Yaml yaml = new Yaml(annotationAwareConstructor);

            Person1 person1 = yaml.loadAs(yamlString, Person1.class);

            assertThat(person1.getName(), is("Homer"));

            assertThat(person1.getAnimal().getClass().getName(), is(Animal.class.getName()));
            assertThat(person1.getAnimal().getName(), is("any pet"));
        }
    }

    /**
     * Tests the following setup.
     * <ul>
     * <li>A global instantiator is present in constructor</li>
     * <li>An instantiator is configured (annotation) for a type, with a different logic</li>
     * <li>An instantiator is configured (programmatically) for a type, with a different logic</li>
     * </ul>
     * <b>Expectation:</b> The instantiator on the type renders the global instantiator and the annotation for this type ineffective and uses its own
     * instantiaton logic to create a Cat
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testValidInstantiatorOverridden() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person1b.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            // manually register instantiator
            annotationAwareConstructor.setGlobalInstantiator(new AnimalInstantiator());
            annotationAwareConstructor.registerCustomInstantiator(Animal.class, Person1CatInstantiator.class);
            Yaml yaml = new Yaml(annotationAwareConstructor);

            Person1 person1 = yaml.loadAs(yamlString, Person1.class);

            assertThat(person1.getName(), is("Homer"));

            // although the name is "mydog", which should usually let the AnimalConstructor create a Dog instance, an "Animal" is created
            assertThat(person1.getAnimal(), instanceOf(Cat.class));
            assertThat(person1.getAnimal().getName(), is("cat"));
            System.out.println(person1);

        }
    }

    /**
     * Tests the following setup.
     * <ul>
     * <li>A global instantiator is present in constructor</li>
     * <li>An instantiator is configured (annotation) for a type, with a different logic</li>
     * <li>An instantiator is removed (programmatically) for a type, ignoring the global instantiator</li>
     * </ul>
     * <b>Expectation:</b> The normal Snakeyaml instantiation logic must have been applied
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testValidInstantiatorUnregisteredGlobalNotEffective() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person1d.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            // manually register instantiator
            annotationAwareConstructor.setGlobalInstantiator(new MouseInstantiator());
            annotationAwareConstructor.registerDefaultInstantiator(Animal.class);
            Yaml yaml = new Yaml(annotationAwareConstructor);

            Person1 person1 = yaml.loadAs(yamlString, Person1.class);

            assertThat(person1.getName(), is("Homer"));

            // since the type specific instantiator is ignored, and also the global one, we get an Animal
            assertThat(person1.getAnimal(), instanceOf(Animal.class));
            assertThat(person1.getAnimal().getName(), is("my animal"));
            System.out.println(person1);

        }
    }

    /**
     * Tests the following setup.
     * <ul>
     * <li>A global instantiator is <b>NOT</b> present in constructor</li>
     * <li>An instantiator is configured (annotation) for a type, with a different logic</li>
     * <li>An instantiator is removed (programmatically) for a type, not ignoring the (not present) global instantiator</li>
     * </ul>
     * <b>Expectation:</b> The normal Snakeyaml instantiation logic must have been applied
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testValidInstantiatorUnregisteredGlobalNotPresent() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person1d.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            // manually register instantiator
            annotationAwareConstructor.registerDefaultInstantiator(Animal.class);
            Yaml yaml = new Yaml(annotationAwareConstructor);

            Person1 person1 = yaml.loadAs(yamlString, Person1.class);

            assertThat(person1.getName(), is("Homer"));

            // since the type specific instantiator is ignored, and there is no global one, we get an Animal
            assertThat(person1.getAnimal(), instanceOf(Animal.class));
            assertThat(person1.getAnimal().getName(), is("my animal"));
            System.out.println(person1);

        }
    }

    /**
     * Tests the following setup.
     * <ul>
     * <li>A global instantiator is present in constructor</li>
     * <li>An instantiator is configured (annotation) for a type, with a different logic</li>
     * <li>An instantiator is removed (programmatically) for a type, but considering the global instantiator</li>
     * </ul>
     * <b>Expectation:</b> The global instantiator's instantiation logic must have been applied
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testValidInstantiatorUnregisteredButGlobalStillEffective() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("person1d.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            // manually register instantiator
            annotationAwareConstructor.setGlobalInstantiator(new MouseInstantiator()); // creates a Mouse
            annotationAwareConstructor.registerGlobalInstantiator(Animal.class); // creates an Animal
            Yaml yaml = new Yaml(annotationAwareConstructor);

            Person1 person1 = yaml.loadAs(yamlString, Person1.class);

            assertThat(person1.getName(), is("Homer"));

            // since the type specific instantiator is ignored, but not the global one, we get a Mouse
            assertThat(person1.getAnimal(), instanceOf(Mouse.class));
            assertThat(person1.getAnimal().getName(), is("my mouse"));
            System.out.println(person1);

        }
    }

    /**
     * Tests that an InstantiationException causes the parsing process to fail if an instantiator cannot be created.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testInvalidInstantiator() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("personbean.yaml")) {

            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            String newYamlString = yamlString + "\ninvalid: {brand: invalid}";
            log.debug("Loaded YAML file:\n{}", newYamlString);

            Yaml yaml = new Yaml(annotationAwareConstructor);

            ConstructorException constructorException = assertThrows(ConstructorException.class, () -> yaml.loadAs(newYamlString, PersonBean.class));
            assertTrue(constructorException.getCause() instanceof YAMLException, constructorException.getCause().toString());
            assertTrue(constructorException.getCause().getCause() instanceof InstantiationException, constructorException.getCause().getCause().toString());
        }
    }

    private void assertHomerPersonBean(PersonBean homerPersonBean, int personAgeByInstantiator) {

        // person
        assertThat(homerPersonBean.getName(), is("Homer"));
        assertThat(homerPersonBean.getAge(), is(43));
        assertThat(homerPersonBean.getPet().getName(), is("MYDOG")); // see AnimalConstructor
        assertThat(homerPersonBean.getCar().getBrand(), is("dodge"));

        // homer's children
        assertThat(homerPersonBean.getChildren().size(), is(2));
        assertThat(homerPersonBean.getChildren().get(0).getName(), is("Bart"));
        assertThat(homerPersonBean.getChildren().get(0).getAge(), is(personAgeByInstantiator));
        assertThat(homerPersonBean.getChildren().get(0).getChildren(), nullValue());

        assertThat(homerPersonBean.getChildren().get(1).getName(), is("Lisa"));
        assertThat(homerPersonBean.getChildren().get(1).getAge(), is(8));
        assertThat(homerPersonBean.getChildren().get(1).getPet().getName(), is("lisas cat")); // created by snakeyaml default constructor

        // Lisa's children
        List<PersonBean> lisasChildren = homerPersonBean.getChildren().get(1).getChildren();
        assertThat(lisasChildren.size(), is(2));

        assertThat(lisasChildren.get(0).getName(), is("lisa1"));
        assertThat(lisasChildren.get(0).getAge(), is(personAgeByInstantiator));
        assertThat(lisasChildren.get(1).getName(), is("lisa2"));
        assertThat(lisasChildren.get(1).getAge(), is(2));
        assertThat(lisasChildren.get(1).getChildren().size(), is(1));
        assertThat(lisasChildren.get(1).getChildren().get(0).getName(), is("lisa2.1"));
    }

    /** Test class. */
    public static class PersonBean {
        private String name;
        private int age;
        private Car car;
        private List<PersonBean> children;
        private Animal pet;
        private Dog dog;
        private Invalid invalid;

        public PersonBean() {

        }

        public static PersonBean of(String name) {
            PersonBean personBean = new PersonBean();
            personBean.setName(name);

            return personBean;
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

        public List<PersonBean> getChildren() {
            return children;
        }

        public void setChildren(List<PersonBean> children) {
            this.children = children;
        }

        public Animal getPet() {
            return pet;
        }

        public void setPet(Animal pet) {
            this.pet = pet;
        }

        public Dog getDog() {
            return dog;
        }

        public void setDog(Dog dog) {
            this.dog = dog;
        }

        @Override
        public String toString() {
            return "PersonBean [name=" + name + ", age=" + age + ", car=" + car + ", pet=" + pet + ", dog=" + dog + ", invalid=" + invalid + ", children=" + children + "]";
        }

        public Car getCar() {
            return car;
        }

        public void setCar(Car car) {
            this.car = car;
        }

        public Invalid getInvalid() {
            return invalid;
        }

        public void setInvalid(Invalid invalid) {
            this.invalid = invalid;
        }

    }

    /** Test class where an {@link AnimalInstantiator} is applied to. */
    @YamlInstantiateBy(AnimalInstantiator.class)
    public static class Animal {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Animal [name=" + name + "]";
        }
    }

    /** Test class. */
    public static class Dog extends Animal {
        public Dog(String name) {
            setName(name);
        }

        @Override
        public String toString() {
            return "Dog [name=" + super.getName() + "]";
        }
    }

    /** Test class. */
    public static class Cat extends Animal {
        public Cat(String name) {
            setName(name);
        }

        @Override
        public String toString() {
            return "Cat [name=" + super.getName() + "]";
        }
    }

    /** Test class. */
    public static class Mouse extends Animal {
        public Mouse(String name) {
            setName(name);
        }

        @Override
        public String toString() {
            return "Mouse [name=" + super.getName() + "]";
        }
    }

    /** Test class with an empty Type annotation (makes no sense, but does not cause any errors). */
    public static class Car {
        private String brand;

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        @Override
        public String toString() {
            return "Car [brand=" + brand + "]";
        }
    }

    /** Test class. */
    @YamlInstantiateBy(InvalidInstantiator.class)
    public static class Invalid {
        private String brand;

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        @Override
        public String toString() {
            return "Invalid [brand=" + brand + "]";
        }
    }

    /** CustomInstantiator that creates a PersonBean using a static factory method if the node is a scalar node. */
    public static class PersonBeanInstantiator extends DefaultGlobalInstantiator implements CustomInstantiator<PersonBean> {

        @Override
        public PersonBean createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator, GlobalInstantiator globalInstantiator)
                throws InstantiationException {
            return (PersonBean) createInstance(node, tryDefault, ancestor, defaultInstantiator);
        }

        @Override
        public Object createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator) throws InstantiationException {
            if (node.getType().equals(PersonBean.class)) {
                if (node instanceof ScalarNode) {
                    // a name was passed
                    PersonBean personBean = PersonBean.of(Objects.toString(NodeUtil.getValue(node)));
                    personBean.setAge(getAge());
                    return personBean;
                }
                return new PersonBean();
            }
            return defaultInstantiator.createInstance(ancestor, node, tryDefault);
        }

        protected int getAge() {
            return 77;
        }

    }

    /** Creates Animal instances or a Dog instance if "mydog" is used as node value. */
    public static class AnimalInstantiator extends DefaultGlobalInstantiator implements CustomInstantiator<Animal> {

        @Override
        public Object createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator)
                throws InstantiationException {
            if (Animal.class.isAssignableFrom(node.getType())) {
                return createInstance(node, tryDefault, ancestor, defaultInstantiator, null);
            }
            return defaultInstantiator.createInstance(ancestor, node, tryDefault);
        }

        @Override
        public Animal createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator, GlobalInstantiator globalInstantiator)
                throws InstantiationException {
            if (Animal.class.isAssignableFrom(node.getType())) {
                if (Objects.toString(NodeUtil.getValue(node), "").equals("mydog")) {
                    return new Dog("MYDOG");
                }
                if (node instanceof MappingNode) {
                    MappingNode mappingNode = (MappingNode) node;
                    Dog dog = mappingNode.getValue().stream()//
                            .filter(nt -> NodeUtil.getValue(nt.getKeyNode()).equals("loudness"))//
                            .filter(nt -> !Objects.toString(NodeUtil.getValue(nt.getValueNode())).contains("pet"))//
                            .findFirst()//
                            .map(nt -> Objects.toString(NodeUtil.getValue(nt.getValueNode()), ""))//
                            .map(name -> new Dog(name))//
                            .orElse(null);
                    if (dog != null) {
                        return dog;
                    }

                    return (Animal) defaultInstantiator.createInstance(ancestor, mappingNode, tryDefault);
                }
            }
            return null;
        }
    }

    /** Creates Mouse instances. */
    public static class MouseInstantiator extends AnimalInstantiator {
        @Override
        public Object createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator)
                throws InstantiationException {
            if (Animal.class.isAssignableFrom(node.getType())) {
                NodeUtil.removeNode((MappingNode) node, "name");
                return new Mouse("my mouse");
            }
            return super.createInstance(node, tryDefault, ancestor, defaultInstantiator);
        }
    }

    /** Creates Dog instances. */
    public static class DogInstantiator extends AnimalInstantiator {

        @Override
        public Animal createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator, GlobalInstantiator globalInstantiator)
                throws InstantiationException {

            if (Dog.class.isAssignableFrom(node.getType())) {
                if (Objects.toString(NodeUtil.getValue(node), "").equals("mydog")) {
                    return new Dog("MY LITTLE DOG");
                }
                if (node instanceof MappingNode) {
                    MappingNode mappingNode = (MappingNode) node;
                    Dog dog = mappingNode.getValue().stream()//
                            .filter(nt -> NodeUtil.getValue(nt.getKeyNode()).equals("name"))//
                            .findFirst()//
                            .map(nt -> Objects.toString(NodeUtil.getValue(nt.getValueNode()), ""))//
                            .map(name -> name.equals("mydog") ? "MY LITTLE DOG" : name)//
                            .map(name -> new Dog(name))//
                            .orElse(null);
                    if (dog != null) {
                        // remove 'name', otherwise it will be overwritten when filling the object
                        NodeUtil.removeNode(mappingNode, "name");
                    }
                    return dog;

                }
            }
            return super.createInstance(node, tryDefault, ancestor, defaultInstantiator, globalInstantiator);
        }
    }

    /** Private constructor, so causes errors when used. */
    public static final class InvalidInstantiator implements CustomInstantiator<Object> {
        private InvalidInstantiator() {
        }

        @Override
        public Object createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator, GlobalInstantiator globalInstantiator)
                throws InstantiationException {
            return null;
        }
    }

    /** Test class. */
    public static class Person1 {
        private String name;
        private Animal animal;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Animal getAnimal() {
            return animal;
        }

        public void setAnimal(Animal animal) {
            this.animal = animal;
        }

        @Override
        public String toString() {
            return "Person1 [name=" + name + ", animal=" + animal + "]";
        }
    }

    /** Test class. */
    public static class Person2 {
        private String name;
        private Dog dog;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Dog getDog() {
            return dog;
        }

        public void setDog(Dog dog) {
            this.dog = dog;
        }

        @Override
        public String toString() {
            return "Person2 [name=" + name + ", dog=" + dog + "]";
        }
    }

    /** CustomInstantiator that uses a special logic to create a Cat. */
    public static class Person1CatInstantiator extends AnimalInstantiator {

        @Override
        public Animal createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator, GlobalInstantiator globalInstantiator)
                throws InstantiationException {
            if (node instanceof ScalarNode) {
                return new Cat((String) NodeUtil.getValue(node));
            } else if (node instanceof MappingNode) {
                return new Cat((String) NodeUtil.getPropertyToValueMap((MappingNode) node).get("name"));
            } else {
                return super.createInstance(node, tryDefault, ancestor, defaultInstantiator, globalInstantiator);
            }
        }
    }

}
