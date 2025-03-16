package com.taylorsuniversity.ev.analytics;

import java.io.Serializable;

public class CostAnalysis implements Serializable {
    private static final long serialVersionUID = 1L;

    private double evEnergyCost;
    private double petrolFuelCost;
    private double evMaintenance;
    private double petrolMaintenance;
    private double evInsurance;
    private double petrolInsurance;
    private double monthlyEvCost;
    private double monthlyPetrolCost;
    private double savingsTarget;

    private static final double ELECTRICITY_RATE = 9.0; // NPR/kWh
    private static final double PETROL_PRICE = 170.0; // NPR/L
    private static final double EV_EFFICIENCY = 0.2; // kWh/km
    private static final double PETROL_EFFICIENCY = 0.08; // L/km

    public CostAnalysis(double dailyDistanceKm) {
        if (dailyDistanceKm < 0) throw new IllegalArgumentException("Daily distance cannot be negative");
        this.evEnergyCost = calculateEvEnergyCost(dailyDistanceKm);
        this.petrolFuelCost = calculatePetrolFuelCost(dailyDistanceKm);
        this.evMaintenance = 50.0; // Daily NPR
        this.petrolMaintenance = 100.0; // Daily NPR
        this.evInsurance = 17.0; // Daily NPR
        this.petrolInsurance = 33.0; // Daily NPR
        this.monthlyEvCost = calculateMonthlyCost(evEnergyCost, evMaintenance, evInsurance);
        this.monthlyPetrolCost = calculateMonthlyCost(petrolFuelCost, petrolMaintenance, petrolInsurance);
        this.savingsTarget = 10000.0; // NPR
    }

    private double calculateEvEnergyCost(double distanceKm) {
        return distanceKm * EV_EFFICIENCY * ELECTRICITY_RATE;
    }

    private double calculatePetrolFuelCost(double distanceKm) {
        return distanceKm * PETROL_EFFICIENCY * PETROL_PRICE;
    }

    private double calculateMonthlyCost(double energyOrFuel, double maintenance, double insurance) {
        return (energyOrFuel + maintenance + insurance) * 30;
    }
    public double calculateMonthlyCost(double energyOrFuel) { // Overloaded method
        return energyOrFuel * 30;
    }

    public double getEvEnergyCost() { return evEnergyCost; }
    public void setEvEnergyCost(double evEnergyCost) { this.evEnergyCost = evEnergyCost >= 0 ? evEnergyCost : 0; }
    public double getPetrolFuelCost() { return petrolFuelCost; }
    public void setPetrolFuelCost(double petrolFuelCost) { this.petrolFuelCost = petrolFuelCost >= 0 ? petrolFuelCost : 0; }
    public double getEvMaintenance() { return evMaintenance; }
    public void setEvMaintenance(double evMaintenance) { this.evMaintenance = evMaintenance >= 0 ? evMaintenance : 0; }
    public double getPetrolMaintenance() { return petrolMaintenance; }
    public void setPetrolMaintenance(double petrolMaintenance) { this.petrolMaintenance = petrolMaintenance >= 0 ? petrolMaintenance : 0; }
    public double getEvInsurance() { return evInsurance; }
    public void setEvInsurance(double evInsurance) { this.evInsurance = evInsurance >= 0 ? evInsurance : 0; }
    public double getPetrolInsurance() { return petrolInsurance; }
    public void setPetrolInsurance(double petrolInsurance) { this.petrolInsurance = petrolInsurance >= 0 ? petrolInsurance : 0; }
    public double getMonthlyEvCost() { return monthlyEvCost; }
    public void setMonthlyEvCost(double monthlyEvCost) { this.monthlyEvCost = monthlyEvCost >= 0 ? monthlyEvCost : 0; }
    public double getMonthlyPetrolCost() { return monthlyPetrolCost; }
    public void setMonthlyPetrolCost(double monthlyPetrolCost) { this.monthlyPetrolCost = monthlyPetrolCost >= 0 ? monthlyPetrolCost : 0; }
    public double getSavingsTarget() { return savingsTarget; }
    public void setSavingsTarget(double savingsTarget) { this.savingsTarget = savingsTarget >= 0 ? savingsTarget : 0; }
}