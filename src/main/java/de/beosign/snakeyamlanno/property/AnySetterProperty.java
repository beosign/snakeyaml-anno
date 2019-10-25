package de.beosign.snakeyamlanno.property;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.introspector.MissingProperty;

/**
 * This special property type is used if properties in a yaml file that have no match in the corresponding POJO should be passed to an
 * annotated method that takes the (not matched) property name and the value. This enables collecting the unmatched properties and e.g. store them in a map.<br>
 * The idea is taken from Jackson's {@code JsonAnySetter} annotation, see <a
 * href="https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations">Jackson-Annotations</a>
 * and
 * <a href="https://www.logicbig.com/tutorials/misc/jackson/jackson-any-setter.html">Jackson-Any-Setter</a>
 * 
 * @author florian
 * @since 1.1.0
 */
public class AnySetterProperty extends AnnotatedProperty {
    private final Method anySetterMethod;

    /**
     * New instance.
     * 
     * @param name name of the property that is not present at the target object.
     * @param anySetterMethod a method that takes two arguments, {@link String} and {@link Object}.
     * @throws IllegalArgumentException if the passed in method does not have exactly two parameters, namely {@link String} and {@link Object}.
     */
    public AnySetterProperty(String name, Method anySetterMethod) {
        super(name, new MissingProperty(name));

        Supplier<String> messageSupplier = () -> {
            return "Method " + anySetterMethod.getName() + " is expected to have two parameters, String and Object, but found: "//
                    + Arrays.stream(anySetterMethod.getParameterTypes()).map(c -> c.getName()).collect(Collectors.joining(", "));
        };

        if (anySetterMethod.getParameterCount() != 2) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        if (!anySetterMethod.getParameterTypes()[0].equals(String.class) || !anySetterMethod.getParameterTypes()[1].equals(Object.class)) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        this.anySetterMethod = anySetterMethod;
    }

    /**
     * Calls the method that takes the property name and its value as parameters.
     */
    @Override
    public void set(Object object, Object value) throws Exception {
        anySetterMethod.invoke(object, getName(), value);
    }

}
