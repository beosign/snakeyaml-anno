package de.beosign.snakeyamlanno.constructor;

import java.lang.annotation.Annotation;

/**
 * Implementation for {@link ConstructBy} annotation.
 * 
 * @author florian
 */
public final class ConstructByFactory {
    private ConstructByFactory() {
    }

    /**
     * Creates a {@link ConstructBy}.
     * 
     * @param customConstructorClass constructor type
     * @return {@link ConstructBy}.
     */
    public static ConstructBy of(Class<? extends CustomConstructor<?>> customConstructorClass) {
        return new ConstructByImpl(customConstructorClass);
    }

    /**
     * Implementation class.
     * 
     * @author florian
     */
    @SuppressWarnings({ "all" })
    private static final class ConstructByImpl implements ConstructBy {
        private Class<? extends CustomConstructor<?>> value;

        private ConstructByImpl(Class<? extends CustomConstructor<?>> value) {
            this.value = value;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return ConstructBy.class;
        }

        @Override
        public Class<? extends CustomConstructor<?>> value() {
            return value;
        }

    }

}
