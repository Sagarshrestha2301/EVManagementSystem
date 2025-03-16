package com.taylorsuniversity.ev.util;

import java.io.Serializable;

public class Location implements Serializable {
    private static final long serialVersionUID = 1L;
    private double latitude;
    private double longitude;
    private String name;

    public Location(double latitude, double longitude, String name) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid latitude or longitude values");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Location name cannot be null or empty");
        }
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name.trim();
    }

    public Location(String name, double latitude, double longitude) {
        this(latitude, longitude, name);
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name + " (" + latitude + ", " + longitude + ")";
    }
}