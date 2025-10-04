-- Seed data for Grocery POS System
USE grocery_pos;

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password_hash, full_name, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFjJqJqJqJqJqJqJqJqJqJq', 'System Administrator', 'ADMIN'),
('cashier1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFjJqJqJqJqJqJqJqJqJqJq', 'John Cashier', 'CASHIER'),
('manager1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFjJqJqJqJqJqJqJqJqJqJq', 'Jane Manager', 'MANAGER');

-- Insert default categories
INSERT INTO categories (name, description) VALUES 
('Groceries', 'General grocery items'),
('Vegetables', 'Fresh vegetables'),
('Fruits', 'Fresh fruits'),
('Dairy', 'Dairy products'),
('Beverages', 'Drinks and beverages'),
('Snacks', 'Snack items'),
('Household', 'Household items'),
('Bakery', 'Bread and bakery items'),
('Meat', 'Meat and poultry'),
('Frozen', 'Frozen food items');

-- Insert sample items
INSERT INTO items (barcode, name, description, price, cost_price, gst_percentage, stock_quantity, min_stock_level, unit, category) VALUES 
('1234567890123', 'Rice 1kg', 'Basmati Rice 1kg', 45.00, 35.00, 5.00, 100, 10, 'kg', 'Groceries'),
('1234567890124', 'Wheat Flour 1kg', 'Whole Wheat Flour 1kg', 35.00, 28.00, 5.00, 50, 5, 'kg', 'Groceries'),
('1234567890125', 'Tomatoes', 'Fresh Red Tomatoes', 25.00, 20.00, 5.00, 200, 20, 'kg', 'Vegetables'),
('1234567890126', 'Onions', 'Fresh Onions', 30.00, 25.00, 5.00, 150, 15, 'kg', 'Vegetables'),
('1234567890127', 'Apples', 'Fresh Red Apples', 80.00, 65.00, 5.00, 50, 10, 'kg', 'Fruits'),
('1234567890128', 'Bananas', 'Fresh Bananas', 40.00, 32.00, 5.00, 100, 20, 'kg', 'Fruits'),
('1234567890129', 'Milk 1L', 'Fresh Cow Milk 1L', 60.00, 50.00, 5.00, 75, 10, 'litre', 'Dairy'),
('1234567890130', 'Bread', 'White Bread Loaf', 25.00, 20.00, 5.00, 30, 5, 'pcs', 'Bakery'),
('1234567890131', 'Coca Cola 500ml', 'Coca Cola Soft Drink', 35.00, 28.00, 18.00, 100, 20, 'bottle', 'Beverages'),
('1234567890132', 'Potato Chips', 'Crispy Potato Chips', 20.00, 15.00, 12.00, 50, 10, 'packet', 'Snacks');

-- Insert default settings
INSERT INTO settings (setting_key, setting_value, description) VALUES 
('shop_name', 'Grocery Store', 'Name of the shop'),
('shop_address', '123 Main Street, City', 'Shop address'),
('shop_phone', '+91-9876543210', 'Shop contact number'),
('gst_number', 'GST123456789', 'GST registration number'),
('currency_symbol', '₹', 'Currency symbol'),
('receipt_footer', 'Thank you for shopping with us!', 'Receipt footer message'),
('low_stock_threshold', '10', 'Low stock alert threshold'),
('auto_backup', 'true', 'Enable automatic database backup'),
('theme', 'light', 'Application theme preference'),
('language', 'en', 'Application language'),
('timezone', 'Asia/Kolkata', 'Application timezone');
