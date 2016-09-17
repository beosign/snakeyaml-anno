package de.beosign.snakeyamlanno;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import de.beosign.snakeyamlanno.exception.AliasedYAMLException;
import de.beosign.snakeyamlanno.property.AnnotatedFieldProperty;
import de.beosign.snakeyamlanno.property.AnnotatedMethodProperty;

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

    @Override
    public Property getProperty(Class<? extends Object> type, String name) throws IntrospectionException {
        if (!classInitialized.getOrDefault(type, false)) {
            initialize(type);
            classInitialized.put(type, true);
        }

        for (Property p : typeToAnotatedPropertiesMap.get(type).values()) {
            if (p instanceof AnnotatedFieldProperty) {
                AnnotatedFieldProperty fp = (AnnotatedFieldProperty) p;
                de.beosign.snakeyamlanno.annotation.Property property = fp.getPropertyAnnotation();

                if (!property.key().equals("")) {
                    if (property.key().equals("") && fp.getName().equals(name)) {
                        // key was not aliased, so compare with field name
                        return p;
                    }
                    if (name.equals(property.key())) {
                        // return super.getProperty(type, fp.getName());
                        return p;
                    }
                    if (name.equals(fp.getName())) {
                        throw new AliasedYAMLException("Property '" + name + "' on class: "
                                + type.getName() + " was found, but has been aliased to " + property.key() + ", so it is not considered visible.", name,
                                property.key());
                    }
                }

            } else if (p instanceof AnnotatedMethodProperty) {
                AnnotatedMethodProperty mp = (AnnotatedMethodProperty) p;

                de.beosign.snakeyamlanno.annotation.Property property = mp.getReadMethodPropertyAnnotation();
                if (property != null) {
                    if (property.key().equals("") && mp.getName().equals(name)) {
                        // key was not aliased, so compare with method name
                        return p;
                    }
                    if (property != null && name.equals(property.key())) {
                        return p;
                    }

                    if (name.equals(mp.getName())) {
                        throw new AliasedYAMLException("Property '" + name + "' on class: "
                                + type.getName() + " was found, but has been aliased to " + property.key() + ", so it is not considered visible.", name,
                                property.key());
                    }

                }

                property = mp.getWriteMethodPropertyAnnotation();
                if (property != null) {
                    if (property.key().equals("") && mp.getName().equals(name)) {
                        // key was not aliased, so compare with method name
                        return p;
                    }
                    if (property != null && name.equals(property.key())) {
                        // return super.getProperty(type, mp.getName());
                        return p;
                    }

                    if (name.equals(mp.getName())) {
                        throw new AliasedYAMLException("Property '" + name + "' on class: "
                                + type.getName() + " was found, but has been aliased to " + property.key() + ", so it is not considered visible.", name,
                                property.key());
                    }
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
            log.trace("Found field: " + field + " for property " + propertyName);

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
            log.trace("Found method: " + propertyDescriptor + " for property " + propertyName);

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
