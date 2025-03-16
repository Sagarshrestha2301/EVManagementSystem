package com.taylorsuniversity.ev.charginginfrastructure;

import com.taylorsuniversity.ev.util.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ChargingStationController {
    private static final Logger LOGGER = Logger.getLogger(ChargingStationController.class.getName());
    private final ChargingStationDAO dao;

    public ChargingStationController() {
        this.dao = new ChargingStationDAO();
    }

    public ChargingStationDAO getDao() {
        return dao;
    }

    public List<ChargingStationDTO> getAllChargingStations() {
        List<ChargingStation> stations = dao.readChargingStations();
        return stations.stream().map(ChargingStationDTO::new).collect(Collectors.toList());
    }

    public List<ChargingStationDTO> getFilteredChargingStations(String filter) {
        List<ChargingStation> stations = dao.readChargingStations();
        List<ChargingStation> filteredStations = new ArrayList<>();
        if (filter == null) filter = "ALL";

        switch (filter.toUpperCase()) {
            case "ALL":
                filteredStations = stations;
                break;
            case "AVAILABLE":
                filteredStations = stations.stream()
                        .filter(station -> "AVAILABLE".equalsIgnoreCase(station.getStatus()))
                        .collect(Collectors.toList());
                break;
            case "FAST CHARGING":
                filteredStations = stations.stream()
                        .filter(station -> station.getChargerType() != null &&
                                (station.getChargerType().contains("CHAdeMO") || station.getChargerType().contains("CCS")))
                        .collect(Collectors.toList());
                break;
            default:
                LOGGER.log(Level.WARNING, "Unknown filter: {0}", filter);
                filteredStations = stations;
        }
        return filteredStations.stream().map(ChargingStationDTO::new).collect(Collectors.toList());
    }

    public List<ChargingStationDTO> getNearbyStations(Location userLocation, double maxDistanceKm) {
        if (userLocation == null) throw new IllegalArgumentException("User location cannot be null");
        if (maxDistanceKm < 0) throw new IllegalArgumentException("Max distance cannot be negative");
        List<ChargingStation> stations = dao.readChargingStations();
        return stations.stream()
                .filter(station -> calculateDistance(userLocation, station.getLocation()) <= maxDistanceKm)
                .map(ChargingStationDTO::new)
                .collect(Collectors.toList());
    }

    public double calculateDistance(Location userLocation, Location stationLocation) {
        if (userLocation == null || stationLocation == null) return Double.MAX_VALUE;
        double lat1 = Math.toRadians(userLocation.getLatitude());
        double lon1 = Math.toRadians(userLocation.getLongitude());
        double lat2 = Math.toRadians(stationLocation.getLatitude());
        double lon2 = Math.toRadians(stationLocation.getLongitude());
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371 * c; // Earth's radius in km
    }

    public double estimateChargingTime(ChargingStationDTO station, double batteryCapacityKWh, double currentChargePercentage) {
        if (station == null || batteryCapacityKWh <= 0 || currentChargePercentage < 0 || currentChargePercentage > 100) {
            return 0;
        }
        double targetChargePercentage = 80.0;
        double energyNeeded = batteryCapacityKWh * (targetChargePercentage - currentChargePercentage) / 100.0;
        if (energyNeeded <= 0) return 0;

        double powerOutputKW;
        try {
            powerOutputKW = Double.parseDouble(station.getPowerOutput().replace("kW", "").trim());
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid power output format: {0}, defaulting to 50kW", station.getPowerOutput());
            powerOutputKW = 50.0;
        }

        double chargingTimeHours = energyNeeded / powerOutputKW;
        return chargingTimeHours * 60; // Convert to minutes
    }

    public int getTotalAvailablePorts() {
        return dao.readChargingStations().stream()
                .mapToInt(station -> {
                    try {
                        return Integer.parseInt(station.getAvailablePorts().split("/")[0]);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .sum();
    }

    public int getTotalPorts() {
        return dao.readChargingStations().stream()
                .mapToInt(station -> {
                    try {
                        return Integer.parseInt(station.getAvailablePorts().split("/")[1]);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .sum();
    }

    public double calculateCarbonOffsetToday() {
        List<ChargingStation> stations = dao.readChargingStations();
        double totalKWh = stations.size() * 100.0; // Arbitrary daily usage per station
        return totalKWh * 0.5; // 0.5 kg CO2 per kWh (example value)
    }
}