package de.beosign.snakeyamlanno.instantiator;

import org.yaml.snakeyaml.nodes.Node;

/**
 * This global instantiator just uses the Snakeyaml default instantiation logic.
 * 
 * @author florian
 * @since 1.0.0
 */
public class DefaultGlobalInstantiator implements GlobalInstantiator {

    /**
     * Creates an instance using SnakeYaml's default logic.
     * 
     * @param node node node
     * @param tryDefault tryDefault flag, see {@link org.yaml.snakeyaml.constructor.BaseConstructor#newInstance}
     * @param ancestor see {@link org.yaml.snakeyaml.constructor.BaseConstructor#newInstance}
     * @param defaultInstantiator delegate to this instantiator to apply the default instantiation logic of Snakeyaml.
     * @return created instance, not <code>null</code>
     * @throws InstantiationException if an exception occurs
     */
    @Override
    public Object createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator) throws InstantiationException {
        return defaultInstantiator.createInstance(ancestor, node, tryDefault);
    }

}
