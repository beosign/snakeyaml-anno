package de.beosign.snakeyamlanno.constructor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * Use this constructor if you have a list at the root of the yaml document and the list items are not explicitly typed, but you still want to have the list
 * items instantiated as a concrete type instead of just {@code Map<String, Object>}.
 * 
 * @author florian
 * @since 0.8.0
 */
public class AnnotationAwareListConstructor extends AnnotationAwareConstructor {
    private static final Logger log = LoggerFactory.getLogger(AnnotationAwareListConstructor.class);

    private final Class<?> collectionItemType;

    /**
     * Creates a constructor for a sequence, typing the sequence items with the given collectionItemType.
     * 
     * @param collectionItemType type of list items
     */
    public AnnotationAwareListConstructor(Class<?> collectionItemType) {
        super(List.class);
        Objects.requireNonNull(collectionItemType, "CollectionItemType parameter must not be null");
        this.collectionItemType = collectionItemType;
    }

    /**
     * Creates a constructor for a sequence, typing the sequence items with the given collectionItemType.
     * 
     * @param collectionItemType type of list items
     * @param caseInsensitive true if parsing should be independent of case of keys
     */
    public AnnotationAwareListConstructor(Class<?> collectionItemType, boolean caseInsensitive) {
        super(List.class, caseInsensitive);
        Objects.requireNonNull(collectionItemType, "CollectionItemType parameter must not be null");
        this.collectionItemType = collectionItemType;
    }

    /**
     * Called at the beginning of the parsing process. Overridden to implement "Allow parsing of list at root without tags" feature.
     */
    @Override
    public Object getSingleData(Class<?> type) {
        if (Collection.class.isAssignableFrom(type)) {
            log.debug("Found a collection type as root node; set type of item nodes to " + collectionItemType.getTypeName());
            SequenceNode node = (SequenceNode) composer.getSingleNode();
            node.setTag(new Tag(type));
            for (Node n : node.getValue()) {
                n.setType(collectionItemType);
            }
            return constructDocument(node);
        }
        return super.getSingleData(type);
    }

}
