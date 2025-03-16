package com.taylorsuniversity.ev.analytics;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class ProgressVisualizationPanel extends JPanel {
    private final AnalysisController costController;
    private final List<EnvironmentalImpact> impacts;

    public ProgressVisualizationPanel(AnalysisController costController, List<EnvironmentalImpact> impacts) {
        if (costController == null) throw new IllegalArgumentException("CostController cannot be null");
        if (impacts == null) throw new IllegalArgumentException("Impacts list cannot be null");
        this.costController = costController;
        this.impacts = impacts;
        setLayout(new GridLayout(1, 2, 30, 30)); // Increased spacing for larger window
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(15, 15, 15, 15)); // Increased padding
        setPreferredSize(new Dimension(900, 300)); // Optimized for 1440x900 dashboard

        add(createSavingsChartCard());
        add(createCO2ChartCard());
    }

    private JPanel createSavingsChartCard() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        double savings = costController.calculateCurrentSavings();
        double target = costController.getSavingsTarget();
        dataset.setValue("Savings Achieved", savings);
        dataset.setValue("Remaining to Target", Math.max(0, target - savings));

        JFreeChart chart = ChartFactory.createPieChart(
                "Savings Progress", dataset, true, true, false); // Enable legend and tooltips
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Savings Achieved", new Color(34, 139, 87));
        plot.setSectionPaint("Remaining to Target", new Color(120, 120, 120));
        plot.setBackgroundPaint(new Color(240, 245, 250));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})", new DecimalFormat("#,##0.0"), new DecimalFormat("0%")));
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 14)); // Larger font for readability
        plot.setLabelGap(0.02);
        plot.setInteriorGap(0.08); // Slightly larger gap
        plot.setOutlineStroke(new BasicStroke(2.0f)); // Thicker outline
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18)); // Larger title

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 250)); // Adjusted for larger window

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 180, 180), 2, true), // Thicker border
                new EmptyBorder(20, 20, 20, 20))); // More padding
        card.add(chartPanel, BorderLayout.CENTER);

        JLabel summaryLabel = new JLabel(String.format("Savings: %.1f / %.1f", savings, target), SwingConstants.CENTER);
        summaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Larger font
        summaryLabel.setForeground(new Color(34, 139, 87));
        card.add(summaryLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCO2ChartCard() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        double totalCO2Saved = impacts.stream().mapToDouble(EnvironmentalImpact::getCarbonSaved).sum();
        double targetCO2 = 1000.0; // Could be made dynamic via a setter or config
        dataset.setValue("CO2 Saved", totalCO2Saved);
        dataset.setValue("Remaining to Target", Math.max(0, targetCO2 - totalCO2Saved));

        JFreeChart chart = ChartFactory.createPieChart(
                "CO2 Reduction", dataset, true, true, false); // Enable legend and tooltips
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("CO2 Saved", new Color(34, 139, 87));
        plot.setSectionPaint("Remaining to Target", new Color(120, 120, 120));
        plot.setBackgroundPaint(new Color(240, 245, 250));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} kg ({2})", new DecimalFormat("#,##0.0"), new DecimalFormat("0%")));
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 14)); // Larger font for readability
        plot.setLabelGap(0.02);
        plot.setInteriorGap(0.08); // Slightly larger gap
        plot.setOutlineStroke(new BasicStroke(2.0f)); // Thicker outline
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18)); // Larger title

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 250)); // Adjusted for larger window

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 180, 180), 2, true), // Thicker border
                new EmptyBorder(20, 20, 20, 20))); // More padding
        card.add(chartPanel, BorderLayout.CENTER);

        JLabel summaryLabel = new JLabel(String.format("CO2 Saved: %.1f / %.1f kg", totalCO2Saved, targetCO2), SwingConstants.CENTER);
        summaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Larger font
        summaryLabel.setForeground(new Color(34, 139, 87));
        card.add(summaryLabel, BorderLayout.SOUTH);

        return card;
    }
}