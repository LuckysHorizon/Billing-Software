package com.grocerypos.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Database utility class for managing MySQL connections
 */
public class DBUtil {
    private static final String PROPERTIES_FILE = "database.properties";
    private static Properties properties;
    private static String url;
    private static String username;
    private static String password;
    private static String driver;

    static {
        loadProperties();
    }

    /**
     * Load database properties from configuration file
     */
    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + PROPERTIES_FILE);
            }
            properties.load(input);
            
            url = properties.getProperty("db.url");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");
            driver = properties.getProperty("db.driver");
            
            // Load MySQL driver
            Class.forName(driver);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    /**
     * Get a database connection
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Get a database connection with auto-commit setting
     */
    public static Connection getConnection(boolean autoCommit) throws SQLException {
        Connection connection = getConnection();
        connection.setAutoCommit(autoCommit);
        return connection;
    }

    /**
     * Close database resources
     */
    public static void closeResources(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database resources: " + e.getMessage());
        }
    }

    /**
     * Close database resources (without ResultSet)
     */
    public static void closeResources(Connection connection, PreparedStatement statement) {
        closeResources(connection, statement, null);
    }

    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Initialize database schema
     */
    public static void initializeDatabase() {
        try (Connection connection = getConnection()) {
            // Read and execute schema.sql
            String schema = readSchemaFile();
            String[] statements = schema.split(";");
            
            for (String statement : statements) {
                statement = statement.trim();
                if (!statement.isEmpty() && !statement.startsWith("--")) {
                    try (PreparedStatement ps = connection.prepareStatement(statement)) {
                        ps.execute();
                    }
                }
            }
            System.out.println("Database schema initialized successfully");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    /**
     * Read schema.sql file
     */
    private static String readSchemaFile() {
        try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (input == null) {
                throw new RuntimeException("Unable to find schema.sql");
            }
            return new String(input.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read schema file", e);
        }
    }

    /**
     * Get database URL
     */
    public static String getUrl() {
        return url;
    }

    /**
     * Get database username
     */
    public static String getUsername() {
        return username;
    }

    /**
     * Get property value
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get property value with default
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
