package de.beosign.snakeyamlanno.property;

import org.yaml.snakeyaml.introspector.Property;

/**
 * This special property type is used to indicate that a property is being accessed by a different name.
 * 
 * @author florian
 */
public class AliasedProperty extends AnnotatedProperty {
    /**
     * New instance.
     * 
     * @param targetProperty property that was discovered and is now registered by an alias
     * @param alias the alias under which the property is now accessible
     */
    public AliasedProperty(Property targetProperty, String alias) {
        super(alias, targetProperty);
    }

}
