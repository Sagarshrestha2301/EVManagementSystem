package com.taylorsuniversity.ev.usermanagement;

import com.taylorsuniversity.ev.analytics.CostAnalysisPanel;
import com.taylorsuniversity.ev.analytics.EnvironmentalPanel;
import com.taylorsuniversity.ev.charginginfrastructure.ChargingStationPanel;
import com.taylorsuniversity.ev.routeplanning.TripPlanningPanel;
import com.taylorsuniversity.ev.vehiclemanagement.VehiclePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ProfilePanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(ProfilePanel.class.getName());
    private UserController userController;
    private final User user;
    private JTextField fullNameField, emailField, phoneField, vehicleField, routeField;
    private DefaultListModel<String> routesModel;

    public ProfilePanel(User user) {
        this.user = user;
        this.userController = new UserController();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30)); // More breathing room

        JPanel leftSidebar = createLeftSidebar();
        add(leftSidebar, BorderLayout.WEST);

        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createLeftSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(34, 139, 87), 0, getHeight(), new Color(20, 83, 52));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(new EmptyBorder(40, 20, 40, 20)); // More padding

        JLabel logoLabel = new JLabel("EcoCharge", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 36));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 50)));

        String[] menuItems = {"Dashboard", "Profile", "Trip Planning", "Charging Stations", "Cost Analysis", "Environmental Impact", "Vehicle Management", "Logout"};
        String[] iconFiles = {"dashboard.png", "user.png", "trip.png", "charging.png", "cost.png", "environment.png", "vehicles.png", "logout.png"};
        for (int i = 0; i < menuItems.length; i++) {
            JPanel menuItem = createMenuItem(menuItems[i], iconFiles[i]);
            sidebar.add(menuItem);
            sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // Increased spacing
        }
        return sidebar;
    }

    private JPanel createMenuItem(String text, String iconFile) {
        JPanel menuItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
            }
        };
        menuItem.setOpaque(false);
        menuItem.setBorder(new EmptyBorder(12, 15, 12, 15));
        menuItem.setMaximumSize(new Dimension(220, 50));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(new Color(230, 255, 235));
        ImageIcon icon = loadIcon("/icons/" + iconFile, 28, 28);
        if (icon != null) label.setIcon(icon);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        menuItem.add(label);
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMenuClick(text);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(Color.WHITE);
                menuItem.setBackground(new Color(255, 255, 255, 80));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(new Color(230, 255, 235));
                menuItem.setBackground(null);
            }
        });
        return menuItem;
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            String resourcePath = (path != null && !path.trim().isEmpty()) ? path : "/icons/user.png";
            java.net.URL imgURL = getClass().getResource(resourcePath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } else {
                LOGGER.log(Level.WARNING, "Icon resource not found: " + resourcePath);
                return new ImageIcon(new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading icon: " + (path != null ? path : "null"), e);
            return new ImageIcon(new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB));
        }
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(new Color(245, 247, 250));
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header with user name and profile picture
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        JLabel userNameLabel = new JLabel("Profile: " + user.getFullName(), SwingConstants.CENTER);
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        userNameLabel.setForeground(new Color(34, 139, 87));
        JLabel profileIcon = new JLabel(loadIcon(user.getProfilePicture(), 60, 60));
        profileIcon.setBorder(new EmptyBorder(0, 0, 0, 10));
        headerPanel.add(profileIcon, BorderLayout.WEST);
        headerPanel.add(userNameLabel, BorderLayout.CENTER);
        rightPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel with card-like styling
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(15, 15, 15, 15)));
        formPanel.setPreferredSize(new Dimension(400, 450));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        fullNameField = createStyledTextField(user.getFullName());
        emailField = createStyledTextField(user.getEmail());
        emailField.setEditable(false);
        emailField.setToolTipText("Email cannot be changed.");
        phoneField = createStyledTextField(user.getPhoneNumber());
        vehicleField = createStyledTextField(user.getVehicleNumber());
        routesModel = new DefaultListModel<>();
        if (user.getPreferredRoutes() != null) {
            user.getPreferredRoutes().forEach(routesModel::addElement);
        }
        JList<String> routesList = new JList<>(routesModel);
        routesList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        routesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = routesList.getSelectedIndex();
                    if (index >= 0) {
                        routesModel.remove(index);
                    }
                }
            }
        });
        routeField = createStyledTextField("");

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Vehicle Number:"), gbc);
        gbc.gridx = 1;
        formPanel.add(vehicleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Add Preferred Route:"), gbc);
        gbc.gridx = 1;
        formPanel.add(routeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton addRouteButton = createStyledButton("Add Route", new Color(34, 139, 87));
        addRouteButton.addActionListener(e -> {
            String route = routeField.getText().trim();
            if (!route.isEmpty() && !routesModel.contains(route)) {
                routesModel.addElement(route);
                routeField.setText("");
            }
        });
        formPanel.add(addRouteButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(routesList), gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        JLabel tripDistanceLabel = new JLabel("Total Trip Distance: " + String.format("%.1f km", user.getTotalTripDistance()));
        tripDistanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tripDistanceLabel.setForeground(new Color(34, 139, 87));
        formPanel.add(tripDistanceLabel, gbc);

        JButton updateButton = createStyledButton("Update Profile", new Color(34, 139, 87));
        updateButton.addActionListener(e -> handleUpdate());

        rightPanel.add(formPanel, BorderLayout.CENTER);
        rightPanel.add(updateButton, BorderLayout.SOUTH);

        return rightPanel;
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 5, 5, 5)));
        field.setBackground(new Color(250, 250, 250));
        return field;
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
        return button;
    }

    private void handleUpdate() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to update your profile?", "Confirm Update", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                user.setFullName(fullNameField.getText().trim());
                user.setPhoneNumber(phoneField.getText().trim());
                user.setVehicleNumber(vehicleField.getText().trim());
                java.util.List<String> routes = new ArrayList<>();
                Enumeration<String> enumeration = routesModel.elements();
                while (enumeration.hasMoreElements()) {
                    routes.add(enumeration.nextElement());
                }
                user.setPreferredRoutes(routes);
                userController.updateUser(user);
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Refresh the header with updated name
                JPanel headerPanel = (JPanel) ((JPanel) getComponent(1)).getComponent(0);
                JLabel userNameLabel = (JLabel) headerPanel.getComponent(1);
                userNameLabel.setText("Profile: " + user.getFullName());
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleMenuClick(String menuItem) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame == null) {
            LOGGER.severe("No parent frame found!");
            return;
        }
        try {
            frame.getContentPane().removeAll();
            UserController userController = new UserController();
            switch (menuItem) {
                case "Dashboard":
                    frame.setContentPane(new DashboardPanel(user));
                    break;
                case "Profile":
                    frame.setContentPane(new ProfilePanel(user));
                    break;
                case "Trip Planning":
                    frame.setContentPane(new TripPlanningPanel(user));
                    break;
                case "Charging Stations":
                    frame.setContentPane(new ChargingStationPanel(user));
                    break;
                case "Cost Analysis":
                    frame.setContentPane(new CostAnalysisPanel(user));
                    break;
                case "Environmental Impact":
                    frame.setContentPane(new EnvironmentalPanel(user));
                    break;
                case "Vehicle Management":
                    frame.setContentPane(new VehiclePanel(user));
                    break;
                case "Logout":
                    if (JOptionPane.showConfirmDialog(frame, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        frame.setContentPane(new LoginPanel(userController, () -> frame.setContentPane(new DashboardPanel(user))));
                    }
                    break;
            }
            frame.revalidate();
            frame.repaint();
        } catch (Exception e) {
            LOGGER.severe("Error navigating to " + menuItem + ": " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Navigation error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}