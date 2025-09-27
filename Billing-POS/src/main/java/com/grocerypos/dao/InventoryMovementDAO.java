package com.grocerypos.dao;

import com.grocerypos.database.DBUtil;
import com.grocerypos.model.InventoryMovement;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for InventoryMovement entity
 */
public class InventoryMovementDAO {
    
    private static final String INSERT_MOVEMENT = 
        "INSERT INTO inventory_movements (item_id, movement_type, quantity_change, " +
        "previous_stock, new_stock, reference_id, reference_type, notes, user_id) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String FIND_BY_ITEM = 
        "SELECT im.*, i.name as item_name, u.full_name as user_name " +
        "FROM inventory_movements im " +
        "JOIN items i ON im.item_id = i.id " +
        "JOIN users u ON im.user_id = u.id " +
        "WHERE im.item_id=? ORDER BY im.created_at DESC";
    
    private static final String FIND_BY_MOVEMENT_TYPE = 
        "SELECT im.*, i.name as item_name, u.full_name as user_name " +
        "FROM inventory_movements im " +
        "JOIN items i ON im.item_id = i.id " +
        "JOIN users u ON im.user_id = u.id " +
        "WHERE im.movement_type=? ORDER BY im.created_at DESC";
    
    private static final String FIND_BY_DATE_RANGE = 
        "SELECT im.*, i.name as item_name, u.full_name as user_name " +
        "FROM inventory_movements im " +
        "JOIN items i ON im.item_id = i.id " +
        "JOIN users u ON im.user_id = u.id " +
        "WHERE DATE(im.created_at) BETWEEN ? AND ? ORDER BY im.created_at DESC";
    
    private static final String FIND_ALL = 
        "SELECT im.*, i.name as item_name, u.full_name as user_name " +
        "FROM inventory_movements im " +
        "JOIN items i ON im.item_id = i.id " +
        "JOIN users u ON im.user_id = u.id " +
        "ORDER BY im.created_at DESC";
    
    private static final String FIND_BY_REFERENCE = 
        "SELECT im.*, i.name as item_name, u.full_name as user_name " +
        "FROM inventory_movements im " +
        "JOIN items i ON im.item_id = i.id " +
        "JOIN users u ON im.user_id = u.id " +
        "WHERE im.reference_id=? AND im.reference_type=? ORDER BY im.created_at DESC";

    /**
     * Insert a new inventory movement
     */
    public int insert(InventoryMovement movement) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_MOVEMENT, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, movement.getItemId());
            statement.setString(2, movement.getMovementType().name());
            statement.setInt(3, movement.getQuantityChange());
            statement.setInt(4, movement.getPreviousStock());
            statement.setInt(5, movement.getNewStock());
            statement.setObject(6, movement.getReferenceId());
            statement.setString(7, movement.getReferenceType() != null ? movement.getReferenceType().name() : null);
            statement.setString(8, movement.getNotes());
            statement.setInt(9, movement.getUserId());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating inventory movement failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating inventory movement failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Find movements by item ID
     */
    public List<InventoryMovement> findByItem(int itemId) throws SQLException {
        List<InventoryMovement> movements = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ITEM)) {
            
            statement.setInt(1, itemId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    movements.add(mapResultSetToMovement(resultSet));
                }
            }
        }
        
        return movements;
    }

    /**
     * Find movements by movement type
     */
    public List<InventoryMovement> findByMovementType(InventoryMovement.MovementType movementType) throws SQLException {
        List<InventoryMovement> movements = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_MOVEMENT_TYPE)) {
            
            statement.setString(1, movementType.name());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    movements.add(mapResultSetToMovement(resultSet));
                }
            }
        }
        
        return movements;
    }

    /**
     * Find movements by date range
     */
    public List<InventoryMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<InventoryMovement> movements = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_DATE_RANGE)) {
            
            statement.setDate(1, Date.valueOf(startDate.toLocalDate()));
            statement.setDate(2, Date.valueOf(endDate.toLocalDate()));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    movements.add(mapResultSetToMovement(resultSet));
                }
            }
        }
        
        return movements;
    }

    /**
     * Find all movements
     */
    public List<InventoryMovement> findAll() throws SQLException {
        List<InventoryMovement> movements = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                movements.add(mapResultSetToMovement(resultSet));
            }
        }
        
        return movements;
    }

    /**
     * Find movements by reference
     */
    public List<InventoryMovement> findByReference(int referenceId, InventoryMovement.ReferenceType referenceType) throws SQLException {
        List<InventoryMovement> movements = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_REFERENCE)) {
            
            statement.setInt(1, referenceId);
            statement.setString(2, referenceType.name());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    movements.add(mapResultSetToMovement(resultSet));
                }
            }
        }
        
        return movements;
    }

    /**
     * Record a stock movement (helper method)
     */
    public void recordStockMovement(int itemId, InventoryMovement.MovementType movementType, 
                                  int quantityChange, int previousStock, int newStock, 
                                  Integer referenceId, InventoryMovement.ReferenceType referenceType, 
                                  String notes, int userId) throws SQLException {
        InventoryMovement movement = new InventoryMovement();
        movement.setItemId(itemId);
        movement.setMovementType(movementType);
        movement.setQuantityChange(quantityChange);
        movement.setPreviousStock(previousStock);
        movement.setNewStock(newStock);
        movement.setReferenceId(referenceId);
        movement.setReferenceType(referenceType);
        movement.setNotes(notes);
        movement.setUserId(userId);
        
        insert(movement);
    }

    /**
     * Map ResultSet to InventoryMovement object
     */
    private InventoryMovement mapResultSetToMovement(ResultSet resultSet) throws SQLException {
        InventoryMovement movement = new InventoryMovement();
        movement.setId(resultSet.getInt("id"));
        movement.setItemId(resultSet.getInt("item_id"));
        movement.setItemName(resultSet.getString("item_name"));
        movement.setMovementType(InventoryMovement.MovementType.valueOf(resultSet.getString("movement_type")));
        movement.setQuantityChange(resultSet.getInt("quantity_change"));
        movement.setPreviousStock(resultSet.getInt("previous_stock"));
        movement.setNewStock(resultSet.getInt("new_stock"));
        movement.setReferenceId(resultSet.getObject("reference_id", Integer.class));
        
        String referenceTypeStr = resultSet.getString("reference_type");
        if (referenceTypeStr != null) {
            movement.setReferenceType(InventoryMovement.ReferenceType.valueOf(referenceTypeStr));
        }
        
        movement.setNotes(resultSet.getString("notes"));
        movement.setUserId(resultSet.getInt("user_id"));
        movement.setUserName(resultSet.getString("user_name"));
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            movement.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return movement;
    }
}
