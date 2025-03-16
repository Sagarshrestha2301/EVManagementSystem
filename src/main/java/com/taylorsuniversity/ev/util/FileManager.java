package com.taylorsuniversity.ev.util;

import com.taylorsuniversity.ev.charginginfrastructure.ChargingStation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FileManager {
    private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());
    private static final String CHARGING_STATIONS_FILE = "src/main/resources/charging_stations.txt";
    private static final Object FILE_LOCK = new Object();

    // Read charging stations from text file
    public static List<ChargingStation> readChargingStations() {
        List<ChargingStation> stations = new ArrayList<>();
        File file = new File(CHARGING_STATIONS_FILE);

        if (!file.exists()) {
            LOGGER.warning("Charging stations file not found: " + CHARGING_STATIONS_FILE);
            return stations;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    String[] parts = line.split(",");
                    if (parts.length == 9) {
                        ChargingStation station = new ChargingStation(
                                parts[0].trim(),           // stationId
                                parts[1].trim(),           // name
                                parts[2].trim(),           // status
                                parts[3].trim(),           // chargerType
                                parts[4].trim(),           // powerOutput
                                parts[5].trim(),           // availablePorts
                                Double.parseDouble(parts[6].trim()), // latitude
                                Double.parseDouble(parts[7].trim()), // longitude
                                Double.parseDouble(parts[8].trim())  // costPerKWh
                        );
                        stations.add(station);
                    } else {
                        LOGGER.warning("Invalid line format: " + line);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Error parsing numbers in line: " + line, e);
                }
            }
            LOGGER.info("Loaded " + stations.size() + " charging stations from " + CHARGING_STATIONS_FILE);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading charging stations file", e);
        }
        return stations;
    }

    // Write charging stations to text file
    public static void writeChargingStations(List<ChargingStation> stations) {
        synchronized (FILE_LOCK) {
            File file = new File(CHARGING_STATIONS_FILE);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (ChargingStation station : stations) {
                    writer.write(station.toString());
                    writer.newLine();
                }
                LOGGER.info("Saved " + stations.size() + " charging stations to " + CHARGING_STATIONS_FILE);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error writing charging stations file", e);
            }
        }
    }

    // Generic method to read serialized objects
    public static <T> List<T> readSerializedObjects(String filePath) {
        List<T> objects = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            LOGGER.warning("Serialized file not found: " + filePath);
            return objects;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                objects = (List<T>) obj;
            }
            LOGGER.info("Loaded " + objects.size() + " objects from " + filePath);
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error reading serialized file: " + filePath, e);
        }
        return objects;
    }

    // Generic method to write serialized objects
    public static <T> void writeSerializedObjects(String filePath, List<T> objects) {
        synchronized (FILE_LOCK) {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(objects);
                LOGGER.info("Saved " + objects.size() + " objects to " + filePath);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error writing serialized file: " + filePath, e);
            }
        }
    }

    // Example usage: Load initial charging stations if file is empty
    public static void initializeChargingStationsFile() {
        File file = new File(CHARGING_STATIONS_FILE);
        if (!file.exists()) {
            List<ChargingStation> defaultStations = new ArrayList<>();
            defaultStations.add(new ChargingStation("CS001", "Kathmandu Hub", "AVAILABLE", "CCS", "50kW", "2/2", 27.7, 85.3, 0.25));
            defaultStations.add(new ChargingStation("CS002", "Pokhara Station", "AVAILABLE", "CHAdeMO", "75kW", "3/3", 28.2, 83.9, 0.30));
            writeChargingStations(defaultStations);
        }
    }
}