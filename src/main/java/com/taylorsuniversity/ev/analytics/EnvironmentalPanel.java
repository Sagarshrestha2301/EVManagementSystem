package com.taylorsuniversity.ev.analytics;

import com.taylorsuniversity.ev.charginginfrastructure.ChargingStationPanel;
import com.taylorsuniversity.ev.routeplanning.TripPlanningPanel;
import com.taylorsuniversity.ev.usermanagement.*;
import com.taylorsuniversity.ev.vehiclemanagement.VehiclePanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EnvironmentalPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final User user;
    private List<EnvironmentalImpact> impacts;
    private List<EnvironmentalImpact> filteredImpacts;
    private ChartPanel chartPanel;
    private JPanel summaryPanel;
    private JLabel tipLabel;
    private static final Logger LOGGER = Logger.getLogger(EnvironmentalPanel.class.getName());

    public EnvironmentalPanel(User user) {
        this.user = user != null ? user : new User("Guest", "guest@example.com", "1234567890", "GUEST123", "ABC123", "password");
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30)); // More breathing room

        impacts = new ArrayList<>();
        initializeSampleData();
        filteredImpacts = new ArrayList<>(impacts);

        JPanel leftSidebar = createLeftSidebar();
        add(leftSidebar, BorderLayout.WEST);

        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.CENTER);
    }

    private void initializeSampleData() {
        impacts.add(new EnvironmentalImpact("Trip1", LocalDate.of(2025, 1, 1), 100.0));
        impacts.add(new EnvironmentalImpact("Trip2", LocalDate.of(2025, 2, 1), 150.0));
        impacts.add(new EnvironmentalImpact("Trip3", LocalDate.of(2025, 3, 1), 200.0));
        impacts.add(new EnvironmentalImpact("Trip4", LocalDate.of(2025, 4, 1), 250.0));
        impacts.add(new EnvironmentalImpact("Trip5", LocalDate.of(2025, 5, 1), 300.0));
        impacts.add(new EnvironmentalImpact("Trip6", LocalDate.of(2025, 6, 1), 350.0));
        impacts.add(new EnvironmentalImpact("Trip7", LocalDate.of(2025, 7, 1), 400.0));
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

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Environmental Impact - Overview of your sustainability metrics and goals");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(34, 139, 87));
        rightPanel.add(titleLabel, gbc);

        // Summary Panels
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.33;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel co2Panel = createSummaryCard("Total CO₂ Saved", "875 kg", "+15% from last month\nEquivalent to 14 trees planted");
        rightPanel.add(co2Panel, gbc);

        gbc.gridx = 1;
        JPanel efficiencyPanel = createSummaryCard("Energy Efficiency", "4.2 mi/kWh", "Above average efficiency\nSaving $45/month vs. gas vehicle");
        rightPanel.add(efficiencyPanel, gbc);

        gbc.gridx = 2;
        JPanel greenPanel = createSummaryCard("Green Energy Usage", "75%", "From renewable sources\nTarget 80%");
        rightPanel.add(greenPanel, gbc);

        // CO₂ Savings Trend
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 0.6;
        chartPanel = createChartPanel();
        rightPanel.add(chartPanel, gbc);

        // Sustainability Goals
        gbc.gridy = 3;
        gbc.weighty = 0;
        JPanel goalsPanel = createGoalsPanel();
        rightPanel.add(goalsPanel, gbc);

        // Eco Driving Tips
        gbc.gridy = 4;
        JPanel tipsPanel = createTipsPanel();
        rightPanel.add(tipsPanel, gbc);

        return rightPanel;
    }

    private JPanel createSummaryCard(String title, String value, String subText) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(new Color(34, 139, 87));
        card.add(valueLabel, BorderLayout.CENTER);

        JLabel subLabel = new JLabel("<html><body>" + subText.replace("\n", "<br>") + "</body></html>");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(100, 100, 100));
        card.add(subLabel, BorderLayout.SOUTH);

        return card;
    }

    private ChartPanel createChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (EnvironmentalImpact impact : impacts) {
            dataset.addValue(impact.getCarbonSaved(), "CO2 Saved", impact.getDate().getMonth().toString().substring(0, 3));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "CO₂ Savings Trend",
                "Month",
                "CO₂ Saved (kg)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(240, 245, 250));
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(34, 139, 87));
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);
        chart.setBackgroundPaint(new Color(245, 247, 250));

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(800, 300));
        return panel;
    }

    private JPanel createGoalsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel("Sustainability Goals");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(34, 139, 87));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel progressPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(34, 139, 87));
                g.fillRect(0, 0, (int) (getWidth() * 0.8), getHeight()); // 80% progress
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect((int) (getWidth() * 0.8), 0, getWidth(), getHeight());
            }
        };
        progressPanel.setPreferredSize(new Dimension(200, 20));
        panel.add(progressPanel, BorderLayout.CENTER);

        JLabel progressLabel = new JLabel("80% achieved");
        progressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progressLabel.setForeground(new Color(100, 100, 100));
        panel.add(progressLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTipsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel("Eco Driving Tips");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(34, 139, 87));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel tipsLabel = new JLabel("<html><body>" +
                "• Maintain steady speed for optimal efficiency<br>" +
                "• Use regenerative braking to maximize range</body></html>");
        tipsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tipsLabel.setForeground(new Color(100, 100, 100));
        panel.add(tipsLabel, BorderLayout.CENTER);

        return panel;
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