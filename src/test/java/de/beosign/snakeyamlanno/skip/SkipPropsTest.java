package de.beosign.snakeyamlanno.skip;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;
import de.beosign.snakeyamlanno.property.YamlProperty;
import de.beosign.snakeyamlanno.representer.AnnotationAwareRepresenter;

/**
 * Tests that properties can be skipped during loading and dumping.
 * 
 * @author florian
 */
public class SkipPropsTest {
    private static final Logger log = LoggerFactory.getLogger(SkipPropsTest.class);

    /**
     * Tests {@link YamlProperty#skipAtLoad()}.
     * 
     * @throws Exception on exception
     */
    @Test
    public void checkPropertyNotLoaded() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("skipProps.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(SkipProps.class));

            SkipProps props = yaml.loadAs(yamlString, SkipProps.class);
            assertThat(props.getSkipLoad(), Is.is("notLoaded"));
            assertThat(props.getSkipDump(), Is.is("mustNotBeOutput"));
            assertThat(props.getName(), Is.is("name1"));
        }
    }

    /**
     * Tests {@link YamlProperty#skipAtLoad()}.
     * 
     * @throws Exception on exception
     */
    @Test
    public void checkPropertySkipLoadedAndDumped() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("skipDumpLoad.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(SkipDumpLoad.class));

            SkipDumpLoad props = yaml.loadAs(yamlString, SkipDumpLoad.class);
            assertThat(props.getLoadedAndDumped(), Is.is("loadedAndDumped"));
            assertThat(props.getNotLoadedButDumped(), IsNull.nullValue());
            assertThat(props.getNotDumpedButLoaded(), Is.is("notDumpedButLoaded"));

            yaml = new Yaml(new AnnotationAwareRepresenter());
            props.setNotLoadedButDumped("notLoadedButDumped");
            String dumped = yaml.dumpAsMap(props);
            System.out.println(yaml.dumpAsMap(props));

            assertThat(dumped, StringContains.containsString("loadedAndDumped: loadedAndDumped"));
            assertThat(dumped, StringContains.containsString("notLoadedButDumped: notLoadedButDumped"));
            assertThat(dumped, IsNot.not(StringContains.containsString("notDumped")));
        }
    }

    /**
     * Tests {@link YamlProperty#skipAtDump()}.
     * 
     * @throws Exception on exception
     */
    @Test
    public void checkPropertyNotDumped() throws Exception {
        String notDumped = "mustNotBeDumped";
        SkipProps skipProps = new SkipProps();
        skipProps.setSkipDump(notDumped);
        skipProps.setName("name1");
        skipProps.setSkipLoad("skipLoad1");

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter(false));
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        assertThat(dumped, IsNot.not(StringContains.containsString(notDumped)));
        assertThat(dumped, StringContains.containsString("name1"));
        assertThat(dumped, StringContains.containsString("skipLoad1"));

    }

    /**
     * Tests {@link YamlProperty#skipAtDump()} overrides {@link YamlProperty#skipAtDumpIf()}.
     * 
     * @throws Exception on exception
     */
    @Test
    public void checkPropertyNotDumpedOverringPredicate() throws Exception {
        String notDumped = "mustNotBeDumped";
        SkipProps skipProps = new SkipProps();
        skipProps.setSkipDump(notDumped);
        skipProps.setName("name1");
        skipProps.setSkipDumpOverridingIf(notDumped);

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter(false));
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        assertThat(dumped, IsNot.not(StringContains.containsString(notDumped)));
        assertThat(dumped, StringContains.containsString("name1"));

    }

    /**
     * Verifies that a property is not dumped if its value is <code>null</code> and annotated appropriately.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void checkPropertyNotDumpedIfNull() throws Exception {
        String notDumped = "mustNotBeDumped";
        SkipProps skipProps = new SkipProps();
        skipProps.setSkipDump(notDumped);
        skipProps.setName("name1");

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter(false));
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        assertThat(dumped, IsNot.not(StringContains.containsString("skipDumpIfNull")));
        assertThat(dumped, StringContains.containsString("name1"));

        skipProps.setSkipDumpIfNull("MUSTBEDUMPED");
        dumped = yaml.dumpAsMap(skipProps);

        assertThat(dumped, StringContains.containsString("MUSTBEDUMPED"));
        assertThat(dumped, StringContains.containsString("name1"));
    }

    /**
     * Verifies that a property is not dumped if its value is an empty String and annotated appropriately.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void checkPropertyNotDumpedIfEmpty() throws Exception {
        SkipProps skipProps = new SkipProps();
        skipProps.setSkipDumpIfEmpty("");
        skipProps.setName("name1");

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter(false));
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        assertThat(dumped, IsNot.not(StringContains.containsString("skipDumpIfEmpty")));
        assertThat(dumped, StringContains.containsString("name1"));

        skipProps.setSkipDumpIfEmpty("MUSTBEDUMPED");
        dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        assertThat(dumped, StringContains.containsString("MUSTBEDUMPED"));
        assertThat(dumped, StringContains.containsString("name1"));
    }

    /**
     * Verifies that a property is not dumped if its value is an empty collection and annotated appropriately.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void checkPropertyNotDumpedIfCollectionEmpty() throws Exception {
        String notDumped = "mustNotBeDumped";
        SkipProps skipProps = new SkipProps();
        skipProps.setSkipDump(notDumped);
        skipProps.setName("name1");

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter(false));
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        assertThat(dumped, IsNot.not(StringContains.containsString("skipDumpIfEmptyCollection")));
        assertThat(dumped, StringContains.containsString("name1"));

        skipProps.setSkipDumpIfEmptyCollection(Arrays.asList("MUSTBEDUMPED"));
        dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        assertThat(dumped, StringContains.containsString("MUSTBEDUMPED"));
        assertThat(dumped, StringContains.containsString("name1"));
    }

    /**
     * Verifies that a property is not dumped if its value is an empty map and annotated appropriately.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void checkPropertyNotDumpedIfMapEmpty() throws Exception {
        String notDumped = "mustNotBeDumped";
        SkipProps skipProps = new SkipProps();
        skipProps.setSkipDump(notDumped);
        skipProps.setName("name1");

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter(false));
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        assertThat(dumped, IsNot.not(StringContains.containsString("skipDumpIfEmptyMap")));
        assertThat(dumped, StringContains.containsString("name1"));

        Map<String, Object> map = new HashMap<>();
        map.put("1", "MUSTBEDUMPED");
        skipProps.setSkipDumpIfEmptyMap(map);
        dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        assertThat(dumped, StringContains.containsString("MUSTBEDUMPED"));
        assertThat(dumped, StringContains.containsString("1"));
        assertThat(dumped, StringContains.containsString("name1"));
    }

    /**
     * Verifies that a property is dumped if it is not empty and of type other than List, Map or String.
     * 
     * @throws Exception on any exception
     */
    @Test
    public void checkPropertyDumpedIfOtherTypeNotEmpty() throws Exception {
        String notDumped = "mustNotBeDumped";
        SkipProps skipProps = new SkipProps();
        skipProps.setSkipDump(notDumped);
        skipProps.setName("name1");

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter(false));
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        assertThat(dumped, StringContains.containsString("name1"));
        assertThat(dumped, StringContains.containsString("skipDumpOtherType"));
    }

    /**
     * Verifies that an exception is thrown if a predicate class cannot be instantiated.
     * 
     * @throws Exception yaml exception
     */
    @Test
    public void checkPropertyDumpedIfBadClass() throws Exception {
        SkipPropsBadClass skipProps = new SkipPropsBadClass();

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter(false));

        assertThrows(YAMLException.class, () -> yaml.dumpAsMap(skipProps));
    }

    // CHECKSTYLE:OFF
    public static class SkipProps {
        private String skipLoad = "notLoaded";
        private String skipDump = "notDumped";
        private String skipDumpOverridingIf;
        private String skipDumpIfNull = null;
        private String skipDumpIfEmpty = "";
        private Date skipDumpOtherType = new Date(0);
        private Collection<String> skipDumpIfEmptyCollection = new ArrayList<>();
        private Map<String, Object> skipDumpIfEmptyMap = new HashMap<>();
        private String name;

        @YamlProperty(skipAtLoad = true)
        public String getSkipLoad() {
            return skipLoad;
        }

        public void setSkipLoad(String skipLoad) {
            this.skipLoad = skipLoad;
        }

        @YamlProperty(skipAtDump = true)
        public String getSkipDump() {
            return skipDump;
        }

        public void setSkipDump(String skipDump) {
            this.skipDump = skipDump;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @YamlProperty(skipAtDump = true, skipAtDumpIf = SkipNever.class)
        public String getSkipDumpOverridingIf() {
            return skipDumpOverridingIf;
        }

        public void setSkipDumpOverridingIf(String skipDumpOverridingIf) {
            this.skipDumpOverridingIf = skipDumpOverridingIf;
        }

        @YamlProperty(skipAtDumpIf = SkipIfEmpty.class)
        public String getSkipDumpIfEmpty() {
            return skipDumpIfEmpty;
        }

        public void setSkipDumpIfEmpty(String skipDumpIfEmpty) {
            this.skipDumpIfEmpty = skipDumpIfEmpty;
        }

        @YamlProperty(skipAtDumpIf = SkipIfEmpty.class)
        public Collection<String> getSkipDumpIfEmptyCollection() {
            return skipDumpIfEmptyCollection;
        }

        public void setSkipDumpIfEmptyCollection(Collection<String> skipDumpIfEmptyCollection) {
            this.skipDumpIfEmptyCollection = skipDumpIfEmptyCollection;
        }

        @YamlProperty(skipAtDumpIf = SkipIfEmpty.class)
        public Map<String, Object> getSkipDumpIfEmptyMap() {
            return skipDumpIfEmptyMap;
        }

        public void setSkipDumpIfEmptyMap(Map<String, Object> skipDumpIfEmptyMap) {
            this.skipDumpIfEmptyMap = skipDumpIfEmptyMap;
        }

        @YamlProperty(skipAtDumpIf = SkipIfNull.class)
        public String getSkipDumpIfNull() {
            return skipDumpIfNull;
        }

        public void setSkipDumpIfNull(String skipDumpIfNull) {
            this.skipDumpIfNull = skipDumpIfNull;
        }

        @YamlProperty(skipAtDumpIf = SkipIfEmpty.class)
        public Date getSkipDumpOtherType() {
            return skipDumpOtherType;
        }

        public void setSkipDumpOtherType(Date skipDumpOtherType) {
            this.skipDumpOtherType = skipDumpOtherType;
        }

    }

    public static class SkipPropsBadClass {
        private String prop;

        @YamlProperty(skipAtDumpIf = PrivatePredicate.class)
        public String getProp() {
            return prop;
        }

        public void setProp(String prop) {
            this.prop = prop;
        }

    }

    private static interface PrivatePredicate extends SkipAtDumpPredicate {

    }

    public static class SkipNever implements SkipAtDumpPredicate {

        @Override
        public boolean skip(Object javaBean, org.yaml.snakeyaml.introspector.Property property, Object propertyValue, Tag customTag) {
            return false;
        }
    }

    public static class SkipDumpLoad {
        private String notDumpedButLoaded;
        private String notLoadedButDumped;
        private String loadedAndDumped;

        @YamlProperty(skipAtDump = true)
        public String getNotDumpedButLoaded() {
            return notDumpedButLoaded;
        }

        public void setNotDumpedButLoaded(String notDumpedButLoaded) {
            this.notDumpedButLoaded = notDumpedButLoaded;
        }

        @YamlProperty(skipAtLoad = true)
        public String getNotLoadedButDumped() {
            return notLoadedButDumped;
        }

        public void setNotLoadedButDumped(String notLoadedButDumped) {
            this.notLoadedButDumped = notLoadedButDumped;
        }

        public String getLoadedAndDumped() {
            return loadedAndDumped;
        }

        public void setLoadedAndDumped(String loadedAndDumped) {
            this.loadedAndDumped = loadedAndDumped;
        }

    }

}
