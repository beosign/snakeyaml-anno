package de.beosign.snakeyamlanno.util;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import de.beosign.snakeyamlanno.util.Person.Skill;

/**
 * Test the {@link NodeUtil} class.
 * 
 * @author florian
 */
public class NodeUtilUnitTest {
    private TestConstructor testConstructor;

    @Before
    public void before() {
        testConstructor = new TestConstructor(Person.class);
    }

    @Test
    public void testMappingNodeValueMap() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("person.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);

            Yaml yaml = new Yaml(testConstructor);
            yaml.load(yamlString);

            Assert.assertThat(testConstructor.personMap, notNullValue());
            Assert.assertThat(testConstructor.personMap.size(), is(5));
            Assert.assertThat(testConstructor.personMap.get("name").toString(), is("Homer"));
            Assert.assertThat(testConstructor.personMap.get("skill").toString(), is(Skill.PRO.name()));

            @SuppressWarnings("unchecked")
            Map<String, Object> firstPet = (Map<String, Object>) testConstructor.personMap.get("firstPet");
            Assert.assertThat(firstPet.get("name"), is("dog1"));
            Assert.assertThat(firstPet.get("age"), is("42"));

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> pets = (List<Map<String, Object>>) testConstructor.personMap.get("pets");

            Assert.assertThat(pets.size(), is(4));
            Assert.assertThat(pets.get(0).get("name"), is("dog2"));
            Assert.assertThat(pets.get(1).get("name"), is("cat1"));
            Assert.assertThat(pets.get(2).get("name"), is("dog4"));
            Assert.assertThat(pets.get(3).get("name"), is("dog1"));
            Assert.assertThat(pets.get(3).get("age"), is("42")); // it is the firstPet

        }
    }

    @Test
    public void testMappingNodeMap() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("person.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);

            Yaml yaml = new Yaml(testConstructor);
            yaml.load(yamlString);

            Assert.assertThat(testConstructor.personKeyToNodeMap, notNullValue());
            Assert.assertThat(testConstructor.personKeyToNodeMap.get("firstPet"), instanceOf(MappingNode.class));
            Assert.assertThat(testConstructor.personKeyToNodeMap.get("pets"), instanceOf(SequenceNode.class));
            Assert.assertThat(testConstructor.personKeyToNodeMap.get("name"), instanceOf(ScalarNode.class));

            MappingNode firstPetNode = (MappingNode) testConstructor.personKeyToNodeMap.get("firstPet");
            Assert.assertThat(NodeUtil.getValue(firstPetNode), instanceOf(Map.class));

            @SuppressWarnings("unchecked")
            Map<String, Object> firstPet = (Map<String, Object>) NodeUtil.getValue(firstPetNode);
            Assert.assertThat(firstPet.get("name"), is("dog1"));
            Assert.assertThat(firstPet.get("age"), is("42"));

            Assert.assertThat(NodeUtil.getValue(testConstructor.personKeyToNodeMap.get("name")), is("Homer"));

        }
    }

    @Test
    public void testPersonNodeValue() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("person.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);

            Yaml yaml = new Yaml(testConstructor);
            yaml.load(yamlString);

            Assert.assertThat(testConstructor.personNodeValue, notNullValue());
            Assert.assertThat(testConstructor.personNodeValue.get("name"), is("Homer"));
            Assert.assertThat(testConstructor.personNodeValue.get("firstPet"), instanceOf(Map.class));
            Assert.assertThat(testConstructor.personNodeValue.get("pets"), instanceOf(List.class));

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> pets = (List<Map<String, Object>>) testConstructor.personNodeValue.get("pets");
            Assert.assertThat(pets.size(), is(4));
            Assert.assertThat(pets.get(0).get("name"), is("dog2"));
            Assert.assertThat(pets.get(1).get("name"), is("cat1"));
            Assert.assertThat(pets.get(2).get("name"), is("dog4"));
            Assert.assertThat(pets.get(3).get("name"), is("dog1"));
            Assert.assertThat(pets.get(3).get("age"), is("42"));

            @SuppressWarnings("unchecked")
            Map<String, Object> firstPet = (Map<String, Object>) testConstructor.personNodeValue.get("firstPet");
            Assert.assertThat(firstPet.get("name"), is("dog1"));
            Assert.assertThat(firstPet.get("age"), is("42"));

        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownNodeType() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("person.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            Yaml yaml = new Yaml(testConstructor);
            yaml.load(yamlString);

            NodeUtil.getValue(new AnchorNode(testConstructor.personKeyToNodeMap.get("name")));
        }
    }

    // CHECKSTYLE:OFF - test classes
    private static class TestConstructor extends Constructor {
        private Map<String, Object> personMap;
        private Map<String, Node> personKeyToNodeMap;
        private Map<String, Object> personNodeValue;

        public TestConstructor(Class<? extends Object> theRoot) {
            super(theRoot);
            yamlClassConstructors.put(NodeId.mapping, new TestMappingConstructor());
        }

        protected class TestMappingConstructor extends ConstructMapping {
            @SuppressWarnings("unchecked")
            @Override
            protected Object constructJavaBean2ndStep(MappingNode node, Object object) {
                if (object instanceof Person) {
                    personMap = NodeUtil.getPropertyToValueMap(node);
                    personKeyToNodeMap = NodeUtil.getPropertyToValueNodeMap(node);

                    personNodeValue = (Map<String, Object>) NodeUtil.getValue(node);
                }

                return super.constructJavaBean2ndStep(node, object);
            }
        }

    }

}
