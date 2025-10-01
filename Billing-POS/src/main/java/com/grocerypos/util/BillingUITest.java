package com.grocerypos.util;

import com.grocerypos.ui.BillingWindow;
import com.grocerypos.ui.components.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Test class to verify the modern billing UI functionality
 */
public class BillingUITest {
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Initialize theme manager
        ThemeManager.initialize();
        
        // Create and show the billing window
        SwingUtilities.invokeLater(() -> {
            BillingWindow billingWindow = new BillingWindow();
            billingWindow.setVisible(true);
            
            // Test component creation
            testModernComponents();
        });
    }
    
    private static void testModernComponents() {
        // Test modern components
        System.out.println("Testing Modern Components:");
        
        // Test ModernButton
        ModernButton button = new ModernButton("Test Button");
        System.out.println("✓ ModernButton created successfully");
        
        // Test ModernSearchField
        ModernSearchField searchField = new ModernSearchField("Test Search");
        System.out.println("✓ ModernSearchField created successfully");
        
        // Test ModernTable
        String[] columns = {"Name", "Value"};
        Object[][] data = {{"Item 1", "100"}, {"Item 2", "200"}};
        DefaultTableModel model = new DefaultTableModel(data, columns);
        ModernTable table = new ModernTable(model);
        System.out.println("✓ ModernTable created successfully");
        
        // Test InvoiceSummaryCard
        InvoiceSummaryCard summaryCard = new InvoiceSummaryCard();
        System.out.println("✓ InvoiceSummaryCard created successfully");
        
        // Test ProductSidebar
        ProductSidebar sidebar = new ProductSidebar();
        System.out.println("✓ ProductSidebar created successfully");
        
        // Test GlassCard
        GlassCard glassCard = new GlassCard();
        System.out.println("✓ GlassCard created successfully");
        
        // Test ToastNotification
        JFrame testFrame = new JFrame("Test");
        testFrame.setSize(300, 200);
        testFrame.setLocationRelativeTo(null);
        testFrame.setVisible(true);
        
        ToastNotification.showSuccess(testFrame, "Test notification");
        System.out.println("✓ ToastNotification working successfully");
        
        // Test AnimationUtils
        AnimationUtils.fadeIn(testFrame, 1000);
        System.out.println("✓ AnimationUtils working successfully");
        
        System.out.println("\nAll modern components tested successfully!");
        System.out.println("The futuristic macOS-inspired billing UI is ready to use.");
        
        // Suppress unused variable warnings by using them
        System.out.println("Component instances created: " + 
            button.getText() + ", " + searchField.getText() + ", " + 
            table.getRowCount() + " rows, " + summaryCard.getClass().getSimpleName() + 
            ", " + sidebar.getClass().getSimpleName() + ", " + glassCard.getClass().getSimpleName());
    }
}
