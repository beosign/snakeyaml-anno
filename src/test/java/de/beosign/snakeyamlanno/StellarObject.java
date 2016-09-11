package de.beosign.snakeyamlanno;

public class StellarObject {
    private String name;
    private String type;
    private String location;
    private String distance;
    private long radius;
    private double absoluteMag;

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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public long getRadius() {
        return radius;
    }

    public void setRadius(long radius) {
        this.radius = radius;
    }

    public double getAbsoluteMag() {
        return absoluteMag;
    }

    public void setAbsoluteMag(double absoluteMag) {
        this.absoluteMag = absoluteMag;
    }

}
