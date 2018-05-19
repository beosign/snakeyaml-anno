package de.beosign.snakeyamlanno.type;

import de.beosign.snakeyamlanno.annotation.Type;
import de.beosign.snakeyamlanno.property.Person;

//CHECKSTYLE:OFF
@Type(substitutionTypes = { WorkingPerson.Employee.class, WorkingPerson.Employer.class })
public class WorkingPerson extends Person {

    public static class Employee extends WorkingPerson {
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

    public static class Employer extends WorkingPerson {
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
