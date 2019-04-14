package de.beosign.snakeyamlanno.type;

import java.util.List;

import org.yaml.snakeyaml.nodes.MappingNode;

import de.beosign.snakeyamlanno.property.Person;

//CHECKSTYLE:OFF
@Type(substitutionTypeSelector = WorkingPerson3.WorkingPerson3TypeSelector.class, substitutionTypes = { WorkingPerson3.Employee.class })
public class WorkingPerson3 extends Person {

    public static class WorkingPerson3TypeSelector implements SubstitutionTypeSelector {
        @Override
        public boolean disableDefaultAlgorithm() {
            return true;
        }

        @Override
        public Class<?> getSelectedType(MappingNode node, List<? extends Class<?>> possibleTypes) {
            return Employee.class;
        }

    }

    public static class Employee extends WorkingPerson3 {
        private int salary;

        public int getSalary() {
            return salary;
        }

        public void setSalary(int salary) {
            this.salary = salary;
        }

        @Override
        public String toString() {
            return "Employee [salary=" + salary + ", getName()=" + getName() + ", getGender()=" + getGender() + "]";
        }

    }
}
