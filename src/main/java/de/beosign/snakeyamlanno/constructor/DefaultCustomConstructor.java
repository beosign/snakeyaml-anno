package de.beosign.snakeyamlanno.constructor;

import java.util.function.Function;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

/**
 * A default constructor just constructs an object by using the passed in defaultConstructor instance.
 * 
 * @author florian
 */
public class DefaultCustomConstructor implements CustomConstructor<Object> {

    /**
     * Constructs an object by just calling <code>defaultConstructor.apply</code>.
     * 
     * @param node node
     * @param defaultConstructor default constructor
     * @return constructed object
     * @throws YAMLException if an exception occurs during construction
     */
    @Override
    public Object construct(Node node, Function<? super Node, ?> defaultConstructor) throws YAMLException {
        return defaultConstructor.apply(node);
    }

}
