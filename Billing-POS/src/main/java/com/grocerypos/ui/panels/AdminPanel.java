package com.grocerypos.ui.panels;

import com.grocerypos.Application;
import com.grocerypos.dao.ItemDAO;
import com.grocerypos.model.Item;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Admin panel for product management
 */
public class AdminPanel extends JPanel {
    private Application parent;
    private JTable itemsTable;
    private DefaultTableModel itemsModel;
    private JTextField searchField;
    private JButton addItemButton;
    private JButton editItemButton;
    private JButton deleteItemButton;
    private JButton refreshButton;
    private JButton lowStockButton;
    
    private ItemDAO itemDAO;

    public AdminPanel(Application parent) {
        this.parent = parent;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        try {
            itemDAO = new ItemDAO();
            loadItems();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeComponents() {
        // Items table
        String[] columnNames = {"ID", "Name", "Barcode", "Price", "Stock", "Min Stock", "Category", "GST%"};
        itemsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        itemsTable = new JTable(itemsModel);
        itemsTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        itemsTable.setRowHeight(30);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.setGridColor(Color.LIGHT_GRAY);
        
        // Configure column widths
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(7).setPreferredWidth(60);
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        searchField.setToolTipText("Search items by name or barcode");
        
        // Buttons
        addItemButton = new JButton("Add Item");
        editItemButton = new JButton("Edit Item");
        deleteItemButton = new JButton("Delete Item");
        refreshButton = new JButton("Refresh");
        lowStockButton = new JButton("Low Stock Alert");
        
        // Configure buttons
        addItemButton.setBackground(new Color(40, 167, 69));
        addItemButton.setForeground(Color.WHITE);
        addItemButton.setFocusPainted(false);
        addItemButton.setPreferredSize(new Dimension(100, 35));
        
        editItemButton.setBackground(new Color(0, 120, 215));
        editItemButton.setForeground(Color.WHITE);
        editItemButton.setFocusPainted(false);
        editItemButton.setPreferredSize(new Dimension(100, 35));
        
        deleteItemButton.setBackground(new Color(220, 53, 69));
        deleteItemButton.setForeground(Color.WHITE);
        deleteItemButton.setFocusPainted(false);
        deleteItemButton.setPreferredSize(new Dimension(100, 35));
        
        refreshButton.setBackground(new Color(108, 117, 125));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        
        lowStockButton.setBackground(new Color(255, 193, 7));
        lowStockButton.setForeground(Color.BLACK);
        lowStockButton.setFocusPainted(false);
        lowStockButton.setPreferredSize(new Dimension(150, 35));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        
        // Top panel - Search and buttons
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Product Management"));
        topPanel.setBackground(Color.WHITE);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(refreshButton);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(lowStockButton);
        buttonPanel.add(addItemButton);
        buttonPanel.add(editItemButton);
        buttonPanel.add(deleteItemButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel - Items table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Products"));
        centerPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        // Search field
        searchField.addActionListener(e -> searchItems());
        
        // Add item button
        addItemButton.addActionListener(e -> addItem());
        
        // Edit item button
        editItemButton.addActionListener(e -> editItem());
        
        // Delete item button
        deleteItemButton.addActionListener(e -> deleteItem());
        
        // Refresh button
        refreshButton.addActionListener(e -> loadItems());
        
        // Low stock button
        lowStockButton.addActionListener(e -> showLowStockItems());
        
        // Table selection
        itemsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = itemsTable.getSelectedRow() != -1;
                editItemButton.setEnabled(hasSelection);
                deleteItemButton.setEnabled(hasSelection);
            }
        });
        
        // Double-click to edit
        itemsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editItem();
                }
            }
        });
    }

    public void refreshData() {
        loadItems();
    }

    private void loadItems() {
        try {
            List<Item> items = itemDAO.findAll();
            itemsModel.setRowCount(0);
            
            for (Item item : items) {
                Object[] row = {
                    item.getId(),
                    item.getName(),
                    item.getBarcode(),
                    "₹" + String.format("%.2f", item.getPrice()),
                    item.getStockQuantity(),
                    item.getMinStockLevel(),
                    item.getCategory(),
                    item.getGstPercentage() + "%"
                };
                itemsModel.addRow(row);
            }
            
            parent.setStatus("Loaded " + items.size() + " items");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            parent.setStatus("Error loading items");
        }
    }

    private void searchItems() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadItems();
            return;
        }
        
        try {
            List<Item> items = itemDAO.searchByName(searchTerm);
            itemsModel.setRowCount(0);
            
            for (Item item : items) {
                Object[] row = {
                    item.getId(),
                    item.getName(),
                    item.getBarcode(),
                    "₹" + String.format("%.2f", item.getPrice()),
                    item.getStockQuantity(),
                    item.getMinStockLevel(),
                    item.getCategory(),
                    item.getGstPercentage() + "%"
                };
                itemsModel.addRow(row);
            }
            
            parent.setStatus("Found " + items.size() + " items matching '" + searchTerm + "'");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            parent.setStatus("Error searching items");
        }
    }

    private void addItem() {
        ItemDialog dialog = new ItemDialog(parent, null, itemDAO);
        dialog.setVisible(true);
        
        if (dialog.isItemSaved()) {
            loadItems();
            parent.setStatus("Item added successfully");
        }
    }

    private void editItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int itemId = (Integer) itemsTable.getValueAt(selectedRow, 0);
            Item item = itemDAO.findById(itemId);
            
            if (item != null) {
                ItemDialog dialog = new ItemDialog(parent, item, itemDAO);
                dialog.setVisible(true);
                
                if (dialog.isItemSaved()) {
                    loadItems();
                    parent.setStatus("Item updated successfully");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            parent.setStatus("Error loading item");
        }
    }

    private void deleteItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this item?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                int itemId = (Integer) itemsTable.getValueAt(selectedRow, 0);
                boolean deleted = itemDAO.delete(itemId);
                
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Item deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadItems();
                    parent.setStatus("Item deleted successfully");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete item", "Error", JOptionPane.ERROR_MESSAGE);
                    parent.setStatus("Failed to delete item");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                parent.setStatus("Error deleting item");
            }
        }
    }

    private void showLowStockItems() {
        try {
            List<Item> lowStockItems = itemDAO.findLowStockItems();
            
            if (lowStockItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No items with low stock", "Low Stock Alert", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Create low stock dialog
            String[] columnNames = {"Name", "Current Stock", "Min Stock", "Category"};
            Object[][] data = new Object[lowStockItems.size()][4];
            
            for (int i = 0; i < lowStockItems.size(); i++) {
                Item item = lowStockItems.get(i);
                data[i][0] = item.getName();
                data[i][1] = item.getStockQuantity();
                data[i][2] = item.getMinStockLevel();
                data[i][3] = item.getCategory();
            }
            
            JTable lowStockTable = new JTable(data, columnNames);
            lowStockTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            lowStockTable.setRowHeight(25);
            
            JScrollPane scrollPane = new JScrollPane(lowStockTable);
            scrollPane.setPreferredSize(new Dimension(500, 200));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Low Stock Items", JOptionPane.WARNING_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading low stock items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            parent.setStatus("Error loading low stock items");
        }
    }
}
