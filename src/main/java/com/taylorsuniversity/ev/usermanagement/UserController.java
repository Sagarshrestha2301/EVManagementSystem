package com.taylorsuniversity.ev.usermanagement;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;

public class UserController {
    private UserDAO userDAO;
    private User currentUser;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    public UserDTO signup(String fullName, String email, String phoneNumber, String vehicleNumber, String password) {
        if (fullName == null || email == null || phoneNumber == null || vehicleNumber == null || password == null ||
                fullName.trim().isEmpty() || email.trim().isEmpty() || phoneNumber.trim().isEmpty() || vehicleNumber.trim().isEmpty()) {
            return null; // Validation: Ensure no fields are empty
        }

        if (userDAO.findUserByEmail(email) != null) {
            return null; // User already exists
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt()); // Hash password
        String userId = userDAO.generateUserId(); // Generate userId using UserDAO
        User user = new User(fullName, email, phoneNumber, userId, vehicleNumber, hashedPassword);
        userDAO.saveUser(user);
        return new UserDTO(fullName, email, phoneNumber, vehicleNumber);
    }

    public UserDTO login(String email, String password) {
        User user = userDAO.findUserByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            this.currentUser = user;
            return new UserDTO(user.getFullName(), user.getEmail(), user.getPhoneNumber(), user.getVehicleNumber());
        }
        return null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public User getUser(String email) {
        return userDAO.findUserByEmail(email);
    }

    public void updateUser(User user) {
        userDAO.saveUser(user);
    }
    public void performLogin(JPanel panel, String email, String password, Runnable onSuccess) {
        try {
            UserDTO userDTO = login(email, password);
            if (userDTO != null) {
                JOptionPane.showMessageDialog(panel, "Login successful! Welcome " + userDTO.getFullName());
                onSuccess.run();
            } else {
                JOptionPane.showMessageDialog(panel, "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}