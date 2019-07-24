package de.beosign.snakeyamlanno.constructor;

import java.util.function.Function;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

/**
 * A CustomConstructor can be used to define how to create a {@link Node} into a Java object. It can be applied on a per-type basis statically via the
 * <code>@YamlConstructBy</code> annotation or dynamically via code, or it can be applied on a per-property basis using the <code>@YamlConstructBy</code> annotation
 * 
 * @author florian
 * @param <T> type of object to create
 */
public interface CustomConstructor<T> {

    /**
     * Creates an object of type <code>T</code> for the given node.
     * 
     * @param node node
     * @param defaultConstructor the constructor function that would be used by default. You can use it to delegate the work after you have done your
     *            customizations.
     * @return object
     * @throws YAMLException if object cannot be created
     */
    T construct(Node node, Function<? super Node, ? extends T> defaultConstructor) throws YAMLException;
}
