package de.beosign.snakeyamlanno.instantiator;

import org.yaml.snakeyaml.nodes.Node;

/**
 * <p>
 * An instantiator can be used to create instances for a node in a special way. For example, if you cannot or do not want to use a (no-arg) constructor to
 * create instances for a given type, you can register an {@link Instantiator} for a type.
 * </p>
 * <p>
 * An instantiator that is present by using the {@code YamlInstantiateBy} annotation takes precedence over the <i>global instantiator</i> that is set on the
 * {@code AnnotationAwareConstructor}.
 * </p>
 * 
 * @param <T> type of object that is instantiated
 * @author florian
 * @since 0.9.0
 */
public interface Instantiator<T> {
    /**
     * Creates an instance. A value of <code>null</code> indicates that the instantiation process should fall back to the default mechanisms.
     * 
     * @param nodeType type of node
     * @param node node node
     * @param tryDefault tryDefault flag, see {@link org.yaml.snakeyaml.constructor.BaseConstructor#newInstance}
     * @param ancestor see {@link org.yaml.snakeyaml.constructor.BaseConstructor#newInstance}
     * @param defaultInstantiator delegate to this instantiator to apply the default instantiation logic of Snakeyaml. This is by design not upper bounded to
     *            <code>T</code> because the default instantiator of snakeyaml operates with <code>Object</code>, which would cause unchecked casts all over the
     *            place. If implementations use the defaultInstantiator, they have to manually cast to <code>T</code>.
     * @return created instance or <code>null</code> to indicate that further fallbacks are to be used to create an instance
     * @throws InstantiationException if an exception occurs
     */
    T createInstance(Class<?> nodeType, Node node, boolean tryDefault, Class<?> ancestor, Instantiator<?> defaultInstantiator) throws InstantiationException;

    /**
     * <b>This interface is for internal purposes only.</b><br>
     * Default value for the converter property; needed because one cannot define a type token with wildcards ({@code Class<? extends Instantiator<?>>}) from a
     * generic type ({@code Instantiator<T>}).
     */
    interface NoInstantiator extends Instantiator<Object> {
    }
}
