package com.taylorsuniversity.ev.vehiclemanagement;

import java.io.Serializable;
import java.util.logging.Logger;

public class BatteryMonitoring implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(BatteryMonitoring.class.getName());

    private double healthStatus; // 0-100%, represents battery degradation
    private double remainingRange; // in kilometers
    private final double initialRange; // Immutable initial range
    private int chargeCycles;

    public BatteryMonitoring(double initialHealth, double initialRange) {
        if (initialRange <= 0) {
            throw new IllegalArgumentException("Initial range must be positive.");
        }
        this.healthStatus = Math.max(0, Math.min(100, initialHealth));
        this.remainingRange = initialRange;
        this.initialRange = initialRange; // Store immutable initial range
        this.chargeCycles = 0;


    }


    public double getHealthStatus() {
        return healthStatus;
    }

    public double getRemainingRange() {
        return remainingRange;
    }

    public double getInitialRange() {
        return initialRange; // Fixed missing return type and implementation
    }

    public int getChargeCycles() {
        return chargeCycles;
    }


    public void updateRange(double distanceTraveled) {
        if (distanceTraveled < 0) {
            throw new IllegalArgumentException("Distance traveled cannot be negative.");
        }
        remainingRange = Math.max(0, remainingRange - distanceTraveled);
    }
    public void chargeBattery(double chargeAmount) {
        if (chargeAmount < 0) {
            throw new IllegalArgumentException("Charge amount cannot be negative.");
        }
        remainingRange = Math.min(remainingRange + chargeAmount, initialRange); // Cap at initial range
        chargeCycles++;
        healthStatus = Math.max(0, healthStatus - 0.1); // Simulate degradation
        LOGGER.info(String.format("Battery charged by %.1f km. Health: %.1f%%, Cycles: %d", chargeAmount, healthStatus, chargeCycles));
    }

    public String getMaintenanceAlert() {
        if (healthStatus < 80) {
            return String.format("Battery health below 80%%: %.1f%%. Consider maintenance.", healthStatus);
        } else if (remainingRange < 20) {
            return String.format("Low range warning: %.1f km remaining.", remainingRange);
        }
        return "Battery status normal.";
    }

    public String getChargeStatus() {
        double chargePercentage = (remainingRange / initialRange) * 100;
        return String.format("%.1f%% (%.1f km / %.1f km)", chargePercentage, remainingRange, initialRange);
    }

}