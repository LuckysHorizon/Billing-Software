package com.grocerypos.ui;

import com.grocerypos.dao.BillDAO;
import com.grocerypos.dao.ItemDAO;
import com.grocerypos.model.Bill;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Reports window for sales and inventory reports
 */
public class ReportsWindow extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable salesTable;
    private DefaultTableModel salesModel;
    private JTable stockTable;
    private DefaultTableModel stockModel;
    private JDatePicker startDatePicker;
    private JDatePicker endDatePicker;
    private JButton generateReportButton;
    private JButton exportButton;
    private JLabel totalSalesLabel;
    private JLabel totalBillsLabel;
    
    private BillDAO billDAO;
    private ItemDAO itemDAO;

    public ReportsWindow() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindow();
        
        try {
            billDAO = new BillDAO();
            itemDAO = new ItemDAO();
            loadDefaultReport();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeComponents() {
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Sales table
        String[] salesColumns = {"Date", "Bill No", "Customer", "Items", "Subtotal", "GST", "Total", "Payment"};
        salesModel = new DefaultTableModel(salesColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        salesTable = new JTable(salesModel);
        salesTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        salesTable.setRowHeight(25);
        
        // Stock table
        String[] stockColumns = {"Name", "Barcode", "Stock", "Min Stock", "Status", "Category"};
        stockModel = new DefaultTableModel(stockColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stockTable = new JTable(stockModel);
        stockTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        stockTable.setRowHeight(25);
        
        // Date pickers (simplified - using text fields for now)
        startDatePicker = new JDatePicker();
        endDatePicker = new JDatePicker();
        
        generateReportButton = new JButton("Generate Report");
        exportButton = new JButton("Export to CSV");
        
        generateReportButton.setBackground(new Color(0, 120, 215));
        generateReportButton.setForeground(Color.WHITE);
        generateReportButton.setFocusPainted(false);
        
        exportButton.setBackground(new Color(40, 167, 69));
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);
        
        totalSalesLabel = new JLabel("Total Sales: ₹0.00");
        totalBillsLabel = new JLabel("Total Bills: 0");
        
        totalSalesLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        totalBillsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel - Date range and controls
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Report Controls"));
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        datePanel.add(new JLabel("From:"));
        datePanel.add(startDatePicker);
        datePanel.add(new JLabel("To:"));
        datePanel.add(endDatePicker);
        datePanel.add(generateReportButton);
        datePanel.add(exportButton);
        
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        summaryPanel.add(totalBillsLabel);
        summaryPanel.add(totalSalesLabel);
        
        topPanel.add(datePanel, BorderLayout.WEST);
        topPanel.add(summaryPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel - Tabbed reports
        tabbedPane.addTab("Sales Report", createSalesPanel());
        tabbedPane.addTab("Stock Report", createStockPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void setupEventHandlers() {
        generateReportButton.addActionListener(e -> generateReport());
        exportButton.addActionListener(e -> exportReport());
        
        // Set default dates
        startDatePicker.setDate(LocalDate.now().minusDays(7));
        endDatePicker.setDate(LocalDate.now());
    }

    private void setupWindow() {
        setTitle("Grocery POS - Reports");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));
    }

    private void loadDefaultReport() {
        generateReport();
    }

    private void generateReport() {
        try {
            LocalDate startDate = startDatePicker.getDate();
            LocalDate endDate = endDatePicker.getDate();
            
            if (startDate == null || endDate == null) {
                JOptionPane.showMessageDialog(this, "Please select both start and end dates", "Date Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (startDate.isAfter(endDate)) {
                JOptionPane.showMessageDialog(this, "Start date cannot be after end date", "Invalid Date Range", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Generate sales report
            generateSalesReport(startDate, endDate);
            
            // Generate stock report
            generateStockReport();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateSalesReport(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            
            List<Bill> bills = billDAO.findByDateRange(startDateTime, endDateTime);
            
            salesModel.setRowCount(0);
            BigDecimal totalSales = BigDecimal.ZERO;
            
            for (Bill bill : bills) {
                Object[] row = {
                    bill.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    bill.getBillNumber(),
                    bill.getCustomerName() != null ? bill.getCustomerName() : "Walk-in",
                    bill.getTotalItems(),
                    "₹" + String.format("%.2f", bill.getSubtotal()),
                    "₹" + String.format("%.2f", bill.getGstAmount()),
                    "₹" + String.format("%.2f", bill.getTotalAmount()),
                    bill.getPaymentMethod().name()
                };
                salesModel.addRow(row);
                totalSales = totalSales.add(bill.getTotalAmount());
            }
            
            totalSalesLabel.setText("Total Sales: ₹" + String.format("%.2f", totalSales));
            totalBillsLabel.setText("Total Bills: " + bills.size());
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading sales data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateStockReport() {
        try {
            List<com.grocerypos.model.Item> items = itemDAO.findAll();
            
            stockModel.setRowCount(0);
            
            for (com.grocerypos.model.Item item : items) {
                String status = item.getStockQuantity() <= item.getMinStockLevel() ? "LOW STOCK" : "OK";
                Color statusColor = item.getStockQuantity() <= item.getMinStockLevel() ? Color.RED : Color.GREEN;
                
                Object[] row = {
                    item.getName(),
                    item.getBarcode(),
                    item.getStockQuantity(),
                    item.getMinStockLevel(),
                    status,
                    item.getCategory()
                };
                stockModel.addRow(row);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading stock data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportReport() {
        JOptionPane.showMessageDialog(this, "Export functionality will be implemented soon!", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
    }
}

/**
 * Simple date picker component
 */
class JDatePicker extends JPanel {
    private JTextField dateField;
    private JButton calendarButton;
    private LocalDate selectedDate;

    public JDatePicker() {
        setLayout(new BorderLayout());
        
        dateField = new JTextField(10);
        dateField.setEditable(false);
        
        calendarButton = new JButton("📅");
        calendarButton.setPreferredSize(new Dimension(30, 25));
        calendarButton.addActionListener(e -> showDateDialog());
        
        add(dateField, BorderLayout.CENTER);
        add(calendarButton, BorderLayout.EAST);
        
        setDate(LocalDate.now());
    }

    public void setDate(LocalDate date) {
        this.selectedDate = date;
        dateField.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    public LocalDate getDate() {
        return selectedDate;
    }

    private void showDateDialog() {
        // Simple date input dialog
        String input = JOptionPane.showInputDialog(this, "Enter date (dd/MM/yyyy):", 
            dateField.getText());
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(input, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                setDate(date);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use dd/MM/yyyy", "Invalid Date", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
