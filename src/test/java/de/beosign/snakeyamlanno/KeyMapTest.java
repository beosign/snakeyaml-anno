package de.beosign.snakeyamlanno;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.collection.IsEmptyCollection.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.core.IsNull.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;
import de.beosign.snakeyamlanno.exception.AliasedYAMLException;

/**
 * Tests the key mapping functionality.
 * 
 * @author florian
 */
public class KeyMapTest {
    private static final Logger log = LoggerFactory.getLogger(KeyMapTest.class);

    /**
     * Parses a YAML that consists of a single custom object. The custom object defines
     * an alias on a field and on a getter.
     * 
     * @throws IOException on {@link IOException}
     */
    @Test
    public void parseSingleObjectWithAliasValidFile() throws IOException {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("stellar1.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(List.class));

            @SuppressWarnings("unchecked")
            List<StellarObject> parseResult = (List<StellarObject>) yaml.load(yamlString);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult, not(empty()));
            assertThat(parseResult.get(0).getAbsoluteMag(), is(4.8));
            assertThat(parseResult.get(0).getName(), is("Sun"));

        }
    }

    /**
     * Parses a YAML that consists of a single custom object with two entries, where one entry uses the new mapped key and the other one uses the old key (which
     * is forbidden). Test on field.
     * 
     * @throws IOException on {@link IOException}
     */
    @Test
    public void parseSingleObjectWithAliasAndUnaliasedFieldInvalidFile() throws IOException {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("stellar2.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(List.class));

            try {
                yaml.load(yamlString);
                Assert.fail("Expected exception");
            } catch (YAMLException e) {
                log.debug("Caught expected exception {}", e.getClass().getName());
                AliasedYAMLException cause = (AliasedYAMLException) e.getCause().getCause();
                assertThat(cause.getAliasedProperty(), is("name"));
                assertThat(cause.getAlias(), is("nameAlias"));
            }

        }
    }

    /**
     * Parses a YAML that consists of a single custom object with two entries, where one entry uses the new mapped key and the other one uses the old key (which
     * is forbidden). Test on getter method.
     * 
     * @throws IOException on {@link IOException}
     */
    @Test
    public void parseSingleObjectWithAliasAndUnaliasedGetterInvalidFile() throws IOException {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("stellar3.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(List.class));

            try {
                yaml.load(yamlString);
                Assert.fail("Expected exception");
            } catch (YAMLException e) {
                log.debug("Caught expected exception {}", e.getClass().getName());
                AliasedYAMLException cause = (AliasedYAMLException) e.getCause().getCause();
                assertThat(cause.getAliasedProperty(), is("absoluteMag"));
                assertThat(cause.getAlias(), is("absMag"));
            }

        }
    }

    /**
     * Parses a YAML that consists of a two custom objects. Both custom object defines
     * an alias on a field and on a getter.
     * 
     * @throws Exception on {@link Exception}
     */
    @Test
    public void parseTwoObjectsWithAliasMethodValidFile() throws Exception {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("universe1.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(Universe.class));

            Universe parseResult = yaml.loadAs(yamlString, Universe.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());
            assertThat(parseResult.getUniverseAge(), is(12.7));
            assertThat(parseResult.getStellarObjects(), notNullValue());
            assertThat(parseResult.getStellarObjects(), not(empty()));
            assertThat(parseResult.getStellarObjects().get(0).getAbsoluteMag(), is(4.8));
            assertThat(parseResult.getStellarObjects().get(0).getName(), is("Sun"));

        }
    }
}
