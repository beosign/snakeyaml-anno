package de.beosign.snakeyamlanno.property;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.beosign.snakeyamlanno.convert.Converter;
import de.beosign.snakeyamlanno.skip.SkipAtDumpPredicate;

/**
 * Defines an alias and/or a converter for a property. The key (if present) must match the key used in the YAML file. The annotation must be placed on the field
 * or getter that is responsible for storing the value found under the given key.
 * 
 * @author florian
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface YamlProperty {
    /**
     * The key as used in the YAML file.
     * 
     * @return key
     */
    String key() default "";

    /**
     * Converter class used to convert the YAML node to a java object (parsing) or vice versa (dumpming).
     * 
     * @return Converter class.
     */
    Class<? extends Converter<?>> converter() default Converter.NoConverter.class;

    /**
     * If true, exceptions are caught so the parsing process continues. This will leave some objects
     * in the tree <code>null</code>
     * 
     * @return <code>true</code> if exceptions are ignored
     */
    boolean ignoreExceptions() default false;

    /**
     * If true, this property will not be overridden by the value given in the imported yaml file.
     * 
     * @return <code>true</code> if property is to be skipped
     */
    boolean skipAtLoad() default false;

    /**
     * If true, this property will not be output when dumping. This <b>overrides</b> any value set by {@link #skipAtDumpIf()}.
     * 
     * @return <code>true</code> if property is to be skipped
     */
    boolean skipAtDump() default false;

    /**
     * <p>
     * If the given predicate evaluates to <code>true</code>, this property will not be output when dumping.
     * </p>
     * <p>
     * If {@link #skipAtDump()} is set to <code>true</code>, this property is <b>ignored</b>.
     * </p>
     * 
     * @return predicate class
     */
    Class<? extends SkipAtDumpPredicate> skipAtDumpIf() default SkipAtDumpPredicate.class;

    /**
     * Specifies the order of this property during dumping. A higher value means the property appears further up. The default value is 0. So for properties to
     * appear at the beginning, use a positive value and for properties to appear at the end use a negative value.
     * 
     * @return order; defaults to 0
     */
    int order() default 0;

}
