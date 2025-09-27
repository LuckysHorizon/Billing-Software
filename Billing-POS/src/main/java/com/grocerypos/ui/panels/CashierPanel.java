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
import java.awt.print.*;
import javax.print.*;
import javax.print.attribute.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.grocerypos.ui.components.ToastNotification;
import com.grocerypos.ui.components.LoadingSpinner;

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
    private JToggleButton barcodeScannerToggle;
    private JLabel scannerStatusLabel;
    private JButton quickAddButton;
    private JButton discountButton;
    private JTextField discountField;
    private JComboBox<String> searchComboBox;
    private List<Item> allItems;
    
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
        barcodeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(107, 114, 128), 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        // Search field with autocomplete
        searchComboBox = new JComboBox<>();
        searchComboBox.setEditable(true);
        searchComboBox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        searchComboBox.setToolTipText("Search items by name (F1 to focus)");
        searchComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(107, 114, 128), 2),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        // Keep the original searchField for compatibility
        searchField = new JTextField(20);
        searchField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        searchField.setToolTipText("Search items by name (F1 to focus)");
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(107, 114, 128), 2),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        // Barcode scanner toggle
        barcodeScannerToggle = new JToggleButton("📷 Enable Scanner");
        barcodeScannerToggle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        barcodeScannerToggle.setBackground(new Color(34, 197, 94));
        barcodeScannerToggle.setForeground(Color.WHITE);
        barcodeScannerToggle.setFocusPainted(false);
        barcodeScannerToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        scannerStatusLabel = new JLabel("Scanner: OFF");
        scannerStatusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        scannerStatusLabel.setForeground(new Color(239, 68, 68));
        
        // Quick add button
        quickAddButton = new JButton("⚡ Quick Add");
        quickAddButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        quickAddButton.setBackground(new Color(168, 85, 247));
        quickAddButton.setForeground(Color.WHITE);
        quickAddButton.setFocusPainted(false);
        quickAddButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Discount controls
        discountButton = new JButton("💰 Apply Discount");
        discountButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        discountButton.setBackground(new Color(245, 158, 11));
        discountButton.setForeground(Color.WHITE);
        discountButton.setFocusPainted(false);
        discountButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        discountField = new JTextField(8);
        discountField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        discountField.setToolTipText("Enter discount percentage");
        discountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(107, 114, 128), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
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
        addItemButton = new JButton("➕ Add Item");
        removeItemButton = new JButton("🗑️ Remove");
        checkoutButton = new JButton("💳 Checkout");
        clearCartButton = new JButton("🧹 Clear Cart");
        printReceiptButton = new JButton("🖨️ Print Receipt");
        
        // Configure buttons with modern styling
        addItemButton.setBackground(new Color(59, 130, 246));
        addItemButton.setForeground(Color.WHITE);
        addItemButton.setFocusPainted(false);
        addItemButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        addItemButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addItemButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        checkoutButton.setBackground(new Color(34, 197, 94));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        checkoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkoutButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        removeItemButton.setBackground(new Color(239, 68, 68));
        removeItemButton.setForeground(Color.WHITE);
        removeItemButton.setFocusPainted(false);
        removeItemButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        removeItemButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeItemButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        clearCartButton.setBackground(new Color(107, 114, 128));
        clearCartButton.setForeground(Color.WHITE);
        clearCartButton.setFocusPainted(false);
        clearCartButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        clearCartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearCartButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        printReceiptButton.setBackground(new Color(14, 165, 233));
        printReceiptButton.setForeground(Color.WHITE);
        printReceiptButton.setFocusPainted(false);
        printReceiptButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        printReceiptButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printReceiptButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
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
        
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(Color.WHITE);
        
        // Top row - Barcode and scanner controls
        JPanel barcodePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        barcodePanel.setBackground(Color.WHITE);
        barcodePanel.add(new JLabel("📱 Barcode:"));
        barcodePanel.add(barcodeField);
        barcodePanel.add(barcodeScannerToggle);
        barcodePanel.add(scannerStatusLabel);
        
        // Middle row - Search and quick add
        JPanel searchRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchRowPanel.setBackground(Color.WHITE);
        searchRowPanel.add(new JLabel("🔍 Search:"));
        searchRowPanel.add(searchComboBox);
        searchRowPanel.add(addItemButton);
        searchRowPanel.add(quickAddButton);
        
        // Bottom row - Discount controls
        JPanel discountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        discountPanel.setBackground(Color.WHITE);
        discountPanel.add(new JLabel("💰 Discount %:"));
        discountPanel.add(discountField);
        discountPanel.add(discountButton);
        
        inputPanel.add(barcodePanel, BorderLayout.NORTH);
        inputPanel.add(searchRowPanel, BorderLayout.CENTER);
        inputPanel.add(discountPanel, BorderLayout.SOUTH);
        
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
        
        // Search combo box autocomplete
        setupAutocomplete();
        
        // Keyboard shortcuts
        setupKeyboardShortcuts();
        
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
        
        // Barcode scanner toggle
        barcodeScannerToggle.addActionListener(e -> toggleBarcodeScanner());
        
        // Quick add button
        quickAddButton.addActionListener(e -> showQuickAddDialog());
        
        // Discount button
        discountButton.addActionListener(e -> applyDiscount());
        
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
        String searchTerm = searchComboBox.getSelectedItem() != null ? 
            searchComboBox.getSelectedItem().toString().trim() : "";
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
            ToastNotification.showInfo(SwingUtilities.getWindowAncestor(this), "Found " + items.size() + " items matching '" + searchTerm + "'");
        } catch (SQLException e) {
            ToastNotification.showError(SwingUtilities.getWindowAncestor(this), "Database error: " + e.getMessage());
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
        
        // Initialize null fields with safe defaults
        if (billItem.getDiscountPercentage() == null) {
            billItem.setDiscountPercentage(BigDecimal.ZERO);
        }
        if (billItem.getDiscountAmount() == null) {
            billItem.setDiscountAmount(BigDecimal.ZERO);
        }
        
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
        
        // Show receipt dialog with print option
        int option = JOptionPane.showOptionDialog(this, 
            scrollPane, 
            "Receipt - " + currentBillNumber, 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{"🖨️ Print Receipt", "📄 View Only"},
            "🖨️ Print Receipt");
        
        if (option == 0) {
            // Print receipt
            try {
                printReceiptToPrinter(receipt.toString());
                parent.setStatus("Receipt printed successfully");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Print failed: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
                parent.setStatus("Print failed: " + e.getMessage());
            }
        } else {
            parent.setStatus("Receipt generated");
        }
    }
    
    private void toggleBarcodeScanner() {
        if (barcodeScannerToggle.isSelected()) {
            barcodeScannerToggle.setText("📷 Scanner ON");
            barcodeScannerToggle.setBackground(new Color(34, 197, 94));
            scannerStatusLabel.setText("Scanner: ON");
            scannerStatusLabel.setForeground(new Color(34, 197, 94));
            barcodeField.setBackground(new Color(240, 253, 244));
            barcodeField.setToolTipText("Scanner is active - scan barcodes directly");
            barcodeField.requestFocus();
            parent.setStatus("Barcode scanner enabled");
        } else {
            barcodeScannerToggle.setText("📷 Enable Scanner");
            barcodeScannerToggle.setBackground(new Color(34, 197, 94));
            scannerStatusLabel.setText("Scanner: OFF");
            scannerStatusLabel.setForeground(new Color(239, 68, 68));
            barcodeField.setBackground(Color.WHITE);
            barcodeField.setToolTipText("Scan barcode or enter item code");
            parent.setStatus("Barcode scanner disabled");
        }
    }
    
    private void showQuickAddDialog() {
        String[] options = {"Add by Barcode", "Add by Name", "Add by Category"};
        int choice = JOptionPane.showOptionDialog(this,
            "How would you like to add items?",
            "Quick Add",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        switch (choice) {
            case 0: // Barcode
                barcodeField.requestFocus();
                break;
            case 1: // Name
                searchField.requestFocus();
                break;
            case 2: // Category
                showCategoryDialog();
                break;
        }
    }
    
    private void showCategoryDialog() {
        try {
            List<String> categories = itemDAO.getAllCategories();
            if (categories.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No categories found", "No Categories", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String[] categoryArray = categories.toArray(new String[0]);
            String selectedCategory = (String) JOptionPane.showInputDialog(this,
                "Select a category:",
                "Category Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                categoryArray,
                categoryArray[0]);
            
            if (selectedCategory != null) {
                searchField.setText(selectedCategory);
                searchItems();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void applyDiscount() {
        try {
            String discountText = discountField.getText().trim();
            if (discountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter discount percentage", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            BigDecimal discountPercent = new BigDecimal(discountText);
            if (discountPercent.compareTo(BigDecimal.ZERO) < 0 || discountPercent.compareTo(new BigDecimal("100")) > 0) {
                JOptionPane.showMessageDialog(this, "Discount must be between 0 and 100%", "Invalid Discount", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Apply discount to all items in cart
            for (BillItem item : cartItems) {
                item.setDiscountPercentageAndRecalculate(discountPercent);
            }
            
            updateCartTable();
            updateTotals();
            
            JOptionPane.showMessageDialog(this, "Discount of " + discountPercent + "% applied to all items", "Discount Applied", JOptionPane.INFORMATION_MESSAGE);
            parent.setStatus("Discount applied: " + discountPercent + "%");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid discount format", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void printReceiptToPrinter(String receiptContent) throws Exception {
        // Create a simple text printer job
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        
        // Set up the print format
        printerJob.setJobName("Grocery POS Receipt - " + currentBillNumber);
        
        // Show print dialog
        if (printerJob.printDialog()) {
            printerJob.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
                
                String[] lines = receiptContent.split("\n");
                int y = 50;
                for (String line : lines) {
                    g2d.drawString(line, 50, y);
                    y += 15;
                }
                
                return Printable.PAGE_EXISTS;
            });
            
            printerJob.print();
        }
    }
    
    private void setupAutocomplete() {
        try {
            if (itemDAO != null) {
                allItems = itemDAO.findAll();
                updateSearchComboBox();
            } else {
                allItems = new ArrayList<>();
            }
        } catch (SQLException e) {
            ToastNotification.showError(SwingUtilities.getWindowAncestor(this), "Error loading items for search: " + e.getMessage());
        }
        
        // Add key listener for autocomplete
        JTextField editor = (JTextField) searchComboBox.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = editor.getText().toLowerCase();
                if (text.length() > 0) {
                    List<String> matches = allItems.stream()
                        .filter(item -> item.getName().toLowerCase().contains(text))
                        .map(Item::getName)
                        .limit(10)
                        .collect(Collectors.toList());
                    
                    searchComboBox.removeAllItems();
                    for (String match : matches) {
                        searchComboBox.addItem(match);
                    }
                    searchComboBox.showPopup();
                }
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchItems();
                }
            }
        });
        
        // Add action listener for selection
        searchComboBox.addActionListener(e -> {
            if (searchComboBox.getSelectedItem() != null) {
                searchItems();
            }
        });
    }
    
    private void updateSearchComboBox() {
        searchComboBox.removeAllItems();
        for (Item item : allItems) {
            searchComboBox.addItem(item.getName());
        }
    }
    
    private void setupKeyboardShortcuts() {
        // F1 - Focus search
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F1"), "focusSearch");
        getActionMap().put("focusSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchComboBox.requestFocus();
                searchComboBox.showPopup();
            }
        });
        
        // F5 - Checkout
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "checkout");
        getActionMap().put("checkout", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkoutButton.isEnabled()) {
                    processCheckout();
                }
            }
        });
        
        // F6 - Clear cart
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F6"), "clearCart");
        getActionMap().put("clearCart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clearCartButton.isEnabled()) {
                    clearCart();
                }
            }
        });
        
        // F7 - Print receipt
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F7"), "printReceipt");
        getActionMap().put("printReceipt", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (printReceiptButton.isEnabled()) {
                    printReceipt();
                }
            }
        });
    }
}
