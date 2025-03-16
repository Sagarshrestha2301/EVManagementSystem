package com.taylorsuniversity.ev.analytics;

import com.taylorsuniversity.ev.charginginfrastructure.ChargingStationPanel;
import com.taylorsuniversity.ev.routeplanning.TripPlanningPanel;
import com.taylorsuniversity.ev.usermanagement.*;
import com.taylorsuniversity.ev.vehiclemanagement.VehiclePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

public class CostAnalysisPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final User user;
    private final AnalysisController controller;
    private static final double DAILY_DISTANCE_KM = 30.0;
    private static final Logger LOGGER = Logger.getLogger(CostAnalysisPanel.class.getName());

    public CostAnalysisPanel(User user) {
        this.user = user != null ? user : new User("Guest", "guest@example.com", "1234567890", "GUEST123", "ABC123", "password");
        this.controller = new AnalysisController(DAILY_DISTANCE_KM);
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
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
            LOGGER.warning("Icon not found: " + path);
            return null;
        } catch (Exception e) {
            LOGGER.severe("Error loading icon " + path + ": " + e.getMessage());
            return null;
        }
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(245, 247, 250));
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Increased spacing
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Cost Analysis - Compare your EV costs with traditional vehicles in Nepal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(34, 139, 87));
        rightPanel.add(titleLabel, gbc);

        // Monthly Cost Overview
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 0.3;
        JPanel overviewPanel = createOverviewPanel();
        rightPanel.add(overviewPanel, gbc);

        // Daily Breakdown and Savings Progress
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.4;
        JPanel breakdownPanel = createBreakdownPanel();
        rightPanel.add(breakdownPanel, gbc);

        gbc.gridx = 1;
        JPanel savingsPanel = createSavingsPanel();
        rightPanel.add(savingsPanel, gbc);

        return rightPanel;
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, getWidth(), getHeight(), new Color(245, 250, 255));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 30, 30);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        JLabel titleLabel = new JLabel("Monthly Cost Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(34, 139, 87));
        panel.add(titleLabel, gbc);

        // EV Bar
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        JLabel evLabel = new JLabel("EV:");
        evLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(evLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        JProgressBar evBar = new JProgressBar(0, 12000);
        evBar.setValue(4500);
        evBar.setForeground(new Color(34, 139, 87));
        evBar.setBackground(new Color(245, 250, 255));
        evBar.setBorder(null);
        evBar.setPreferredSize(new Dimension(0, 20));
        panel.add(evBar, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.2;
        JLabel evCostLabel = new JLabel("Rs. 4,500");
        evCostLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(evCostLabel, gbc);

        // Petrol Bar
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        JLabel petrolLabel = new JLabel("Petrol:");
        petrolLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(petrolLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        JProgressBar petrolBar = new JProgressBar(0, 12000);
        petrolBar.setValue(12000);
        petrolBar.setForeground(new Color(255, 165, 0));
        petrolBar.setBackground(new Color(245, 250, 255));
        petrolBar.setBorder(null);
        petrolBar.setPreferredSize(new Dimension(0, 20));
        panel.add(petrolBar, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.2;
        JLabel petrolCostLabel = new JLabel("Rs. 12,000");
        petrolCostLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(petrolCostLabel, gbc);

        return panel;
    }

    private JPanel createBreakdownPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, getWidth(), getHeight(), new Color(245, 250, 255));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 30, 30);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        JLabel titleLabel = new JLabel("Daily Breakdown");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(34, 139, 87));
        panel.add(titleLabel, gbc);

        // Headers
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.4;
        panel.add(new JLabel(""), gbc); // Empty for alignment
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        JLabel evLabel = new JLabel("EV");
        evLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(evLabel, gbc);
        gbc.gridx = 2;
        JLabel petrolLabel = new JLabel("Petrol");
        petrolLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(petrolLabel, gbc);

        // Energy/Fuel
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.4;
        JLabel energyLabel = new JLabel("Energy/Fuel:");
        energyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(energyLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        JLabel evEnergy = new JLabel("Rs. 83");
        evEnergy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(evEnergy, gbc);
        gbc.gridx = 2;
        JLabel petrolEnergy = new JLabel("Rs. 267");
        petrolEnergy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(petrolEnergy, gbc);

        // Maintenance
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.4;
        JLabel maintLabel = new JLabel("Maintenance:");
        maintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(maintLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        JLabel evMaint = new JLabel("Rs. 50");
        evMaint.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(evMaint, gbc);
        gbc.gridx = 2;
        JLabel petrolMaint = new JLabel("Rs. 100");
        petrolMaint.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(petrolMaint, gbc);

        // Insurance
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.4;
        JLabel insLabel = new JLabel("Insurance:");
        insLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(insLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        JLabel evIns = new JLabel("Rs. 17");
        evIns.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(evIns, gbc);
        gbc.gridx = 2;
        JLabel petrolIns = new JLabel("Rs. 33");
        petrolIns.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(petrolIns, gbc);

        return panel;
    }

    private JPanel createSavingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, getWidth(), getHeight(), new Color(245, 250, 255));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 30, 30);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Savings Progress");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(34, 139, 87));
        panel.add(titleLabel, gbc);

        // Progress Bar
        gbc.gridy = 1;
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(75);
        progressBar.setForeground(new Color(34, 139, 87));
        progressBar.setBackground(new Color(245, 250, 255));
        progressBar.setBorder(null);
        progressBar.setPreferredSize(new Dimension(0, 20));
        panel.add(progressBar, gbc);

        // Progress Label
        gbc.gridy = 2;
        JLabel progressLabel = new JLabel("75% of Target");
        progressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(progressLabel, gbc);

        // Current Monthly Savings
        gbc.gridy = 3;
        JLabel savingsLabel = new JLabel("Rs. 7,500");
        savingsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        savingsLabel.setForeground(new Color(34, 139, 87));
        panel.add(savingsLabel, gbc);

        gbc.gridy = 4;
        JLabel savingsSubLabel = new JLabel("Current Monthly Savings");
        savingsSubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(savingsSubLabel, gbc);

        // Target Savings
        gbc.gridy = 5;
        JLabel targetLabel = new JLabel("Rs. 10,000");
        targetLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(targetLabel, gbc);

        gbc.gridy = 6;
        JLabel targetSubLabel = new JLabel("Monthly Target");
        targetSubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(targetSubLabel, gbc);

        // Tip
        gbc.gridy = 7;
        JLabel tipLabel = new JLabel("⚡ Charge during off-peak hours (11PM - 5AM) to save more");
        tipLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tipLabel.setForeground(new Color(255, 165, 0));
        panel.add(tipLabel, gbc);

        return panel;
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