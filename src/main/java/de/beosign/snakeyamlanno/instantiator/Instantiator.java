package de.beosign.snakeyamlanno.instantiator;

import org.yaml.snakeyaml.nodes.Node;

/**
 * <p>
 * An instantiator can be used to create instances for a node in a special way. For example, if you cannot or do not want to use a (no-arg) constructor to
 * create instances for a given type, you can register an {@link Instantiator} for a type.
 * </p>
 * <p>
 * An instantiator that is present by using the {@code Type} annotation takes precedence over an instantiator that is set on the
 * {@code AnnotationAwareConstructor}.
 * </p>
 * 
 * @author florian
 * @since 0.9.0
 */
public interface Instantiator {
    /**
     * Creates an instance. A value of <code>null</code> indicates that the instantiation process should fall back to the default mechanisms.
     * 
     * @param nodeType type of node
     * @param node node node
     * @param tryDefault tryDefault flag, see {@link org.yaml.snakeyaml.constructor.BaseConstructor#newInstance}
     * @param ancestor see {@link org.yaml.snakeyaml.constructor.BaseConstructor#newInstance}
     * @param defaultInstantiator delegate to this instantiator to apply the default instantiation logic of Snakeyaml
     * @return created instance or <code>null</code> to indicate that further fallbacks are to be used to create an instance
     * @throws InstantiationException if an exception occurs
     */
    Object createInstance(Class<?> nodeType, Node node, boolean tryDefault, Class<?> ancestor, Instantiator defaultInstantiator) throws InstantiationException;

}
