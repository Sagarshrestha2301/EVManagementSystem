package com.taylorsuniversity.ev.analytics;

import java.time.LocalDate;

public class EnvironmentalImpact {
    private String tripId;
    private LocalDate date;
    private double carbonSaved;
    private double energyConsumed;
    private double distanceKm;

    private static final double PETROL_CO2_PER_LITER = 2.31; // kg/L
    private static final double PETROL_EFFICIENCY = 0.08; // L/km
    private static final double GRID_CO2_PER_KWH = 0.02; // kg/kWh
    private static final double EV_EFFICIENCY = 0.2; // kWh/km

    public EnvironmentalImpact(String tripId, LocalDate date, double distanceKm) {
        if (tripId == null || tripId.trim().isEmpty())
            throw new IllegalArgumentException("Trip ID cannot be null or empty");
        if (date == null) throw new IllegalArgumentException("Date cannot be null");
        if (distanceKm < 0) throw new IllegalArgumentException("Distance cannot be negative");
        this.tripId = tripId;
        this.date = date;
        this.distanceKm = distanceKm;
        this.energyConsumed = distanceKm * EV_EFFICIENCY;
        this.carbonSaved = calculateCarbonSaved(distanceKm);
    }

    private double calculateCarbonSaved(double distanceKm) {
        double petrolCO2 = distanceKm * PETROL_EFFICIENCY * PETROL_CO2_PER_LITER;
        double evCO2 = energyConsumed * GRID_CO2_PER_KWH;
        return petrolCO2 - evCO2;
    }

    public double getEfficiency() {
        return distanceKm == 0 ? 0 : energyConsumed / distanceKm;
    }

    public String getDrivingTip() {
        if (getEfficiency() > EV_EFFICIENCY * 1.2) {
            return "Drive slower and avoid rapid acceleration to improve efficiency.";
        }
        return "Great job! Keep maintaining steady speeds for optimal efficiency.";
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId != null && !tripId.trim().isEmpty() ? tripId : this.tripId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date != null ? date : this.date;
    }

    public double getCarbonSaved() {
        return carbonSaved;
    }

    public double getEnergyConsumed() {
        return energyConsumed;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getDistance() {
        return distanceKm;
    }

    public double calculateCarbonSaved(double distanceKm, double customGridCO2) {
        double petrolCO2 = distanceKm * PETROL_EFFICIENCY * PETROL_CO2_PER_LITER;
        double evCO2 = energyConsumed * customGridCO2;
        return petrolCO2 - evCO2;
    }
}