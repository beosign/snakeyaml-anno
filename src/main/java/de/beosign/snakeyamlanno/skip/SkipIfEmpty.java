package de.beosign.snakeyamlanno.skip;

import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * Skips the property while dumping if one of the following conditions is true.
 * <ul>
 * <li>the value is an empty collection</li>
 * <li>the value is an empty map</li>
 * <li>the value is an empty String (length 0)</li>
 * </ul>
 * 
 * @author florian
 */
public class SkipIfEmpty implements SkipAtDumpPredicate {
    private static final SkipIfEmpty instance = new SkipIfEmpty();

    /**
     * Returns the singleton instance.
     * 
     * @return singleton
     */
    public static SkipIfEmpty getInstance() {
        return instance;
    }

    @Override
    public boolean skip(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        if (propertyValue instanceof Map) {
            return ((Map<?, ?>) propertyValue).size() == 0;
        }
        if (propertyValue instanceof List) {
            return ((List<?>) propertyValue).size() == 0;
        }

        if (propertyValue instanceof String) {
            return ((String) propertyValue).length() == 0;
        }

        return false;

    }

}
