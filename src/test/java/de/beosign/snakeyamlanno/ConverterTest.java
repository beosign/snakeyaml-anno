package de.beosign.snakeyamlanno;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsNull.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;

/**
 * Tests the key mapping functionality.
 * 
 * @author florian
 */
public class ConverterTest {
    private static final Logger log = LoggerFactory.getLogger(ConverterTest.class);

    @Test
    public void parseWithConverter() throws IOException {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("person1.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.debug("Loaded YAML file:\n{}", yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(List.class));

            @SuppressWarnings("unchecked")
            List<Person> parseResult = yaml.loadAs(yamlString, List.class);
            log.debug("Parsed YAML file:\n{}", parseResult);

            assertThat(parseResult, notNullValue());

        }
    }
}
