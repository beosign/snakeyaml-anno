package de.beosign.snakeyamlanno.representer;

import java.beans.IntrospectionException;
import java.util.Iterator;
import java.util.Set;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import de.beosign.snakeyamlanno.AnnotationAwarePropertyUtils;
import de.beosign.snakeyamlanno.property.AnnotatedProperty;

public class AnnotationAwareRepresenter extends Representer {

    public AnnotationAwareRepresenter() {
        setPropertyUtils(new AnnotationAwarePropertyUtils());
    }

    @Override
    protected Set<Property> getProperties(Class<? extends Object> type) throws IntrospectionException {
        Set<Property> propertySet = super.getProperties(type);

        // Remove properties that must be skipped during dumping
        Iterator<Property> iterator = propertySet.iterator();
        while (iterator.hasNext()) {
            Property prop = iterator.next();

            if (prop instanceof AnnotatedProperty) {
                AnnotatedProperty annotatedProperty = (AnnotatedProperty) prop;
                if (annotatedProperty.getPropertyAnnotation().skipAtDump()) {
                    iterator.remove();
                }
            }
        }

        return propertySet;
    }

    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        // TODO Auto-generated method stub
        return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
    }

}
