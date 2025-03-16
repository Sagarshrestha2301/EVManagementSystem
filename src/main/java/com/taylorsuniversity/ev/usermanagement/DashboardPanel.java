package com.taylorsuniversity.ev.usermanagement;

import com.taylorsuniversity.ev.analytics.AnalysisController;
import com.taylorsuniversity.ev.analytics.CostAnalysisPanel;
import com.taylorsuniversity.ev.analytics.EnvironmentalImpact;
import com.taylorsuniversity.ev.analytics.EnvironmentalPanel;
import com.taylorsuniversity.ev.charginginfrastructure.ChargingStationPanel;
import com.taylorsuniversity.ev.routeplanning.TripPlanningPanel;
import com.taylorsuniversity.ev.util.Observer;
import com.taylorsuniversity.ev.vehiclemanagement.BatteryMonitoring;
import com.taylorsuniversity.ev.vehiclemanagement.EmergencySystem;
import com.taylorsuniversity.ev.vehiclemanagement.Vehicle;
import com.taylorsuniversity.ev.vehiclemanagement.VehiclePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel implements Observer {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DashboardPanel.class.getName());
    private final User user;
    private final AnalysisController analysisController;
    private final Vehicle vehicle;
    private JLabel chargingStatusLabel; // Label to display charging station updates

    public DashboardPanel(User user) {
        this.user = (user != null) ? user : new User("Guest", "guest@example.com", "1234567890", "GUEST123", "ABC123", "password");
        this.analysisController = new AnalysisController(1000.0);
        this.vehicle = this.user.getVehicle() != null ? this.user.getVehicle() : new Vehicle("DefaultVehicle", new BatteryMonitoring(100.0, 300.0), new EmergencySystem());
        setLayout(new BorderLayout());
        setBackground(new Color(240, 244, 248));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        try {
            JPanel leftSidebar = createLeftSidebar();
            add(leftSidebar, BorderLayout.WEST);

            JPanel mainContent = createMainContent();
            add(mainContent, BorderLayout.CENTER);
        } catch (Exception e) {
            LOGGER.severe("Error initializing DashboardPanel: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "An error occurred while loading the dashboard.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void update(String message) {
        SwingUtilities.invokeLater(() -> {
            if (chargingStatusLabel != null) {
                chargingStatusLabel.setText(message);
                LOGGER.info("Dashboard updated: " + message);
            }
        });
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
        sidebar.setBorder(new EmptyBorder(40, 20, 40, 20));

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
            sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
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

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(240, 244, 248));
        mainContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(34, 139, 87));
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainContent.add(welcomeLabel, BorderLayout.NORTH);

        JPanel contentArea = new JPanel(new GridBagLayout());
        contentArea.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        BatteryMonitoring battery = vehicle.getBatteryMonitoring();
        EmergencySystem emergency = vehicle.getEmergencySystem();

        // Left Column
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setOpaque(false);

        String batteryDetails = String.format(
                "Last Updated: %s\nRange: %.1f km\nHealth: %.1f%%",
                LocalDate.now(), battery.getRemainingRange(), battery.getHealthStatus()
        );
        JPanel batteryCard = createRoundedCard("Battery Status", batteryDetails, battery.getChargeStatus(), "/icons/battery.png", new Color(34, 139, 87));
        leftColumn.add(batteryCard);
        leftColumn.add(Box.createRigidArea(new Dimension(0, 25)));

        if (emergency.isEcoModeActive()) {
            JLabel emergencyLabel = new JLabel("Eco-Mode Active: Low Range");
            emergencyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            emergencyLabel.setForeground(new Color(220, 53, 69));
            emergencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            leftColumn.add(emergencyLabel);
            leftColumn.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        List<String> recentActivities = user.getTripHistory().stream()
                .limit(3)
                .map(trip -> trip != null ? String.format("%s to %s: %.1f km", trip.getStartLocation(), trip.getEndLocation(), trip.getDistance()) : "N/A")
                .collect(Collectors.toList());
        if (recentActivities.isEmpty()) recentActivities.add("Sample: Home to Work, 20.0 km");
        leftColumn.add(createRoundedCard("Recent Trips", String.join("\n", recentActivities), null, "/icons/activity.png", new Color(52, 152, 219)));

        // Right Column
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setOpaque(false);

        String healthDetails = String.format("Next Service: %d km\nBattery: %.1f%%", user.getNextServiceMiles(), battery.getHealthStatus());
        rightColumn.add(createRoundedCard("Vehicle Health", healthDetails, battery.getHealthStatus() >= 80 ? "Good" : "Check", "/icons/vehicles.png", new Color(255, 165, 0)));
        rightColumn.add(Box.createRigidArea(new Dimension(0, 25)));

        double co2Saved = user.getTripHistory().stream().mapToDouble(trip -> trip != null ? trip.getDistance() * 0.12 : 0).sum();
        if (co2Saved == 0) co2Saved = 50.0;
        rightColumn.add(createRoundedCard("CO₂ Saved", String.format("%.1f kg this month", co2Saved), null, "/icons/environment.png", new Color(46, 204, 113)));
        rightColumn.add(Box.createRigidArea(new Dimension(0, 25)));

        // Charging Station Status Card
        chargingStatusLabel = new JLabel("Charging Station Status: Waiting for updates...");
        chargingStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightColumn.add(createRoundedCard("Charging Updates", chargingStatusLabel.getText(), null, "/icons/charging.png", new Color(155, 89, 182)));

        gbc.gridx = 0;
        contentArea.add(leftColumn, gbc);
        gbc.gridx = 1;
        contentArea.add(rightColumn, gbc);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        actionPanel.setOpaque(false);
        actionPanel.add(createRoundedButton("Plan Trip", new Color(34, 139, 87), () -> handleMenuClick("Trip Planning")));
        actionPanel.add(createRoundedButton("Find Charger", new Color(34, 139, 87), () -> handleMenuClick("Charging Stations")));
        actionPanel.add(createRoundedButton("View Impact", new Color(34, 139, 87), () -> handleMenuClick("Environmental Impact")));

        mainContent.add(actionPanel, BorderLayout.SOUTH);
        mainContent.add(contentArea, BorderLayout.CENTER);
        return mainContent;
    }

    private JPanel createRoundedCard(String title, String details, String highlight, String iconPath, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(15, 15)) {
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
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(380, highlight != null ? 180 : 150));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(accentColor);
        card.add(titleLabel, BorderLayout.NORTH);

        JTextArea detailsArea = new JTextArea(details);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailsArea.setForeground(new Color(60, 60, 60));
        detailsArea.setEditable(false);
        detailsArea.setOpaque(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        card.add(detailsArea, BorderLayout.CENTER);

        if (highlight != null) {
            JLabel highlightLabel = new JLabel(highlight, SwingConstants.CENTER);
            highlightLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            highlightLabel.setForeground(accentColor);
            card.add(highlightLabel, BorderLayout.EAST);
        }

        JLabel iconLabel = new JLabel(loadIcon(iconPath, 40, 40));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 15));
        card.add(iconLabel, BorderLayout.WEST);

        if (title.equals("Battery Status")) {
            JProgressBar progress = new JProgressBar(0, 100);
            progress.setValue((int) (vehicle.getBatteryMonitoring().getRemainingRange() / vehicle.getBatteryMonitoring().getInitialRange() * 100));
            progress.setForeground(accentColor);
            progress.setBackground(new Color(245, 250, 255));
            progress.setBorder(null);
            progress.setPreferredSize(new Dimension(200, 10));
            card.add(progress, BorderLayout.SOUTH);
        }

        return card;
    }

    private JButton createRoundedButton(String text, Color baseColor, Runnable action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(isEnabled() ? baseColor : baseColor.darker());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
            @Override
            protected void paintBorder(Graphics g) {}
            @Override
            public boolean contains(int x, int y) {
                return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25).contains(x, y);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 45));
        button.addActionListener(e -> action.run());
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(baseColor.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(baseColor); }
        });
        return button;
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
                case "Dashboard": frame.setContentPane(new DashboardPanel(user)); break;
                case "Profile": frame.setContentPane(new ProfilePanel(user)); break;
                case "Trip Planning": frame.setContentPane(new TripPlanningPanel(user)); break;
                case "Charging Stations": frame.setContentPane(new ChargingStationPanel(user)); break;
                case "Cost Analysis": frame.setContentPane(new CostAnalysisPanel(user)); break;
                case "Environmental Impact": frame.setContentPane(new EnvironmentalPanel(user)); break;
                case "Vehicle Management": frame.setContentPane(new VehiclePanel(user)); break;
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