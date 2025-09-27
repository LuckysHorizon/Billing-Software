-- Fix password hashes for Grocery POS users
USE grocery_pos;

-- Update admin user with simple password hash for 'admin123'
UPDATE users SET password_hash = 'admin123' WHERE username = 'admin';

-- Update cashier user with simple password hash for 'cashier123'  
UPDATE users SET password_hash = 'cashier123' WHERE username = 'cashier';

-- Verify the updates
SELECT username, full_name, role FROM users WHERE username IN ('admin', 'cashier');

-- Display success message
SELECT 'Password hashes updated successfully!' as message;
SELECT 'You can now login with:' as info;
SELECT 'Admin: admin / admin123' as admin_credentials;
SELECT 'Cashier: cashier / cashier123' as cashier_credentials;