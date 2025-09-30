package com.grocerypos.ui;

import com.grocerypos.util.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Main window of the Grocery POS application
 */
public class MainWindow extends JFrame {
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JPanel mainPanel;
    private JLabel statusLabel;
    private JLabel userLabel;
    
    // Menu items
    private JMenuItem newBillItem;
    private JMenuItem inventoryItem;
    private JMenuItem reportsItem;
    private JMenuItem usersItem;
    private JMenuItem settingsItem;
    private JMenuItem logoutItem;
    private JMenuItem exitItem;
    
    // Toolbar buttons
    private JButton newBillButton;
    private JButton inventoryButton;
    private JButton reportsButton;
    private JButton settingsButton;

    public MainWindow() {
        initializeComponents();
        setupMenuBar();
        setupToolBar();
        setupMainPanel();
        setupEventHandlers();
        setupWindow();
    }

    private void initializeComponents() {
        // Create menu bar
        menuBar = new JMenuBar();
        
        // Create toolbar
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        
        // Create main panel
        mainPanel = new JPanel(new BorderLayout());
        
        // Create status bar
        statusLabel = new JLabel("Ready");
        userLabel = new JLabel();
        
        // Initialize menu items (using text icons for now)
        newBillItem = new JMenuItem("New Bill");
        inventoryItem = new JMenuItem("Inventory Management");
        reportsItem = new JMenuItem("Reports");
        usersItem = new JMenuItem("User Management");
        settingsItem = new JMenuItem("Settings");
        logoutItem = new JMenuItem("Logout");
        exitItem = new JMenuItem("Exit");
        
        // Initialize toolbar buttons (using text for now)
        newBillButton = new JButton("New Bill");
        inventoryButton = new JButton("Inventory");
        reportsButton = new JButton("Reports");
        settingsButton = new JButton("Settings");
    }

    private void setupMenuBar() {
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.add(newBillItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);
        
        // Inventory menu
        JMenu inventoryMenu = new JMenu("Inventory");
        inventoryMenu.setMnemonic('I');
        inventoryMenu.add(inventoryItem);
        
        // Reports menu
        JMenu reportsMenu = new JMenu("Reports");
        reportsMenu.setMnemonic('R');
        reportsMenu.add(reportsItem);
        
        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic('T');
        toolsMenu.add(settingsItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(inventoryMenu);
        menuBar.add(reportsMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        // Add user info to menu bar
        menuBar.add(Box.createHorizontalGlue());
        userLabel.setText("Welcome, " + SessionManager.getCurrentUserName());
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        menuBar.add(userLabel);
        
        setJMenuBar(menuBar);
    }

    private void setupToolBar() {
        // Configure toolbar buttons
        configureToolBarButton(newBillButton, "Create a new bill");
        configureToolBarButton(inventoryButton, "Manage inventory");
        configureToolBarButton(reportsButton, "View reports");
        configureToolBarButton(settingsButton, "Application settings");
        
        // Add buttons to toolbar
        toolBar.add(newBillButton);
        toolBar.addSeparator();
        toolBar.add(inventoryButton);
        toolBar.add(reportsButton);
        toolBar.addSeparator();
        toolBar.add(settingsButton);
        
        add(toolBar, BorderLayout.NORTH);
    }

    private void configureToolBarButton(JButton button, String tooltip) {
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.putClientProperty("JComponent.roundRect", true);
        button.setRolloverEnabled(true);
    }

    private void setupMainPanel() {
        // Create welcome panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        welcomePanel.setBackground(Color.WHITE);
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'>" +
            "<h1 style='color: #0078d7; margin-bottom: 10px;'>Welcome to Grocery POS</h1>" +
            "<p style='font-size: 16px; color: #666; margin-bottom: 20px;'>" +
            "Your complete billing and inventory management solution</p>" +
            "<p style='font-size: 14px; color: #888;'>" +
            "Select an option from the menu or toolbar to get started</p>" +
            "</div></html>");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Quick action buttons
        JPanel quickActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        quickActionsPanel.setBackground(Color.WHITE);
        
        JButton quickNewBill = new JButton("Start New Bill");
        JButton quickInventory = new JButton("Manage Inventory");
        JButton quickReports = new JButton("View Reports");
        
        configureQuickActionButton(quickNewBill);
        configureQuickActionButton(quickInventory);
        configureQuickActionButton(quickReports);
        
        quickActionsPanel.add(quickNewBill);
        quickActionsPanel.add(quickInventory);
        quickActionsPanel.add(quickReports);
        
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        welcomePanel.add(quickActionsPanel, BorderLayout.SOUTH);
        
        mainPanel.add(welcomePanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        JLabel versionLabel = new JLabel("Grocery POS v1.0.0");
        versionLabel.setForeground(Color.GRAY);
        statusBar.add(versionLabel, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void configureQuickActionButton(JButton button) {
        button.setPreferredSize(new Dimension(150, 40));
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.putClientProperty("JComponent.roundRect", true);
        button.setBackground(new Color(0,122,255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    private void setupEventHandlers() {
        // Menu item actions
        newBillItem.addActionListener(e -> openNewBill());
        inventoryItem.addActionListener(e -> openInventory());
        reportsItem.addActionListener(e -> openReports());
        usersItem.addActionListener(e -> openUserManagement());
        settingsItem.addActionListener(e -> openSettings());
        logoutItem.addActionListener(e -> logout());
        exitItem.addActionListener(e -> exitApplication());
        
        // Toolbar button actions
        newBillButton.addActionListener(e -> openNewBill());
        inventoryButton.addActionListener(e -> openInventory());
        reportsButton.addActionListener(e -> openReports());
        settingsButton.addActionListener(e -> openSettings());
        
        // About menu item
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
    }

    private void setupWindow() {
        setTitle("Grocery POS - Billing & Inventory Management");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
        
        // Handle window closing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApplication();
            }
        });
    }

    // Action methods
    private void openNewBill() {
        statusLabel.setText("Opening new bill...");
        try {
            BillingWindow billingWindow = new BillingWindow();
            billingWindow.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening billing window: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openInventory() {
        statusLabel.setText("Opening inventory management...");
        try {
            InventoryWindow inventoryWindow = new InventoryWindow();
            inventoryWindow.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening inventory window: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openReports() {
        statusLabel.setText("Opening reports...");
        try {
            ReportsWindow reportsWindow = new ReportsWindow();
            reportsWindow.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening reports window: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openUserManagement() {
        if (!SessionManager.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Access denied. Admin privileges required.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }
        statusLabel.setText("Opening user management...");
        // TODO: Implement user management window
        JOptionPane.showMessageDialog(this, "User Management feature will be implemented soon!", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openSettings() {
        statusLabel.setText("Opening settings...");
        // TODO: Implement settings window
        JOptionPane.showMessageDialog(this, "Settings feature will be implemented soon!", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            SessionManager.clearSession();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit the application?",
            "Exit Application",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "<html><div style='text-align: center;'>" +
            "<h2>Grocery POS System</h2>" +
            "<p>Version 1.0.0</p>" +
            "<p>Complete billing and inventory management solution</p>" +
            "<p>Built with Java Swing and MySQL</p>" +
            "<p>© 2024 Grocery POS. All rights reserved.</p>" +
            "</div></html>",
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
