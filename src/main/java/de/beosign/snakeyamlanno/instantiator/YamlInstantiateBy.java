package de.beosign.snakeyamlanno.instantiator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Objects;

/**
 * Register a {@link CustomInstantiator} class that determines how new instances are created. Usually, they are created using the
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
     * Sets an {@link CustomInstantiator} that will be used to create new instances.
     * </p>
     * <p>
     * A common use case might be to use a static factory method for objects. If you are looking for a way to customize the instantiation process for all types
     * in general, consider using a {@link GlobalInstantiator} instead.
     * </p>
     * <p>
     * Please consider that Snakeyaml already provides some mechanisms to create instances with no default (no-arg) constructor, see
     * <a href="https://bitbucket.org/asomov/snakeyaml/wiki/CompactObjectNotation">Compact Object Notation</a> and
     * <a href="https://bitbucket.org/asomov/snakeyaml/wiki/Documentation#markdown-header-immutable-instances">Immutable Instances</a>.
     * </p>
     * 
     * @return instantiator class
     */
    Class<? extends CustomInstantiator<?>> value();

    /** Factory for YamlInstantiateBy instances. */
    final class Factory {
        private Factory() {
        }

        public static YamlInstantiateBy of(Class<? extends CustomInstantiator<?>> instantiator) {
            return new YamlInstantiateByImpl(instantiator);
        }

        /** Internal implementation class. */
        @SuppressWarnings({ "all" })
        private static final class YamlInstantiateByImpl implements YamlInstantiateBy {
            private final Class<? extends CustomInstantiator<?>> instantiatorType;

            private YamlInstantiateByImpl(Class<? extends CustomInstantiator<?>> instantiatorType) {
                Objects.requireNonNull(instantiatorType, "CustomInstantiator Type must not be null");
                this.instantiatorType = instantiatorType;
            }

            @Override
            public Class<? extends CustomInstantiator<?>> value() {
                return instantiatorType;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return YamlInstantiateBy.class;
            }
        }

    }

}
