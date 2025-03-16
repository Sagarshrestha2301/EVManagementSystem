package com.taylorsuniversity.ev.charginginfrastructure;

import java.io.Serializable;

public class ChargingStationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String stationId;
    private String name;
    private String status;
    private String chargerType;
    private String powerOutput;
    private String availablePorts;
    private double latitude;
    private double longitude;
    private double costPerKWh; // New field for cost per kWh

    public ChargingStationDTO(ChargingStation station) {
        if (station == null) throw new IllegalArgumentException("Station cannot be null");
        this.stationId = station.getStationId();
        this.name = station.getName();
        this.status = station.getStatus();
        this.chargerType = station.getChargerType();
        this.powerOutput = station.getPowerOutput();
        this.availablePorts = station.getAvailablePorts();
        this.latitude = station.getLatitude();
        this.longitude = station.getLongitude();
        // Assuming ChargingStation has a getCostPerKWh() method; otherwise, set a default
        this.costPerKWh = station.getCostPerKWh != 0.20 ? station.getCostPerKWh() : 0.20; // Default to $0.20/kWh
    }

    public ChargingStationDTO(String stationId, String name, double latitude, double longitude,
                              String status, String chargerType, String powerOutput, String availablePorts,
                              double costPerKWh) {
        this.stationId = stationId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.chargerType = chargerType;
        this.powerOutput = powerOutput;
        this.availablePorts = availablePorts;
        this.costPerKWh = costPerKWh;
    }

    public String getStationId() { return stationId; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getChargerType() { return chargerType; }
    public String getPowerOutput() { return powerOutput; }
    public String getAvailablePorts() { return availablePorts; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getCostPerKWh() { return costPerKWh; }

    // Optional: Setter if you need to update cost later
    public void setCostPerKWh(double costPerKWh) { this.costPerKWh = costPerKWh; }
}