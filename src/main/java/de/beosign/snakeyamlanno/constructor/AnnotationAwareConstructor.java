package de.beosign.snakeyamlanno.constructor;

import org.yaml.snakeyaml.constructor.Constructor;

import de.beosign.snakeyamlanno.AnnotationAwarePropertyUtils;

public class AnnotationAwareConstructor extends Constructor {
    public AnnotationAwareConstructor(Class<? extends Object> theRoot) {
        super(theRoot);
        setPropertyUtils(new AnnotationAwarePropertyUtils());
    }
}
