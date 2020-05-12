package de.beosign.snakeyamlanno.property;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.yaml.snakeyaml.introspector.PropertyUtils;

/**
 * <p>
 * Must be placed on a public method that takes exactly two parameters: {@link String} and {@link Object}.
 * </p>
 * <p>
 * It can be used to define a fallback method that is called if the yaml file contains a property that is not present in the target object. Usually, an
 * exception would be thrown (unless the flag {@link PropertyUtils#isSkipMissingProperties()} is set). But if there is a method annotated with this annotation
 * (maybe inherited), the method is called with the property name as first parameter and the
 * property value as second parameter. For example, those unmatched properties could be collected into a map.
 * </p>
 * <p>
 * If there is more than one method with this annotation, it is unspecified which method will be invoked.<br>
 * If the annotated method does not take the required two parameters, an {@link IllegalArgumentException} will be thrown.
 * </p>
 * 
 * @author florian
 * @since 1.1.0
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface YamlAnySetter {
}
