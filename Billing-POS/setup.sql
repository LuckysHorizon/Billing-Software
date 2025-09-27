-- Grocery POS Database Setup Script
-- Run this script to set up the database and initial data

-- Create database
CREATE DATABASE IF NOT EXISTS grocery_pos 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE grocery_pos;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'CASHIER', 'MANAGER') NOT NULL DEFAULT 'CASHIER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create items table
CREATE TABLE IF NOT EXISTS items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    barcode VARCHAR(50) UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    cost_price DECIMAL(10,2),
    gst_percentage DECIMAL(5,2) DEFAULT 0.00,
    stock_quantity INT DEFAULT 0,
    min_stock_level INT DEFAULT 5,
    unit VARCHAR(20) DEFAULT 'pcs',
    category VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_barcode (barcode),
    INDEX idx_name (name),
    INDEX idx_category (category)
);

-- Create bills table
CREATE TABLE IF NOT EXISTS bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(20) UNIQUE NOT NULL,
    customer_name VARCHAR(100),
    customer_phone VARCHAR(15),
    subtotal DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    gst_amount DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('CASH', 'CARD', 'UPI', 'ONLINE') DEFAULT 'CASH',
    payment_status ENUM('PENDING', 'COMPLETED', 'REFUNDED') DEFAULT 'COMPLETED',
    cashier_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cashier_id) REFERENCES users(id),
    INDEX idx_bill_number (bill_number),
    INDEX idx_created_at (created_at),
    INDEX idx_cashier (cashier_id)
);

-- Create bill_items table
CREATE TABLE IF NOT EXISTS bill_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    discount_percentage DECIMAL(5,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    gst_percentage DECIMAL(5,2) NOT NULL,
    gst_amount DECIMAL(10,2) NOT NULL,
    line_total DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id),
    INDEX idx_bill_id (bill_id),
    INDEX idx_item_id (item_id)
);

-- Create inventory_movements table
CREATE TABLE IF NOT EXISTS inventory_movements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    movement_type ENUM('PURCHASE', 'SALE', 'ADJUSTMENT', 'RETURN', 'DAMAGE') NOT NULL,
    quantity_change INT NOT NULL,
    previous_stock INT NOT NULL,
    new_stock INT NOT NULL,
    reference_id INT,
    reference_type ENUM('BILL', 'PURCHASE', 'ADJUSTMENT') DEFAULT 'BILL',
    notes TEXT,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_item_id (item_id),
    INDEX idx_movement_type (movement_type),
    INDEX idx_created_at (created_at)
);

-- Create categories table
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create settings table
CREATE TABLE IF NOT EXISTS settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) UNIQUE NOT NULL,
    setting_value TEXT,
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert default admin user (password: admin123)
-- Using simple password for development/testing
INSERT INTO users (username, password_hash, full_name, role) VALUES 
('admin', 'admin123', 'System Administrator', 'ADMIN');

-- Insert sample cashier user (password: cashier123)
INSERT INTO users (username, password_hash, full_name, role) VALUES 
('cashier', 'cashier123', 'Cashier User', 'CASHIER');

-- Insert default categories
INSERT INTO categories (name, description) VALUES 
('Groceries', 'General grocery items'),
('Vegetables', 'Fresh vegetables'),
('Fruits', 'Fresh fruits'),
('Dairy', 'Dairy products'),
('Beverages', 'Drinks and beverages'),
('Snacks', 'Snack items'),
('Household', 'Household items');

-- Insert default settings
INSERT INTO settings (setting_key, setting_value, description) VALUES 
('shop_name', 'Grocery Store', 'Name of the shop'),
('shop_address', '123 Main Street, City', 'Shop address'),
('shop_phone', '+91-9876543210', 'Shop contact number'),
('gst_number', 'GST123456789', 'GST registration number'),
('currency_symbol', '₹', 'Currency symbol'),
('receipt_footer', 'Thank you for shopping with us!', 'Receipt footer message'),
('low_stock_threshold', '10', 'Low stock alert threshold'),
('auto_backup', 'true', 'Enable automatic database backup');

-- Insert sample items
INSERT INTO items (barcode, name, description, price, cost_price, gst_percentage, stock_quantity, min_stock_level, unit, category) VALUES 
('1234567890123', 'Rice 1kg', 'Basmati Rice 1kg', 120.00, 100.00, 5.00, 50, 10, 'kg', 'Groceries'),
('1234567890124', 'Wheat Flour 1kg', 'Whole Wheat Flour 1kg', 45.00, 35.00, 5.00, 30, 5, 'kg', 'Groceries'),
('1234567890125', 'Milk 1L', 'Fresh Milk 1 Liter', 60.00, 50.00, 5.00, 25, 5, 'L', 'Dairy'),
('1234567890126', 'Bread', 'White Bread', 25.00, 20.00, 5.00, 20, 5, 'pcs', 'Groceries'),
('1234567890127', 'Onion 1kg', 'Fresh Onions 1kg', 40.00, 30.00, 0.00, 15, 5, 'kg', 'Vegetables'),
('1234567890128', 'Tomato 1kg', 'Fresh Tomatoes 1kg', 50.00, 40.00, 0.00, 20, 5, 'kg', 'Vegetables'),
('1234567890129', 'Apple 1kg', 'Fresh Apples 1kg', 80.00, 60.00, 0.00, 10, 3, 'kg', 'Fruits'),
('1234567890130', 'Banana 1kg', 'Fresh Bananas 1kg', 30.00, 25.00, 0.00, 15, 5, 'kg', 'Fruits');

-- Display success message
SELECT 'Database setup completed successfully!' as message;
SELECT 'Default admin credentials: admin / admin123' as credentials;
SELECT 'Default cashier credentials: cashier / cashier123' as cashier_credentials;
