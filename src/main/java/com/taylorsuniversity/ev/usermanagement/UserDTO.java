package com.taylorsuniversity.ev.usermanagement;

public class UserDTO {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String vehicleNumber;

    public UserDTO(String fullName, String email, String phoneNumber, String vehicleNumber) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.vehicleNumber = vehicleNumber;
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getVehicleNumber() { return vehicleNumber; }
}