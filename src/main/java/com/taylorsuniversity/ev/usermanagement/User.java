package com.taylorsuniversity.ev.usermanagement;

import com.taylorsuniversity.ev.routeplanning.Trip;
import com.taylorsuniversity.ev.vehiclemanagement.Vehicle;
import com.taylorsuniversity.ev.vehiclemanagement.BatteryMonitoring;
import com.taylorsuniversity.ev.vehiclemanagement.EmergencySystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String userId; // Unique user identifier (e.g., "U123456")
    private String vehicleNumber; // Vehicle registration number (e.g., "ABC123")
    private String password; // Hashed password
    private String vehicleModel; // EV model (e.g., "Tata Nexon EV")
    private double batteryRange; // Battery range in km
    private double currentChargeLevel; // Current charge level in %
    private List<String> preferredRoutes; // User-preferred routes
    private List<Trip> tripHistory; // History of trips taken
    private String profilePicture; // Path or URL to user's profile picture
    private Vehicle vehicle; // Reference to the user's Vehicle object
    private int nextServiceMiles; // Miles until next service
    private String tirePressureStatus; // Tire pressure status
    private double batteryHealth; // Battery health percentage

    // Constructor for basic user creation
    public User(String fullName, String email, String phoneNumber, String userId, String vehicleNumber, String password) {
        this(fullName, email, phoneNumber, userId, vehicleNumber, password, "Tata Nexon EV", 300.0, 100.0);
    }

    // Full constructor with EV details
    public User(String fullName, String email, String phoneNumber, String userId, String vehicleNumber, String password,
                String vehicleModel, double batteryRange, double currentChargeLevel) {
        setFullName(fullName);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        setUserId(userId);
        setVehicleNumber(vehicleNumber);
        setPassword(password);
        setVehicleModel(vehicleModel);
        setBatteryRange(batteryRange);
        setCurrentChargeLevel(currentChargeLevel);
        this.preferredRoutes = new ArrayList<>();
        this.tripHistory = new ArrayList<>();
        this.profilePicture = "/resources/default_profile.png";
        // Initialize Vehicle with default values synced with batteryRange
        this.vehicle = new Vehicle(vehicleNumber, vehicleModel, "Tata", 75.0, batteryRange);
        this.nextServiceMiles = 2500; // Default value in km
        this.tirePressureStatus = "Optimal"; // Default status
        this.batteryHealth = 100.0; // Default health synced with BatteryMonitoring
    }

    // Constructor used by UserController.signup
    public User(String fullName, String email, String phoneNumber, String vehicleNumber, String hashedPassword) {
        this(fullName, email, phoneNumber, generateUserId(), vehicleNumber, hashedPassword, "Tata Nexon EV", 300.0, 100.0);
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getUserId() { return userId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getPassword() { return password; }
    public String getVehicleModel() { return vehicleModel; }
    public double getBatteryRange() { return batteryRange; }
    public double getCurrentChargeLevel() { return currentChargeLevel; }
    public List<String> getPreferredRoutes() {
        if (preferredRoutes == null) preferredRoutes = new ArrayList<>();
        return new ArrayList<>(preferredRoutes); // Defensive copy
    }
    public List<Trip> getTripHistory() {
        if (tripHistory == null) tripHistory = new ArrayList<>();
        return new ArrayList<>(tripHistory); // Defensive copy
    }
    public String getName() { return fullName; } // Alias for fullName
    public String getProfilePicture() { return profilePicture; }
    public Vehicle getVehicle() { return vehicle; }
    public int getNextServiceMiles() { return nextServiceMiles; }
    public String getTirePressureStatus() { return tirePressureStatus; }
    public double getBatteryHealth() {
        return vehicle != null ? vehicle.getBatteryMonitoring().getHealthStatus() : batteryHealth;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) throw new IllegalArgumentException("Full name cannot be empty");
        this.fullName = fullName.trim();
    }

    public void setEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (email == null || !Pattern.matches(emailRegex, email)) throw new IllegalArgumentException("Invalid email format");
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !Pattern.matches("\\d{10}", phoneNumber))
            throw new IllegalArgumentException("Phone number must be exactly 10 digits");
        this.phoneNumber = phoneNumber;
    }

    public void setUserId(String userId) {
        if (userId == null || !Pattern.matches("[A-Za-z0-9]{1,10}", userId)) {
            throw new IllegalArgumentException("User ID must be alphanumeric, 1-10 characters");
        }
        this.userId = userId;
    }

    public void setVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null || !Pattern.matches("[A-Za-z0-9]{1,10}", vehicleNumber)) {
            throw new IllegalArgumentException("Vehicle number must be alphanumeric, 1-10 characters");
        }
        this.vehicleNumber = vehicleNumber;
        if (this.vehicle != null) this.vehicle = new Vehicle(vehicleNumber, vehicleModel, "Tata", 75.0, batteryRange);
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters");
        this.password = password; // Hashing should occur in UserDAO or signup logic
    }

    public void setVehicleModel(String vehicleModel) {
        if (vehicleModel == null || vehicleModel.trim().isEmpty()) throw new IllegalArgumentException("Vehicle model cannot be empty");
        this.vehicleModel = vehicleModel.trim();
        if (this.vehicle != null) this.vehicle = new Vehicle(vehicleNumber, vehicleModel, "Tata", 75.0, batteryRange);
    }

    public void setBatteryRange(double batteryRange) {
        if (batteryRange <= 0) throw new IllegalArgumentException("Battery range must be positive");
        this.batteryRange = batteryRange;
        if (this.vehicle != null) this.vehicle = new Vehicle(vehicleNumber, vehicleModel, "Tata", 75.0, batteryRange);
    }

    public void setCurrentChargeLevel(double currentChargeLevel) {
        if (currentChargeLevel < 0 || currentChargeLevel > 100)
            throw new IllegalArgumentException("Charge level must be between 0 and 100%");
        this.currentChargeLevel = currentChargeLevel;
        if (this.vehicle != null) {
            double range = (currentChargeLevel / 100.0) * batteryRange;
            vehicle.getBatteryMonitoring().chargeBattery(range - vehicle.getBatteryMonitoring().getRemainingRange());
        }
    }

    public void setPreferredRoutes(List<String> routes) {
        if (routes == null) {
            this.preferredRoutes = new ArrayList<>();
        } else {
            this.preferredRoutes = new ArrayList<>();
            for (String route : routes) {
                if (route != null && !route.trim().isEmpty() && !this.preferredRoutes.contains(route)) {
                    this.preferredRoutes.add(route.trim());
                }
            }
        }
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = (profilePicture == null || profilePicture.trim().isEmpty())
                ? "/resources/default_profile.png"
                : profilePicture.trim();
    }

    public void setVehicle(Vehicle vehicle) {
        if (vehicle == null) throw new IllegalArgumentException("Vehicle cannot be null");
        this.vehicle = vehicle;
        this.batteryRange = vehicle.getBatteryMonitoring().getInitialRange();
        this.currentChargeLevel = (vehicle.getBatteryMonitoring().getRemainingRange() / batteryRange) * 100.0;
        this.vehicleModel = vehicle.getModel();
        this.vehicleNumber = vehicle.getId();
    }

    public void setNextServiceMiles(int miles) {
        if (miles < 0) throw new IllegalArgumentException("Next service miles cannot be negative");
        this.nextServiceMiles = miles;
    }

    public void setTirePressureStatus(String status) {
        this.tirePressureStatus = (status == null || status.trim().isEmpty()) ? "Optimal" : status.trim();
    }

    public void setBatteryHealth(double health) {
        if (health < 0 || health > 100) throw new IllegalArgumentException("Battery health must be between 0 and 100%");
        this.batteryHealth = health;
        if (this.vehicle != null) {
            // Note: BatteryMonitoring doesn't have a setter for healthStatus; this is a placeholder
            // You might need to adjust BatteryMonitoring to allow health updates if required
        }
    }

    public void addPreferredRoute(String route) {
        if (route != null && !route.trim().isEmpty()) {
            getPreferredRoutes();
            if (!preferredRoutes.contains(route)) preferredRoutes.add(route.trim());
        }
    }

    public void removePreferredRoute(String route) {
        if (route != null && preferredRoutes != null) preferredRoutes.remove(route);
    }

    public void addTrip(Trip trip) {
        if (trip == null) throw new IllegalArgumentException("Trip cannot be null");
        getTripHistory();
        if (!tripHistory.contains(trip)) {
            tripHistory.add(trip);
            updateChargeLevelAfterTrip(trip);
            if (vehicle != null) vehicle.travel(trip.getDistance());
        }
    }

    public void removeTrip(String tripId) {
        if (tripId != null && tripHistory != null) {
            tripHistory.removeIf(trip -> trip != null && trip.getTripId().equals(tripId));
        }
    }

    public void clearTripHistory() {
        getTripHistory();
        tripHistory.clear();
    }

    public void updateChargeLevelAfterTrip(Trip trip) {
        if (trip == null) throw new IllegalArgumentException("Trip cannot be null");
        if (tripHistory.contains(trip) && vehicle != null) {
            vehicle.travel(trip.getDistance()); // Updates BatteryMonitoring
            this.currentChargeLevel = (vehicle.getBatteryMonitoring().getRemainingRange() / batteryRange) * 100.0;
        }
    }

    public void chargeVehicle(double chargePercentage) {
        if (chargePercentage < 0 || chargePercentage > 100) {
            throw new IllegalArgumentException("Charge percentage must be between 0 and 100");
        }
        setCurrentChargeLevel(chargePercentage);
    }

    // Utility methods
    public double getTotalTripDistance() {
        if (tripHistory == null || tripHistory.isEmpty()) return 0.0;
        return tripHistory.stream().mapToDouble(Trip::getDistance).sum();
    }

    private static String generateUserId() {
        return "U" + System.currentTimeMillis() % 1000000; // Simple unique ID generation
    }

    @Override
    public String toString() {
        return String.format("User{fullName='%s', email='%s', userId='%s', vehicleNumber='%s', vehicleModel='%s', batteryRange=%.1fkm, currentCharge=%.1f%%}",
                fullName, email, userId, vehicleNumber, vehicleModel, batteryRange, currentChargeLevel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId) && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return userId.hashCode() + email.hashCode();
    }
}