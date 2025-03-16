package com.taylorsuniversity.ev.vehiclemanagement;

import com.taylorsuniversity.ev.analytics.CostAnalysisPanel;
import com.taylorsuniversity.ev.analytics.EnvironmentalPanel;
import com.taylorsuniversity.ev.charginginfrastructure.ChargingStationPanel;
import com.taylorsuniversity.ev.routeplanning.TripPlanningPanel;
import com.taylorsuniversity.ev.usermanagement.DashboardPanel;
import com.taylorsuniversity.ev.usermanagement.LoginPanel;
import com.taylorsuniversity.ev.usermanagement.ProfilePanel;
import com.taylorsuniversity.ev.usermanagement.User;
import com.taylorsuniversity.ev.usermanagement.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class VehiclePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final VehicleController controller;
    private final User user; // Added User instance variable
    private JTable vehicleTable;
    private VehicleTableModel tableModel;
    private static final String VEHICLE_DATA_FILE = "src/main/resources/vehicle_data.txt";
    private static final Logger LOGGER = Logger.getLogger(VehiclePanel.class.getName());

    public VehiclePanel(User user) {
        this.user = user != null ? user : new User("Guest", "guest@example.com", "1234567890", "GUEST123", "ABC123", "password");
        this.controller = new VehicleController();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30)); // More breathing room


        tableModel = new VehicleTableModel(new ArrayList<>());
        vehicleTable = new JTable(tableModel);
        vehicleTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        vehicleTable.setRowHeight(40);
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vehicleTable.setGridColor(new Color(230, 235, 240));
        vehicleTable.setBackground(Color.WHITE);
        vehicleTable.setFillsViewportHeight(true);

        JTableHeader header = vehicleTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(34, 139, 87));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createLineBorder(new Color(20, 83, 52)));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        vehicleTable.setDefaultRenderer(Object.class, renderer);

        initializeSampleData();

        JPanel sidebar = createLeftSidebar();
        add(sidebar, BorderLayout.WEST);

        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
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


    private void handleMenuClick(String menuItem) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.getContentPane().removeAll();
            UserController userController = new UserController();
            switch (menuItem) {
                case "Dashboard":
                    frame.getContentPane().add(new DashboardPanel(user));
                    break;
                case "Profile":
                    frame.getContentPane().add(new ProfilePanel(user));
                    break;
                case "Trip Planning":
                    frame.getContentPane().add(new TripPlanningPanel(user));
                    break;
                case "Charging Stations":
                    frame.getContentPane().add(new ChargingStationPanel(user));
                    break;
                case "Cost Analysis":
                    frame.getContentPane().add(new CostAnalysisPanel(user));
                    break;
                case "Environmental Impact":
                    frame.getContentPane().add(new EnvironmentalPanel(user));
                    break;
                case "Vehicle Management":
                    frame.getContentPane().add(new VehiclePanel(user));
                    break;
                case "Logout":
                    int confirm = JOptionPane.showConfirmDialog(
                            frame,
                            "Are you sure you want to logout?",
                            "Confirm Logout",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        frame.setContentPane(new LoginPanel(userController, () -> {
                            frame.setContentPane(new DashboardPanel(user));
                        }));
                    }
                    // If "No" is selected, do nothing; the current panel remains
                    break;
            }
            frame.revalidate();
            frame.repaint();
        } else {
            System.err.println("No parent frame found!");
        }
    }

    private void initializeSampleData() {
        File file = new File(VEHICLE_DATA_FILE);
        if (!file.exists()) {
            if (controller.getAllVehicles().isEmpty()) {
                controller.addVehicle(new Vehicle("V001", "Tata Nexon EV", "Tata Motors", 30.4, 300));
                controller.addVehicle(new Vehicle("V002", "Hyundai Kona", "Hyundai", 39.2, 350));
                appendVehicleToFile(new Vehicle("V001", "Tata Nexon EV", "Tata Motors", 30.4, 300));
                appendVehicleToFile(new Vehicle("V002", "Hyundai Kona", "Hyundai", 39.2, 350));
            }
        } else {
            loadVehiclesFromFile();
        }
        refreshTable();
    }

    private void loadVehiclesFromFile() {
        List<Vehicle> vehicles = controller.getAllVehicles();
        vehicles.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(VEHICLE_DATA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    String id = data[0].trim();
                    String model = data[1].trim();
                    String manufacturer = data[2].trim();
                    double batteryCapacity = Double.parseDouble(data[3].trim());
                    double initialRange = Double.parseDouble(data[4].trim());
                    if (controller.getVehicle(id) == null) {
                        controller.addVehicle(new Vehicle(id, model, manufacturer, batteryCapacity, initialRange));
                    }
                }
            }
            refreshTable();
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading vehicle data: " + e.getMessage());
        }
    }

    private void appendVehicleToFile(Vehicle vehicle) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(VEHICLE_DATA_FILE, true))) {
            bw.write(String.format("%s,%s,%s,%.1f,%.1f%n",
                    vehicle.getId(),
                    vehicle.getModel(),
                    vehicle.getManufacturer(),
                    vehicle.getBatteryCapacity(),
                    vehicle.getBatteryMonitoring().getRemainingRange()));
        } catch (IOException e) {
            System.err.println("Error appending vehicle data: " + e.getMessage());
        }
    }

    private void deleteVehicleFromFile(String vehicleId) {
        File inputFile = new File(VEHICLE_DATA_FILE);
        File tempFile = new File("src/main/resources/vehicle_data_temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5 && !data[0].trim().equals(vehicleId)) {
                    writer.write(line + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.err.println("Error deleting vehicle from file: " + e.getMessage());
            return;
        }

        if (inputFile.delete()) {
            if (!tempFile.renameTo(inputFile)) {
                System.err.println("Error renaming temp file to original file.");
            }
        } else {
            System.err.println("Error deleting original file.");
        }
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel titleLabel = new JLabel("Vehicle Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titleLabel.setForeground(new Color(34, 139, 87));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        JLabel subtitleLabel = new JLabel("Effortlessly manage your electric vehicle fleet");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(90, 100, 110));
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 2, new Color(150, 150, 150, 100)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setOpaque(false);

        JButton addButton = createStyledButton("Add Vehicle", new Color(34, 139, 87), "/icons/add.png");
        addButton.addActionListener(e -> showAddVehicleDialog());
        buttonPanel.add(addButton);

        JButton deleteButton = createStyledButton("Delete Vehicle", new Color(200, 50, 50), "/icons/delete.png");
        deleteButton.addActionListener(e -> deleteVehicle());
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JButton createStyledButton(String text, Color baseColor, String iconPath) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        if (iconPath != null) {
            ImageIcon icon = loadIcon(iconPath, 16, 16);
            if (icon != null) button.setIcon(icon);
        }
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

    private void showAddVehicleDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add New Vehicle", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(245, 247, 250));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Add New Vehicle");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(34, 139, 87));
        headerPanel.add(titleLabel);
        dialog.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 15, 20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        formPanel.setOpaque(false);

        JLabel idLabel = new JLabel("Vehicle ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField idField = new JTextField();
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel modelLabel = new JLabel("Model:");
        modelLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField modelField = new JTextField();
        modelField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel manufacturerLabel = new JLabel("Manufacturer:");
        manufacturerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField manufacturerField = new JTextField();
        manufacturerField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel batteryLabel = new JLabel("Battery Capacity (kWh):");
        batteryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField batteryField = new JTextField();
        batteryField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel rangeLabel = new JLabel("Initial Range (km):");
        rangeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField rangeField = new JTextField();
        rangeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(modelLabel);
        formPanel.add(modelField);
        formPanel.add(manufacturerLabel);
        formPanel.add(manufacturerField);
        formPanel.add(batteryLabel);
        formPanel.add(batteryField);
        formPanel.add(rangeLabel);
        formPanel.add(rangeField);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setOpaque(false);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton addButton = new JButton("Add Vehicle");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(new Color(34, 139, 87));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String model = modelField.getText().trim();
                String manufacturer = manufacturerField.getText().trim();
                double batteryCapacity = Double.parseDouble(batteryField.getText().trim());
                double initialRange = Double.parseDouble(rangeField.getText().trim());

                if (id.isEmpty() || model.isEmpty() || manufacturer.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (controller.getVehicle(id) != null) {
                    JOptionPane.showMessageDialog(dialog, "Vehicle ID already exists.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Vehicle newVehicle = new Vehicle(id, model, manufacturer, batteryCapacity, initialRange);
                appendVehicleToFile(newVehicle);
                controller.addVehicle(newVehicle);
                refreshTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Vehicle added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteVehicle() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String vehicleId = (String) vehicleTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete vehicle " + vehicleId + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteVehicle(vehicleId);
            deleteVehicleFromFile(vehicleId);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Vehicle deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshTable() {
        List<Vehicle> vehicles = controller.getAllVehicles();
        tableModel.setVehicles(vehicles);
        tableModel.fireTableDataChanged();
    }

    private class VehicleTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private List<Vehicle> vehicles;
        private final String[] columnNames = {"ID", "Model", "Manufacturer", "Battery Capacity (kWh)", "Range (km)", "Status"};

        public VehicleTableModel(List<Vehicle> vehicles) {
            this.vehicles = vehicles;
        }

        public void setVehicles(List<Vehicle> vehicles) {
            this.vehicles = vehicles;
        }

        @Override
        public int getRowCount() {
            return vehicles.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Vehicle vehicle = vehicles.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return vehicle.getId();
                case 1:
                    return vehicle.getModel();
                case 2:
                    return vehicle.getManufacturer();
                case 3:
                    return vehicle.getBatteryCapacity();
                case 4:
                    return vehicle.getBatteryMonitoring().getRemainingRange();
                case 5:
                    return vehicle.getBatteryMonitoring().getChargeStatus();
                default:
                    return null;
            }
        }
    }
}