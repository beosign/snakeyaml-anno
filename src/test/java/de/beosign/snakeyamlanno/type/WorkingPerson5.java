package de.beosign.snakeyamlanno.type;

import java.util.List;

import org.yaml.snakeyaml.nodes.MappingNode;

import de.beosign.snakeyamlanno.annotation.Type;
import de.beosign.snakeyamlanno.property.Person;

//CHECKSTYLE:OFF
@Type(substitutionTypeSelector = WorkingPerson5.WorkingPerson5TypeSelector.class, substitutionTypes = Person.class)
public class WorkingPerson5 extends Person {
    public static class WorkingPerson5TypeSelector implements SubstitutionTypeSelector {

        public WorkingPerson5TypeSelector(String arg) {
        }

        @Override
        public Class<?> getSelectedType(MappingNode node, List<? extends Class<?>> possibleTypes) {
            return null;
        }
    }

}
