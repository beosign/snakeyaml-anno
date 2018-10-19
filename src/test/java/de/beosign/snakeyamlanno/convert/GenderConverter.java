package de.beosign.snakeyamlanno.convert;

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
    public Gender convertToModel(String value) {
        for (Gender g : Gender.values()) {
            if (g.getAbbr().equals(value)) {
                return g;
            }
        }
        return null;
    }
}
