package com.taylorsuniversity.ev;

import com.taylorsuniversity.ev.usermanagement.LoginPanel;
import com.taylorsuniversity.ev.usermanagement.UserController;
import com.taylorsuniversity.ev.usermanagement.DashboardPanel;
import com.taylorsuniversity.ev.usermanagement.User;

import javax.swing.*;
import java.awt.*;

public class EVApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Create the main application window
                JFrame frame = new JFrame("EcoCharge - EV Management System");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Set window properties
                frame.setSize(1440, 900);
                frame.setMinimumSize(new Dimension(800, 600));
                frame.setResizable(true);

                // Initialize UserController
                UserController userController = new UserController();

                // Create LoginPanel with a callback to switch to DashboardPanel after successful login
                LoginPanel loginPanel = new LoginPanel(userController, () -> {
                    User currentUser = userController.getCurrentUser();
                    if (currentUser != null) {
                        frame.setContentPane(new DashboardPanel(currentUser));
                        frame.revalidate();
                        frame.repaint();
                    }
                });

                // Set initial content pane
                frame.setContentPane(loginPanel);
                frame.setLocationRelativeTo(null); // Center the window
                frame.setVisible(true);

            } catch (Exception e) {
                // Basic error handling
                JOptionPane.showMessageDialog(null,
                        "An error occurred while starting the application: " + e.getMessage(),
                        "Application Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}


