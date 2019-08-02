package de.beosign.snakeyamlanno.instantiator;

import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.nodes.Node;

/**
 * This interface represents the SnakeYaml interface for creating a new instance. It has the same signature as {@link BaseConstructor#newInstance(Node)}.
 * 
 * @author florian
 * @since 1.0.0
 */
public interface DefaultInstantiator {

    /**
     * Creates an instance.
     * 
     * @param ancestor ancestor
     * @param node node
     * @param tryDefault default flag
     * @return created instance
     * @throws InstantiationException if an instance could not be created
     */
    Object createInstance(Class<?> ancestor, Node node, boolean tryDefault) throws InstantiationException;

}
