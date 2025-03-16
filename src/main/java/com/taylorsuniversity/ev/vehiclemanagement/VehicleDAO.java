package com.taylorsuniversity.ev.vehiclemanagement;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {
    private static final String FILE_PATH = "src/main/resources/vehicle_data.txt";

    public void saveVehicle(Vehicle vehicle) {
        List<Vehicle> vehicles = getAllVehicles();
        vehicles.removeIf(v -> v.getId().equals(vehicle.getId())); // Remove if exists
        vehicles.add(vehicle);
        saveToFile(vehicles);
    }

    public Vehicle getVehicle(String id) {
        return getAllVehicles().stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 5) {
                        String id = data[0].trim();
                        String model = data[1].trim();
                        String manufacturer = data[2].trim();
                        double batteryCapacity = Double.parseDouble(data[3].trim());
                        double initialRange = Double.parseDouble(data[4].trim());
                        vehicles.add(new Vehicle(id, model, manufacturer, batteryCapacity, initialRange));
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error loading vehicle data: " + e.getMessage());
            }
        }
        return vehicles;
    }

    public void deleteVehicle(String id) {
        List<Vehicle> vehicles = getAllVehicles();
        vehicles.removeIf(v -> v.getId().equals(id));
        saveToFile(vehicles);
    }

    private void saveToFile(List<Vehicle> vehicles) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Vehicle vehicle : vehicles) {
                bw.write(String.format("%s,%s,%s,%.1f,%.1f%n",
                        vehicle.getId(),
                        vehicle.getModel(),
                        vehicle.getManufacturer(),
                        vehicle.getBatteryCapacity(),
                        vehicle.getBatteryMonitoring().getRemainingRange()));
            }
        } catch (IOException e) {
            System.err.println("Error saving vehicle data: " + e.getMessage());
        }
    }
}