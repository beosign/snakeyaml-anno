package de.beosign.snakeyamlanno.property;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;

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

    public static class Person {
        private String name;
        private List<Integer> favoriteNumbers;

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

        @Override
        public String toString() {
            return "Person [name=" + name + ", favoriteNumbers=" + favoriteNumbers + "]";
        }

    }

    public static class Family {
        private Person father;
        private List<Person> children;

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

        @Override
        public String toString() {
            return "Family [father=" + father + ", children=" + children + "]";
        }

    }
}
