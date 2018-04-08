package de.beosign.snakeyamlanno.annotation;

import java.lang.annotation.Annotation;

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

    public TypeImpl(Class<?>[] substitutionTypes) {
        this(substitutionTypes, NoSubstitutionTypeSelector.class);
    }

    public TypeImpl(Class<?>[] substitutionTypes, Class<? extends SubstitutionTypeSelector> typeSelector) {
        this.substitutionTypes = substitutionTypes;
        this.typeSelector = typeSelector;
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

}
