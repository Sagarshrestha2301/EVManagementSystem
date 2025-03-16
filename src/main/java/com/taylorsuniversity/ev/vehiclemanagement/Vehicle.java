package com.taylorsuniversity.ev.vehiclemanagement;

import java.io.Serializable;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private String vehicleId;
    private String model;
    private String manufacturer;
    private double batteryCapacity; // in kWh
    private BatteryMonitoring batteryMonitoring;
    private EmergencySystem emergencySystem;

    public Vehicle(String vehicleId, String model, String manufacturer, double batteryCapacity, double initialRange) {
        if (vehicleId == null || model == null || manufacturer == null || batteryCapacity <= 0 || initialRange <= 0) {
            throw new IllegalArgumentException("Invalid vehicle parameters.");
        }
        this.vehicleId = vehicleId;
        this.model = model;
        this.manufacturer = manufacturer;
        this.batteryCapacity = batteryCapacity;
        this.batteryMonitoring = new BatteryMonitoring(100, initialRange); // 100% health initially
        this.emergencySystem = new EmergencySystem();
    }

    public void travel(double distance) {
        batteryMonitoring.updateRange(distance);
        emergencySystem.checkStatus(this);
    }

    public void charge(double chargeAmount) {
        batteryMonitoring.chargeBattery(chargeAmount
        );
    }


    public Vehicle(String vehicleId, BatteryMonitoring batteryMonitoring, EmergencySystem emergencySystem) {
        if (vehicleId == null || batteryMonitoring == null || emergencySystem == null) {
            throw new IllegalArgumentException("Invalid vehicle parameters.");
        }
        this.vehicleId = vehicleId;
        this.model = "DefaultModel"; // Default values for incomplete constructor
        this.manufacturer = "DefaultManufacturer";
        this.batteryCapacity = 50.0; // Default capacity in kWh
        this.batteryMonitoring = batteryMonitoring;
        this.emergencySystem = emergencySystem;
    }

    public String getId() {
        return vehicleId;
    }

    public String getModel() {
        return model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public double getBatteryCapacity() {
        return batteryCapacity;
    }

    public BatteryMonitoring getBatteryMonitoring() {
        return batteryMonitoring;
    }

    public EmergencySystem getEmergencySystem() {
        return emergencySystem;
    }


}