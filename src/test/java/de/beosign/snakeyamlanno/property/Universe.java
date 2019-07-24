package de.beosign.snakeyamlanno.property;

import java.util.List;

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

    @YamlProperty(key = "age")
    public double getUniverseAge() {
        return universeAge;
    }

    public void setUniverseAge(double universeAge) {
        this.universeAge = universeAge;
    }

}
