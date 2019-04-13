package de.beosign.snakeyamlanno.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

/**
 * Helper class for dealing with nodes.
 * 
 * @author florian
 */
public final class NodeUtil {

    private NodeUtil() {
    }

    /**
     * Returns a mapping from the property name to the value node. So basically, the (scalar) key node of the mapping node is converted to its String
     * representation whereas the mapped value node stays the same.
     * 
     * @param mappingNode mapping node
     * @return a mapping from the property name to the value node.
     */
    public static Map<String, Node> getPropertyToValueNodeMap(MappingNode mappingNode) {
        Map<String, Node> map = new HashMap<>();

        for (NodeTuple nt : mappingNode.getValue()) {
            ScalarNode sn = (ScalarNode) nt.getKeyNode();
            map.put(sn.getValue(), nt.getValueNode());
        }

        return map;
    }

    /**
     * Returns a mapping from a property name to its associated value, so all {@link Node}s in the mapping node are converted to their plain Java
     * representations (their values).
     * 
     * @param mappingNode mapping node
     * @return map from a property name to its plain Java value
     */
    public static Map<String, Object> getPropertyToValueMap(MappingNode mappingNode) {
        Map<String, Object> map = new HashMap<>();

        for (NodeTuple nt : mappingNode.getValue()) {
            ScalarNode sn = (ScalarNode) nt.getKeyNode();
            map.put(sn.getValue(), getValue(nt.getValueNode()));
        }

        return map;
    }

    /**
     * Returns the plain value of the given node. The node must be one of
     * <ul>
     * <li>ScalarNode</li>
     * <li>MappingNode</li>
     * <li>SequenceNode</li>
     * </ul>
     * 
     * @param node node
     * @return value of node
     * @throws IllegalArgumentException if given node is not one of the supported three node types
     */
    public static Object getValue(Node node) {
        if (node instanceof ScalarNode) {
            return ((ScalarNode) node).getValue();
        } else if (node instanceof MappingNode) {
            return getPropertyToValueMap((MappingNode) node);
        } else if (node instanceof SequenceNode) {
            List<Object> values = new ArrayList<>();
            for (Node n : ((SequenceNode) node).getValue()) {
                values.add(getValue(n));
            }
            return values;
        } else {
            throw new IllegalArgumentException("Unknown node type: " + node.getClass());
        }
    }

    /**
     * Removes the node with the given key from the given mapping node.
     * 
     * @param mappingNode mapping node from which a node is to be removed
     * @param key the key of the node that is to be removed
     * @return <code>true</code> if something has been removed
     */
    public static boolean removeNode(MappingNode mappingNode, String key) {
        return mappingNode.getValue().removeIf(nt -> NodeUtil.getValue(nt.getKeyNode()).equals(key));
    }

}
