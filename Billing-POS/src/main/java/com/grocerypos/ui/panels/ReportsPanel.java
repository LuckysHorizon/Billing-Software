package com.grocerypos.ui.panels;

import com.grocerypos.Application;
import com.grocerypos.dao.BillDAO;
import com.grocerypos.dao.ItemDAO;
import com.grocerypos.model.Bill;

import javax.swing.*;
import com.grocerypos.ui.components.GlassCard;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
// removed unused AWT event imports
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Reports panel for sales and inventory reports
 */
public class ReportsPanel extends JPanel {
    private Application parent;
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

    public ReportsPanel(Application parent) {
        this.parent = parent;
        System.out.println("[ReportsPanel] Constructing new instance " + this);
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
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
        salesTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        salesTable.setRowHeight(26);
        salesTable.putClientProperty("JTable.showGrid", false);
        salesTable.putClientProperty("JTable.alternateRowColor", new Color(246,246,248));
        
        // Stock table
        String[] stockColumns = {"Name", "Barcode", "Stock", "Min Stock", "Status", "Category"};
        stockModel = new DefaultTableModel(stockColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stockTable = new JTable(stockModel);
        stockTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        stockTable.setRowHeight(26);
        stockTable.putClientProperty("JTable.showGrid", false);
        stockTable.putClientProperty("JTable.alternateRowColor", new Color(246,246,248));
        
        // Date pickers
        startDatePicker = new JDatePicker();
        endDatePicker = new JDatePicker();
        
        generateReportButton = new JButton("Generate Report");
        exportButton = new JButton("Export to CSV");
        generateReportButton.putClientProperty("JButton.buttonType", "roundRect");
        exportButton.putClientProperty("JButton.buttonType", "roundRect");
        generateReportButton.setBackground(new Color(0,122,255));
        generateReportButton.setForeground(Color.WHITE);
        exportButton.setBackground(new Color(108,117,125));
        exportButton.setForeground(Color.WHITE);
        
        totalSalesLabel = new JLabel("Total Sales: ₹0.00");
        totalBillsLabel = new JLabel("Total Bills: 0");
        
        totalSalesLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        totalBillsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        
        // Top panel - Date range and controls
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Report Controls"));
        topPanel.setBackground(Color.WHITE);
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        datePanel.setBackground(Color.WHITE);
        datePanel.add(new JLabel("From:"));
        datePanel.add(startDatePicker);
        datePanel.add(new JLabel("To:"));
        datePanel.add(endDatePicker);
        datePanel.add(generateReportButton);
        datePanel.add(exportButton);
        
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        summaryPanel.setBackground(Color.WHITE);
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
        GlassCard card = new GlassCard();
        card.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStockPanel() {
        GlassCard card = new GlassCard();
        card.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void setupEventHandlers() {
        generateReportButton.addActionListener(e -> generateReport());
        exportButton.addActionListener(e -> exportReport());
        
        // Set default dates
        startDatePicker.setDate(LocalDate.now().minusDays(7));
        endDatePicker.setDate(LocalDate.now());
    }

    public void refreshData() {
        generateReport();
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
            
            parent.setStatus("Report generated successfully");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            parent.setStatus("Error generating report: " + e.getMessage());
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
            parent.setStatus("Error loading sales data: " + e.getMessage());
        }
    }

    private void generateStockReport() {
        try {
            List<com.grocerypos.model.Item> items = itemDAO.findAll();
            
            stockModel.setRowCount(0);
            
            for (com.grocerypos.model.Item item : items) {
                String status = item.getStockQuantity() <= item.getMinStockLevel() ? "LOW STOCK" : "OK";
                
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
            parent.setStatus("Error loading stock data: " + e.getMessage());
        }
    }

    private void exportReport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Sales Report as CSV");
        chooser.setSelectedFile(new java.io.File("sales.csv"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        java.io.File file = chooser.getSelectedFile();
        try (java.io.FileWriter csv = new java.io.FileWriter(file)) {
            csv.write("Date,Bill No,Customer,Items,Subtotal,GST,Total,Payment\n");
            for (int r = 0; r < salesModel.getRowCount(); r++) {
                StringBuilder line = new StringBuilder();
                for (int c = 0; c < salesModel.getColumnCount(); c++) {
                    Object val = salesModel.getValueAt(r, c);
                    String text = val != null ? val.toString() : "";
                    text = text.replace("₹", "").replace(",", "");
                    if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
                        text = '"' + text.replace("\"", "\"\"") + '"';
                    }
                    line.append(text);
                    if (c < salesModel.getColumnCount() - 1) line.append(',');
                }
                csv.write(line.toString());
                csv.write("\n");
            }
            csv.flush();
            JOptionPane.showMessageDialog(this, "Exported to: " + file.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            parent.setStatus("Exported report to CSV");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to export CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            parent.setStatus("Export failed: " + ex.getMessage());
        }
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
        setBackground(Color.WHITE);
        
        dateField = new JTextField(10);
        dateField.setEditable(false);
        dateField.setBackground(Color.WHITE);
        
        calendarButton = new JButton("📅");
        calendarButton.setPreferredSize(new Dimension(30, 25));
        calendarButton.setBackground(new Color(0, 120, 215));
        calendarButton.setForeground(Color.WHITE);
        calendarButton.setFocusPainted(false);
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
