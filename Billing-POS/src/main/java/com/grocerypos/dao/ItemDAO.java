package com.grocerypos.dao;

import com.grocerypos.database.DBUtil;
import com.grocerypos.model.Item;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Item entity
 */
public class ItemDAO {
    
    private static final String INSERT_ITEM = 
        "INSERT INTO items (barcode, name, description, price, cost_price, gst_percentage, " +
        "stock_quantity, min_stock_level, unit, category, is_active) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_ITEM = 
        "UPDATE items SET barcode=?, name=?, description=?, price=?, cost_price=?, " +
        "gst_percentage=?, stock_quantity=?, min_stock_level=?, unit=?, category=?, " +
        "is_active=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
    
    private static final String DELETE_ITEM = "UPDATE items SET is_active=false WHERE id=?";
    
    private static final String FIND_BY_ID = "SELECT * FROM items WHERE id=? AND is_active=true";
    
    private static final String FIND_BY_BARCODE = "SELECT * FROM items WHERE barcode=? AND is_active=true";
    
    private static final String FIND_ALL = "SELECT * FROM items WHERE is_active=true ORDER BY name";
    
    private static final String FIND_BY_CATEGORY = "SELECT * FROM items WHERE category=? AND is_active=true ORDER BY name";
    
    private static final String SEARCH_BY_NAME = "SELECT * FROM items WHERE name LIKE ? AND is_active=true ORDER BY name";
    
    private static final String FIND_LOW_STOCK = "SELECT * FROM items WHERE stock_quantity <= min_stock_level AND is_active=true ORDER BY stock_quantity";
    
    private static final String UPDATE_STOCK = "UPDATE items SET stock_quantity=? WHERE id=?";
    
    private static final String FIND_BY_NAME_PATTERN = "SELECT * FROM items WHERE name LIKE ? AND is_active=true ORDER BY name LIMIT 10";

    /**
     * Insert a new item
     */
    public int insert(Item item) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_ITEM, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, item.getBarcode());
            statement.setString(2, item.getName());
            statement.setString(3, item.getDescription());
            statement.setBigDecimal(4, item.getPrice());
            statement.setBigDecimal(5, item.getCostPrice());
            statement.setBigDecimal(6, item.getGstPercentage());
            statement.setInt(7, item.getStockQuantity());
            statement.setInt(8, item.getMinStockLevel());
            statement.setString(9, item.getUnit());
            statement.setString(10, item.getCategory());
            statement.setBoolean(11, item.isActive());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating item failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating item failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Update an existing item
     */
    public boolean update(Item item) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_ITEM)) {
            
            statement.setString(1, item.getBarcode());
            statement.setString(2, item.getName());
            statement.setString(3, item.getDescription());
            statement.setBigDecimal(4, item.getPrice());
            statement.setBigDecimal(5, item.getCostPrice());
            statement.setBigDecimal(6, item.getGstPercentage());
            statement.setInt(7, item.getStockQuantity());
            statement.setInt(8, item.getMinStockLevel());
            statement.setString(9, item.getUnit());
            statement.setString(10, item.getCategory());
            statement.setBoolean(11, item.isActive());
            statement.setInt(12, item.getId());
            
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Delete an item (soft delete)
     */
    public boolean delete(int id) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_ITEM)) {
            
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Find item by ID
     */
    public Item findById(int id) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToItem(resultSet);
                }
                return null;
            }
        }
    }

    /**
     * Find item by barcode
     */
    public Item findByBarcode(String barcode) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_BARCODE)) {
            
            statement.setString(1, barcode);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToItem(resultSet);
                }
                return null;
            }
        }
    }

    /**
     * Find all items
     */
    public List<Item> findAll() throws SQLException {
        List<Item> items = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                items.add(mapResultSetToItem(resultSet));
            }
        }
        
        return items;
    }

    /**
     * Find items by category
     */
    public List<Item> findByCategory(String category) throws SQLException {
        List<Item> items = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CATEGORY)) {
            
            statement.setString(1, category);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapResultSetToItem(resultSet));
                }
            }
        }
        
        return items;
    }

    /**
     * Search items by name
     */
    public List<Item> searchByName(String namePattern) throws SQLException {
        List<Item> items = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SEARCH_BY_NAME)) {
            
            statement.setString(1, "%" + namePattern + "%");
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapResultSetToItem(resultSet));
                }
            }
        }
        
        return items;
    }

    /**
     * Find items with low stock
     */
    public List<Item> findLowStockItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_LOW_STOCK);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                items.add(mapResultSetToItem(resultSet));
            }
        }
        
        return items;
    }

    /**
     * Update stock quantity
     */
    public boolean updateStock(int itemId, int newStock) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_STOCK)) {
            
            statement.setInt(1, newStock);
            statement.setInt(2, itemId);
            
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Find items by name pattern (for autocomplete)
     */
    public List<Item> findByNamePattern(String pattern) throws SQLException {
        List<Item> items = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME_PATTERN)) {
            
            statement.setString(1, "%" + pattern + "%");
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapResultSetToItem(resultSet));
                }
            }
        }
        
        return items;
    }

    /**
     * Map ResultSet to Item object
     */
    private Item mapResultSetToItem(ResultSet resultSet) throws SQLException {
        Item item = new Item();
        item.setId(resultSet.getInt("id"));
        item.setBarcode(resultSet.getString("barcode"));
        item.setName(resultSet.getString("name"));
        item.setDescription(resultSet.getString("description"));
        
        // Null-safe BigDecimal mapping with defaults
        item.setPrice(resultSet.getBigDecimal("price") != null ? resultSet.getBigDecimal("price") : BigDecimal.ZERO);
        item.setCostPrice(resultSet.getBigDecimal("cost_price") != null ? resultSet.getBigDecimal("cost_price") : BigDecimal.ZERO);
        item.setGstPercentage(resultSet.getBigDecimal("gst_percentage") != null ? resultSet.getBigDecimal("gst_percentage") : BigDecimal.ZERO);
        
        item.setStockQuantity(resultSet.getInt("stock_quantity"));
        item.setMinStockLevel(resultSet.getInt("min_stock_level"));
        item.setUnit(resultSet.getString("unit") != null ? resultSet.getString("unit") : "pcs");
        item.setCategory(resultSet.getString("category"));
        item.setActive(resultSet.getBoolean("is_active"));
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            item.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            item.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return item;
    }
}
