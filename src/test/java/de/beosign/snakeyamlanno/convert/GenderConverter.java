package de.beosign.snakeyamlanno.convert;

import java.util.Objects;

import de.beosign.snakeyamlanno.property.Person.Gender;

/**
 * Test converter.
 * 
 * @author florian
 */
public class GenderConverter implements Converter<Gender> {

    @Override
    public String convertToYaml(Gender modelValue) {
        return modelValue.getAbbr();
    }

    @Override
    public Gender convertToModel(Object value) {
        for (Gender g : Gender.values()) {
            if (g.getAbbr().equals(Objects.toString(value, null))) {
                return g;
            }
        }
        return null;
    }
}
