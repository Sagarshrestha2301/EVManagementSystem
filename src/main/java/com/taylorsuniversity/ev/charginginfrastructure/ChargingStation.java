package com.taylorsuniversity.ev.charginginfrastructure;

import com.taylorsuniversity.ev.util.Location;
import com.taylorsuniversity.ev.util.Observer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChargingStation implements Serializable {
    private static final long serialVersionUID = 1L;
    public double getCostPerKWh;

    private String stationId;
    private String name;
    private String status;
    private String chargerType;
    private String powerOutput;
    private String availablePorts;
    private Location location;
    private double costPerKWh;
    private List<Observer> observers; // List of observers

    public ChargingStation(String stationId, String name, String status, String chargerType,
                           String powerOutput, String availablePorts, double latitude, double longitude, double costPerKWh) {
        this.stationId = stationId;
        this.name = name;
        this.status = status;
        this.chargerType = chargerType;
        this.powerOutput = powerOutput;
        this.availablePorts = availablePorts;
        this.location = new Location(name, latitude, longitude);
        this.costPerKWh = costPerKWh >= 0 ? costPerKWh : 0;
        this.observers = new ArrayList<>();
    }

    public ChargingStation(String stationId, Location location) {
        this.stationId = stationId;
        this.name = location.getName();
        this.status = "AVAILABLE";
        this.chargerType = "CCS";
        this.powerOutput = "50kW";
        this.availablePorts = "2/2";
        this.location = location;
        this.costPerKWh = 0.25;
        this.observers = new ArrayList<>();
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
    }

    public ChargingStation(String name, double latitude, double longitude) {
        this.name = name;
    }

    // Add an observer
    public void addObserver(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    // Remove an observer
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    // Notify all observers of a change
    private void notifyObservers() {
        String message = "Charging Station " + stationId + " - Status: " + status + ", Ports: " + availablePorts;
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    // Modified setters to notify observers
    public void setStatus(String status) {
        this.status = status;
        notifyObservers();
    }

    public void setAvailablePorts(String availablePorts) {
        this.availablePorts = availablePorts;
        notifyObservers();
    }

    // Existing getters and setters (unchanged)
    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public String getChargerType() { return chargerType; }
    public void setChargerType(String chargerType) { this.chargerType = chargerType; }
    public String getPowerOutput() { return powerOutput; }
    public void setPowerOutput(String powerOutput) { this.powerOutput = powerOutput; }
    public String getAvailablePorts() { return availablePorts; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) {
        if (location == null) throw new IllegalArgumentException("Location cannot be null");
        this.location = location;
    }
    public double getCostPerKWh() { return costPerKWh; }
    public void setCostPerKWh(double costPerKWh) { this.costPerKWh = costPerKWh >= 0 ? costPerKWh : 0; }
    public double getLatitude() { return location.getLatitude(); }
    public double getLongitude() { return location.getLongitude(); }

    @Override
    public String toString() {
        return stationId + "," + name + "," + status + "," + chargerType + "," +
                powerOutput + "," + availablePorts + "," + location.getLatitude() + "," +
                location.getLongitude() + "," + costPerKWh;
    }
}