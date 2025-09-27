package com.grocerypos.util;

/**
 * Utility class to generate proper password hashes for database setup
 */
public class GeneratePasswords {
    
    public static void main(String[] args) {
        System.out.println("Generating password hashes for Grocery POS...");
        System.out.println();
        
        // Generate hash for admin123
        String adminPassword = "admin123";
        String adminHash = PasswordUtil.hashPassword(adminPassword);
        System.out.println("Admin password hash for 'admin123':");
        System.out.println(adminHash);
        System.out.println();
        
        // Generate hash for cashier123
        String cashierPassword = "cashier123";
        String cashierHash = PasswordUtil.hashPassword(cashierPassword);
        System.out.println("Cashier password hash for 'cashier123':");
        System.out.println(cashierHash);
        System.out.println();
        
        // Generate SQL update statements
        System.out.println("SQL Update Statements:");
        System.out.println("UPDATE users SET password_hash = '" + adminHash + "' WHERE username = 'admin';");
        System.out.println("UPDATE users SET password_hash = '" + cashierHash + "' WHERE username = 'cashier';");
        System.out.println();
        
        // Test verification
        System.out.println("Testing password verification:");
        System.out.println("admin123 verification: " + PasswordUtil.verifyPassword(adminPassword, adminHash));
        System.out.println("cashier123 verification: " + PasswordUtil.verifyPassword(cashierPassword, cashierHash));
    }
}
