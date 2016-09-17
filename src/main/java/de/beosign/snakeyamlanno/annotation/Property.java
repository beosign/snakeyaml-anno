package de.beosign.snakeyamlanno.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.beosign.snakeyamlanno.convert.Converter;
import de.beosign.snakeyamlanno.convert.NoConverter;

/**
 * Defines an alias and/or a converter for a property. The key (if present) must match the key used in the YAML file. The annotation must be placed on the field
 * or getter that is responsible for storing the value found under the given key.
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
    String key() default "";

    /**
     * Converter class used to convert the YAML node to a java object (parsing) or vice versa (dumpming).
     * 
     * @return Converter class.
     */
    Class<? extends Converter<?>> converter() default NoConverter.class;

    /**
     * If true, exceptions are caught so the parsing process continues. This will leave some objects
     * in the tree <code>null</code>
     */
    boolean ignoreExceptions() default false;

}
