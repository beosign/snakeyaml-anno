package de.beosign.snakeyamlanno.skip;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * Skips the property while dumping if the value is null.
 * 
 * @author florian
 */
public class SkipIfNull implements SkipAtDumpPredicate {
    @Override
    public boolean skip(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        return propertyValue == null;

    }

}
