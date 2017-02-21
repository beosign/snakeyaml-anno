package de.beosign.snakeyamlanno;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import de.beosign.snakeyamlanno.exception.AliasedYAMLException;
import de.beosign.snakeyamlanno.property.AnnotatedFieldProperty;
import de.beosign.snakeyamlanno.property.AnnotatedMethodProperty;
import de.beosign.snakeyamlanno.property.AnnotatedProperty;

/**
 * Property Utils that instantiates property subclasses of {@link AnnotatedProperty}. These properties have an annotation where
 * mapping and converting information can be set.
 * 
 * @author florian
 */
public class AnnotationAwarePropertyUtils extends PropertyUtils {
    private static final Logger log = LoggerFactory.getLogger(AnnotationAwarePropertyUtils.class);

    private BeanAccess beanAccess = BeanAccess.DEFAULT;
    private Map<Class<?>, Map<String, Property>> typeToAnotatedPropertiesMap = new LinkedHashMap<>();
    private Map<Class<?>, Boolean> classInitialized = new HashMap<>();

    // Store bean access as there is no getter and member is private
    @Override
    public void setBeanAccess(BeanAccess beanAccess) {
        super.setBeanAccess(beanAccess);
        this.beanAccess = beanAccess;
    }

    /**
     * Used during dumping. Searches the type for annotations and then replaces each property by its {@link AnnotatedProperty} version.
     */
    @Override
    public Set<Property> getProperties(Class<? extends Object> type, BeanAccess bAccess) throws IntrospectionException {
        if (!classInitialized.getOrDefault(type, false)) {
            initialize(type);
            classInitialized.put(type, true);
        }

        Set<Property> originSet = super.getProperties(type, bAccess);
        originSet.removeIf(origin -> typeToAnotatedPropertiesMap.get(type).containsValue(origin));
        originSet.addAll(typeToAnotatedPropertiesMap.get(type).values());
        return originSet;
    }

    /**
     * Used during loading. Searches the type for annotations and then returns the {@link AnnotatedProperty} instead of the normal one (if property is
     * annotated).
     */
    @Override
    public Property getProperty(Class<? extends Object> type, String name) throws IntrospectionException {
        if (!classInitialized.getOrDefault(type, false)) {
            initialize(type);
            classInitialized.put(type, true);
        }

        for (Property p : typeToAnotatedPropertiesMap.get(type).values()) {
            if (p instanceof AnnotatedProperty) {
                AnnotatedProperty annotatedProperty = (AnnotatedProperty) p;
                de.beosign.snakeyamlanno.annotation.Property property = annotatedProperty.getPropertyAnnotation();

                if (property.key().equals("") && p.getName().equals(name)) {
                    // key was not aliased, so compare with field name
                    return p;
                }
                if (name.equals(property.key())) {
                    log.trace("Type: {}, property {} is mapped to {}", type.getName(), name, property.key());
                    return p;
                }
                if (name.equals(p.getName())) {
                    throw new AliasedYAMLException("Property '" + name + "' on class: "
                            + type.getName() + " was found, but has been aliased to " + property.key() + ", so it is not considered visible.", name,
                            property.key());
                }

            }
        }
        return super.getProperty(type, name);
    }

    /**
     * Scans the given type for {@link de.beosign.snakeyamlanno.annotation.Property} annotations and stores a mapping from a property name to its property
     * including annotation in a map.
     * 
     * @param type type
     * @throws IntrospectionException on {@link IntrospectionException}
     */
    private void initialize(Class<? extends Object> type) throws IntrospectionException {
        log.debug("Initializing AnnotatedPropertiesMap for type {}", type.getName());
        Map<String, Property> propertiesMap = getPropertiesMap(type, beanAccess);

        Map<String, Property> annotatedPropertiesMap = typeToAnotatedPropertiesMap.get(type);
        if (annotatedPropertiesMap == null) {
            annotatedPropertiesMap = new HashMap<>();
            typeToAnotatedPropertiesMap.put(type, annotatedPropertiesMap);
        }

        for (String propertyName : propertiesMap.keySet()) {
            Field field = Arrays.asList(type.getDeclaredFields()).stream()
                    .filter(f -> f.getName().equals(propertyName))
                    .findFirst()
                    .orElse(null);
            log.trace("Type: {}, found field {} for property {}", type.getName(), field, propertyName);

            de.beosign.snakeyamlanno.annotation.Property property = null;
            if (field != null) {
                // may be in superclass
                property = field.getAnnotation(de.beosign.snakeyamlanno.annotation.Property.class);
                if (property != null) {
                    AnnotatedFieldProperty fieldProperty = new AnnotatedFieldProperty(field);
                    annotatedPropertiesMap.put(propertyName, fieldProperty);
                }
            }

            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(type).getPropertyDescriptors();
            PropertyDescriptor propertyDescriptor = Arrays.asList(propertyDescriptors).stream()
                    .filter(pd -> pd.getName().equals(propertyName))
                    .findFirst()
                    .orElse(null);
            log.trace("Type: {}, found method {} for property {}", type.getName(), propertyDescriptor, propertyName);

            if (propertyDescriptor != null) {
                property = propertyDescriptor.getReadMethod().getAnnotation(de.beosign.snakeyamlanno.annotation.Property.class);
                if (property != null) {
                    AnnotatedMethodProperty methodProperty = new AnnotatedMethodProperty(propertyDescriptor);
                    annotatedPropertiesMap.put(propertyName, methodProperty);
                }
            }
        }
        log.debug("Annotated Properties Map for type " + type + ":\n" + annotatedPropertiesMap);
    }
}
