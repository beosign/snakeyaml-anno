package de.beosign.snakeyamlanno.skip;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * Determines if a property is not to be output while dumping.
 * 
 * @author florian
 */
public interface SkipAtDumpPredicate {
    /**
     * Must return <code>true</code> iff the property is not to be dumped.
     * 
     * @param javaBean the java object that is to be dumped
     * @param property property
     * @param propertyValue value
     * @param customTag custom tag
     * @return <code>true</code> if property is not to be dumped
     */
    boolean skip(Object javaBean, Property property, Object propertyValue, Tag customTag);
}
