package de.beosign.snakeyamlanno.property;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import de.beosign.snakeyamlanno.representer.AnnotationAwareRepresenter;

/**
 * Tests the "Any-Getter" functionality.
 * 
 * @author florian
 */
public class AnyGetterTest {

    /**
     * Tests if properties from the "unmapped" map are flattened.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void testAnyGetter() throws Exception {
        Car car = new Car();
        car.setBrand("Ferrari");
        car.addUnmapped("doors", 3);
        car.addUnmapped("color", "red");
        car.addUnmapped("keyValue", new KeyValue("key1", new Date(0))); // test pojo in unmapped
        car.getEngine().setId("Id E");
        car.getEngine().setPower(800);

        car.getEngine().addUnmapped("e1", 1);
        car.getEngine().addUnmapped("e2", new Date(0));
        car.getEngine().addUnmapped("power", "Power");

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter());

        String dumped = yaml.dumpAsMap(car);

        // reload as nested Map<String, Object> to assert everything
        Map<String, Object> rootMap = new Yaml().load(dumped);

        assertThat(rootMap.keySet(), hasSize(5));
        assertThat(rootMap.get("doors"), is(3)); // flattened
        assertThat(rootMap.get("color"), is("red")); // flattened
        assertThat(rootMap.get("keyValue"), instanceOf(KeyValue.class));

        KeyValue keyValue = (KeyValue) rootMap.get("keyValue");
        assertThat(keyValue.key, is("key1"));
        assertThat(keyValue.value, is(new Date(0)));

        Map<?, ?> engineMap = (Map<?, ?>) rootMap.get("engine");
        assertThat(engineMap, not(nullValue()));
        assertThat(engineMap.get("e1"), is(1)); // flattened
        assertThat(engineMap.get("e2"), is(new Date(0))); // flattened
        assertThat(engineMap.get("id"), is("Id E"));
        assertThat(engineMap.get("power"), is("Power")); // overridden

    }

    @Test
    public void testAnyGetterOnScalarType() throws Exception {
        ScalarTypeAnnotated scalarTypeAnnotated = new ScalarTypeAnnotated();

        YAMLException e = assertThrows(YAMLException.class, () -> new Yaml(new AnnotationAwareRepresenter()).dump(scalarTypeAnnotated));
        assertThat(e.getMessage(), containsString(YamlAnyGetter.class.getSimpleName()));
        assertThat(e.getMessage(), containsString("placed on properties of type " + Map.class.getName()));
    }

    @Test
    public void testAnyGetterOnListType() throws Exception {
        ListTypeAnnotated listTypeAnnotated = new ListTypeAnnotated();

        YAMLException e = assertThrows(YAMLException.class, () -> new Yaml(new AnnotationAwareRepresenter()).dump(listTypeAnnotated));
        assertThat(e.getMessage(), containsString(YamlAnyGetter.class.getSimpleName()));
        assertThat(e.getMessage(), containsString("placed on properties of type " + Map.class.getName()));
    }

    /**
     * May be allowed in the future.
     */
    @Test
    public void testAnyGetterOnPojoType() {
        PojoTypeAnnotated pojoTypeAnnotated = new PojoTypeAnnotated();

        YAMLException e = assertThrows(YAMLException.class, () -> new Yaml(new AnnotationAwareRepresenter()).dump(pojoTypeAnnotated));
        assertThat(e.getMessage(), containsString(YamlAnyGetter.class.getSimpleName()));
        assertThat(e.getMessage(), containsString("placed on properties of type " + Map.class.getName()));
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

        @YamlAnyGetter
        public Map<String, Object> getUnmapped() {
            return unmapped;
        }

        public void setUnmapped(Map<String, Object> unmapped) {
            this.unmapped = unmapped;
        }

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
    public static class Engine {
        private String id;
        private int power;
        private Map<String, Object> unmapped = new HashMap<>();

        @YamlAnyGetter
        public Map<String, Object> getUnmapped() {
            return unmapped;
        }

        public void setUnmapped(Map<String, Object> unmapped) {
            this.unmapped = unmapped;
        }

        public void addUnmapped(String key, Object value) {
            unmapped.put(key, value);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }
    }

    /** Test class. */
    protected static final class KeyValue {
        private String key;
        private Object value;

        protected KeyValue() {
        }

        protected KeyValue(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    protected static class ScalarTypeAnnotated {

        @YamlAnyGetter
        public int getId() {
            return 1;
        }

        public void setId(int id) {
        }
    }

    protected static class ListTypeAnnotated {

        @YamlAnyGetter
        public List<String> getIds() {
            return Arrays.asList("one", "two");
        }

        public void setIds(List<String> ids) {
        }
    }

    protected static class PojoTypeAnnotated {

        @YamlAnyGetter
        public Car getCar() {
            return new Car();
        }

        public void setCar(Car car) {
        }
    }
}
