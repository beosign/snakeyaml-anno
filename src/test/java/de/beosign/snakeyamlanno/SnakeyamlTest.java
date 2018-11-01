package de.beosign.snakeyamlanno;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import de.beosign.snakeyamlanno.property.Person;

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

}
