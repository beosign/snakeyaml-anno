package de.beosign.snakeyamlanno.property;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

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

    // CHECKSTYLE:OFF
    public static class SkipProps {
        private String skipLoad = "notLoaded";
        private String skipDump = "notDumped";
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

    }

}
