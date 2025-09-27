package com.grocerypos.ui.panels;

import com.grocerypos.Application;
import com.grocerypos.dao.BillDAO;
import com.grocerypos.dao.ItemDAO;
import com.grocerypos.model.Bill;
import com.grocerypos.model.Item;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dashboard panel with charts and analytics
 */
public class DashboardPanel extends JPanel {
    private Application parent;
    private BillDAO billDAO;
    private ItemDAO itemDAO;
    
    private JPanel chartsPanel;
    private JLabel totalSalesLabel;
    private JLabel totalBillsLabel;
    private JLabel lowStockLabel;
    private JLabel topProductLabel;
    
    public DashboardPanel(Application parent) {
        this.parent = parent;
        initializeComponents();
        setupLayout();
        loadDashboardData();
    }
    
    private void initializeComponents() {
        try {
            billDAO = new BillDAO();
            itemDAO = new ItemDAO();
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            // Initialize with null - will be handled gracefully
        }
        
        // Summary labels
        totalSalesLabel = new JLabel("Total Sales: ₹0.00");
        totalBillsLabel = new JLabel("Total Bills: 0");
        lowStockLabel = new JLabel("Low Stock Items: 0");
        topProductLabel = new JLabel("Top Product: N/A");
        
        // Style labels
        Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
        totalSalesLabel.setFont(labelFont);
        totalBillsLabel.setFont(labelFont);
        lowStockLabel.setFont(labelFont);
        topProductLabel.setFont(labelFont);
        
        totalSalesLabel.setForeground(new Color(34, 197, 94));
        totalBillsLabel.setForeground(new Color(59, 130, 246));
        lowStockLabel.setForeground(new Color(239, 68, 68));
        topProductLabel.setForeground(new Color(168, 85, 247));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        
        // Top panel - Summary cards
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.NORTH);
        
        // Center panel - Charts
        chartsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        chartsPanel.setBackground(Color.WHITE);
        chartsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(chartsPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create summary cards
        panel.add(createSummaryCard("💰", totalSalesLabel, "Total Sales", new Color(34, 197, 94)));
        panel.add(createSummaryCard("📊", totalBillsLabel, "Total Bills", new Color(59, 130, 246)));
        panel.add(createSummaryCard("⚠️", lowStockLabel, "Low Stock", new Color(239, 68, 68)));
        panel.add(createSummaryCard("🏆", topProductLabel, "Top Product", new Color(168, 85, 247)));
        
        return panel;
    }
    
    private JPanel createSummaryCard(String icon, JLabel valueLabel, String title, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setForeground(color);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.GRAY);
        
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    public void refreshData() {
        loadDashboardData();
    }
    
    private void loadDashboardData() {
        try {
            // Load summary data
            loadSummaryData();
            
            // Load charts
            loadCharts();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading dashboard data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSummaryData() throws SQLException {
        if (billDAO == null || itemDAO == null) {
            return; // Skip loading if DAOs are not initialized
        }
        
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        
        List<Bill> todayBills = billDAO.findByDateRange(startOfDay, endOfDay);
        
        // Calculate total sales
        BigDecimal totalSales = todayBills.stream()
            .map(Bill::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        totalSalesLabel.setText("Total Sales: ₹" + String.format("%.2f", totalSales));
        totalBillsLabel.setText("Total Bills: " + todayBills.size());
        
        // Count low stock items
        List<Item> lowStockItems = itemDAO.findLowStockItems();
        lowStockLabel.setText("Low Stock Items: " + lowStockItems.size());
        
        // Find top selling product
        String topProduct = findTopSellingProduct(todayBills);
        topProductLabel.setText("Top Product: " + topProduct);
    }
    
    private String findTopSellingProduct(List<Bill> bills) {
        // This is a simplified version - in a real app, you'd analyze bill items
        if (bills.isEmpty()) {
            return "N/A";
        }
        
        // For demo purposes, return a placeholder
        return "Sample Product";
    }
    
    private void loadCharts() throws SQLException {
        chartsPanel.removeAll();
        
        // Sales trend chart
        ChartPanel salesChart = createSalesTrendChart();
        chartsPanel.add(salesChart);
        
        // Top products chart
        ChartPanel productsChart = createTopProductsChart();
        chartsPanel.add(productsChart);
        
        // Stock status pie chart
        ChartPanel stockChart = createStockStatusChart();
        chartsPanel.add(stockChart);
        
        // Payment methods chart
        ChartPanel paymentChart = createPaymentMethodsChart();
        chartsPanel.add(paymentChart);
        
        chartsPanel.revalidate();
        chartsPanel.repaint();
    }
    
    private ChartPanel createSalesTrendChart() throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Get sales data for the last 7 days
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            
            List<Bill> dayBills = billDAO.findByDateRange(startOfDay, endOfDay);
            BigDecimal daySales = dayBills.stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            dataset.addValue(daySales.doubleValue(), "Sales", date.format(DateTimeFormatter.ofPattern("MM/dd")));
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            "📈 Sales Trend (Last 7 Days)",
            "Date",
            "Sales (₹)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Customize chart
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(59, 130, 246));
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        
        return new ChartPanel(chart);
    }
    
    private ChartPanel createTopProductsChart() throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Sample data - in a real app, you'd get this from bill items
        dataset.addValue(150, "Sales", "Product A");
        dataset.addValue(120, "Sales", "Product B");
        dataset.addValue(90, "Sales", "Product C");
        dataset.addValue(75, "Sales", "Product D");
        dataset.addValue(60, "Sales", "Product E");
        
        JFreeChart chart = ChartFactory.createBarChart(
            "🏆 Top Selling Products",
            "Products",
            "Sales (₹)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(34, 197, 94));
        
        return new ChartPanel(chart);
    }
    
    private ChartPanel createStockStatusChart() throws SQLException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        List<Item> allItems = itemDAO.findAll();
        List<Item> lowStockItems = itemDAO.findLowStockItems();
        
        int normalStock = allItems.size() - lowStockItems.size();
        int lowStock = lowStockItems.size();
        
        dataset.setValue("Normal Stock", normalStock);
        dataset.setValue("Low Stock", lowStock);
        
        JFreeChart chart = ChartFactory.createPieChart(
            "📊 Stock Status",
            dataset,
            true,
            true,
            false
        );
        
        chart.setBackgroundPaint(Color.WHITE);
        
        return new ChartPanel(chart);
    }
    
    private ChartPanel createPaymentMethodsChart() throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Sample data - in a real app, you'd analyze actual payment methods
        dataset.addValue(45, "Count", "Cash");
        dataset.addValue(30, "Count", "Card");
        dataset.addValue(20, "Count", "UPI");
        dataset.addValue(5, "Count", "Online");
        
        JFreeChart chart = ChartFactory.createBarChart(
            "💳 Payment Methods",
            "Method",
            "Count",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(168, 85, 247));
        
        return new ChartPanel(chart);
    }
}
