package de.beosign.snakeyamlanno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import de.beosign.snakeyamlanno.exception.AliasedYAMLException;

/**
 * Property Utils that considers defined aliases when loooking for a property by its name.
 * 
 * @author florian
 */
public class AnnotationAwarePropertyUtils extends PropertyUtils {
    private static final Logger log = LoggerFactory.getLogger(AnnotationAwarePropertyUtils.class);

    private BeanAccess beanAccess = BeanAccess.DEFAULT;

    // Store bean access as there is no getter and member is private
    @Override
    public void setBeanAccess(BeanAccess beanAccess) {
        super.setBeanAccess(beanAccess);
        this.beanAccess = beanAccess;
    }

    /**
     * Used during loading. Searches the aliases and returns the property referenced by the alias.
     */
    @Override
    public Property getProperty(Class<? extends Object> type, String name) {
        for (Property p : getProperties(type, beanAccess)) {
            de.beosign.snakeyamlanno.annotation.Property property = p.getAnnotation(de.beosign.snakeyamlanno.annotation.Property.class);
            if (property != null) {
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

}
