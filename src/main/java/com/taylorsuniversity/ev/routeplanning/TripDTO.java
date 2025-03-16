package com.taylorsuniversity.ev.routeplanning;

import com.taylorsuniversity.ev.charginginfrastructure.ChargingStation;
import com.taylorsuniversity.ev.util.Location;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TripDTO {
    private String tripId;
    private Location startLocation;
    private Location endLocation;
    private List<ChargingStation> chargingStops;
    private double distance;
    private double energyConsumption;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double averageSpeed;
    private String vehicleModel;
    private double batteryRange;
    private double currentChargeLevel;

    public TripDTO(Trip trip) {
        if (trip == null) throw new IllegalArgumentException("Trip cannot be null");
        this.tripId = trip.getTripId();
        this.startLocation = trip.getStartLocation();
        this.endLocation = trip.getEndLocation();
        this.chargingStops = trip.getChargingStops() != null ? new ArrayList<>(trip.getChargingStops()) : new ArrayList<>();
        this.distance = trip.getDistance();
        this.energyConsumption = trip.getEnergyConsumption();
        this.startTime = trip.getStartTime();
        this.endTime = trip.getEndTime();
        this.averageSpeed = trip.getAverageSpeed();
        this.vehicleModel = trip.getVehicleModel();
        this.batteryRange = trip.getBatteryRange();
        this.currentChargeLevel = trip.getCurrentChargeLevel();
    }

    public String getTripId() { return tripId; }
    public Location getStartLocation() { return startLocation; }
    public Location getEndLocation() { return endLocation; }
    public List<ChargingStation> getChargingStops() { return chargingStops != null ? new ArrayList<>(chargingStops) : new ArrayList<>(); }
    public double getDistance() { return distance; }
    public double getEnergyConsumption() { return energyConsumption; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public double getAverageSpeed() { return averageSpeed; }
    public String getVehicleModel() { return vehicleModel; }
    public double getBatteryRange() { return batteryRange; }
    public double getCurrentChargeLevel() { return currentChargeLevel; }
}