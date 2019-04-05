package de.beosign.snakeyamlanno.annotation;

import java.lang.annotation.Annotation;

import de.beosign.snakeyamlanno.type.Instantiator;
import de.beosign.snakeyamlanno.type.NoSubstitutionTypeSelector;
import de.beosign.snakeyamlanno.type.SubstitutionTypeSelector;

/**
 * Implementation for {@link Type} annotation.
 * 
 * @author florian
 */
@SuppressWarnings({ "all" })
public class TypeImpl implements Type {
    private Class<?>[] substitutionTypes;
    private Class<? extends SubstitutionTypeSelector> typeSelector;
    private Class<? extends Instantiator> instantiatorClass;

    public TypeImpl(Class<?>[] substitutionTypes) {
        this(substitutionTypes, NoSubstitutionTypeSelector.class);
    }

    public TypeImpl(Class<?>[] substitutionTypes, Class<? extends SubstitutionTypeSelector> typeSelector) {
        this(substitutionTypes, typeSelector, Instantiator.class);
    }

    public TypeImpl(Class<?>[] substitutionTypes, Class<? extends SubstitutionTypeSelector> typeSelector, Class<? extends Instantiator> instantiatorClass) {
        this.substitutionTypes = substitutionTypes;
        this.typeSelector = typeSelector;
        this.instantiatorClass = instantiatorClass;
    }

    public TypeImpl(Class<? extends Instantiator> instantiatorClass) {
        this.substitutionTypes = new Class<?>[0];
        this.typeSelector = NoSubstitutionTypeSelector.class;
        this.instantiatorClass = instantiatorClass;
    }

    @Override
    public Class<?>[] substitutionTypes() {
        return substitutionTypes;
    }

    @Override
    public Class<? extends SubstitutionTypeSelector> substitutionTypeSelector() {
        return typeSelector;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Type.class;
    }

    @Override
    public Class<? extends Instantiator> instantiator() {
        return instantiatorClass;
    }

}
