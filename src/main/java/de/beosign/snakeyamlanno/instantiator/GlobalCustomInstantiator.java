package de.beosign.snakeyamlanno.instantiator;

import org.yaml.snakeyaml.nodes.Node;

/**
 * This instantiator just uses the passed in global instantiator to create an instance. It can be used in cases where one wants to cancel out both a
 * <i>custom</i> instantiator and apply the <i>global</i> instantiator instead.
 * 
 * @author florian
 * @since 1.0.0
 */
public class GlobalCustomInstantiator implements CustomInstantiator<Object> {

    @Override
    public Object createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator, GlobalInstantiator globalInstantiator)
            throws InstantiationException {
        return globalInstantiator.createInstance(node, tryDefault, ancestor, defaultInstantiator);
    }

}
