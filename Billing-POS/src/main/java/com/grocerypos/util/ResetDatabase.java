package com.grocerypos.util;

import com.grocerypos.dao.UserDAO;
import com.grocerypos.model.User;
import java.sql.SQLException;

/**
 * Utility to reset database with correct password hashes
 */
public class ResetDatabase {
    
    public static void main(String[] args) {
        try {
            System.out.println("Resetting database passwords...");
            
            UserDAO userDAO = new UserDAO();
            
            // Update admin user
            User admin = userDAO.findByUsername("admin");
            if (admin != null) {
                admin.setPasswordHash("admin123");
                userDAO.update(admin);
                System.out.println("Admin password updated successfully");
            }
            
            // Update cashier user
            User cashier = userDAO.findByUsername("cashier");
            if (cashier != null) {
                cashier.setPasswordHash("cashier123");
                userDAO.update(cashier);
                System.out.println("Cashier password updated successfully");
            }
            
            // Test the updates
            System.out.println("\nTesting updated passwords:");
            User testAdmin = userDAO.findByUsername("admin");
            System.out.println("Admin password verification: " + PasswordUtil.verifyPassword("admin123", testAdmin.getPasswordHash()));
            
            User testCashier = userDAO.findByUsername("cashier");
            System.out.println("Cashier password verification: " + PasswordUtil.verifyPassword("cashier123", testCashier.getPasswordHash()));
            
            System.out.println("\nDatabase reset completed successfully!");
            System.out.println("You can now login with:");
            System.out.println("Admin: admin / admin123");
            System.out.println("Cashier: cashier / cashier123");
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
