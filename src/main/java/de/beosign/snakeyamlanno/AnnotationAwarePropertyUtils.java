package de.beosign.snakeyamlanno;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import de.beosign.snakeyamlanno.property.AnnotatedFieldProperty;
import de.beosign.snakeyamlanno.property.AnnotatedMethodProperty;

public class AnnotationAwarePropertyUtils extends PropertyUtils {
    private BeanAccess beanAccess = BeanAccess.DEFAULT;
    private Map<String, Property> enhancedPropertiesMap = new LinkedHashMap<>();
    private boolean initialized = false;

    // Store bean access as there is no getter and member is private
    @Override
    public void setBeanAccess(BeanAccess beanAccess) {
        super.setBeanAccess(beanAccess);
        this.beanAccess = beanAccess;
    }

    @Override
    public Set<Property> getProperties(Class<? extends Object> type, BeanAccess bAccess) throws IntrospectionException {
        Set<Property> propertySet = super.getProperties(type, bAccess);

        for (Property p : propertySet) {
            String name = p.getName();

        }

        return propertySet;
    }

    @Override
    public Property getProperty(Class<? extends Object> type, String name) throws IntrospectionException {
        if (!initialized) {
            initialize(type);
        }

        return super.getProperty(type, name);

    }

    private void initialize(Class<? extends Object> type) throws IntrospectionException {
        Map<String, Property> propertiesMap = getPropertiesMap(type, beanAccess);
        for (String propertyName : propertiesMap.keySet()) {
            Field field = Arrays.asList(type.getDeclaredFields()).stream()
                    .filter(f -> f.getName().equals(propertyName))
                    .findFirst()
                    .orElse(null);
            System.out.println("Found field: " + field);

            de.beosign.snakeyamlanno.annotation.Property property = field.getAnnotation(de.beosign.snakeyamlanno.annotation.Property.class);
            if (property != null) {
                AnnotatedFieldProperty fieldProperty = new AnnotatedFieldProperty(field);
                enhancedPropertiesMap.put(propertyName, fieldProperty);
            } else {
                enhancedPropertiesMap.put(propertyName, propertiesMap.get(propertyName));
            }

            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(type).getPropertyDescriptors();
            PropertyDescriptor propertyDescriptor = Arrays.asList(propertyDescriptors).stream()
                    .filter(pd -> pd.getName().equals(propertyName))
                    .findFirst()
                    .orElse(null);
            System.out.println("Found method: " + propertyDescriptor);

            property = propertyDescriptor.getReadMethod().getAnnotation(de.beosign.snakeyamlanno.annotation.Property.class);
            if (property != null) {
                AnnotatedMethodProperty methodProperty = new AnnotatedMethodProperty(propertyDescriptor);
                enhancedPropertiesMap.put(propertyName, methodProperty);
            } else {
                enhancedPropertiesMap.put(propertyName, propertiesMap.get(propertyName));
            }
        }
        initialized = true;
        System.out.println("Enhanced Properties Map:\n" + enhancedPropertiesMap);
    }
}
