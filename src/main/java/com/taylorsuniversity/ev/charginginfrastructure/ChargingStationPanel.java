package com.taylorsuniversity.ev.charginginfrastructure;

import com.taylorsuniversity.ev.analytics.CostAnalysisPanel;
import com.taylorsuniversity.ev.analytics.EnvironmentalPanel;
import com.taylorsuniversity.ev.routeplanning.TripPlannerController;
import com.taylorsuniversity.ev.routeplanning.TripPlanningPanel;
import com.taylorsuniversity.ev.usermanagement.*;
import com.taylorsuniversity.ev.util.Location;
import com.taylorsuniversity.ev.vehiclemanagement.VehiclePanel;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ChargingStationPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final User user;
    private final ChargingStationController controller;
    private final TripPlannerController tripController;
    private List<ChargingStationDTO> chargingStations;
    private JXMapViewer mapViewer;
    private JPanel detailsPanel;
    private JLabel statsLabel;
    private Location userLocation;
    private JList<String> stationList;
    private DefaultListModel<String> stationListModel;
    private static final Logger LOGGER = Logger.getLogger(ChargingStationPanel.class.getName());

    public ChargingStationPanel(User user) {
        this.user = user != null ? user : new User("Guest", "guest@example.com", "1234567890", "GUEST123", "ABC123", "password");
        this.controller = new ChargingStationController();
        this.tripController = new TripPlannerController();
        this.userLocation = new Location("User Location", 27.7172, 85.3240);
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30)); // More breathing room

        chargingStations = controller.getFilteredChargingStations("AVAILABLE");

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
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        JLabel titleLabel = new JLabel("Charging Stations in Nepal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titleLabel.setForeground(new Color(34, 139, 87));
        rightPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JButton allButton = createStyledButton("ALL", new Color(34, 139, 87));
        allButton.addActionListener(e -> filterStations("ALL"));
        rightPanel.add(allButton, gbc);

        gbc.gridx = 1;
        JButton availableButton = createStyledButton("AVAILABLE", new Color(120, 120, 120));
        availableButton.addActionListener(e -> filterStations("AVAILABLE"));
        rightPanel.add(availableButton, gbc);

        gbc.gridx = 2;
        JButton fastButton = createStyledButton("FAST CHARGING", new Color(120, 120, 120));
        fastButton.addActionListener(e -> filterStations("FAST CHARGING"));
        rightPanel.add(fastButton, gbc);

        gbc.gridx = 3;
        JButton nearbyButton = createStyledButton("NEARBY", new Color(120, 120, 120));
        nearbyButton.addActionListener(e -> filterStations("NEARBY"));
        rightPanel.add(nearbyButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0.4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.7;
        stationListModel = new DefaultListModel<>();
        for (ChargingStationDTO station : chargingStations) {
            stationListModel.addElement(station.getName() + " (" + station.getStatus() + ")");
        }
        stationList = new JList<>(stationListModel);
        stationList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        stationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = stationList.getSelectedIndex();
                if (index >= 0) {
                    updateDetails(chargingStations.get(index));
                    updateMap(chargingStations.get(index));
                }
            }
        });
        JScrollPane listScrollPane = new JScrollPane(stationList);
        rightPanel.add(listScrollPane, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0.6;
        mapViewer = createMapViewer();
        rightPanel.add(mapViewer, gbc);

        // Update map after mapViewer is assigned
        updateMap(chargingStations.isEmpty() ? null : chargingStations.get(0));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 0.3;
        detailsPanel = createDetailsPanel(chargingStations.isEmpty() ? null : chargingStations.get(0));
        rightPanel.add(detailsPanel, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        statsLabel = new JLabel("Available Ports: " + controller.getTotalAvailablePorts() + "/" + controller.getTotalPorts() +
                " | Carbon Offset Today: " + String.format("%.1f kg", controller.calculateCarbonOffsetToday()));
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statsLabel.setForeground(new Color(34, 139, 87));
        rightPanel.add(statsLabel, gbc);

        return rightPanel;
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
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

    private JXMapViewer createMapViewer() {
        JXMapViewer map = new JXMapViewer();
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        map.setTileFactory(tileFactory);
        map.addMouseListener(new PanMouseInputListener(map));
        map.addMouseMotionListener(new PanMouseInputListener(map));
        map.addMouseWheelListener(new ZoomMouseWheelListenerCenter(map));
        GeoPosition defaultPos = new GeoPosition(27.7172, 85.3240); // Kathmandu
        map.setAddressLocation(defaultPos);
        map.setZoom(7);
        // Do NOT call updateMap here; defer it until mapViewer is assigned
        return map;
    }

    private JPanel createDetailsPanel(ChargingStationDTO station) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        if (station == null) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel noSelectionLabel = new JLabel("Select a station to view details");
            noSelectionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panel.add(noSelectionLabel, gbc);
            return panel;
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Station: " + station.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(new Color(34, 139, 87));
        panel.add(nameLabel, gbc);

        gbc.gridy = 1;
        panel.add(new JLabel("Status: " + station.getStatus()), gbc);

        gbc.gridy = 2;
        panel.add(new JLabel("Charger Type: " + station.getChargerType()), gbc);

        gbc.gridy = 3;
        panel.add(new JLabel("Power Output: " + station.getPowerOutput()), gbc);

        gbc.gridy = 4;
        panel.add(new JLabel("Ports: " + station.getAvailablePorts()), gbc);

        gbc.gridy = 5;
        double distance = controller.calculateDistance(userLocation, new Location(station.getName(), station.getLatitude(), station.getLongitude()));
        panel.add(new JLabel("Distance: " + String.format("%.1f km", distance)), gbc);

        gbc.gridy = 6;
        double chargingTime = controller.estimateChargingTime(station, user.getBatteryRange() * 0.2, user.getCurrentChargeLevel());
        panel.add(new JLabel("Est. Charging Time: " + String.format("%.1f min", chargingTime)), gbc);

        gbc.gridy = 7;
        JButton navigateButton = new JButton("Navigate to Station");
        navigateButton.setBackground(new Color(34, 139, 87));
        navigateButton.setForeground(Color.WHITE);
        navigateButton.setFocusPainted(false);
        navigateButton.addActionListener(e -> navigateToStation(station));
        panel.add(navigateButton, gbc);

        return panel;
    }

    private void updateMap(ChargingStationDTO station) {
        if (mapViewer == null) {
            // This should not happen after the fix, but adding as a safety check
            System.err.println("mapViewer is null in updateMap");
            return;
        }

        List<GeoPosition> routePositions = new ArrayList<>();
        Set<Waypoint> waypoints = new HashSet<>();
        waypoints.add(new DefaultWaypoint(userLocation.getLatitude(), userLocation.getLongitude()));
        routePositions.add(new GeoPosition(userLocation.getLatitude(), userLocation.getLongitude()));
        if (station != null) {
            waypoints.add(new DefaultWaypoint(station.getLatitude(), station.getLongitude()));
            routePositions.add(new GeoPosition(station.getLatitude(), station.getLongitude()));
        }
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);

        Painter<JXMapViewer> routePainter = (g2d, map, width, height) -> {
            g2d.setColor(new Color(34, 139, 87));
            g2d.setStroke(new BasicStroke(2));
            for (int i = 0; i < routePositions.size() - 1; i++) {
                Point2D p1 = mapViewer.getTileFactory().geoToPixel(routePositions.get(i), mapViewer.getZoom());
                Point2D p2 = mapViewer.getTileFactory().geoToPixel(routePositions.get(i + 1), mapViewer.getZoom());
                int x1 = (int) p1.getX();
                int y1 = (int) p1.getY();
                int x2 = (int) p2.getX();
                int y2 = (int) p2.getY();
                g2d.drawLine(x1, y1, x2, y2);
            }
        };

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(waypointPainter, routePainter);
        mapViewer.setOverlayPainter(painter);
        mapViewer.zoomToBestFit(new HashSet<>(routePositions), 0.9);
    }

    private void updateDetails(ChargingStationDTO station) {
        detailsPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        detailsPanel.add(createDetailsPanel(station), gbc);
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private void filterStations(String filter) {
        if ("NEARBY".equals(filter)) {
            chargingStations = controller.getNearbyStations(userLocation, 50.0);
        } else {
            chargingStations = controller.getFilteredChargingStations(filter);
        }
        stationListModel.clear();
        for (ChargingStationDTO station : chargingStations) {
            stationListModel.addElement(station.getName() + " (" + station.getStatus() + ")");
        }
        statsLabel.setText("Available Ports: " + controller.getTotalAvailablePorts() + "/" + controller.getTotalPorts() +
                " | Carbon Offset Today: " + String.format("%.1f kg", controller.calculateCarbonOffsetToday()));
        updateDetails(chargingStations.isEmpty() ? null : chargingStations.get(0));
        updateMap(chargingStations.isEmpty() ? null : chargingStations.get(0));
    }

    private void navigateToStation(ChargingStationDTO station) {
        Location stationLocation = new Location(station.getName(), station.getLatitude(), station.getLongitude());
        try {
            tripController.planTrip(user, userLocation, stationLocation);
            JOptionPane.showMessageDialog(this, "Trip to " + station.getName() + " planned successfully!", "Navigation", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to plan trip: " + e.getMessage());
            // Fallback: Show direct line on map
            updateMapWithFallback(station);
            JOptionPane.showMessageDialog(this,
                    "No valid route found to " + station.getName() + ". Showing direct distance instead.",
                    "Navigation Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateMapWithFallback(ChargingStationDTO station) {
        if (mapViewer == null) {
            System.err.println("mapViewer is null in updateMapWithFallback");
            return;
        }

        List<GeoPosition> routePositions = new ArrayList<>();
        Set<Waypoint> waypoints = new HashSet<>();
        waypoints.add(new DefaultWaypoint(userLocation.getLatitude(), userLocation.getLongitude()));
        routePositions.add(new GeoPosition(userLocation.getLatitude(), userLocation.getLongitude()));
        if (station != null) {
            waypoints.add(new DefaultWaypoint(station.getLatitude(), station.getLongitude()));
            routePositions.add(new GeoPosition(station.getLatitude(), station.getLongitude()));
        }
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);

        Painter<JXMapViewer> routePainter = (g2d, map, width, height) -> {
            g2d.setColor(new Color(255, 165, 0)); // Orange for fallback
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0)); // Dashed line
            Point2D p1 = mapViewer.getTileFactory().geoToPixel(routePositions.get(0), mapViewer.getZoom());
            Point2D p2 = mapViewer.getTileFactory().geoToPixel(routePositions.get(1), mapViewer.getZoom());
            int x1 = (int) p1.getX();
            int y1 = (int) p1.getY();
            int x2 = (int) p2.getX();
            int y2 = (int) p2.getY();
            g2d.drawLine(x1, y1, x2, y2);
        };

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(waypointPainter, routePainter);
        mapViewer.setOverlayPainter(painter);
        mapViewer.zoomToBestFit(new HashSet<>(routePositions), 0.9);
    }

    private void handleMenuClick(String menuItem) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
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
                frame.setContentPane(new LoginPanel(userController, () -> {
                    frame.setContentPane(new DashboardPanel(user));
                }));
                break;
        }
        frame.revalidate();
        frame.repaint();
    }
}