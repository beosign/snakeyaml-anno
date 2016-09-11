package de.beosign.snakeyamlanno.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface Property {
    String key();

    String readMethod() default "";

    String writeMethod() default "";

    Class<?> beanClass() default Object.class;
}
