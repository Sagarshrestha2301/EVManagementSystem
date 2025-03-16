package com.taylorsuniversity.ev.vehiclemanagement;

import java.util.List;

public class VehicleController {
    private VehicleDAO vehicleDAO;

    public VehicleController() {
        this.vehicleDAO = new VehicleDAO();
    }

    public void addVehicle(Vehicle vehicle) {
        vehicleDAO.saveVehicle(vehicle);
    }

    public Vehicle getVehicle(String id) {
        return vehicleDAO.getVehicle(id);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleDAO.getAllVehicles();
    }

    public void chargeVehicle(String id, double chargeAmount) {
        Vehicle vehicle = getVehicle(id);
        if (vehicle != null) {
            vehicle.charge(chargeAmount);
            vehicleDAO.saveVehicle(vehicle); // Update persistent storage
        }
    }

    public void travelVehicle(String id, double distance) {
        Vehicle vehicle = getVehicle(id);
        if (vehicle != null) {
            vehicle.travel(distance);
            vehicleDAO.saveVehicle(vehicle);
        }
    }

    public void deleteVehicle(String id) {
        vehicleDAO.deleteVehicle(id);
    }


}