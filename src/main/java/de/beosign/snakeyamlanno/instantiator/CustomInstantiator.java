package de.beosign.snakeyamlanno.instantiator;

import org.yaml.snakeyaml.nodes.Node;

/**
 * <p>
 * A custom instantiator can be used to create instances for a node in a special way. For example, if you cannot or do not want to use a (no-arg) constructor to
 * create instances for a given type, you can register an {@link CustomInstantiator} for a type.
 * </p>
 * <p>
 * A custom instantiator that is present by using the {@code YamlInstantiateBy} annotation takes precedence over the <i>global instantiator</i> that is set on
 * the {@code AnnotationAwareConstructor}.
 * </p>
 * 
 * @param <T> type of object that is instantiated
 * @author florian
 * @since 1.0.0
 */
public interface CustomInstantiator<T> {

    /**
     * Creates an instance, must not be <code>null</code>.
     * 
     * @param node node node
     * @param tryDefault tryDefault flag, see {@link org.yaml.snakeyaml.constructor.BaseConstructor#newInstance}
     * @param ancestor see {@link org.yaml.snakeyaml.constructor.BaseConstructor#newInstance}
     * @param defaultInstantiator delegate to this instantiator to apply the default instantiation logic of Snakeyaml. This is by design not upper bounded to
     *            <code>T</code> because the default instantiator of snakeyaml operates with <code>Object</code>. If implementations use the
     *            defaultInstantiator, they have to manually cast to <code>T</code>.
     * @param globalInstantiator delegate to this instantiator to use the logic of the registered global instantiator. By default, this behaves the same as the
     *            default instantiator.
     * @return created instance, not <code>null</code>
     * @throws InstantiationException if an exception occurs
     */
    T createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator, GlobalInstantiator globalInstantiator)
            throws InstantiationException;

}
