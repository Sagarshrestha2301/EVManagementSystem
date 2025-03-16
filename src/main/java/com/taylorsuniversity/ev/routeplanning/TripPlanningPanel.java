package com.taylorsuniversity.ev.routeplanning;

import com.taylorsuniversity.ev.analytics.CostAnalysisPanel;
import com.taylorsuniversity.ev.analytics.EnvironmentalPanel;
import com.taylorsuniversity.ev.charginginfrastructure.ChargingStation;
import com.taylorsuniversity.ev.charginginfrastructure.ChargingStationPanel;
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
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TripPlanningPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(TripPlanningPanel.class.getName());

    private static final long serialVersionUID = 1L;
    private final User user;
    private final TripPlannerController tripPlannerController;
    private JXMapViewer mapViewer;
    private JComboBox<String> startComboBox;
    private JComboBox<String> endComboBox;
    private JTextArea tripDetailsArea;
    private JLabel vehicleDetailsLabel;
    private JComboBox<String> vehicleComboBox;
    private List<VehicleData> vehicles;
    private List<ChargingStation> chargingStations;
    private boolean settingStart = true;
    private GeoPosition startPosition;
    private GeoPosition endPosition;

    // Major cities in Nepal for quick selection
    private static final String[] NEPAL_CITIES = {
            "Kathmandu (Lat: 27.7172, Lon: 85.3240)",
            "Lalitpur (Lat: 27.6766, Lon: 85.3188)",
            "Pokhara (Lat: 28.2096, Lon: 83.9856)",
            "Biratnagar (Lat: 26.4525, Lon: 87.2718)",
            "Bharatpur (Lat: 27.6833, Lon: 84.4333)",
            "Birgunj (Lat: 27.0104, Lon: 84.8777)",
            "Dhangadhi (Lat: 28.7016, Lon: 80.5898)",
            "Nepalgunj (Lat: 28.0500, Lon: 81.6167)"
    };

    public TripPlanningPanel(User user) {
        this.user = user != null ? user : new User("Guest", "guest@example.com", "1234567890", "GUEST123", "ABC123", "password");
        this.tripPlannerController = new TripPlannerController();
        this.vehicles = loadVehiclesFromFile();
        this.chargingStations = loadChargingStationsFromFile();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30)); // More breathing room

        JPanel leftSidebar = createLeftSidebar();
        add(leftSidebar, BorderLayout.WEST);

        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.CENTER);

        updateMap(null);
    }

    private List<VehicleData> loadVehiclesFromFile() {
        List<VehicleData> vehicleList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/vehicle_data.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String id = parts[0];
                    String model = parts[1];
                    String manufacturer = parts[2];
                    double batteryCapacity = Double.parseDouble(parts[3]);
                    double range = Double.parseDouble(parts[4]);
                    vehicleList.add(new VehicleData(id, model, manufacturer, batteryCapacity, range));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading vehicle data: " + e.getMessage());
        }
        return vehicleList;
    }

    private List<ChargingStation> loadChargingStationsFromFile() {
        List<ChargingStation> stationList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/charging_stations.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0].trim();
                    double latitude = Double.parseDouble(parts[1].trim());
                    double longitude = Double.parseDouble(parts[2].trim());
                    stationList.add(new ChargingStation(name, latitude, longitude));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading charging stations: " + e.getMessage());
        }
        return stationList;
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
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Trip Planning (Nepal)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titleLabel.setForeground(new Color(34, 139, 87));
        rightPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel vehicleLabel = new JLabel("Vehicle:");
        vehicleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        rightPanel.add(vehicleLabel, gbc);

        gbc.gridx = 1;
        String[] vehicleNames = vehicles.stream().map(v -> v.model).toArray(String[]::new);
        vehicleComboBox = new JComboBox<>(vehicleNames);
        vehicleComboBox.setSelectedItem(user.getVehicleModel());
        vehicleComboBox.addActionListener(e -> updateVehicleDetails());
        vehicleComboBox.setToolTipText("Select your vehicle");
        rightPanel.add(vehicleComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        vehicleDetailsLabel = new JLabel();
        vehicleDetailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        vehicleDetailsLabel.setForeground(new Color(34, 139, 87));
        updateVehicleDetails();
        rightPanel.add(vehicleDetailsLabel, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        JLabel startLabel = new JLabel("Start:");
        startLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        rightPanel.add(startLabel, gbc);

        gbc.gridx = 1;
        startComboBox = new JComboBox<>(NEPAL_CITIES);
        startComboBox.setEditable(true);
        startComboBox.addActionListener(e -> {
            String selected = (String) startComboBox.getSelectedItem();
            if (selected != null && !selected.trim().isEmpty()) {
                Location loc = parseLocation(selected);
                if (loc != null) {
                    startPosition = new GeoPosition(loc.getLatitude(), loc.getLongitude());
                    zoomToLocation(startPosition);
                    updateMap(null);
                }
            }
        });
        startComboBox.setToolTipText("Select or type start location, or click map");
        rightPanel.add(startComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel endLabel = new JLabel("End:");
        endLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        rightPanel.add(endLabel, gbc);

        gbc.gridx = 1;
        endComboBox = new JComboBox<>(NEPAL_CITIES);
        endComboBox.setEditable(true);
        endComboBox.addActionListener(e -> {
            String selected = (String) endComboBox.getSelectedItem();
            if (selected != null && !selected.trim().isEmpty()) {
                Location loc = parseLocation(selected);
                if (loc != null) {
                    endPosition = new GeoPosition(loc.getLatitude(), loc.getLongitude());
                    zoomToLocation(endPosition);
                    updateMap(null);
                }
            }
        });
        endComboBox.setToolTipText("Select or type end location, or click map");
        rightPanel.add(endComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        JButton resetButton = createStyledButton("Reset", new Color(200, 80, 80), 80, 30);
        resetButton.setToolTipText("Reset all selections");
        resetButton.addActionListener(e -> resetSelections());
        rightPanel.add(resetButton, gbc);

        gbc.gridx = 1;
        JButton planButton = createStyledButton("Plan Trip", new Color(34, 139, 87), 100, 30);
        planButton.setToolTipText("Calculate and show trip route");
        planButton.addActionListener(this::planTripAction);
        rightPanel.add(planButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7;
        mapViewer = createMapViewer();
        mapViewer.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        rightPanel.add(mapViewer, gbc);

        gbc.gridy = 7;
        gbc.weighty = 0.2;
        tripDetailsArea = new JTextArea("Select start and end locations in Nepal, then plan your trip.");
        tripDetailsArea.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        tripDetailsArea.setForeground(new Color(34, 139, 87));
        tripDetailsArea.setEditable(false);
        tripDetailsArea.setLineWrap(true);
        tripDetailsArea.setWrapStyleWord(true);
        tripDetailsArea.setBackground(new Color(245, 247, 250));
        JScrollPane scrollPane = new JScrollPane(tripDetailsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Trip Details"));
        rightPanel.add(scrollPane, gbc);

        return rightPanel;
    }

    private void updateVehicleDetails() {
        String selectedModel = (String) vehicleComboBox.getSelectedItem();
        VehicleData selectedVehicle = vehicles.stream()
                .filter(v -> v.model.equals(selectedModel))
                .findFirst()
                .orElse(vehicles.get(0));
        user.setVehicleModel(selectedVehicle.model);
        user.setBatteryRange(selectedVehicle.range);
        vehicleDetailsLabel.setText(String.format("Vehicle: %s, Battery: %.1f kWh, Range: %.1f km",
                selectedVehicle.model, selectedVehicle.batteryCapacity, selectedVehicle.range));
    }

    private JButton createStyledButton(String text, Color baseColor, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(width, height));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JXMapViewer createMapViewer() {
        JXMapViewer map = new JXMapViewer();
        DefaultTileFactory tileFactory = new DefaultTileFactory(new OSMTileFactoryInfo());
        map.setTileFactory(tileFactory);
        map.setZoom(5);
        map.setAddressLocation(new GeoPosition(27.7172, 85.3240)); // Center on Kathmandu, Nepal

        map.addMouseListener(new PanMouseInputListener(map));
        map.addMouseWheelListener(new ZoomMouseWheelListenerCenter(map));
        map.addMouseMotionListener(new PanMouseInputListener(map));

        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    GeoPosition clickedPos = map.convertPointToGeoPosition(e.getPoint());
                    String locationName = String.format("Lat: %.4f, Lon: %.4f", clickedPos.getLatitude(), clickedPos.getLongitude());
                    if (settingStart) {
                        startComboBox.addItem(locationName);
                        startComboBox.setSelectedItem(locationName);
                        startPosition = clickedPos;
                        settingStart = false;
                        map.setToolTipText("Click to set End location");
                    } else {
                        endComboBox.addItem(locationName);
                        endComboBox.setSelectedItem(locationName);
                        endPosition = clickedPos;
                        settingStart = true;
                        map.setToolTipText("Click to set Start location");
                    }
                    // Do not zoom, just update the map with the new pin
                    updateMap(null);
                }
            }
        });
        map.setToolTipText("Click to set Start location");

        return map;
    }

    private void zoomToLocation(GeoPosition position) {
        mapViewer.setAddressLocation(position);
        mapViewer.setZoom(10); // Zoom level to focus on the selected location
    }

    private void resetSelections() {
        startComboBox.setSelectedIndex(-1);
        endComboBox.setSelectedIndex(-1);
        startPosition = null;
        endPosition = null;
        tripDetailsArea.setText("Select start and end locations in Nepal, then plan your trip.");
        updateMap(null);
        settingStart = true;
        mapViewer.setToolTipText("Click to set Start location");
        mapViewer.setAddressLocation(new GeoPosition(27.7172, 85.3240)); // Reset to Kathmandu
        mapViewer.setZoom(5);
    }

    private void planTripAction(ActionEvent e) {
        String startName = (String) startComboBox.getSelectedItem();
        String endName = (String) endComboBox.getSelectedItem();
        if (startName == null || endName == null || startName.trim().isEmpty() || endName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please specify both start and end locations.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (startName.equals(endName)) {
            JOptionPane.showMessageDialog(this, "Start and end locations cannot be the same.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Location start = parseLocation(startName);
        Location end = parseLocation(endName);

        if (start == null || end == null) {
            JOptionPane.showMessageDialog(this, "Invalid location format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double distance = calculateDistance(start, end);
            double duration = tripPlannerController.estimateTripTime(distance);
            Trip trip = new Trip(start, end, distance, new ArrayList<ChargingStation>());
            trip.setVehicleModel(user.getVehicleModel());
            trip.setBatteryRange(user.getBatteryRange());
            trip.setCurrentChargeLevel(100.0); // Assume full charge at start

            ChargingStation nearestStation = findNearestChargingStation(start, end);
            boolean isFeasible = trip.isFeasible();

            updateMap(trip);

            DecimalFormat df = new DecimalFormat("#.##");
            StringBuilder summary = new StringBuilder();
            summary.append(String.format("Trip: %s to %s\n", start.getName(), end.getName()));
            summary.append(String.format("Distance: %s km\n", df.format(distance)));
            summary.append(String.format("Duration: %s hours\n", df.format(duration)));
            summary.append(String.format("Feasible: %s\n", isFeasible ? "Yes" : "No (Charge needed)"));
            if (nearestStation != null) {
                summary.append(String.format("Nearest Charging Station: %s (Lat: %.4f, Lon: %.4f)\n",
                        nearestStation.getName(), nearestStation.getLatitude(), nearestStation.getLongitude()));
            } else {
                summary.append("No charging stations found along the route.\n");
            }
            tripDetailsArea.setText(summary.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error planning trip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Location parseLocation(String locationStr) {
        try {
            if (locationStr.contains("(")) {
                String[] parts = locationStr.split("\\(");
                String name = parts[0].trim();
                String[] coords = parts[1].replace(")", "").split(",");
                double latitude = Double.parseDouble(coords[0].replace("Lat: ", "").trim());
                double longitude = Double.parseDouble(coords[1].replace("Lon: ", "").trim());
                return new Location(name, latitude, longitude);
            } else {
                String[] parts = locationStr.replace("Lat: ", "").replace("Lon: ", "").split(", ");
                double latitude = Double.parseDouble(parts[0].trim());
                double longitude = Double.parseDouble(parts[1].trim());
                return new Location(locationStr, latitude, longitude);
            }
        } catch (Exception e) {
            System.err.println("Error parsing location: " + e.getMessage());
            return null;
        }
    }

    private double calculateDistance(Location start, Location end) {
        final int R = 6371; // Earth radius in kilometers
        double lat1 = Math.toRadians(start.getLatitude());
        double lon1 = Math.toRadians(start.getLongitude());
        double lat2 = Math.toRadians(end.getLatitude());
        double lon2 = Math.toRadians(end.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private ChargingStation findNearestChargingStation(Location start, Location end) {
        if (chargingStations.isEmpty()) return null;

        double minDistance = Double.MAX_VALUE;
        ChargingStation nearest = null;

        double midLat = (start.getLatitude() + end.getLatitude()) / 2;
        double midLon = (start.getLongitude() + end.getLongitude()) / 2;
        Location midpoint = new Location("Midpoint", midLat, midLon);

        for (ChargingStation station : chargingStations) {
            double distToMid = calculateDistance(midpoint, new Location(station.getName(), station.getLatitude(), station.getLongitude()));
            if (distToMid < minDistance) {
                minDistance = distToMid;
                nearest = station;
            }
        }
        return nearest;
    }

    private void updateMap(Trip trip) {
        List<GeoPosition> routePositions = new ArrayList<>();
        Set<DefaultWaypoint> waypoints = new HashSet<>();

        // Add charging stations as waypoints
        for (ChargingStation station : chargingStations) {
            GeoPosition stationPos = new GeoPosition(station.getLatitude(), station.getLongitude());
            waypoints.add(new DefaultWaypoint(stationPos));
        }

        if (trip != null) {
            GeoPosition startPos = new GeoPosition(trip.getStartLocation().getLatitude(), trip.getStartLocation().getLongitude());
            GeoPosition endPos = new GeoPosition(trip.getEndLocation().getLatitude(), trip.getEndLocation().getLongitude());
            routePositions.add(startPos);
            routePositions.add(endPos);
            waypoints.add(new DefaultWaypoint(startPos));
            waypoints.add(new DefaultWaypoint(endPos));
            adjustMapToFitRoute(routePositions);
        } else {
            if (startPosition != null) {
                waypoints.add(new DefaultWaypoint(startPosition));
                routePositions.add(startPosition);
            }
            if (endPosition != null) {
                waypoints.add(new DefaultWaypoint(endPosition));
                routePositions.add(endPosition);
            }
            if (!routePositions.isEmpty() && trip == null) {
                // Do not adjust zoom unless planning a trip
                // Just update the pins
            }
        }

        WaypointPainter<DefaultWaypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);
        waypointPainter.setRenderer((g, map, waypoint) -> {
            Point2D p = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom());
            boolean isStart = startPosition != null && waypoint.getPosition().equals(startPosition);
            boolean isEnd = endPosition != null && waypoint.getPosition().equals(endPosition);
            if (isStart) {
                g.setColor(new Color(34, 139, 87)); // Green for start
                g.fillOval((int) p.getX() - 10, (int) p.getY() - 10, 20, 20);
                g.setColor(Color.WHITE);
                g.drawOval((int) p.getX() - 10, (int) p.getY() - 10, 20, 20);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g.drawString("Start", (int) p.getX() - 15, (int) p.getY() - 15);
            } else if (isEnd) {
                g.setColor(new Color(200, 80, 80)); // Red for end
                g.fillOval((int) p.getX() - 10, (int) p.getY() - 10, 20, 20);
                g.setColor(Color.WHITE);
                g.drawOval((int) p.getX() - 10, (int) p.getY() - 10, 20, 20);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g.drawString("End", (int) p.getX() - 10, (int) p.getY() - 15);
            } else {
                g.setColor(Color.BLUE); // Blue for charging stations
                g.fillRect((int) p.getX() - 8, (int) p.getY() - 8, 16, 16);
                g.setColor(Color.WHITE);
                g.drawRect((int) p.getX() - 8, (int) p.getY() - 8, 16, 16);
            }
        });

        Painter<JXMapViewer> routePainter = (g2d, map, width, height) -> {
            if (routePositions.size() < 2) return;
            g2d.setColor(new Color(34, 139, 87));
            g2d.setStroke(new BasicStroke(3));
            Point2D p1 = map.getTileFactory().geoToPixel(routePositions.get(0), map.getZoom());
            Point2D p2 = map.getTileFactory().geoToPixel(routePositions.get(1), map.getZoom());
            g2d.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
        };

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(waypointPainter, routePainter);
        mapViewer.setOverlayPainter(painter);
        mapViewer.repaint();
    }

    private void adjustMapToFitRoute(List<GeoPosition> routePositions) {
        if (routePositions.isEmpty()) {
            mapViewer.setAddressLocation(new GeoPosition(27.7172, 85.3240)); // Kathmandu
            mapViewer.setZoom(5);
            return;
        }

        double minLat = routePositions.stream().mapToDouble(GeoPosition::getLatitude).min().getAsDouble();
        double maxLat = routePositions.stream().mapToDouble(GeoPosition::getLatitude).max().getAsDouble();
        double minLon = routePositions.stream().mapToDouble(GeoPosition::getLongitude).min().getAsDouble();
        double maxLon = routePositions.stream().mapToDouble(GeoPosition::getLongitude).max().getAsDouble();

        // Include charging stations in bounds
        for (ChargingStation station : chargingStations) {
            minLat = Math.min(minLat, station.getLatitude());
            maxLat = Math.max(maxLat, station.getLatitude());
            minLon = Math.min(minLon, station.getLongitude());
            maxLon = Math.max(maxLon, station.getLongitude());
        }

        GeoPosition center = new GeoPosition((minLat + maxLat) / 2, (minLon + maxLon) / 2);
        mapViewer.setAddressLocation(center);

        double latDiff = Math.abs(maxLat - minLat);
        double lonDiff = Math.abs(maxLon - minLon);
        int zoom = Math.min(15, (int) (5 - Math.log(Math.max(latDiff, lonDiff)) / Math.log(2)));
        mapViewer.setZoom(zoom);
    }

    private void handleMenuClick(String menuItem) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
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
                frame.setContentPane(new LoginPanel(userController, () -> frame.setContentPane(new DashboardPanel(user))));
                break;
        }
        frame.revalidate();
        frame.repaint();
    }

    private static class VehicleData {
        String id, model, manufacturer;
        double batteryCapacity, range;

        VehicleData(String id, String model, String manufacturer, double batteryCapacity, double range) {
            this.id = id;
            this.model = model;
            this.manufacturer = manufacturer;
            this.batteryCapacity = batteryCapacity;
            this.range = range;
        }
    }
}