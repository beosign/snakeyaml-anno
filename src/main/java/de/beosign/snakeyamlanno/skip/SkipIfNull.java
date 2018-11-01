package de.beosign.snakeyamlanno.skip;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * Skips the property while dumping if the value is null.
 * 
 * @author florian
 */
public class SkipIfNull implements SkipAtDumpPredicate {
    private static final SkipIfNull instance = new SkipIfNull();

    /**
     * Returns the singleton instance.
     * 
     * @return singleton
     */
    public static SkipIfNull getInstance() {
        return instance;
    }

    @Override
    public boolean skip(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        return propertyValue == null;
    }

}
