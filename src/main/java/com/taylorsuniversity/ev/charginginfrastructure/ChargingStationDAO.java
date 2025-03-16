package com.taylorsuniversity.ev.charginginfrastructure;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ChargingStationDAO {
    private static final String FILE_PATH = "src/main/resources/charging_stations.txtcharging_stations.txt";
    private static final Logger LOGGER = Logger.getLogger(ChargingStationDAO.class.getName());

    public List<ChargingStation> readChargingStations() {
        List<ChargingStation> stations = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            initializeSampleData();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 9) {
                    ChargingStation station = new ChargingStation(
                            parts[0], parts[1], parts[2], parts[3], parts[4], parts[5],
                            Double.parseDouble(parts[6]), Double.parseDouble(parts[7]), Double.parseDouble(parts[8])
                    );
                    stations.add(station);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading charging stations", e);
            throw new RuntimeException("Failed to read charging stations: " + e.getMessage(), e);
        }
        return stations;
    }

    public void saveChargingStations(List<ChargingStation> stations) {
        File file = new File(FILE_PATH);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (ChargingStation station : stations) {
                writer.write(station.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving charging stations", e);
            throw new RuntimeException("Failed to save charging stations: " + e.getMessage(), e);
        }
    }

    private void initializeSampleData() {
        List<ChargingStation> sampleStations = new ArrayList<>();
        sampleStations.add(new ChargingStation("CS001", "Durbar Marg Station", "AVAILABLE", "CCS/CHAdeMO", "50kW", "3/4", 27.7017, 85.3206, 15.0));
        sampleStations.add(new ChargingStation("CS002", "Thamel Station", "MAINTENANCE", "Type 2", "22kW", "0/2", 27.7150, 85.3100, 14.5));
        sampleStations.add(new ChargingStation("CS003", "Patan Station", "OFFLINE", "CCS", "50kW", "0/1", 27.6780, 85.3250, 15.0));
        sampleStations.add(new ChargingStation("CS004", "Boudha Station", "AVAILABLE", "CHAdeMO", "50kW", "2/2", 27.7215, 85.3620, 14.0));
        sampleStations.add(new ChargingStation("CS005", "Kalanki Station", "AVAILABLE", "Type 2", "22kW", "1/3", 27.6930, 85.2800, 14.5));
        saveChargingStations(sampleStations);
    }
}