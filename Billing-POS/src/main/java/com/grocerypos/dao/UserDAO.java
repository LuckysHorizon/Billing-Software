package com.grocerypos.dao;

import com.grocerypos.database.DBUtil;
import com.grocerypos.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entity
 */
public class UserDAO {
    
    private static final String INSERT_USER = 
        "INSERT INTO users (username, password_hash, full_name, role, is_active) " +
        "VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_USER = 
        "UPDATE users SET username=?, password_hash=?, full_name=?, role=?, " +
        "is_active=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
    
    private static final String DELETE_USER = "UPDATE users SET is_active=false WHERE id=?";
    
    private static final String FIND_BY_ID = "SELECT * FROM users WHERE id=? AND is_active=true";
    
    private static final String FIND_BY_USERNAME = "SELECT * FROM users WHERE username=? AND is_active=true";
    
    private static final String FIND_ALL = "SELECT * FROM users WHERE is_active=true ORDER BY full_name";
    
    private static final String FIND_BY_ROLE = "SELECT * FROM users WHERE role=? AND is_active=true ORDER BY full_name";
    
    private static final String AUTHENTICATE = "SELECT * FROM users WHERE username=? AND password_hash=? AND is_active=true";
    
    private static final String UPDATE_PASSWORD = "UPDATE users SET password_hash=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
    
    private static final String FIND_ACTIVE_USERS = "SELECT * FROM users WHERE is_active=true ORDER BY full_name";

    /**
     * Insert a new user
     */
    public int insert(User user) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getRole().name());
            statement.setBoolean(5, user.isActive());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Update an existing user
     */
    public boolean update(User user) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER)) {
            
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getRole().name());
            statement.setBoolean(5, user.isActive());
            statement.setInt(6, user.getId());
            
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Delete a user (soft delete)
     */
    public boolean delete(int id) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER)) {
            
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Find user by ID
     */
    public User findById(int id) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
                return null;
            }
        }
    }

    /**
     * Find user by username
     */
    public User findByUsername(String username) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USERNAME)) {
            
            statement.setString(1, username);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
                return null;
            }
        }
    }

    /**
     * Find all users
     */
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        }
        
        return users;
    }

    /**
     * Find users by role
     */
    public List<User> findByRole(User.Role role) throws SQLException {
        List<User> users = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ROLE)) {
            
            statement.setString(1, role.name());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapResultSetToUser(resultSet));
                }
            }
        }
        
        return users;
    }

    /**
     * Authenticate user with username and password
     */
    public User authenticate(String username, String passwordHash) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(AUTHENTICATE)) {
            
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
                return null;
            }
        }
    }

    /**
     * Update user password
     */
    public boolean updatePassword(int userId, String newPasswordHash) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PASSWORD)) {
            
            statement.setString(1, newPasswordHash);
            statement.setInt(2, userId);
            
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Find all active users
     */
    public List<User> findActiveUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ACTIVE_USERS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        }
        
        return users;
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) throws SQLException {
        return findByUsername(username) != null;
    }

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setFullName(resultSet.getString("full_name"));
        user.setRole(User.Role.valueOf(resultSet.getString("role")));
        user.setActive(resultSet.getBoolean("is_active"));
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
}
