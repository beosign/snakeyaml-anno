package de.beosign.snakeyamlanno.property;

// CHECKSTYLE:OFF
public class StellarObject {

    private String name;
    private String type;
    private String location;
    private String distance;
    private long radius;
    private double absoluteMag;

    @YamlProperty(key = "nameAlias", order = 10)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @YamlProperty(order = -5)
    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @YamlProperty(order = 5)
    public long getRadius() {
        return radius;
    }

    public void setRadius(long radius) {
        this.radius = radius;
    }

    @YamlProperty(key = "absMag")
    public double getAbsoluteMag() {
        return absoluteMag;
    }

    public void setAbsoluteMag(double absoluteMag) {
        this.absoluteMag = absoluteMag;
    }

    @Override
    public String toString() {
        return "StellarObject [name=" + name + ", type=" + type + ", location=" + location + ", distance=" + distance + ", radius=" + radius + ", absoluteMag="
                + absoluteMag + "]";
    }

}
