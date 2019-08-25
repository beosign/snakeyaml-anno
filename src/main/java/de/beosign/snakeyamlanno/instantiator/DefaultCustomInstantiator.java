package de.beosign.snakeyamlanno.instantiator;

import org.yaml.snakeyaml.nodes.Node;

/**
 * This instantiator just uses the passed in default instantiator to create an instance. It can be used in cases where one wants to cancel out both a
 * <i>custom</i> instantiator and the <i>global</i> instantiator that is set at the Constructor level, so that the normal
 * instantiation logic is applied instead.
 * 
 * @author florian
 * @since 1.0.0
 */
public class DefaultCustomInstantiator implements CustomInstantiator<Object> {

    @Override
    public Object createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator, GlobalInstantiator globalInstantiator)
            throws InstantiationException {
        return defaultInstantiator.createInstance(ancestor, node, tryDefault);
    }

}
