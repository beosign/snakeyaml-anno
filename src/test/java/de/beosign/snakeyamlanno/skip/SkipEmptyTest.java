package de.beosign.snakeyamlanno.skip;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.hamcrest.core.IsNot;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import de.beosign.snakeyamlanno.property.Property;
import de.beosign.snakeyamlanno.representer.AnnotationAwareRepresenter;

/**
 * Tests the global "skipEmpty" setting in Representer.
 * 
 * @author florian
 */
public class SkipEmptyTest {

    @Test
    public void skipEmptyFalseTest() {
        SkipEmptyProps skipEmptyProps = new SkipEmptyProps();

        AnnotationAwareRepresenter representer = new AnnotationAwareRepresenter(false);
        Yaml yaml = new Yaml(new Constructor(), representer);

        String dumped = yaml.dump(skipEmptyProps);

        assertThat(dumped, IsNot.not(StringContains.containsString("notDumped")));
        assertThat(dumped, StringContains.containsString("Homer"));
        assertThat(dumped, StringContains.containsString("collection: []"));
        assertThat(dumped, StringContains.containsString("map: null"));
        assertThat(dumped, StringContains.containsString("emptyString:"));
        assertThat(dumped, StringContains.containsString("nullString: null"));
    }

    @Test
    public void skipEmptyTrueTest() {
        SkipEmptyProps skipEmptyProps = new SkipEmptyProps();

        AnnotationAwareRepresenter representer = new AnnotationAwareRepresenter();
        Yaml yaml = new Yaml(new Constructor(), representer);

        String dumped = yaml.dump(skipEmptyProps);

        assertThat(dumped, IsNot.not(StringContains.containsString("notDumped")));
        assertThat(dumped, StringContains.containsString("Homer"));
        assertThat(dumped, IsNot.not(StringContains.containsString("collection:")));
        assertThat(dumped, IsNot.not(StringContains.containsString("map:")));
        assertThat(dumped, IsNot.not(StringContains.containsString("emptyString:")));
        assertThat(dumped, IsNot.not(StringContains.containsString("nullString:")));
    }

    // CHECKSTYLE:OFF
    public static class SkipEmptyProps {
        private String skipDump = "notDumped";
        private Collection<String> collection = new ArrayList<>();
        private Map<String, Object> map;
        private String name = "Homer";
        private String emptyString = "";
        private String nullString;

        @Property(skipAtDump = true)
        public String getSkipDump() {
            return skipDump;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSkipDump(String skipDump) {
            this.skipDump = skipDump;
        }

        public Collection<String> getCollection() {
            return collection;
        }

        public void setCollection(Collection<String> collection) {
            this.collection = collection;
        }

        public Map<String, Object> getMap() {
            return map;
        }

        public void setMap(Map<String, Object> map) {
            this.map = map;
        }

        @Property(skipAtDump = false)
        public String getEmptyString() {
            return emptyString;
        }

        public void setEmptyString(String emptyString) {
            this.emptyString = emptyString;
        }

        public String getNullString() {
            return nullString;
        }

        public void setNullString(String nullString) {
            this.nullString = nullString;
        }
    }
}
