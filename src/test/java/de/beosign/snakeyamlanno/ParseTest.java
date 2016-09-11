package de.beosign.snakeyamlanno;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import de.beosign.snakeyamlanno.constructor.AnnotationAwareConstructor;

public class ParseTest {
    @Test
    public void parse() throws IOException {
        try (InputStream is = ParseTest.class.getResourceAsStream("model1.yaml")) {
            String yamlString = IOUtils.toString(is, StandardCharsets.UTF_8);
            System.out.println(yamlString);

            Yaml yaml = new Yaml(new AnnotationAwareConstructor(List.class));
            System.out.println(yaml.load(yamlString));

        }
    }
}
