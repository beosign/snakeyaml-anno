package de.beosign.snakeyamlanno.constructor;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * Can be used to define a custom way of how a Java object should be constructed from a Node.
 * </p>
 * <p>
 * The difference to a {@link de.beosign.snakeyamlanno.convert.Converter} is that a {@link CustomConstructor} can be applied on a per-type basis, whereas a
 * Converter can only be used on a per-property basis.
 * For example, if you have an Enum that should be parseable in a custom way, and that enum is used multiple times, then a CustomConstructor is preferable
 * because it is only necessary to register it once for the enum, and not on each property of that enum type.<br>
 * However, it is also possible to register a CustomConstructor on a per-property basis only.
 * </p>
 * <p>
 * Additionally, one can register CustomConstructors for any class using a programmatic approach. This is useful if an already existing class cannot be
 * modified, see {@link AnnotationAwareConstructor#registerCustomConstructor(Class, Class)}.
 * </p>
 * 
 * @author florian
 */
@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Inherited
public @interface YamlConstructBy {
    Class<? extends CustomConstructor<?>> value();

    /**
     * Factory that creates instances of {@link YamlConstructBy}.
     */
    final class Factory {
        private Factory() {
        }

        /**
         * Creates a {@link YamlConstructBy}.
         * 
         * @param customConstructorClass constructor type
         * @return {@link YamlConstructBy}.
         */
        public static YamlConstructBy of(Class<? extends CustomConstructor<?>> customConstructorClass) {
            return new YamlConstructByImpl(customConstructorClass);
        }

        /**
         * Implementation class.
         * 
         * @author florian
         */
        @SuppressWarnings({ "all" })
        private static final class YamlConstructByImpl implements YamlConstructBy {
            private Class<? extends CustomConstructor<?>> value;

            private YamlConstructByImpl(Class<? extends CustomConstructor<?>> value) {
                this.value = value;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return YamlConstructBy.class;
            }

            @Override
            public Class<? extends CustomConstructor<?>> value() {
                return value;
            }

        }

    }
}
