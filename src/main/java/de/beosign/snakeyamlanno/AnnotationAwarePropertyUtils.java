package de.beosign.snakeyamlanno;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
 * Property Utils that considers defined aliases when loooking for a property by its name.
 * 
 * @author florian
 */
public class AnnotationAwarePropertyUtils extends PropertyUtils {
    private Map<Class<?>, Map<String, Property>> typeToPropertiesMap = new HashMap<>();

    @Override
    protected Map<String, Property> getPropertiesMap(Class<?> type, BeanAccess bAccess) {
        if (typeToPropertiesMap.containsKey(type)) {
            return typeToPropertiesMap.get(type);
        }

        Map<String, Property> properties = super.getPropertiesMap(type, bAccess);

        Map<String, Property> replacedMap = new HashMap<String, Property>();
        for (String name : properties.keySet()) {
            ReplacementResult replacementResult;
            try {
                replacementResult = getReplacement(new HashSet<>(properties.values()), type, properties.get(name));
            } catch (InstantiationException | IllegalAccessException e) {
                throw new YAMLException("Error while calculating a replacement property for property " + type.getTypeName() + "." + properties.get(name), e);
            }
            if (replacementResult != null) {
                replacedMap.put(replacementResult.getName(), replacementResult.getProperty());
            }
        }

        typeToPropertiesMap.put(type, replacedMap);
        return replacedMap;
    }

    /**
     * Calculate a name/property pair that will be used instead of the default property by evaluating the annotations on the property.
     * 
     * @param properties properties
     * @param type type
     * @param defaultProperty property that was found by default
     * @return
     * @throws IllegalAccessException if converter could not be accessed
     * @throws InstantiationException if converer couuld not be instantiated
     */
    private ReplacementResult getReplacement(Set<Property> properties, Class<? extends Object> type, Property defaultProperty)
            throws InstantiationException, IllegalAccessException {
        Property replacementProperty = defaultProperty;
        String replacementName = defaultProperty.getName();

        de.beosign.snakeyamlanno.annotation.Property propertyAnnotation = defaultProperty.getAnnotation(de.beosign.snakeyamlanno.annotation.Property.class);
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
     * Name/Property value holder.
     * 
     * @author florian
     */
    private static final class ReplacementResult {
        private final Property property;
        private final String name;

        /**
         * New ReplacementResult.
         * 
         * @param property property
         */
        private ReplacementResult(Property property) {
            this(property.getName(), property);
        }

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
