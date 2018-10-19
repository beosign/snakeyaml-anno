package de.beosign.snakeyamlanno.constructor;

import java.util.function.Function;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

import de.beosign.snakeyamlanno.util.NodeUtil;

/**
 * Test constructor for an enum that is case insensitive.
 * 
 * @author florian
 */
public class EnumConstructor implements CustomConstructor<Enum<?>> {

    @Override
    public Enum<?> construct(Node node, Function<? super Node, ? extends Enum<?>> defaultConstructor) throws YAMLException {

        String nodeValue = (String) NodeUtil.getValue(node);
        for (Object enumConstant : node.getType().getEnumConstants()) {
            if (nodeValue.equalsIgnoreCase(((Enum<?>) enumConstant).name())) {
                return (Enum<?>) enumConstant;
            }
        }
        return null;
    }

}
