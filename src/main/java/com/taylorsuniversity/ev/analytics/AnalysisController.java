package com.taylorsuniversity.ev.analytics;

import com.taylorsuniversity.ev.analytics.CostAnalysis;

public class AnalysisController {
    private CostAnalysis costAnalysis;

    public AnalysisController(double dailyDistanceKm) {
        this.costAnalysis = new CostAnalysis(dailyDistanceKm);
    }

    public double getEvEnergyCost(String period) {
        return scaleCost(costAnalysis.getEvEnergyCost(), period);
    }

    public double getPetrolFuelCost(String period) {
        return scaleCost(costAnalysis.getPetrolFuelCost(), period);
    }

    public double getEvMaintenance(String period) {
        return scaleCost(costAnalysis.getEvMaintenance(), period);
    }

    public double getPetrolMaintenance(String period) {
        return scaleCost(costAnalysis.getPetrolMaintenance(), period);
    }

    public double getEvInsurance(String period) {
        return scaleCost(costAnalysis.getEvInsurance(), period);
    }

    public double getPetrolInsurance(String period) {
        return scaleCost(costAnalysis.getPetrolInsurance(), period);
    }

    public double getMonthlyEvCost() {
        return costAnalysis.getMonthlyEvCost();
    }

    public double getMonthlyPetrolCost() {
        return costAnalysis.getMonthlyPetrolCost();
    }

    public double getSavingsTarget() {
        return costAnalysis.getSavingsTarget();
    }

    public double calculateCurrentSavings() {
        return getMonthlyPetrolCost() - getMonthlyEvCost();
    }

    public double calculateSavingsProgress() {
        return (calculateCurrentSavings() / getSavingsTarget()) * 100;
    }

    public int getChargingCost(String period) {
        return (int) getEvEnergyCost(period);
    }

    public int getMaintenanceCost(String period) {
        return (int) (getEvMaintenance(period) + getPetrolMaintenance(period));
    }

    public int getOtherCost(String period) {
        return (int) (getEvInsurance(period) + getPetrolInsurance(period));
    }

    public int getTotalCost(String period) {
        double total = getEvEnergyCost(period) + getPetrolFuelCost(period) +
                getEvMaintenance(period) + getPetrolMaintenance(period) +
                getEvInsurance(period) + getPetrolInsurance(period);
        return (int) total;
    }

    private double scaleCost(double dailyCost, String period) {
        return switch (period) {
            case "WEEKLY" -> dailyCost * 7;
            case "MONTHLY" -> dailyCost * 30;
            case "YEARLY" -> dailyCost * 365;
            default -> dailyCost;
        };
    }
}