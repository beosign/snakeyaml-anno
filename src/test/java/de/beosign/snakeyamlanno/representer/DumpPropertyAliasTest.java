package de.beosign.snakeyamlanno.representer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import de.beosign.snakeyamlanno.property.StellarObject;

/**
 * Tests that aliasing properties works.
 */
public class DumpPropertyAliasTest {

    /**
     * Tests aliasing on a getter and on a field.
     */
    @Test
    public void testAlias() {
        StellarObject stellarObject = new StellarObject();
        stellarObject.setAbsoluteMag(1.0);
        stellarObject.setDistance("3km");
        stellarObject.setLocation("here");
        stellarObject.setName("my name");
        stellarObject.setRadius(4);
        stellarObject.setType("my type");

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter());
        String dumped = yaml.dumpAsMap(stellarObject);

        assertThat(dumped, containsString("distance: 3km"));
        assertThat(dumped, containsString("location: here"));
        assertThat(dumped, containsString("radius: 4"));
        assertThat(dumped, containsString("type: my type"));
        assertThat(dumped, containsString("absMag: 1.0"));
        assertThat(dumped, containsString("nameAlias: my name"));
    }

}
