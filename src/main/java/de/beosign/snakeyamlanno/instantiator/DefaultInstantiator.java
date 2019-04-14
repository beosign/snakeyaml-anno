package de.beosign.snakeyamlanno.instantiator;

import org.yaml.snakeyaml.nodes.Node;

/**
 * This instantiator just uses the passed in default instantiator to create an instance. It can be used in cases where one wants to cancel out the
 * <i>global</i> instantiator that is set at the Constructor level for a particular type and to apply the normal instantiation logic instead.
 * 
 * @author florian
 * @since 0.9.0
 */
public class DefaultInstantiator implements Instantiator {

    @Override
    public Object createInstance(Class<?> nodeType, Node node, boolean tryDefault, Class<?> ancestor, Instantiator defaultInstantiator) throws InstantiationException {
        return defaultInstantiator.createInstance(nodeType, node, tryDefault, ancestor, null);
    }

}
