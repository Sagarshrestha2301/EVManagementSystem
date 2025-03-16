package com.taylorsuniversity.ev.routeplanning;

import com.taylorsuniversity.ev.charginginfrastructure.ChargingStation;
import com.taylorsuniversity.ev.util.Location;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Trip implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tripId;
    private String userEmail;
    private Location startLocation;
    private Location endLocation;
    private transient List<Location> waypoints;
    private List<ChargingStation> chargingStops;
    private double distance; // km
    private double energyConsumption; // kWh
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double averageSpeed; // km/h
    private String vehicleModel;
    private double batteryRange; // km
    private double currentChargeLevel; // %

    // Constructor used by TripPlanningPanel
    public Trip(Location start, Location end, double distance, List<ChargingStation> chargingStops) {
        this("TRIP_" + System.currentTimeMillis(), "unknown@example.com", start, end, new ArrayList<>(),
                chargingStops, distance, 0.0, "Unknown", 300.0, 100.0);
    }

    public Trip(String tripId, String userEmail, Location startLocation, Location endLocation, List<Location> waypoints) {
        this(tripId, userEmail, startLocation, endLocation, waypoints, new ArrayList<>(), 0.0, 0.0, "Unknown", 300.0, 100.0);
    }

    public Trip(String tripId, String userEmail, Location startLocation, Location endLocation, List<Location> waypoints,
                List<ChargingStation> chargingStops, double distance, double energyConsumption,
                String vehicleModel, double batteryRange, double currentChargeLevel) {
        validateInputs(distance, energyConsumption, batteryRange, currentChargeLevel);
        this.tripId = tripId != null ? tripId : "TRIP_DEFAULT_" + System.currentTimeMillis();
        this.userEmail = userEmail != null ? userEmail : "unknown@example.com";
        this.startLocation = startLocation != null ? startLocation : new Location("Unknown Start", 0.0, 0.0);
        this.endLocation = endLocation != null ? endLocation : new Location("Unknown End", 0.0, 0.0);
        this.waypoints = (waypoints != null) ? new ArrayList<>(waypoints) : new ArrayList<>();
        this.chargingStops = (chargingStops != null) ? new ArrayList<>(chargingStops) : new ArrayList<>();
        this.distance = distance;
        this.energyConsumption = energyConsumption;
        this.vehicleModel = vehicleModel != null ? vehicleModel : "Unknown";
        this.batteryRange = batteryRange;
        this.currentChargeLevel = currentChargeLevel;
        this.startTime = LocalDateTime.now();
        this.endTime = null;
        this.averageSpeed = 0.0;
    }

    private void validateInputs(double distance, double energyConsumption, double batteryRange, double currentChargeLevel) {
        if (distance < 0) throw new IllegalArgumentException("Distance cannot be negative");
        if (energyConsumption < 0) throw new IllegalArgumentException("Energy consumption cannot be negative");
        if (batteryRange < 0) throw new IllegalArgumentException("Battery range cannot be negative");
        if (currentChargeLevel < 0 || currentChargeLevel > 100) throw new IllegalArgumentException("Charge level must be between 0 and 100");
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        waypoints = new ArrayList<>();
    }

    public String getUserEmail() { return userEmail; }
    public String getTripId() { return tripId; }
    public Location getStartLocation() { return startLocation; }
    public Location getEndLocation() { return endLocation; }
    public List<Location> getWaypoints() { return waypoints != null ? new ArrayList<>(waypoints) : new ArrayList<>(); }
    public List<ChargingStation> getChargingStops() { return chargingStops != null ? new ArrayList<>(chargingStops) : new ArrayList<>(); }
    public double getDistance() { return distance; }
    public double getEnergyConsumption() { return energyConsumption; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public double getAverageSpeed() { return averageSpeed; }
    public String getVehicleModel() { return vehicleModel; }
    public double getBatteryRange() { return batteryRange; }
    public double getCurrentChargeLevel() { return currentChargeLevel; }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        if (startTime != null && endTime != null && distance > 0) {
            long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
            if (minutes > 0) {
                this.averageSpeed = distance / (minutes / 60.0);
            }
        }
    }

    public void setEnergyConsumption(double energyConsumption) {
        if (energyConsumption < 0) throw new IllegalArgumentException("Energy consumption cannot be negative");
        this.energyConsumption = energyConsumption;
    }

    public void setAverageSpeed(double averageSpeed) {
        if (averageSpeed < 0) throw new IllegalArgumentException("Average speed cannot be negative");
        this.averageSpeed = averageSpeed;
    }

    public void setCurrentChargeLevel(double currentChargeLevel) {
        if (currentChargeLevel < 0 || currentChargeLevel > 100) throw new IllegalArgumentException("Charge level must be 0-100");
        this.currentChargeLevel = currentChargeLevel;
    }

    public void setBatteryRange(double batteryRange) {
        if (batteryRange < 0) throw new IllegalArgumentException("Battery range cannot be negative");
        this.batteryRange = batteryRange;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel != null ? vehicleModel : "Unknown";
    }

    public void addChargingStop(ChargingStation stop) {
        if (chargingStops == null) chargingStops = new ArrayList<>();
        if (stop != null) chargingStops.add(stop);
    }

    public void addWaypoint(Location waypoint) {
        if (waypoints == null) waypoints = new ArrayList<>();
        if (waypoint != null) waypoints.add(waypoint);
    }

    public long getDurationInMinutes() {
        if (startTime == null || endTime == null) return 0;
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    public boolean isFeasible() {
        double consumptionRate = 0.15; // kWh/km
        double requiredEnergy = distance * consumptionRate;
        double availableEnergy = (currentChargeLevel / 100.0) * (batteryRange * consumptionRate);
        int stopsNeeded = chargingStops != null ? chargingStops.size() : 0;
        double totalAvailableEnergy = availableEnergy + (stopsNeeded * batteryRange * consumptionRate * 0.8);
        return totalAvailableEnergy >= requiredEnergy;
    }

    @Override
    public String toString() {
        String chargingInfo = chargingStops.isEmpty() ? "No charging needed" : "Charging at " + chargingStops.size() + " stops";
        return "Trip{id=" + tripId + ", user=" + userEmail + ", start=" + startLocation + ", end=" + endLocation +
                ", distance=" + distance + "km, energy=" + energyConsumption + "kWh, " + chargingInfo + "}";
    }

    public void setRouteDetails(double totalDistanceKm, double energyConsumptionKWh) {
        if (totalDistanceKm < 0 || energyConsumptionKWh < 0) {
            throw new IllegalArgumentException("Route details cannot have negative values");
        }
        this.distance = totalDistanceKm;
        this.energyConsumption = energyConsumptionKWh;
        if (endTime != null && startTime != null && totalDistanceKm > 0) {
            long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
            if (minutes > 0) {
                this.averageSpeed = totalDistanceKm / (minutes / 60.0);
            }
        }
    }

    public double getEnergyConsumed() {
        return energyConsumption;
    }
}