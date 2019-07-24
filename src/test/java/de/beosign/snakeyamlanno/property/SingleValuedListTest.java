package de.beosign.snakeyamlanno.property;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;
import de.beosign.snakeyamlanno.constructor.YamlConstructBy;
import de.beosign.snakeyamlanno.constructor.CustomConstructor;
import de.beosign.snakeyamlanno.util.NodeUtil;

/**
 * Tests the simplified parsing functionality in case a list property consists of only one entry.
 * 
 * @author florian
 */
public class SingleValuedListTest {
    private static final Logger log = LoggerFactory.getLogger(SingleValuedListTest.class);

    @Test
    public void testNormalList() {
        String yamlString = "";
        yamlString += "father:\n";
        yamlString += "  name: homer\n";
        yamlString += "  favoriteNumbers: [42, 7]\n";
        yamlString += "children:\n";
        yamlString += "- name: bart\n";
        yamlString += "- name: lisa\n";

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Family.class);
        Yaml yaml = new Yaml(constructor);

        Family parseResult = yaml.load(yamlString);
        log.debug("Parsed YAML file:\n{}", parseResult);

        assertThat(parseResult, notNullValue());
        assertThat(parseResult.getFather().getName(), is("homer"));
        assertThat(parseResult.getChildren().size(), is(2));
        assertThat(parseResult.getChildren().get(0).getName(), is("bart"));
        assertThat(parseResult.getChildren().get(1).getName(), is("lisa"));
        assertThat(parseResult.getFather().getFavoriteNumbers().get(0), is(42));
        assertThat(parseResult.getFather().getFavoriteNumbers().get(1), is(7));
    }

    @Test
    public void testSingleList() {
        String yamlString = "";
        yamlString += "father:\n";
        yamlString += "  name: homer\n";
        yamlString += "  favoriteNumbers: 42\n";
        yamlString += "children: {name: bart}\n";

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Family.class);
        Yaml yaml = new Yaml(constructor);

        Family parseResult = yaml.load(yamlString);
        log.debug("Parsed YAML file:\n{}", parseResult);

        assertThat(parseResult, notNullValue());
        assertThat(parseResult.getFather().getName(), is("homer"));
        assertThat(parseResult.getChildren().size(), is(1));
        assertThat(parseResult.getChildren().get(0).getName(), is("bart"));
    }

    @Test
    public void testSingleListWithNullItem() {
        String yamlString = "";
        yamlString += "pets: 42\n";

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Family.class);

        Yaml yaml = new Yaml(constructor);

        Family parseResult = yaml.load(yamlString);
        log.debug("Parsed YAML file:\n{}", parseResult);

        assertThat(parseResult, notNullValue());
        assertThat(parseResult.getPets(), IsNull.nullValue());
    }

    @Test
    public void testSingleListWithCustomConstructor() {
        String yamlString = "";
        yamlString += "father:\n";
        yamlString += "  name: homer\n";
        yamlString += "  favoriteColors: bLuE\n";

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Family.class);
        Yaml yaml = new Yaml(constructor);

        Family parseResult = yaml.load(yamlString);
        log.debug("Parsed YAML file:\n{}", parseResult);

        assertThat(parseResult, notNullValue());
        assertThat(parseResult.getFather().getName(), is("homer"));
        assertThat(parseResult.getFather().getFavoriteColors().size(), is(1));
        assertThat(parseResult.getFather().getFavoriteColors().get(0), is(Color.BLUE));
    }

    @Test
    public void testMixedLists() {
        String yamlString = "";
        yamlString += "father:\n";
        yamlString += "  name: homer\n";
        yamlString += "  favoriteNumbers: 42\n";
        yamlString += "children:\n";
        yamlString += "- name: bart\n";
        yamlString += "  favoriteNumbers: 7\n";
        yamlString += "- name: lisa\n";
        yamlString += "  favoriteNumbers:\n";
        yamlString += "  - 1\n";
        yamlString += "  - 2\n";

        AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Family.class);
        Yaml yaml = new Yaml(constructor);

        Family parseResult = yaml.load(yamlString);
        log.debug("Parsed YAML file:\n{}", parseResult);

        assertThat(parseResult, notNullValue());
        assertThat(parseResult.getFather().getName(), is("homer"));
        assertThat(parseResult.getChildren().size(), is(2));
        assertThat(parseResult.getChildren().get(0).getName(), is("bart"));
        assertThat(parseResult.getChildren().get(0).getFavoriteNumbers().get(0), is(7));
        assertThat(parseResult.getChildren().get(1).getName(), is("lisa"));
        assertThat(parseResult.getChildren().get(1).getFavoriteNumbers().get(0), is(1));
        assertThat(parseResult.getChildren().get(1).getFavoriteNumbers().get(1), is(2));
    }

    /**
     * Test pojo.
     * 
     * @author florian
     */
    public static class Person {
        private String name;
        private List<Integer> favoriteNumbers;
        private List<Color> favoriteColors;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Integer> getFavoriteNumbers() {
            return favoriteNumbers;
        }

        public void setFavoriteNumbers(List<Integer> favoriteNumbers) {
            this.favoriteNumbers = favoriteNumbers;
        }

        public List<Color> getFavoriteColors() {
            return favoriteColors;
        }

        public void setFavoriteColors(List<Color> favoriteColors) {
            this.favoriteColors = favoriteColors;
        }

        @Override
        public String toString() {
            return "Person [name=" + name + ", favoriteNumbers=" + favoriteNumbers + ", favoriteColors=" + favoriteColors + "]";
        }

    }

    /**
     * Test pojo.
     * 
     * @author florian
     */
    public static class Family {
        private Person father;
        private List<Person> children;
        private List<Pet> pets;

        public Person getFather() {
            return father;
        }

        public void setFather(Person father) {
            this.father = father;
        }

        public List<Person> getChildren() {
            return children;
        }

        public void setChildren(List<Person> children) {
            this.children = children;
        }

        public List<Pet> getPets() {
            return pets;
        }

        public void setPets(List<Pet> pets) {
            this.pets = pets;
        }

        @Override
        public String toString() {
            return "Family [father=" + father + ", children=" + children + "]";
        }

    }

    /**
     * Always null.
     * 
     * @author florian
     */
    @YamlConstructBy(NullPersonCustomConstructor.class)
    public static class Pet {

    }

    /**
     * Test enum.
     * 
     * @author florian
     */
    @YamlConstructBy(ColorCustomConstructor.class)
    public enum Color {
        BLUE, RED;
    }

    /**
     * Test constructor.
     * 
     * @author florian
     */
    public static class ColorCustomConstructor implements CustomConstructor<Color> {

        @Override
        public Color construct(Node node, Function<? super Node, ? extends Color> defaultConstructor) throws YAMLException {
            return Arrays.stream(Color.values()).filter(color -> color.name().equalsIgnoreCase(NodeUtil.getValue(node).toString())).findFirst().orElseThrow(() -> {
                return new IllegalArgumentException("No color enum found for " + NodeUtil.getValue(node));
            });
        }

    }

    /**
     * Test constructor.
     * 
     * @author florian
     */
    public static class NullPersonCustomConstructor implements CustomConstructor<Pet> {

        @Override
        public Pet construct(Node node, Function<? super Node, ? extends Pet> defaultConstructor) throws YAMLException {
            return null;
        }

    }
}
