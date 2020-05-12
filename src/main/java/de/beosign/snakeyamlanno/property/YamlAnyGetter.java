package de.beosign.snakeyamlanno.property;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * Must be placed on a public getter that is of type {@link java.util.Map}.
 * </p>
 * <p>
 * It can be used to define that the properties of the map are <i>flattened</i> into the object. This is kind of an inverse function of the
 * {@link YamlAnySetter} feature.
 * </p>
 * <p>
 * If the annotated getter is not of type {@link java.util.Map}, a YamlException is thrown during properties retrieval.
 * </p>
 * 
 * @author florian
 * @since 1.1.0
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface YamlAnyGetter {
}
