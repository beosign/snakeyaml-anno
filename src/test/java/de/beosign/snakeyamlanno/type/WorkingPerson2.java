package de.beosign.snakeyamlanno.type;

import java.util.List;

import org.yaml.snakeyaml.nodes.MappingNode;

import de.beosign.snakeyamlanno.property.Person;

//CHECKSTYLE:OFF
@Type(substitutionTypeSelector = WorkingPerson2.WorkingPerson2TypeSelector.class,
        substitutionTypes = { WorkingPerson2.Employee.class, WorkingPerson2.Employer.class })
public class WorkingPerson2 extends Person {

    public static class WorkingPerson2TypeSelector implements SubstitutionTypeSelector {
        private List<? extends Class<?>> possibleTypes;

        @Override
        public Class<?> getSelectedType(MappingNode node, List<? extends Class<?>> possibleTypes) {
            // store for unit tests
            this.possibleTypes = possibleTypes;
            return possibleTypes.get(1);
        }

        public List<? extends Class<?>> getPossibleTypes() {
            return possibleTypes;
        }
    }

    public static class Employee extends WorkingPerson2 {
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

    public static class Employer extends WorkingPerson2 {
        private int nrEmployees;

        public int getNrEmployees() {
            return nrEmployees;
        }

        public void setNrEmployees(int nrEmployees) {
            this.nrEmployees = nrEmployees;
        }

        @Override
        public String toString() {
            return "Employer [nrEmployees=" + nrEmployees + ", getName()=" + getName() + ", getGender()=" + getGender() + ", getAnimal()=" + getAnimal()
                    + ", getHeight()=" + getHeight() + "]";
        }

    }
}
