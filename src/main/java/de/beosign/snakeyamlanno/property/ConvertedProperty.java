package de.beosign.snakeyamlanno.property;

import org.yaml.snakeyaml.introspector.Property;

/**
 * This special property type is used to indicate that a value has already been converted and set into the model class before, so a call to "set" does not
 * override the already converted value that had been stored before.
 * 
 * @author florian
 */
public class ConvertedProperty extends Property {
    /**
     * New instance.
     * 
     * @param name name of property
     */
    public ConvertedProperty(String name) {
        super(name, Object.class);
    }

    @Override
    public Class<?>[] getActualTypeArguments() {
        return new Class[0];
    }

    /**
     * Setter does nothing.
     */
    @Override
    public void set(Object object, Object value) throws Exception {
    }

    @Override
    public Object get(Object object) {
        return object;
    }

}
