package de.beosign.snakeyamlanno.instantiator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Objects;

/**
 * Register an {@link Instantiator} class that determines how new instances are created. Usually, they are created using the
 * default no-arg constructor. An instantiator can however use a different approach.
 * 
 * @author florian
 * @since 0.9.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface YamlInstantiateBy {

    /**
     * <p>
     * Sets an {@link Instantiator} that will be used to create new instances.
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
     */
    Class<? extends Instantiator> value();

    /** Factory for YamlInstantiateBy instances. */
    final class Factory {
        private Factory() {
        }

        public static YamlInstantiateBy of(Class<? extends Instantiator> instantiator) {
            return new YamlInstantiateByImpl(instantiator);
        }

        /** Internal implementation class. */
        @SuppressWarnings({ "all" })
        private static final class YamlInstantiateByImpl implements YamlInstantiateBy {
            private final Class<? extends Instantiator> instantiatorType;

            private YamlInstantiateByImpl(Class<? extends Instantiator> instantiatorType) {
                Objects.requireNonNull(instantiatorType, "Instantiator Type must not be null");
                this.instantiatorType = instantiatorType;
            }

            @Override
            public Class<? extends Instantiator> value() {
                return instantiatorType;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return YamlInstantiateBy.class;
            }
        }

    }

}
