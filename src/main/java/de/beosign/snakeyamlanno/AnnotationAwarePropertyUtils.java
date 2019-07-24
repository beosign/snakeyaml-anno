package de.beosign.snakeyamlanno;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import de.beosign.snakeyamlanno.convert.NoConverter;
import de.beosign.snakeyamlanno.property.AliasedProperty;
import de.beosign.snakeyamlanno.property.AnnotatedProperty;
import de.beosign.snakeyamlanno.property.ConvertedProperty;
import de.beosign.snakeyamlanno.property.SkippedProperty;

/**
 * YamlProperty Utils where properties are replaced by delegating properties so features like converting and aliasing can be implemented.
 * 
 * @author florian
 */
public class AnnotationAwarePropertyUtils extends PropertyUtils {
    private Map<Class<?>, Map<String, Property>> typeToPropertiesMap = new HashMap<>();
    private final boolean caseInsensitive;

    public AnnotationAwarePropertyUtils() {
        this(false);
    }

    public AnnotationAwarePropertyUtils(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    @Override
    protected Map<String, Property> getPropertiesMap(Class<?> type, BeanAccess bAccess) {
        if (typeToPropertiesMap.containsKey(type)) {
            return typeToPropertiesMap.get(type);
        }

        Map<String, Property> properties = super.getPropertiesMap(type, bAccess);

        // Search for annotations and create instances of AnnotatedProperty in this case
        Map<String, Property> replacedMap = new LinkedHashMap<String, Property>();
        for (String name : properties.keySet()) {
            ReplacementResult replacementResult;
            try {
                replacementResult = getReplacement(properties.get(name));
                replacedMap.put(replacementResult.getName(), replacementResult.getProperty());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new YAMLException("Error while calculating a replacement property for property " + type.getTypeName() + "." + properties.get(name), e);
            }
        }

        if (caseInsensitive) {
            // Case Insensitivity feature - that's all that needs to be done for it
            replacedMap = toCaseInsensitiveMap(replacedMap);
        }

        typeToPropertiesMap.put(type, replacedMap);
        return replacedMap;
    }

    /**
     * Calculate a name/property pair that will be used instead of the default property by evaluating the annotations on the property.
     * 
     * @param defaultProperty property that was found by default
     * @return {@link ReplacementResult}
     * @throws IllegalAccessException if converter could not be accessed
     * @throws InstantiationException if converter could not be instantiated
     */
    private ReplacementResult getReplacement(Property defaultProperty)
            throws InstantiationException, IllegalAccessException {
        Property replacementProperty = defaultProperty;
        String replacementName = defaultProperty.getName();

        de.beosign.snakeyamlanno.property.YamlProperty propertyAnnotation = defaultProperty.getAnnotation(de.beosign.snakeyamlanno.property.YamlProperty.class);
        if (propertyAnnotation != null) {
            if (propertyAnnotation.converter() != NoConverter.class) {
                replacementProperty = new ConvertedProperty(replacementProperty, propertyAnnotation.converter());
            }
            if (!propertyAnnotation.key().equals("")) {
                replacementName = propertyAnnotation.key();
                replacementProperty = new AliasedProperty(replacementProperty, replacementName);
            }

            if (propertyAnnotation.skipAtLoad()) {
                replacementProperty = new SkippedProperty(replacementProperty);
            }

            if (!(replacementProperty instanceof AnnotatedProperty)) {
                // be sure to use the AnnotatedProperty as base class, so common functionality works
                replacementProperty = new AnnotatedProperty(replacementProperty);
            }

        }

        return new ReplacementResult(replacementName, replacementProperty);
    }

    /**
     * Taken from <a href="https://stackoverflow.com/questions/8236945/case-insensitive-string-as-hashmap-key">here</a>.
     * 
     * @param <T> type of values in map
     * @return a case insensitive map
     */
    private static <T> Map<String, T> caseInsensitiveMap() {
        return new TreeMap<String, T>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Creates a new map that is case insensitive and contains all entries of the given map.
     * 
     * @param <T> type of values in map
     * @param map map
     * @return case insensitive version of given map
     */
    private static <T> Map<String, T> toCaseInsensitiveMap(Map<String, T> map) {
        Map<String, T> caseInsensitiveMap = caseInsensitiveMap();
        caseInsensitiveMap.putAll(map);
        return caseInsensitiveMap;
    }

    /**
     * Name/YamlProperty value holder.
     * 
     * @author florian
     */
    private static final class ReplacementResult {
        private final Property property;
        private final String name;

        /**
         * New ReplacementResult.
         * 
         * @param name of the property (alias)
         * @param property property
         */
        private ReplacementResult(String name, Property property) {
            this.name = name;
            this.property = property;
        }

        public String getName() {
            return name;
        }

        public Property getProperty() {
            return property;
        }
    }
}
