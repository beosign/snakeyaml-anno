package de.beosign.snakeyamlanno;

import java.util.List;

import de.beosign.snakeyamlanno.annotation.Property;

//CHECKSTYLE:OFF
public class Universe {
    private List<StellarObject> stellarObjects;

    private double universeAge;

    public List<StellarObject> getStellarObjects() {
        return stellarObjects;
    }

    public void setStellarObjects(List<StellarObject> stellarObjects) {
        this.stellarObjects = stellarObjects;
    }

    @Property(key = "age")
    public double getUniverseAge() {
        return universeAge;
    }

    public void setUniverseAge(double universeAge) {
        this.universeAge = universeAge;
    }

}
