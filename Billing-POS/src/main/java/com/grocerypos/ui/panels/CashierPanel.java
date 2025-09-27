package com.grocerypos.ui.panels;

import com.grocerypos.Application;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Cashier panel for POS operations
 */
public class CashierPanel extends JPanel {
    private Application parent;
    private JTextField barcodeField;
    private JTextField searchField;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JTable searchTable;
    private DefaultTableModel searchModel;
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

    public CashierPanel(Application parent) {
        this.parent = parent;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
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
        
        // Search results table
        String[] searchColumns = {"Name", "Barcode", "Price", "Stock", "Category"};
        searchModel = new DefaultTableModel(searchColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        searchTable = new JTable(searchModel);
        searchTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        searchTable.setRowHeight(25);
        searchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Cart table
        String[] cartColumns = {"Item", "Barcode", "Qty", "Price", "GST%", "GST Amt", "Total"};
        cartModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only quantity is editable
            }
        };
        cartTable = new JTable(cartModel);
        cartTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        cartTable.setRowHeight(25);
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
        addItemButton.setBackground(new Color(0, 120, 215));
        addItemButton.setForeground(Color.WHITE);
        addItemButton.setFocusPainted(false);
        
        checkoutButton.setBackground(new Color(40, 167, 69));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        removeItemButton.setBackground(new Color(220, 53, 69));
        removeItemButton.setForeground(Color.WHITE);
        removeItemButton.setFocusPainted(false);
        
        clearCartButton.setBackground(new Color(108, 117, 125));
        clearCartButton.setForeground(Color.WHITE);
        clearCartButton.setFocusPainted(false);
        
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
        setBackground(Color.WHITE);
        
        // Top panel - Input area
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Item Entry"));
        topPanel.setBackground(Color.WHITE);
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(new JLabel("Barcode:"));
        inputPanel.add(barcodeField);
        inputPanel.add(new JLabel("Search:"));
        inputPanel.add(searchField);
        inputPanel.add(addItemButton);
        
        topPanel.add(inputPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel - Search and Cart
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        
        // Search results panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Results"));
        searchPanel.setBackground(Color.WHITE);
        
        JScrollPane searchScrollPane = new JScrollPane(searchTable);
        searchScrollPane.setPreferredSize(new Dimension(800, 150));
        searchPanel.add(searchScrollPane, BorderLayout.CENTER);
        
        // Cart panel
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        cartPanel.setBackground(Color.WHITE);
        
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartScrollPane.setPreferredSize(new Dimension(800, 200));
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);
        
        // Cart buttons
        JPanel cartButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        cartButtonPanel.setBackground(Color.WHITE);
        cartButtonPanel.add(removeItemButton);
        cartButtonPanel.add(clearCartButton);
        cartPanel.add(cartButtonPanel, BorderLayout.SOUTH);
        
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(cartPanel, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel - Totals and checkout
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Bill Summary"));
        bottomPanel.setBackground(Color.WHITE);
        
        // Totals panel
        JPanel totalsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        totalsPanel.setBackground(Color.WHITE);
        totalsPanel.add(totalLabel);
        totalsPanel.add(gstLabel);
        totalsPanel.add(finalTotalLabel);
        
        // Checkout panel
        JPanel checkoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        checkoutPanel.setBackground(Color.WHITE);
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
                    searchItems();
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
        
        // Search table double-click
        searchTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addSelectedSearchItem();
                }
            }
        });
        
        // Cart table cell editing
        cartTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 2) { // Quantity column
                updateItemQuantity(cartTable.getSelectedRow());
            }
        });
    }

    public void refreshData() {
        generateNewBillNumber();
        clearCart();
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
                parent.setStatus("Item added to cart");
            } else {
                JOptionPane.showMessageDialog(this, "Item not found with barcode: " + barcode, "Item Not Found", JOptionPane.WARNING_MESSAGE);
                barcodeField.setText("");
                barcodeField.requestFocus();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            parent.setStatus("Database error: " + e.getMessage());
        }
    }

    private void searchItems() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            searchModel.setRowCount(0);
            return;
        }
        
        try {
            List<Item> items = itemDAO.searchByName(searchTerm);
            searchModel.setRowCount(0);
            
            for (Item item : items) {
                Object[] row = {
                    item.getName(),
                    item.getBarcode(),
                    "₹" + String.format("%.2f", item.getPrice()),
                    item.getStockQuantity(),
                    item.getCategory()
                };
                searchModel.addRow(row);
            }
            
            parent.setStatus("Found " + items.size() + " items matching '" + searchTerm + "'");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            parent.setStatus("Database error: " + e.getMessage());
        }
    }

    private void addSelectedSearchItem() {
        int selectedRow = searchTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from search results", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String barcode = (String) searchTable.getValueAt(selectedRow, 1);
            Item item = itemDAO.findByBarcode(barcode);
            if (item != null) {
                addItemToCart(item, 1);
                parent.setStatus("Item added to cart");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            parent.setStatus("Database error: " + e.getMessage());
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
                "₹" + String.format("%.2f", item.getUnitPrice()),
                item.getGstPercentage() + "%",
                "₹" + String.format("%.2f", item.getGstAmount()),
                "₹" + String.format("%.2f", item.getLineTotal())
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
            parent.setStatus("Item removed from cart");
        }
    }

    private void clearCart() {
        cartItems.clear();
        updateCartTable();
        updateTotals();
        parent.setStatus("Cart cleared");
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
        PaymentDialog paymentDialog = new PaymentDialog(parent, finalTotal);
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
                parent.setStatus("Bill processed successfully");
                
                // Clear cart and generate new bill number
                cartItems.clear();
                updateCartTable();
                updateTotals();
                generateNewBillNumber();
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error processing bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                parent.setStatus("Error processing bill: " + e.getMessage());
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
        parent.setStatus("Receipt generated");
    }
}
