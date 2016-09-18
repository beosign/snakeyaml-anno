package de.beosign.snakeyamlanno.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.beosign.snakeyamlanno.type.NoSubstitutionTypeSelector;
import de.beosign.snakeyamlanno.type.SubstitutionTypeSelector;
import de.beosign.snakeyamlanno.type.TypeConstructor;

/**
 * Can be defined on an (abstract) class or interface to define how a type is constructed and which possible (sub)types yaml should try to use when parsing a
 * property of the given type. This eliminates the need to specify a tag for that property in the YAML file.
 * 
 * @author florian
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface Type {
    /**
     * List of possible substitution classes.
     */
    Class<?>[] substitutionTypes() default {};

    /**
     * <p>
     * Can optionally be used to select a substitution type from the list of the possible types given by {@link #substitutionTypes()}. The list is
     * restricted to valid (parseable) types unless the {@link SubstitutionTypeSelector#disableDefaultAlgorithm()} returns true.
     * </p>
     * <p>
     * Has <b>no</b> effect if {@link #substitutionTypes()} is empty!
     * </p>
     */
    Class<? extends SubstitutionTypeSelector> substitutionTypeSelector() default NoSubstitutionTypeSelector.class;

    Class<? extends TypeConstructor> typeConstructor() default TypeConstructor.class;
}
