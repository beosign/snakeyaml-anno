package de.beosign.snakeyamlanno.type;

import java.util.List;

import org.yaml.snakeyaml.nodes.MappingNode;

/**
 * Used to determine which subtype to choose among a list of possible subtypes.
 * 
 * @author florian
 */
public interface SubstitutionTypeSelector {
    /**
     * If <code>true</code>, the default algorithm for determining the set of possible subtypes will <b>not</b> be used. This means, that the parameter
     * <code>possibleTypes</code>
     * in {@link #getSelectedType(MappingNode, List)} will be empty!
     * 
     * @return <code>false</code> by default
     */
    default boolean disableDefaultAlgorithm() {
        return false;
    }

    /**
     * Returns the selected subtype to be used during parsing of the given node.
     * 
     * @param node node - use {@link MappingNode#getType()} to retrieve the type that would be normally used.
     * @param possibleTypes a list of possible types that have already been considered valid.
     * @return subtype to use
     */
    Class<?> getSelectedType(MappingNode node, List<? extends Class<?>> possibleTypes);
}
