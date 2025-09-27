package com.grocerypos.util;

import com.grocerypos.dao.UserDAO;
import com.grocerypos.model.User;
import java.sql.SQLException;

/**
 * Test class to debug login issues
 */
public class TestLogin {
    
    public static void main(String[] args) {
        try {
            System.out.println("Testing login system...");
            
            UserDAO userDAO = new UserDAO();
            
            // Test finding admin user
            User admin = userDAO.findByUsername("admin");
            if (admin != null) {
                System.out.println("Admin user found:");
                System.out.println("Username: " + admin.getUsername());
                System.out.println("Password Hash: " + admin.getPasswordHash());
                System.out.println("Full Name: " + admin.getFullName());
                System.out.println("Role: " + admin.getRole());
                
                // Test password verification
                boolean adminResult = PasswordUtil.verifyPassword("admin123", admin.getPasswordHash());
                System.out.println("Admin password verification: " + adminResult);
            } else {
                System.out.println("Admin user NOT found!");
            }
            
            // Test finding cashier user
            User cashier = userDAO.findByUsername("cashier");
            if (cashier != null) {
                System.out.println("\nCashier user found:");
                System.out.println("Username: " + cashier.getUsername());
                System.out.println("Password Hash: " + cashier.getPasswordHash());
                System.out.println("Full Name: " + cashier.getFullName());
                System.out.println("Role: " + cashier.getRole());
                
                // Test password verification
                boolean cashierResult = PasswordUtil.verifyPassword("cashier123", cashier.getPasswordHash());
                System.out.println("Cashier password verification: " + cashierResult);
            } else {
                System.out.println("Cashier user NOT found!");
            }
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
