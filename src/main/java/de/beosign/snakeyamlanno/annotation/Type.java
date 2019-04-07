package de.beosign.snakeyamlanno.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.beosign.snakeyamlanno.type.Instantiator;
import de.beosign.snakeyamlanno.type.NoSubstitutionTypeSelector;
import de.beosign.snakeyamlanno.type.SubstitutionTypeSelector;

/**
 * <p>
 * Can be defined on an (abstract) class or interface to define which possible (sub)types yaml should try to use when parsing a
 * property of the given type. This eliminates the need to specify a tag for that property in the YAML file.
 * </p>
 * <p>
 * Additionally, one can register a {@link Instantiator} class that determines how new instances are created. Usually, they are created using the
 * default constructor. An instantiator can however use a different approach.
 * </p>
 * 
 * @author florian
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface Type {
    /**
     * <p>
     * Can optionally be set to define one or more concrete types that can be used when creating an instance of the type where this annotation is placed onto.
     * </p>
     * <p>
     * A common use case is where you have a property of a type that is an interface or abstract class, and there are one or (usually) more concrete
     * implementations. The yaml parser will then try to select one of the given concrete types that let the parsing of the node succeed.
     * </p>
     * 
     * @return List of possible substitution classes.
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
     * 
     * @return SubstitutionTypeSelector implementation class
     */
    Class<? extends SubstitutionTypeSelector> substitutionTypeSelector() default NoSubstitutionTypeSelector.class;

    /**
     * <p>
     * Can optionally be set to define an {@link Instantiator} that will be used to create new instances.
     * </p>
     * <p>
     * A common use case might be to use a static factory method for objects or a dependency injection framework that should create instances, like CDI.
     * </p>
     * <p>
     * Please consider that Snakeyaml already provides some mechanisms to create instances with no default (no-arg) constructor, see
     * <a href="https://bitbucket.org/asomov/snakeyaml/wiki/CompactObjectNotation">Compact Object Notation</a> and
     * <a href="https://bitbucket.org/asomov/snakeyaml/wiki/Documentation#markdown-header-immutable-instances">Immutable Instances</a>.
     * </p>
     * 
     * @return instantiator class
     * @since 0.9.0
     */
    Class<? extends Instantiator> instantiator() default Instantiator.class;

    final class Factory {
        private Factory() {
        }

        public static Type of(Class<? extends Instantiator> instantiator, Class<? extends SubstitutionTypeSelector> substitutionTypeSelectorType, Class<?>... substitutionTypes) {
            return new TypeImpl(instantiator, substitutionTypeSelectorType, substitutionTypes);
        }

        @SuppressWarnings({ "all" })
        private static final class TypeImpl implements Type {
            private Class<?>[] substitutionTypes = new Class<?>[0];
            private Class<? extends SubstitutionTypeSelector> substitutionTypeSelectorType = NoSubstitutionTypeSelector.class;
            private Class<? extends Instantiator> instantiatorType = Instantiator.class;

            private TypeImpl(Class<? extends Instantiator> instantiatorType, Class<? extends SubstitutionTypeSelector> substitutionTypeSelectorType, Class<?>... substitutionTypes) {
                if (substitutionTypes != null) {
                    this.substitutionTypes = substitutionTypes;
                }
                if (substitutionTypeSelectorType != null) {
                    this.substitutionTypeSelectorType = substitutionTypeSelectorType;
                }
                if (instantiatorType != null) {
                    this.instantiatorType = instantiatorType;
                }
            }

            @Override
            public Class<?>[] substitutionTypes() {
                return substitutionTypes;
            }

            @Override
            public Class<? extends SubstitutionTypeSelector> substitutionTypeSelector() {
                return substitutionTypeSelectorType;
            }

            @Override
            public Class<? extends Instantiator> instantiator() {
                return instantiatorType;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Type.class;
            }
        }

    }

}
