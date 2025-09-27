package com.grocerypos.ui.panels;

import com.grocerypos.Application;
import com.grocerypos.dao.ItemDAO;
import com.grocerypos.model.Item;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Dialog for adding/editing items with auto-barcode generation
 */
public class ItemDialog extends JDialog {
    private Application parent;
    private Item item;
    private ItemDAO itemDAO;
    private boolean itemSaved = false;
    
    private JTextField nameField;
    private JTextField barcodeField;
    private JTextField descriptionField;
    private JTextField priceField;
    private JTextField costPriceField;
    private JTextField gstField;
    private JTextField stockField;
    private JTextField minStockField;
    private JTextField unitField;
    private JTextField categoryField;
    private JButton generateBarcodeButton;
    private JButton saveButton;
    private JButton cancelButton;

    public ItemDialog(Application parent, Item item, ItemDAO itemDAO) {
        super(parent, item == null ? "Add Item" : "Edit Item", true);
        this.parent = parent;
        this.item = item;
        this.itemDAO = itemDAO;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDialog();
        
        if (item != null) {
            loadItemData();
        } else {
            generateBarcode();
        }
    }

    private void initializeComponents() {
        nameField = new JTextField(20);
        barcodeField = new JTextField(20);
        descriptionField = new JTextField(20);
        priceField = new JTextField(10);
        costPriceField = new JTextField(10);
        gstField = new JTextField(10);
        stockField = new JTextField(10);
        minStockField = new JTextField(10);
        unitField = new JTextField(10);
        categoryField = new JTextField(20);
        
        // Set default values
        unitField.setText("pcs");
        minStockField.setText("5");
        gstField.setText("0.00");
        stockField.setText("0");
        
        // Configure text areas
        descriptionField.setToolTipText("Item description");
        
        generateBarcodeButton = new JButton("Generate Barcode");
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        // Configure buttons
        generateBarcodeButton.setBackground(new Color(0, 120, 215));
        generateBarcodeButton.setForeground(Color.WHITE);
        generateBarcodeButton.setFocusPainted(false);
        
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        // Make barcode field read-only
        barcodeField.setEditable(false);
        barcodeField.setBackground(Color.LIGHT_GRAY);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Main form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(nameField, gbc);
        
        // Barcode
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Barcode:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel barcodePanel = new JPanel(new BorderLayout(5, 0));
        barcodePanel.add(barcodeField, BorderLayout.CENTER);
        barcodePanel.add(generateBarcodeButton, BorderLayout.EAST);
        formPanel.add(barcodePanel, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(descriptionField, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Price *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(priceField, gbc);
        
        // Cost Price
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Cost Price:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(costPriceField, gbc);
        
        // GST
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("GST %:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(gstField, gbc);
        
        // Stock
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Stock Quantity:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(stockField, gbc);
        
        // Min Stock
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Min Stock Level:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(minStockField, gbc);
        
        // Unit
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Unit:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(unitField, gbc);
        
        // Category
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(categoryField, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        generateBarcodeButton.addActionListener(e -> generateBarcode());
        saveButton.addActionListener(e -> saveItem());
        cancelButton.addActionListener(e -> {
            itemSaved = false;
            dispose();
        });
        
        // Enter key on price field
        priceField.addActionListener(e -> saveItem());
    }

    private void setupDialog() {
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Set focus to name field
        nameField.requestFocus();
    }

    private void generateBarcode() {
        // Generate unique barcode with POS prefix + timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String barcode = "POS" + timestamp;
        barcodeField.setText(barcode);
    }

    private void loadItemData() {
        if (item != null) {
            nameField.setText(item.getName());
            barcodeField.setText(item.getBarcode());
            descriptionField.setText(item.getDescription());
            priceField.setText(item.getPrice().toString());
            costPriceField.setText(item.getCostPrice() != null ? item.getCostPrice().toString() : "");
            gstField.setText(item.getGstPercentage().toString());
            stockField.setText(String.valueOf(item.getStockQuantity()));
            minStockField.setText(String.valueOf(item.getMinStockLevel()));
            unitField.setText(item.getUnit());
            categoryField.setText(item.getCategory());
        }
    }

    private void saveItem() {
        if (!validateInput()) {
            return;
        }
        
        try {
            if (item == null) {
                // Create new item
                item = new Item();
            }
            
            item.setName(nameField.getText().trim());
            item.setBarcode(barcodeField.getText().trim());
            item.setDescription(descriptionField.getText().trim());
            item.setPrice(new BigDecimal(priceField.getText().trim()));
            
            // Set cost price with null-safe handling
            if (!costPriceField.getText().trim().isEmpty()) {
                item.setCostPrice(new BigDecimal(costPriceField.getText().trim()));
            } else {
                item.setCostPrice(BigDecimal.ZERO);
            }
            
            // Ensure GST percentage is never null
            item.setGstPercentage(new BigDecimal(gstField.getText().trim()));
            item.setStockQuantity(Integer.parseInt(stockField.getText().trim()));
            item.setMinStockLevel(Integer.parseInt(minStockField.getText().trim()));
            item.setUnit(unitField.getText().trim().isEmpty() ? "pcs" : unitField.getText().trim());
            item.setCategory(categoryField.getText().trim());
            item.setActive(true); // Ensure item is active
            
            if (item.getId() == 0) {
                // Insert new item
                itemDAO.insert(item);
                JOptionPane.showMessageDialog(this, "Item added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                parent.setStatus("Item added successfully");
            } else {
                // Update existing item
                itemDAO.update(item);
                JOptionPane.showMessageDialog(this, "Item updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                parent.setStatus("Item updated successfully");
            }
            
            itemSaved = true;
            dispose();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            parent.setStatus("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format", "Input Error", JOptionPane.ERROR_MESSAGE);
            parent.setStatus("Invalid number format");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            parent.setStatus("Error saving item: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        if (priceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Price is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return false;
        }
        
        if (stockField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Stock quantity is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            stockField.requestFocus();
            return false;
        }
        
        if (gstField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "GST percentage is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            gstField.requestFocus();
            return false;
        }
        
        try {
            new BigDecimal(priceField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format", "Validation Error", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return false;
        }
        
        if (!costPriceField.getText().trim().isEmpty()) {
            try {
                new BigDecimal(costPriceField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid cost price format", "Validation Error", JOptionPane.ERROR_MESSAGE);
                costPriceField.requestFocus();
                return false;
            }
        }
        
        try {
            new BigDecimal(gstField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid GST format", "Validation Error", JOptionPane.ERROR_MESSAGE);
            gstField.requestFocus();
            return false;
        }
        
        try {
            Integer.parseInt(stockField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid stock quantity format", "Validation Error", JOptionPane.ERROR_MESSAGE);
            stockField.requestFocus();
            return false;
        }
        
        try {
            Integer.parseInt(minStockField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid min stock format", "Validation Error", JOptionPane.ERROR_MESSAGE);
            minStockField.requestFocus();
            return false;
        }
        
        return true;
    }

    public boolean isItemSaved() {
        return itemSaved;
    }
}
