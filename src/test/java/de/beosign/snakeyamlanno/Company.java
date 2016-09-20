package de.beosign.snakeyamlanno;

import java.util.List;

public class Company {
    private List<WorkingPerson> workingPersons;

    public List<WorkingPerson> getWorkingPersons() {
        return workingPersons;
    }

    public void setWorkingPersons(List<WorkingPerson> workingPersons) {
        this.workingPersons = workingPersons;
    }

    @Override
    public String toString() {
        return "Company [workingPersons=" + workingPersons + "]";
    }

}
