package de.beosign.snakeyamlanno.instantiator;

import org.yaml.snakeyaml.nodes.Node;

/**
 * <p>
 * A global instantiator can be used to create instances for a node in a special way. Unlike a {@link CustomInstantiator}, it is automatically applied to all
 * types. A {@link CustomInstantiator} that is registered to create an instance for the current type takes precedence.
 * </p>
 * <p>
 * By default, this instantiator just uses the Snakeyaml default instantiation logic. By subclassing and registering it with the
 * {@code AnnotationAwareConstructor} you can provide your own logic of instantiation.
 * </p>
 * 
 * @author florian
 * @since 1.0.0
 */
public class GlobalInstantiator {

    /**
     * Creates an instance, must not be <code>null</code>.
     * 
     * @param nodeType type of node
     * @param node node node
     * @param tryDefault tryDefault flag, see {@link org.yaml.snakeyaml.constructor.BaseConstructor#newInstance}
     * @param ancestor see {@link org.yaml.snakeyaml.constructor.BaseConstructor#newInstance}
     * @param defaultInstantiator delegate to this instantiator to apply the default instantiation logic of Snakeyaml.
     * @return created instance, not <code>null</code>
     * @throws InstantiationException if an exception occurs
     */
    public Object createInstance(Class<?> nodeType, Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator) throws InstantiationException {
        return defaultInstantiator.createInstance(ancestor, node, tryDefault);
    }

}
