package de.beosign.snakeyamlanno.property;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;

/**
 * Tests the "Any-Setter" functionality.
 * 
 * @author florian
 */
public class AnySetterTest {
    private static final Logger log = LoggerFactory.getLogger(AnySetterTest.class);

    /**
     * Tests that unknown properties are added to a map.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testAnySetter() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("anySetter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(Car.class));

            Car parseResult = yaml.loadAs(yamlString, Car.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.getBrand(), is("Ferrari"));
            assertThat(parseResult.getUnmapped().size(), is(2));
            assertThat(parseResult.getUnmapped().get("doors"), is(3));
            assertThat(parseResult.getUnmapped().get("color"), is("red"));
            assertThat(parseResult.getEngine().getId(), is("my engine"));
            assertThat(parseResult.getEngine().getUnmapped().get("power"), is(800));
            assertThat(parseResult.getEngine().getUnmapped().get("builtOn"), is(new Date(0)));
        }
    }

    /**
     * Tests that unknown properties are added to a map if the annotated method is in superclass.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testAnySetterOnSuperclass() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("anySetter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(SpecialCar.class));

            SpecialCar parseResult = yaml.loadAs(yamlString, SpecialCar.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, instanceOf(SpecialCar.class));
            assertThat(parseResult.getBrand(), is("Ferrari"));
            assertThat(parseResult.getUnmapped().size(), is(2));
            assertThat(parseResult.getUnmapped().get("doors"), is(3));
            assertThat(parseResult.getUnmapped().get("color"), is("red"));
            assertThat(parseResult.getEngine().getId(), is("my engine"));
            assertThat(parseResult.getEngine().getUnmapped().get("power"), is(800));
            assertThat(parseResult.getEngine().getUnmapped().get("builtOn"), is(new Date(0)));
        }
    }

    /**
     * Tests that unknown properties are added to a map if the annotated method is in interface.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testAnySetterOnInterface() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("anySetter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(CarWithInterface.class));

            CarWithInterface parseResult = yaml.loadAs(yamlString, CarWithInterface.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, instanceOf(CarWithInterface.class));
            assertThat(parseResult.getMap().size(), is(4));
            assertThat(parseResult.getMap().get("brand"), is("Ferrari"));
            assertThat(parseResult.getMap().get("doors"), is(3));
            assertThat(parseResult.getMap().get("color"), is("red"));

            assertThat(parseResult.getMap().get("engine"), instanceOf(Map.class));
            Map<?, ?> engineMap = (Map<?, ?>) parseResult.getMap().get("engine");
            assertThat(engineMap.keySet(), hasSize(3));
            assertThat(engineMap.get("id"), is("my engine"));
            assertThat(engineMap.get("power"), is(800));
            assertThat(engineMap.get("builtOn"), is(new Date(0)));
        }
    }

    /**
     * Tests that unknown properties are added to a map of the most concrete class.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testAnySetterOnOwnAndSuperclass() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("anySetter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(SpecialCarOwnSetter.class));

            SpecialCarOwnSetter parseResult = yaml.loadAs(yamlString, SpecialCarOwnSetter.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, instanceOf(SpecialCarOwnSetter.class));
            assertThat(parseResult.getBrand(), is("Ferrari"));
            assertThat(parseResult.getUnmapped().size(), is(0));
            assertThat(parseResult.getSpecialMap().size(), is(2));
            assertThat(parseResult.getSpecialMap().get("doors"), is(3));
            assertThat(parseResult.getSpecialMap().get("color"), is("red"));
            assertThat(parseResult.getEngine().getId(), is("my engine"));
            assertThat(parseResult.getEngine().getUnmapped().get("power"), is(800));
            assertThat(parseResult.getEngine().getUnmapped().get("builtOn"), is(new Date(0)));
        }
    }

    /**
     * Tests that unknown properties are added to a map. Two methods are annotated with the annotation, but this doesn't matter.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testAnySetterTwoAnnotations() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("anySetter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(Car2.class));

            Car2 parseResult = yaml.loadAs(yamlString, Car2.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.getBrand(), is("Ferrari"));
            assertThat(parseResult.unmapped.size(), is(3));
            assertThat(parseResult.unmapped.get("doors"), is(3));
            assertThat(parseResult.unmapped.get("color"), is("red"));
            assertThat(parseResult.unmapped.get("engine"), instanceOf(Map.class));

            Map<?, ?> engineMap = (Map<?, ?>) parseResult.unmapped.get("engine");
            assertThat(engineMap.keySet(), hasSize(3));
            assertThat(engineMap.get("id"), is("my engine"));
            assertThat(engineMap.get("power"), is(800));
            assertThat(engineMap.get("builtOn"), is(new Date(0)));
        }
    }

    /**
     * Tests that an exception is thrown if the annotated method does not have two parameters.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testAnySetterWrongParamCount() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("anySetter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(CarWrongParamCount.class));

            ConstructorException e = assertThrows(ConstructorException.class, () -> yaml.loadAs(yamlString, CarWrongParamCount.class));
            IllegalArgumentException illegalArgumentException = (IllegalArgumentException) e.getCause();
            assertThat(illegalArgumentException.getMessage(),
                    is("Method addUnmapped is expected to have two parameters, String and Object, but found: "//
                            + String.class.getName() + ", " + Object.class.getName() + ", " + int.class.getName()));
        }
    }

    /**
     * Tests that an exception is thrown if the annotated method does not have two parameters of the expected type.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testAnySetterWrongParamType() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("anySetter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(CarWrongParamType.class));

            ConstructorException e = assertThrows(ConstructorException.class, () -> yaml.loadAs(yamlString, CarWrongParamType.class));
            IllegalArgumentException illegalArgumentException = (IllegalArgumentException) e.getCause();
            assertThat(illegalArgumentException.getMessage(),
                    is("Method addUnmapped is expected to have two parameters, String and Object, but found: "//
                            + String.class.getName() + ", " + Number.class.getName()));
        }
    }

    /**
     * Tests that the annotated method is ignored if it is not public.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testAnySetterNotPublicNotFound() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("anySetter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(CarProtected.class));

            ConstructorException e = assertThrows(ConstructorException.class, () -> yaml.loadAs(yamlString, CarProtected.class));
            assertThat(e.getMessage(), containsString("Unable to find property 'brand'"));
        }
    }

    /**
     * Tests that unknown properties are added to a map. The unmapped properties may be complex objects.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testAnySetterComplex() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("anySetterComplex.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(Car.class));

            Car parseResult = yaml.loadAs(yamlString, Car.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.getBrand(), is("Ferrari"));
            assertThat(parseResult.getUnmapped().size(), is(1));
            assertThat(parseResult.getUnmapped().get("labels"), instanceOf(List.class));

            List<?> labels = (List<?>) parseResult.getUnmapped().get("labels");
            assertThat(labels, hasSize(3));
            assertThat(labels.stream().allMatch(listEntry -> listEntry instanceof Map), is(true));

            String[] labelNames = new String[] { "labelRed", "labelGreen", "labelBlue" };
            String[] colorNames = new String[] { "red", "green", "blue" };

            for (int i = 0; i < labels.size(); i++) {
                Map<?, ?> labelMap = (Map<?, ?>) labels.get(i);
                assertThat(labelMap.get("name"), is(labelNames[i]));
                assertThat(labelMap.get("color"), is(colorNames[i]));
            }

        }
    }

    /**
     * Tests that unknown properties are added to a map. The unmapped properties may be complex objects and even Pojos again.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testAnySetterComplexWithPojo() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("anySetterComplexWithPojo.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Car.class);
            constructor.addTypeDescription(new TypeDescription(Label.class, "!Label"));

            Yaml yaml = new Yaml(constructor);

            Car parseResult = yaml.loadAs(yamlString, Car.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.getBrand(), is("Ferrari"));
            assertThat(parseResult.getUnmapped().size(), is(1));
            assertThat(parseResult.getUnmapped().get("labels"), instanceOf(List.class));

            List<?> labels = (List<?>) parseResult.getUnmapped().get("labels");
            assertThat(labels, hasSize(3));
            assertThat(labels.toString(), labels.stream().allMatch(listEntry -> listEntry instanceof Label), is(true));

            String[] labelNames = new String[] { "labelRed", "labelGreen", "labelBlue" };
            String[] colorNames = new String[] { "red", "green", "blue" };

            for (int i = 0; i < labels.size(); i++) {
                Label label = (Label) labels.get(i);
                assertThat(label.getName(), is(labelNames[i]));
                assertThat(label.getColor(), is(colorNames[i]));
                if (i == labels.size() - 1) { // last entry
                    assertThat(label.unmapped.get("type"), is(1));
                } else {
                    assertThat(label.unmapped.keySet(), hasSize(0));
                }
            }

        }
    }

    /**
     * Tests that unknown properties are NOT added to a map if the flag {@link PropertyUtils#isSkipMissingProperties()} is set.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testAnySetterNotCalledIfSkipMissingPropertiesIsSet() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("anySetter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(Car.class) {
                {
                    getPropertyUtils().setSkipMissingProperties(true);
                }
            });

            Car parseResult = yaml.loadAs(yamlString, Car.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.getBrand(), is("Ferrari"));
            assertThat(parseResult.getEngine().getId(), is("my engine"));
            assertThat(parseResult.getUnmapped().size(), is(0));
            assertThat(parseResult.getEngine().getUnmapped().size(), is(0));
        }
    }

    /**
     * Tests that {@link YamlAnySetter} also works on a static method.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testAnySetterCalledForStaticMethod() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("anySetter.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(CarStatic.class));

            CarStatic parseResult = yaml.loadAs(yamlString, CarStatic.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.getBrand(), is("Ferrari"));
            assertThat(CarStatic.getUnmapped().size(), is(2));
            assertThat(CarStatic.getUnmapped().get("doors"), is(3));
            assertThat(CarStatic.getUnmapped().get("color"), is("red"));
            assertThat(parseResult.getEngine().getId(), is("my engine"));
            assertThat(parseResult.getEngine().getUnmapped().get("power"), is(800));
            assertThat(parseResult.getEngine().getUnmapped().get("builtOn"), is(new Date(0)));
        }
    }

    /**
     * Test class.
     */
    public static class Car {
        private String brand;
        private Map<String, Object> unmapped = new HashMap<>();
        private Engine engine = new Engine();

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public Map<String, Object> getUnmapped() {
            return unmapped;
        }

        @YamlAnySetter
        public void addUnmapped(String key, Object value) {
            unmapped.put(key, value);
        }

        public Engine getEngine() {
            return engine;
        }

        public void setEngine(Engine engine) {
            this.engine = engine;
        }
    }

    /**
     * Test class.
     */
    public static class CarStatic {
        private static Map<String, Object> unmapped = new HashMap<>();
        private String brand;
        private Engine engine = new Engine();

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public static Map<String, Object> getUnmapped() {
            return unmapped;
        }

        @YamlAnySetter
        public static void addUnmapped(String key, Object value) {
            unmapped.put(key, value);
        }

        public Engine getEngine() {
            return engine;
        }

        public void setEngine(Engine engine) {
            this.engine = engine;
        }
    }

    /**
     * Test class.
     */
    public static class Car2 {
        private String brand;
        private Map<String, Object> unmapped = new HashMap<>();

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        @YamlAnySetter
        public void addUnmapped(String key, Object value) {
            unmapped.put(key, value);
        }

        @YamlAnySetter
        public void addUnmapped2(String key, Object value) {
            unmapped.put(key, value);
        }

    }

    /**
     * Test class.
     */
    public static class CarWrongParamCount {
        private String brand;

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        @YamlAnySetter
        public void addUnmapped(String key, Object value, int x) {
        }
    }

    /**
     * Test class.
     */
    public static class CarWrongParamType {
        private String brand;

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        @YamlAnySetter
        public void addUnmapped(String key, Number value) {
        }
    }

    /**
     * Test class.
     */
    public static class SpecialCar extends Car {
    }

    /**
     * Test class.
     */
    public static class SpecialCarOwnSetter extends Car {
        private Map<String, Object> specialMap = new HashMap<>();

        public Map<String, Object> getSpecialMap() {
            return specialMap;
        }

        @YamlAnySetter
        public void addSpecial(String key, Object value) {
            specialMap.put(key, value);
        }
    }

    /**
     * Test class.
     */
    public interface UnmappedPropertyCollector {
        @YamlAnySetter
        void addByInterface(String key, Object value);
    }

    /**
     * Test class.
     */
    public static class CarWithInterface implements UnmappedPropertyCollector {
        private Map<String, Object> map = new HashMap<>();

        public Map<String, Object> getMap() {
            return map;
        }

        @Override
        public void addByInterface(String key, Object value) {
            map.put(key, value);
        }

    }

    /**
     * Test class.
     */
    public static class CarProtected {
        private Map<String, Object> map = new HashMap<>();

        public Map<String, Object> getMap() {
            return map;
        }

        @YamlAnySetter
        protected void add(String key, Object value) {
            map.put(key, value);
        }

        @YamlAnySetter
        private void add2(String key, Object value) {
            map.put(key, value);
        }

    }

    /**
     * Test class.
     */
    public static class Engine {
        private String id;
        private Map<String, Object> unmapped = new HashMap<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<String, Object> getUnmapped() {
            return unmapped;
        }

        @YamlAnySetter
        public void addUnmapped(String key, Object value) {
            unmapped.put(key, value);
        }
    }

    /**
     * Test class.
     */
    public static class Label {
        private String name;
        private String color;
        private Map<String, Object> unmapped = new HashMap<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        @YamlAnySetter
        public void addUnmapped(String key, Object value) {
            unmapped.put(key, value);
        }
    }

}
