package de.beosign.snakeyamlanno.type;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Objects;

/**
 * <p>
 * Can be defined on an (abstract) class or interface to define which possible (sub)types yaml should try to use when parsing a
 * property of the given type. This eliminates the need to specify a tag for that property in the YAML file.
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
     * Sets one or more concrete types that can be used when creating an instance of the type where this annotation is placed onto.
     * </p>
     * <p>
     * A common use case is where you have a property of a type that is an interface or abstract class, and there are one or (usually) more concrete
     * implementations. The yaml parser will then try to select one of the given concrete types that let the parsing of the node succeed.
     * </p>
     * 
     * @return List of possible substitution classes.
     */
    Class<?>[] substitutionTypes();

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
    Class<? extends SubstitutionTypeSelector> substitutionTypeSelector() default SubstitutionTypeSelector.class;

    /**
     * Factory for Type instances.
     */
    final class Factory {
        private Factory() {
        }

        public static Type of(Class<? extends SubstitutionTypeSelector> substitutionTypeSelectorType, Class<?>... substitutionTypes) {
            return new TypeImpl(substitutionTypeSelectorType, substitutionTypes);
        }

        /** Internal implementation class. */
        @SuppressWarnings({ "all" })
        private static final class TypeImpl implements Type {
            private Class<?>[] substitutionTypes;
            private Class<? extends SubstitutionTypeSelector> substitutionTypeSelectorType = SubstitutionTypeSelector.class;

            private TypeImpl(Class<? extends SubstitutionTypeSelector> substitutionTypeSelectorType, Class<?>... substitutionTypes) {
                Objects.requireNonNull(substitutionTypes, "Substitution types must not be null");

                this.substitutionTypes = substitutionTypes;
                if (substitutionTypeSelectorType != null) {
                    this.substitutionTypeSelectorType = substitutionTypeSelectorType;
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
            public Class<? extends Annotation> annotationType() {
                return Type.class;
            }
        }

    }

}
