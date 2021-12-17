package de.beosign.snakeyamlanno;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;
import de.beosign.snakeyamlanno.constructor.CustomConstructor;
import de.beosign.snakeyamlanno.property.Person;
import de.beosign.snakeyamlanno.util.NodeUtil;

/**
 * Basic test.
 * 
 * @author florian
 */
public class SnakeyamlTest {
    private static final Logger log = LoggerFactory.getLogger(SnakeyamlTest.class);

    @Test
    public void standardTest() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("standardYaml.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Constructor constructor = new Constructor(Person.class);
            Yaml yaml = new Yaml(constructor);

            Person parseResult = yaml.loadAs(yamlString, Person.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.getName(), is("Homer"));
        }
    }

    @Test
    public void parseWithDefaultKey() throws Exception {
        String yamlString = "entities:\n  entity1:\n    someProperty: abc"; // entity without "id" being set

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(EntitiesMap.class);
        constructor.registerCustomConstructor(Map.class, IdMapConstructor.class);
        Yaml yaml = new Yaml(constructor);

        EntitiesMap parseResult = yaml.loadAs(yamlString, EntitiesMap.class);
        log.debug("Parsed entites :\n{}", parseResult);

        assertThat(parseResult, notNullValue());

        Entity entity1 = parseResult.getEntities().get("entity1");
        assertThat(entity1, notNullValue());

        assertThat(entity1.getSomeProperty(), is("abc"));
        assertThat(entity1.getId(), is("entity1")); // entity contains "id"
    }

    public static class IdMapConstructor implements CustomConstructor<Map<String, Entity>> {

        @Override
        public Map<String, Entity> construct(Node node, Function<? super Node, ? extends Map<String, Entity>> defaultConstructor) throws YAMLException {
            Map<String, Entity> myEntityMap = new HashMap<>();
            Map<String, Node> keyValueNodeMap = NodeUtil.getPropertyToValueNodeMap((MappingNode) node);
            for (Map.Entry<String, Node> entry : keyValueNodeMap.entrySet()) {
                String id = entry.getKey();
                Node valueNode = entry.getValue();

                Entity entity = (Entity) defaultConstructor.apply(valueNode); // throws ClassCastException
                entity.setId(id);
                myEntityMap.put(id, entity);
            }
            return myEntityMap;
        }
    }

    protected static class EntitiesMap {
        private Map<String, Entity> entities;

        public Map<String, Entity> getEntities() {
            return entities;
        }

        public void setEntities(Map<String, Entity> entities) {
            this.entities = entities;
        }

        @Override
        public String toString() {
            return "EntitiesMap [entities=" + entities + "]";
        }

    }

    protected static class Entity {
        private String id;
        private String someProperty;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSomeProperty() {
            return someProperty;
        }

        public void setSomeProperty(String someProperty) {
            this.someProperty = someProperty;
        }

        @Override
        public String toString() {
            return "Entity [id=" + id + ", someProperty=" + someProperty + "]";
        }

    }

}
