package com.taylorsuniversity.ev.vehiclemanagement;

import java.io.Serializable;
import java.util.logging.Logger;

public class EmergencySystem implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EmergencySystem.class.getName());
    private boolean ecoModeActive;

    public EmergencySystem() {
        this.ecoModeActive = false;
    }

    public void activateEcoMode(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        if (!ecoModeActive) {
            ecoModeActive = true;
            double rangeReduction = vehicle.getBatteryMonitoring().getRemainingRange() * 0.1;
            vehicle.getBatteryMonitoring().updateRange(rangeReduction);
            LOGGER.info("Eco-mode activated for Vehicle " + vehicle.getId() + " to extend range.");
        }
    }

    public String checkStatus(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        if (vehicle.getBatteryMonitoring().getRemainingRange() < 20) {
            activateEcoMode(vehicle);
            return String.format("Emergency: Low range detected (%.1f km). Eco-mode enabled.", vehicle.getBatteryMonitoring().getRemainingRange());
        }
        return "No emergency detected.";
    }

    public boolean isEcoModeActive() {
        return ecoModeActive; // 
    }

    // Overloaded method
    public void activateEcoMode(Vehicle vehicle, double customReductionFactor) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        if (!ecoModeActive) {
            ecoModeActive = true;
            double rangeReduction = vehicle.getBatteryMonitoring().getRemainingRange() * customReductionFactor;
            vehicle.getBatteryMonitoring().updateRange(rangeReduction);
            LOGGER.info("Eco-mode activated for Vehicle " + vehicle.getId() + " with custom factor to extend range.");
        }
    }
}
