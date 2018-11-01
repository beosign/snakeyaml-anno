package de.beosign.snakeyamlanno.representer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import de.beosign.snakeyamlanno.property.StellarObject;

/**
 * Tests that orderinf of properties works.
 */
public class DumpPropertyOrderTest {

    /**
     * Tests simple ordering.
     */
    @Test
    public void testOrder() {
        StellarObject stellarObject = new StellarObject();
        stellarObject.setAbsoluteMag(1.0);
        stellarObject.setDistance("3km");
        stellarObject.setLocation("here");
        stellarObject.setName("my name");
        stellarObject.setRadius(4);
        stellarObject.setType("my type");

        Yaml yaml = new Yaml(new AnnotationAwareRepresenter());
        String dumped = yaml.dumpAsMap(stellarObject);

        System.out.println(dumped);

        assertThat(dumped.indexOf("nameAlias") >= 0, is(true));
        assertThat(dumped.indexOf("radius") >= 0, is(true));
        assertThat(dumped.indexOf("distance") >= 0, is(true));

        assertThat(dumped.indexOf("nameAlias") < dumped.indexOf("radius"), is(true));
        assertThat(dumped.indexOf("nameAlias") < dumped.indexOf("distance"), is(true));
        assertThat(dumped.indexOf("radius") < dumped.indexOf("distance"), is(true));
        assertThat(dumped.trim().endsWith("distance: 3km"), is(true));

        assertThat(dumped.indexOf("location: here") >= 0, is(true));
        assertThat(dumped.indexOf("type: my type") >= 0, is(true));

    }

}
