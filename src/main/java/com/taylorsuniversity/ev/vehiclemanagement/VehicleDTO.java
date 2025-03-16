package com.taylorsuniversity.ev.vehiclemanagement;

public class VehicleDTO {
    private String id;
    private String model;
    private String manufacturer;
    private double batteryCapacity;
    private double remainingRange;
    private double healthStatus;

    public VehicleDTO(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.model = vehicle.getModel();
        this.manufacturer = vehicle.getManufacturer();
        this.batteryCapacity = vehicle.getBatteryCapacity();
        this.remainingRange = vehicle.getBatteryMonitoring().getRemainingRange();
        this.healthStatus = vehicle.getBatteryMonitoring().getHealthStatus();
    }

    public String getId() {
        return id;
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

    public double getRemainingRange() {
        return remainingRange;
    }

    public double getHealthStatus() {
        return healthStatus;
    }
}