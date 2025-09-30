package com.grocerypos.ui;

import com.grocerypos.dao.ItemDAO;
import com.grocerypos.dao.BillDAO;
import com.grocerypos.dao.InventoryMovementDAO;
import com.grocerypos.model.Item;
import com.grocerypos.model.Bill;
import com.grocerypos.model.BillItem;
import com.grocerypos.model.InventoryMovement;
import com.grocerypos.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
// removed unused ActionEvent/ActionListener imports
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Billing window for POS operations
 */
public class BillingWindow extends JFrame {
    private JTextField barcodeField;
    private JTextField searchField;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel totalLabel;
    private JLabel gstLabel;
    private JLabel finalTotalLabel;
    private JButton addItemButton;
    private JButton removeItemButton;
    private JButton checkoutButton;
    private JButton clearCartButton;
    private JButton printReceiptButton;
    
    private List<BillItem> cartItems;
    private BigDecimal subtotal;
    private BigDecimal gstAmount;
    private BigDecimal finalTotal;
    
    private ItemDAO itemDAO;
    private BillDAO billDAO;
    private InventoryMovementDAO inventoryMovementDAO;
    
    private String currentBillNumber;

    public BillingWindow() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindow();
        
        cartItems = new ArrayList<>();
        subtotal = BigDecimal.ZERO;
        gstAmount = BigDecimal.ZERO;
        finalTotal = BigDecimal.ZERO;
        
        try {
            itemDAO = new ItemDAO();
            billDAO = new BillDAO();
            inventoryMovementDAO = new InventoryMovementDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        generateNewBillNumber();
        updateTotals();
    }

    private void initializeComponents() {
        // Barcode input
        barcodeField = new JTextField(20);
        barcodeField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        barcodeField.setToolTipText("Scan barcode or enter item code");
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        searchField.setToolTipText("Search items by name");
        
        // Cart table
        String[] columnNames = {"Item", "Barcode", "Qty", "Price", "GST%", "GST Amt", "Total"};
        cartModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only quantity is editable
            }
        };
        cartTable = new JTable(cartModel);
        cartTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        cartTable.setRowHeight(28);
        cartTable.putClientProperty("JTable.showGrid", false);
        cartTable.putClientProperty("JTable.alternateRowColor", new Color(246, 246, 248));
        cartTable.getTableHeader().putClientProperty("FlatLaf.style", "font: medium 13");
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        cartTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        // Buttons
        addItemButton = new JButton("Add Item");
        removeItemButton = new JButton("Remove");
        checkoutButton = new JButton("Checkout");
        clearCartButton = new JButton("Clear Cart");
        printReceiptButton = new JButton("Print Receipt");
        
        // Configure buttons
        addItemButton.putClientProperty("JButton.buttonType", "roundRect");
        addItemButton.setBackground(new Color(0,122,255));
        addItemButton.setForeground(Color.WHITE);
        addItemButton.setFocusPainted(false);
        
        checkoutButton.putClientProperty("JButton.buttonType", "roundRect");
        checkoutButton.setBackground(new Color(0,122,255));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        removeItemButton.putClientProperty("JButton.buttonType", "roundRect");
        removeItemButton.setBackground(new Color(220, 53, 69));
        removeItemButton.setForeground(Color.WHITE);
        removeItemButton.setFocusPainted(false);
        
        clearCartButton.putClientProperty("JButton.buttonType", "roundRect");
        clearCartButton.setBackground(new Color(108, 117, 125));
        clearCartButton.setForeground(Color.WHITE);
        clearCartButton.setFocusPainted(false);
        
        printReceiptButton.putClientProperty("JButton.buttonType", "roundRect");
        printReceiptButton.setBackground(new Color(23, 162, 184));
        printReceiptButton.setForeground(Color.WHITE);
        printReceiptButton.setFocusPainted(false);
        
        // Total labels
        totalLabel = new JLabel("Subtotal: ₹0.00");
        gstLabel = new JLabel("GST: ₹0.00");
        finalTotalLabel = new JLabel("Total: ₹0.00");
        
        totalLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        gstLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        finalTotalLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        finalTotalLabel.setForeground(new Color(0, 120, 215));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel - Input area
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Item Entry"));
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.add(new JLabel("Barcode:"));
        inputPanel.add(barcodeField);
        inputPanel.add(new JLabel("Search:"));
        inputPanel.add(searchField);
        inputPanel.add(addItemButton);
        
        topPanel.add(inputPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel - Cart table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Cart buttons
        JPanel cartButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        cartButtonPanel.add(removeItemButton);
        cartButtonPanel.add(clearCartButton);
        centerPanel.add(cartButtonPanel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel - Totals and checkout
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                Color base = new Color(255,255,255,200);
                g2.setColor(base);
                g2.fillRoundRect(0, 0, w, h, 16, 16);
                g2.setColor(new Color(0,0,0,25));
                g2.drawRoundRect(0, 0, w-1, h-1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bottomPanel.setOpaque(false);
        
        // Totals panel
        JPanel totalsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        totalsPanel.add(totalLabel);
        totalsPanel.add(gstLabel);
        totalsPanel.add(finalTotalLabel);
        
        // Checkout panel
        JPanel checkoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        checkoutPanel.add(printReceiptButton);
        checkoutPanel.add(checkoutButton);
        
        bottomPanel.add(totalsPanel, BorderLayout.WEST);
        bottomPanel.add(checkoutPanel, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // Barcode field enter key
        barcodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addItemByBarcode();
                }
            }
        });
        
        // Search field enter key
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchAndAddItem();
                }
            }
        });
        
        // Add item button
        addItemButton.addActionListener(e -> addItemByBarcode());
        
        // Remove item button
        removeItemButton.addActionListener(e -> removeSelectedItem());
        
        // Clear cart button
        clearCartButton.addActionListener(e -> clearCart());
        
        // Checkout button
        checkoutButton.addActionListener(e -> processCheckout());
        
        // Print receipt button
        printReceiptButton.addActionListener(e -> printReceipt());
        
        // Cart table selection
        cartTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                removeItemButton.setEnabled(cartTable.getSelectedRow() != -1);
            }
        });
        
        // Cart table cell editing
        cartTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 2) { // Quantity column
                updateItemQuantity(cartTable.getSelectedRow());
            }
        });
    }

    private void setupWindow() {
        setTitle("Grocery POS - Billing");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
        
        // Set focus to barcode field
        barcodeField.requestFocus();
    }

    private void generateNewBillNumber() {
        currentBillNumber = "BILL-" + System.currentTimeMillis();
    }

    private void addItemByBarcode() {
        String barcode = barcodeField.getText().trim();
        if (barcode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a barcode", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Item item = itemDAO.findByBarcode(barcode);
            if (item != null) {
                addItemToCart(item, 1);
                barcodeField.setText("");
                barcodeField.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Item not found with barcode: " + barcode, "Item Not Found", JOptionPane.WARNING_MESSAGE);
                barcodeField.setText("");
                barcodeField.requestFocus();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchAndAddItem() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            List<Item> items = itemDAO.searchByName(searchTerm);
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No items found matching: " + searchTerm, "No Results", JOptionPane.INFORMATION_MESSAGE);
            } else if (items.size() == 1) {
                addItemToCart(items.get(0), 1);
                searchField.setText("");
                searchField.requestFocus();
            } else {
                // Show item selection dialog
                showItemSelectionDialog(items);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showItemSelectionDialog(List<Item> items) {
        String[] columnNames = {"Name", "Barcode", "Price", "Stock"};
        Object[][] data = new Object[items.size()][4];
        
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            data[i][0] = item.getName();
            data[i][1] = item.getBarcode();
            data[i][2] = "₹" + item.getPrice();
            data[i][3] = item.getStockQuantity();
        }
        
        JTable selectionTable = new JTable(data, columnNames);
        selectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(selectionTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        
        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Select Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int selectedRow = selectionTable.getSelectedRow();
            if (selectedRow != -1) {
                addItemToCart(items.get(selectedRow), 1);
                searchField.setText("");
                searchField.requestFocus();
            }
        }
    }

    private void addItemToCart(Item item, int quantity) {
        if (item.getStockQuantity() < quantity) {
            JOptionPane.showMessageDialog(this, "Insufficient stock. Available: " + item.getStockQuantity(), "Stock Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if item already exists in cart
        for (BillItem cartItem : cartItems) {
            if (cartItem.getItemId() == item.getId()) {
                cartItem.setQuantityAndRecalculate(cartItem.getQuantity() + quantity);
                updateCartTable();
                updateTotals();
                return;
            }
        }
        
        // Add new item to cart
        BillItem billItem = new BillItem();
        billItem.setItemId(item.getId());
        billItem.setItemName(item.getName());
        billItem.setItemBarcode(item.getBarcode());
        billItem.setQuantity(quantity);
        billItem.setUnitPrice(item.getPrice());
        billItem.setGstPercentage(item.getGstPercentage());
        billItem.calculateTotals();
        
        cartItems.add(billItem);
        updateCartTable();
        updateTotals();
    }

    private void updateCartTable() {
        cartModel.setRowCount(0);
        for (BillItem item : cartItems) {
            Object[] row = {
                item.getItemName(),
                item.getItemBarcode(),
                item.getQuantity(),
                "₹" + item.getUnitPrice(),
                item.getGstPercentage() + "%",
                "₹" + item.getGstAmount(),
                "₹" + item.getLineTotal()
            };
            cartModel.addRow(row);
        }
    }

    private void updateItemQuantity(int row) {
        if (row >= 0 && row < cartItems.size()) {
            try {
                int newQuantity = Integer.parseInt(cartTable.getValueAt(row, 2).toString());
                if (newQuantity > 0) {
                    cartItems.get(row).setQuantityAndRecalculate(newQuantity);
                    updateCartTable();
                    updateTotals();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid quantity", "Input Error", JOptionPane.ERROR_MESSAGE);
                updateCartTable(); // Refresh table
            }
        }
    }

    private void removeSelectedItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow != -1 && selectedRow < cartItems.size()) {
            cartItems.remove(selectedRow);
            updateCartTable();
            updateTotals();
        }
    }

    private void clearCart() {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear the cart?", "Clear Cart", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            cartItems.clear();
            updateCartTable();
            updateTotals();
        }
    }

    private void updateTotals() {
        subtotal = BigDecimal.ZERO;
        gstAmount = BigDecimal.ZERO;
        
        for (BillItem item : cartItems) {
            subtotal = subtotal.add(item.getLineTotal().subtract(item.getGstAmount()));
            gstAmount = gstAmount.add(item.getGstAmount());
        }
        
        finalTotal = subtotal.add(gstAmount);
        
        totalLabel.setText("Subtotal: ₹" + String.format("%.2f", subtotal));
        gstLabel.setText("GST: ₹" + String.format("%.2f", gstAmount));
        finalTotalLabel.setText("Total: ₹" + String.format("%.2f", finalTotal));
    }

    private void processCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty", "No Items", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show payment dialog
        PaymentDialog paymentDialog = new PaymentDialog(this, finalTotal);
        paymentDialog.setVisible(true);
        
        if (paymentDialog.isPaymentSuccessful()) {
            try {
                // Create bill
                Bill bill = new Bill();
                bill.setBillNumber(currentBillNumber);
                bill.setSubtotal(subtotal);
                bill.setGstAmount(gstAmount);
                bill.setTotalAmount(finalTotal);
                bill.setPaymentMethod(paymentDialog.getPaymentMethod());
                bill.setCashierId(SessionManager.getCurrentUserId());
                bill.setBillItems(cartItems);
                
                // Save bill to database
                int billId = billDAO.insertBill(bill);
                bill.setId(billId);
                
                // Update stock
                for (BillItem item : cartItems) {
                    Item dbItem = itemDAO.findById(item.getItemId());
                    if (dbItem != null) {
                        int newStock = dbItem.getStockQuantity() - item.getQuantity();
                        itemDAO.updateStock(item.getItemId(), newStock);
                        
                        // Record inventory movement
                        inventoryMovementDAO.recordStockMovement(
                            item.getItemId(),
                            InventoryMovement.MovementType.SALE,
                            -item.getQuantity(),
                            dbItem.getStockQuantity(),
                            newStock,
                            billId,
                            InventoryMovement.ReferenceType.BILL,
                            "Sale - Bill " + currentBillNumber,
                            SessionManager.getCurrentUserId()
                        );
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Bill processed successfully!\nBill Number: " + currentBillNumber, "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear cart and generate new bill number
                cartItems.clear();
                updateCartTable();
                updateTotals();
                generateNewBillNumber();
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error processing bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void printReceipt() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty", "No Items", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create receipt content
        StringBuilder receipt = new StringBuilder();
        receipt.append("================================\n");
        receipt.append("        GROCERY STORE\n");
        receipt.append("    123 Main Street, City\n");
        receipt.append("    Phone: +91-9876543210\n");
        receipt.append("================================\n");
        receipt.append("Bill No: ").append(currentBillNumber).append("\n");
        receipt.append("Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        receipt.append("Cashier: ").append(SessionManager.getCurrentUserName()).append("\n");
        receipt.append("--------------------------------\n");
        
        for (BillItem item : cartItems) {
            receipt.append(String.format("%-20s %2d x ₹%6.2f\n", 
                item.getItemName().substring(0, Math.min(item.getItemName().length(), 20)), 
                item.getQuantity(), 
                item.getUnitPrice()));
            receipt.append(String.format("                    ₹%6.2f\n", item.getLineTotal()));
        }
        
        receipt.append("--------------------------------\n");
        receipt.append(String.format("Subtotal:           ₹%6.2f\n", subtotal));
        receipt.append(String.format("GST:                ₹%6.2f\n", gstAmount));
        receipt.append(String.format("Total:              ₹%6.2f\n", finalTotal));
        receipt.append("================================\n");
        receipt.append("    Thank you for shopping!\n");
        receipt.append("================================\n");
        
        // Show receipt in dialog
        JTextArea receiptArea = new JTextArea(receipt.toString());
        receiptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        receiptArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setPreferredSize(new Dimension(400, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Receipt", JOptionPane.INFORMATION_MESSAGE);
    }
}
