package de.beosign.snakeyamlanno.skip;

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
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;

import de.beosign.snakeyamlanno.annotation.Property;
import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;
import de.beosign.snakeyamlanno.representer.AnnotationAwareRepresenter;

/**
 * Tests that properties can be skipped during loading and dumping.
 * 
 * @author florian
 */
public class SkipPropsTest {
    private static final Logger log = LoggerFactory.getLogger(SkipPropsTest.class);

    /**
     * Tests {@link Property#skipAtLoad()}.
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
            Assert.assertThat(props.getSkipLoad(), Is.is("notLoaded"));
            Assert.assertThat(props.getSkipDump(), Is.is("mustNotBeOutput"));
            Assert.assertThat(props.getName(), Is.is("name1"));
        }
    }

    /**
     * Tests {@link Property#skipAtDump()}.
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

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter());
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        Assert.assertThat(dumped, IsNot.not(StringContains.containsString(notDumped)));
        Assert.assertThat(dumped, StringContains.containsString("name1"));
        Assert.assertThat(dumped, StringContains.containsString("skipLoad1"));

    }

    /**
     * Tests {@link Property#skipAtDump()} overrides {@link Property#skipAtDumpIf()}.
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

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter());
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        Assert.assertThat(dumped, IsNot.not(StringContains.containsString(notDumped)));
        Assert.assertThat(dumped, StringContains.containsString("name1"));

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

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter());
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        Assert.assertThat(dumped, IsNot.not(StringContains.containsString("skipDumpIfNull")));
        Assert.assertThat(dumped, StringContains.containsString("name1"));

        skipProps.setSkipDumpIfNull("MUSTBEDUMPED");
        dumped = yaml.dumpAsMap(skipProps);

        Assert.assertThat(dumped, StringContains.containsString("MUSTBEDUMPED"));
        Assert.assertThat(dumped, StringContains.containsString("name1"));
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

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter());
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        Assert.assertThat(dumped, IsNot.not(StringContains.containsString("skipDumpIfEmpty")));
        Assert.assertThat(dumped, StringContains.containsString("name1"));

        skipProps.setSkipDumpIfEmpty("MUSTBEDUMPED");
        dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        Assert.assertThat(dumped, StringContains.containsString("MUSTBEDUMPED"));
        Assert.assertThat(dumped, StringContains.containsString("name1"));
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

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter());
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        Assert.assertThat(dumped, IsNot.not(StringContains.containsString("skipDumpIfEmptyCollection")));
        Assert.assertThat(dumped, StringContains.containsString("name1"));

        skipProps.setSkipDumpIfEmptyCollection(Arrays.asList("MUSTBEDUMPED"));
        dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        Assert.assertThat(dumped, StringContains.containsString("MUSTBEDUMPED"));
        Assert.assertThat(dumped, StringContains.containsString("name1"));
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

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter());
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        Assert.assertThat(dumped, IsNot.not(StringContains.containsString("skipDumpIfEmptyMap")));
        Assert.assertThat(dumped, StringContains.containsString("name1"));

        Map<String, Object> map = new HashMap<>();
        map.put("1", "MUSTBEDUMPED");
        skipProps.setSkipDumpIfEmptyMap(map);
        dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        Assert.assertThat(dumped, StringContains.containsString("MUSTBEDUMPED"));
        Assert.assertThat(dumped, StringContains.containsString("1"));
        Assert.assertThat(dumped, StringContains.containsString("name1"));
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

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter());
        String dumped = yaml.dumpAsMap(skipProps);

        System.out.println(dumped);

        Assert.assertThat(dumped, StringContains.containsString("name1"));
        Assert.assertThat(dumped, StringContains.containsString("skipDumpOtherType"));
    }

    /**
     * Verifies that an exception is thrown if a predicate class cannot be instantiated.
     * 
     * @throws Exception yaml exception
     */
    @Test(expected = YAMLException.class)
    public void checkPropertyDumpedIfBadClass() throws Exception {
        SkipPropsBadClass skipProps = new SkipPropsBadClass();

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter());
        yaml.dumpAsMap(skipProps);
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

        @Property(skipAtLoad = true)
        public String getSkipLoad() {
            return skipLoad;
        }

        public void setSkipLoad(String skipLoad) {
            this.skipLoad = skipLoad;
        }

        @Property(skipAtDump = true)
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

        @Property(skipAtDump = true, skipAtDumpIf = SkipNever.class)
        public String getSkipDumpOverridingIf() {
            return skipDumpOverridingIf;
        }

        public void setSkipDumpOverridingIf(String skipDumpOverridingIf) {
            this.skipDumpOverridingIf = skipDumpOverridingIf;
        }

        @Property(skipAtDumpIf = SkipIfEmpty.class)
        public String getSkipDumpIfEmpty() {
            return skipDumpIfEmpty;
        }

        public void setSkipDumpIfEmpty(String skipDumpIfEmpty) {
            this.skipDumpIfEmpty = skipDumpIfEmpty;
        }

        @Property(skipAtDumpIf = SkipIfEmpty.class)
        public Collection<String> getSkipDumpIfEmptyCollection() {
            return skipDumpIfEmptyCollection;
        }

        public void setSkipDumpIfEmptyCollection(Collection<String> skipDumpIfEmptyCollection) {
            this.skipDumpIfEmptyCollection = skipDumpIfEmptyCollection;
        }

        @Property(skipAtDumpIf = SkipIfEmpty.class)
        public Map<String, Object> getSkipDumpIfEmptyMap() {
            return skipDumpIfEmptyMap;
        }

        public void setSkipDumpIfEmptyMap(Map<String, Object> skipDumpIfEmptyMap) {
            this.skipDumpIfEmptyMap = skipDumpIfEmptyMap;
        }

        @Property(skipAtDumpIf = SkipIfNull.class)
        public String getSkipDumpIfNull() {
            return skipDumpIfNull;
        }

        public void setSkipDumpIfNull(String skipDumpIfNull) {
            this.skipDumpIfNull = skipDumpIfNull;
        }

        @Property(skipAtDumpIf = SkipIfEmpty.class)
        public Date getSkipDumpOtherType() {
            return skipDumpOtherType;
        }

        public void setSkipDumpOtherType(Date skipDumpOtherType) {
            this.skipDumpOtherType = skipDumpOtherType;
        }

    }

    public static class SkipPropsBadClass {
        private String prop;

        @Property(skipAtDumpIf = PrivatePredicate.class)
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

}
