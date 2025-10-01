package com.grocerypos.ui;

import com.grocerypos.dao.ItemDAO;
import com.grocerypos.model.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Dialog for adding/editing items
 */
public class ItemDialog extends JDialog {
    private JTextField nameField;
    private JTextField barcodeField;
    private JTextArea descriptionField;
    private JTextField priceField;
    private JTextField costPriceField;
    private JTextField gstField;
    private JTextField stockField;
    private JTextField minStockField;
    private JTextField unitField;
    private JTextField categoryField;
    private JButton saveButton;
    private JButton cancelButton;
    
    private Item item;
    private boolean itemSaved = false;
    private ItemDAO itemDAO;

    public ItemDialog(JFrame parent, Item item) {
        super(parent, item == null ? "Add Item" : "Edit Item", true);
        this.item = item;
        
        try {
            itemDAO = new ItemDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDialog();
        
        if (item != null) {
            loadItemData();
        }
    }

    private void initializeComponents() {
        nameField = new JTextField(20);
        barcodeField = new JTextField(20);
        descriptionField = new JTextArea(3, 20);
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
        
        // Configure text areas
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Main form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name *:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        // Barcode
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Barcode:"), gbc);
        gbc.gridx = 1;
        formPanel.add(barcodeField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(descriptionField), gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Price *:"), gbc);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);
        
        // Cost Price
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Cost Price:"), gbc);
        gbc.gridx = 1;
        formPanel.add(costPriceField, gbc);
        
        // GST
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("GST %:"), gbc);
        gbc.gridx = 1;
        formPanel.add(gstField, gbc);
        
        // Stock
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Stock Quantity:"), gbc);
        gbc.gridx = 1;
        formPanel.add(stockField, gbc);
        
        // Min Stock
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Min Stock Level:"), gbc);
        gbc.gridx = 1;
        formPanel.add(minStockField, gbc);
        
        // Unit
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Unit:"), gbc);
        gbc.gridx = 1;
        formPanel.add(unitField, gbc);
        
        // Category
        gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        formPanel.add(categoryField, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
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
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Set focus to name field
        nameField.requestFocus();
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
            
            if (!costPriceField.getText().trim().isEmpty()) {
                item.setCostPrice(new BigDecimal(costPriceField.getText().trim()));
            }
            
            item.setGstPercentage(new BigDecimal(gstField.getText().trim()));
            item.setStockQuantity(Integer.parseInt(stockField.getText().trim()));
            item.setMinStockLevel(Integer.parseInt(minStockField.getText().trim()));
            item.setUnit(unitField.getText().trim());
            item.setCategory(categoryField.getText().trim());
            
            if (item.getId() == 0) {
                // Insert new item
                itemDAO.insert(item);
                JOptionPane.showMessageDialog(this, "Item added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Update existing item
                itemDAO.update(item);
                JOptionPane.showMessageDialog(this, "Item updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            
            itemSaved = true;
            dispose();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
