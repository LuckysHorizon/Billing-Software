package com.grocerypos.ui;

import com.grocerypos.dao.ItemDAO;
import com.grocerypos.dao.BillDAO;
import com.grocerypos.dao.InventoryMovementDAO;
import com.grocerypos.model.Item;
import com.grocerypos.model.Bill;
import com.grocerypos.model.BillItem;
import com.grocerypos.model.InventoryMovement;
import com.grocerypos.util.SessionManager;
import com.grocerypos.ui.components.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern futuristic macOS-inspired billing panel with glass morphism effects
 */
public class BillingWindow extends JPanel {
    // Modern components
    private ModernSearchField barcodeField;
    private ModernSearchField searchField;
    private ModernTable cartTable;
    private DefaultTableModel cartModel;
    private InvoiceSummaryCard summaryCard;
    private ProductSidebar productSidebar;
    
    // Action buttons
    private ModernButton addItemButton;
    private ModernButton removeItemButton;
    private ModernButton clearCartButton;
    private ModernButton printReceiptButton;
    private ModernButton discountButton;
    
    // Data
    private List<BillItem> cartItems;
    private BigDecimal subtotal;
    private BigDecimal gstAmount;
    private BigDecimal finalTotal;
    private BigDecimal discountPercent = BigDecimal.ZERO;
    
    // DAOs
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
            ToastNotification.showError(SwingUtilities.getWindowAncestor(this), "Database connection error: " + e.getMessage());
        }
        
        generateNewBillNumber();
        updateTotals();
    }

    private void initializeComponents() {
        // Modern search fields
        barcodeField = new ModernSearchField("Scan barcode or enter code");
        searchField = new ModernSearchField("Search products");
        
        // Cart table with modern styling
        String[] columnNames = {"Item", "Barcode", "Qty", "Price", "GST%", "GST Amt", "Total"};
        cartModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only quantity is editable
            }
        };
        cartTable = new ModernTable(cartModel);
        
        // Set column widths
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        cartTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        // Modern buttons
        addItemButton = new ModernButton("Add Item", new Color(0, 122, 255));
        removeItemButton = new ModernButton("Remove", new Color(220, 53, 69));
        clearCartButton = new ModernButton("Clear Cart", new Color(108, 117, 125));
        printReceiptButton = new ModernButton("Print Receipt", new Color(23, 162, 184));
        discountButton = new ModernButton("% Discount", new Color(255, 193, 7));
        discountButton.setForeground(new Color(50, 50, 50));
        
        // Modern components
        summaryCard = new InvoiceSummaryCard();
        productSidebar = new ProductSidebar();
    }

    private void setupLayout() {
        setLayout(new BorderLayout(16, 16));
        setBackground(new Color(240, 242, 247));
        
        // Top panel - Search and input area
        GlassCard topCard = new GlassCard();
        topCard.setLayout(new BorderLayout(16, 16));
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        inputPanel.setOpaque(false);
        
        // Barcode input with icon
        JPanel barcodePanel = createInputPanel("🔖", barcodeField);
        JPanel searchPanel = createInputPanel("🔎", searchField);
        
        inputPanel.add(barcodePanel);
        inputPanel.add(searchPanel);
        inputPanel.add(addItemButton);
        inputPanel.add(discountButton);
        
        topCard.add(inputPanel, BorderLayout.CENTER);
        add(topCard, BorderLayout.NORTH);
        
        // Center panel - Main content area
        JPanel centerPanel = new JPanel(new BorderLayout(16, 16));
        centerPanel.setOpaque(false);
        
        // Left sidebar for quick product access
        centerPanel.add(productSidebar, BorderLayout.WEST);
        
        // Main billing area
        GlassCard billingCard = new GlassCard();
        billingCard.setLayout(new BorderLayout(16, 16));
        
        // Cart table with modern styling
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        billingCard.add(scrollPane, BorderLayout.CENTER);
        
        // Cart action buttons
        JPanel cartButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        cartButtonPanel.setOpaque(false);
        cartButtonPanel.add(removeItemButton);
        cartButtonPanel.add(clearCartButton);
        cartButtonPanel.add(printReceiptButton);
        
        billingCard.add(cartButtonPanel, BorderLayout.SOUTH);
        
        centerPanel.add(billingCard, BorderLayout.CENTER);
        
        // Right panel - Invoice summary
        centerPanel.add(summaryCard, BorderLayout.EAST);
        
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createInputPanel(String icon, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
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
        
        // Button actions
        addItemButton.addActionListener(e -> addItemByBarcode());
        removeItemButton.addActionListener(e -> removeSelectedItem());
        clearCartButton.addActionListener(e -> clearCart());
        printReceiptButton.addActionListener(e -> printReceipt());
        discountButton.addActionListener(e -> promptDiscount());
        
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
        
        // Summary card checkout
        summaryCard.addCheckoutListener(e -> processCheckout());
        
        // Product sidebar listener
        productSidebar.setListener(new ProductSidebar.ProductSidebarListener() {
            @Override
            public void onProductSelected(String productName) {
                searchField.setText(productName);
                searchAndAddItem();
            }
            
            @Override
            public void onCategorySelected(String category) {
                // Could implement category filtering here
            }
        });
        
        // Keyboard shortcuts
        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        // Get the root pane from the parent window
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane != null) {
            rootPane.registerKeyboardAction(e -> barcodeField.requestFocus(),
                KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
            
            rootPane.registerKeyboardAction(e -> searchField.requestFocus(),
                KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
            
            rootPane.registerKeyboardAction(e -> processCheckout(),
                KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
            
            rootPane.registerKeyboardAction(e -> clearCart(),
                KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        }
    }

    private void setupWindow() {
        // Apply glass effect to the entire panel
        setBackground(new Color(240, 242, 247));
        
        // Add smooth animations
        AnimationUtils.fadeIn(this, 300);
        
        // Set focus to barcode field
        SwingUtilities.invokeLater(() -> barcodeField.requestFocus());
    }

    private void generateNewBillNumber() {
        currentBillNumber = "BILL-" + System.currentTimeMillis();
    }

    private void addItemByBarcode() {
        String barcode = barcodeField.getText().trim();
        if (barcode.isEmpty()) {
            ToastNotification.showWarning(SwingUtilities.getWindowAncestor(this), "Please enter a barcode");
            return;
        }
        
        try {
            Item item = itemDAO.findByBarcode(barcode);
            if (item != null) {
                addItemToCart(item, 1);
                barcodeField.setText("");
                barcodeField.requestFocus();
                ToastNotification.showSuccess(SwingUtilities.getWindowAncestor(this), "Item added: " + item.getName());
            } else {
                ToastNotification.showWarning(SwingUtilities.getWindowAncestor(this), "Item not found with barcode: " + barcode);
                barcodeField.setText("");
                barcodeField.requestFocus();
            }
        } catch (SQLException e) {
            ToastNotification.showError(SwingUtilities.getWindowAncestor(this), "Database error: " + e.getMessage());
        }
    }

    private void searchAndAddItem() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            ToastNotification.showWarning(SwingUtilities.getWindowAncestor(this), "Please enter a search term");
            return;
        }
        
        try {
            List<Item> items = itemDAO.searchByName(searchTerm);
            if (items.isEmpty()) {
                ToastNotification.showInfo(SwingUtilities.getWindowAncestor(this), "No items found matching: " + searchTerm);
            } else if (items.size() == 1) {
                addItemToCart(items.get(0), 1);
                searchField.setText("");
                searchField.requestFocus();
                ToastNotification.showSuccess(SwingUtilities.getWindowAncestor(this), "Item added: " + items.get(0).getName());
            } else {
                showItemSelectionDialog(items);
            }
        } catch (SQLException e) {
            ToastNotification.showError(SwingUtilities.getWindowAncestor(this), "Database error: " + e.getMessage());
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
        
        ModernTable selectionTable = new ModernTable(new DefaultTableModel(data, columnNames));
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
                ToastNotification.showSuccess(SwingUtilities.getWindowAncestor(this), "Item added: " + items.get(selectedRow).getName());
            }
        }
    }

    private void addItemToCart(Item item, int quantity) {
        if (item.getStockQuantity() < quantity) {
            ToastNotification.showWarning(SwingUtilities.getWindowAncestor(this), "Insufficient stock. Available: " + item.getStockQuantity());
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
                ToastNotification.showError(SwingUtilities.getWindowAncestor(this), "Invalid quantity");
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
            ToastNotification.showInfo(SwingUtilities.getWindowAncestor(this), "Item removed from cart");
        }
    }

    private void clearCart() {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear the cart?", 
            "Clear Cart", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            cartItems.clear();
            updateCartTable();
            updateTotals();
            ToastNotification.showInfo(SwingUtilities.getWindowAncestor(this), "Cart cleared");
        }
    }

    private void updateTotals() {
        subtotal = BigDecimal.ZERO;
        gstAmount = BigDecimal.ZERO;
        
        for (BillItem item : cartItems) {
            subtotal = subtotal.add(item.getLineTotal().subtract(item.getGstAmount()));
            gstAmount = gstAmount.add(item.getGstAmount());
        }
        
        BigDecimal gross = subtotal.add(gstAmount);
        BigDecimal discountAmount = gross.multiply(discountPercent).divide(new BigDecimal("100"));
        finalTotal = gross.subtract(discountAmount);
        
        // Update summary card
        summaryCard.updateSummary(subtotal, gstAmount, discountPercent, finalTotal, cartItems.size());
        summaryCard.setCheckoutEnabled(!cartItems.isEmpty());
    }

    private void promptDiscount() {
        String input = JOptionPane.showInputDialog(this, "Enter discount % (0-100):", 
            discountPercent.toPlainString());
        if (input == null) return;
        
        try {
            BigDecimal v = new BigDecimal(input.trim());
            if (v.compareTo(BigDecimal.ZERO) < 0 || v.compareTo(new BigDecimal("100")) > 0) {
                ToastNotification.showWarning(SwingUtilities.getWindowAncestor(this), "Discount must be between 0 and 100");
                return;
            }
            discountPercent = v;
            updateTotals();
            ToastNotification.showSuccess(SwingUtilities.getWindowAncestor(this), "Discount applied: " + v + "%");
        } catch (NumberFormatException ex) {
            ToastNotification.showError(SwingUtilities.getWindowAncestor(this), "Invalid number");
        }
    }

    private void processCheckout() {
        if (cartItems.isEmpty()) {
            ToastNotification.showWarning(SwingUtilities.getWindowAncestor(this), "Cart is empty");
            return;
        }
        
        // Show payment dialog
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        PaymentDialog paymentDialog = new PaymentDialog(parentWindow instanceof JFrame ? (JFrame) parentWindow : null, finalTotal);
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
                
                ToastNotification.showSuccess(SwingUtilities.getWindowAncestor(this), "Bill processed successfully!\nBill Number: " + currentBillNumber);
                
                // Clear cart and generate new bill number
                cartItems.clear();
                updateCartTable();
                updateTotals();
                generateNewBillNumber();
                
            } catch (SQLException e) {
                ToastNotification.showError(SwingUtilities.getWindowAncestor(this), "Error processing bill: " + e.getMessage());
            }
        }
    }

    private void printReceipt() {
        if (cartItems.isEmpty()) {
            ToastNotification.showWarning(SwingUtilities.getWindowAncestor(this), "Cart is empty");
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