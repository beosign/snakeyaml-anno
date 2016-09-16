package de.beosign.snakeyamlanno.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines an alias for a property. The key must match the key used in the YAML file. The annotation must be placed on the field or getter that is responsible
 * for storing the value found under the given key.
 * 
 * @author florian
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface Property {
    /**
     * The key as used in the YAML file.
     * 
     * @return key
     */
    String key();
}
