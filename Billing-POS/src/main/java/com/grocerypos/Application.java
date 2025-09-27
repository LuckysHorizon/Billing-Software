package com.grocerypos;

import com.formdev.flatlaf.FlatLightLaf;
import com.grocerypos.database.DBUtil;
import com.grocerypos.ui.panels.*;
import com.grocerypos.ui.components.ToastNotification;
import com.grocerypos.util.SessionManager;
import com.grocerypos.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main application class with CardLayout navigation
 */
public class Application extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel sidebarPanel;
    private JLabel userLabel;
    private JLabel statusLabel;
    
    // Panels
    private LoginPanel loginPanel;
    private AdminPanel adminPanel;
    private CashierPanel cashierPanel;
    private ReportsPanel reportsPanel;
    private DashboardPanel dashboardPanel;
    private SettingsPanel settingsPanel;
    
    // Navigation buttons
    private JButton dashboardButton;
    private JButton adminButton;
    private JButton cashierButton;
    private JButton reportsButton;
    private JButton settingsButton;
    private JButton logoutButton;
    private JButton themeButton;

    public Application() {
        initializeApplication();
        setupUI();
        setupEventHandlers();
        showLoginPanel();
    }

    private void initializeApplication() {
        setTitle("Grocery POS System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 700));
        
        // Initialize database
        try {
            System.out.println("Initializing database...");
            DBUtil.initializeDatabase();
            System.out.println("Database initialized successfully!");
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Failed to initialize database. Please check your MySQL connection.\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Create main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Initialize panels
        loginPanel = new LoginPanel(this);
        adminPanel = new AdminPanel(this);
        cashierPanel = new CashierPanel(this);
        reportsPanel = new ReportsPanel(this);
        dashboardPanel = new DashboardPanel(this);
        settingsPanel = new SettingsPanel(this);
        
        // Add panels to main panel
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(adminPanel, "ADMIN");
        mainPanel.add(cashierPanel, "CASHIER");
        mainPanel.add(reportsPanel, "REPORTS");
        mainPanel.add(settingsPanel, "SETTINGS");
        
        // Create sidebar
        createSidebar();
        
        // Add components to frame
        add(sidebarPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        
        // Create status bar
        createStatusBar();
    }

    private void createSidebar() {
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBackground(new Color(45, 55, 72));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(74, 85, 104)));
        
        // User info panel
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(new Color(66, 153, 225));
        userPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        userLabel = new JLabel("Not Logged In");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userPanel.add(userLabel, BorderLayout.CENTER);
        
        // Navigation buttons panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 15, 15));
        navPanel.setBackground(new Color(45, 55, 72));
        
        // Create navigation buttons
        dashboardButton = createNavButton("Dashboard", "📊");
        adminButton = createNavButton("Admin Panel", "👨‍💼");
        cashierButton = createNavButton("Cashier Panel", "🛒");
        reportsButton = createNavButton("Reports", "📈");
        settingsButton = createNavButton("Settings", "⚙️");
        themeButton = createNavButton("Theme", "🎨");
        logoutButton = createNavButton("Logout", "🚪");
        
        // Add buttons to navigation panel
        navPanel.add(dashboardButton);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(adminButton);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(cashierButton);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(reportsButton);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(settingsButton);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(themeButton);
        navPanel.add(Box.createVerticalStrut(20));
        navPanel.add(logoutButton);
        
        sidebarPanel.add(userPanel, BorderLayout.NORTH);
        sidebarPanel.add(navPanel, BorderLayout.CENTER);
        
        // Initially hide sidebar
        sidebarPanel.setVisible(false);
    }

    private JButton createNavButton(String text, String icon) {
        JButton button = new JButton("<html><center>" + icon + "<br>" + text + "</center></html>");
        button.setPreferredSize(new Dimension(190, 70));
        button.setMaximumSize(new Dimension(190, 70));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(74, 85, 104));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(107, 114, 128), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        button.setFocusPainted(false);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(66, 153, 225));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(59, 130, 246), 2),
                    BorderFactory.createEmptyBorder(13, 13, 13, 13)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(74, 85, 104));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(107, 114, 128), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });
        
        return button;
    }

    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        statusBar.setBackground(new Color(240, 240, 240));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel versionLabel = new JLabel("Grocery POS v1.0.0");
        versionLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        versionLabel.setForeground(Color.GRAY);
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(versionLabel, BorderLayout.EAST);
        
        add(statusBar, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        dashboardButton.addActionListener(e -> showDashboardPanel());
        adminButton.addActionListener(e -> showAdminPanel());
        cashierButton.addActionListener(e -> showCashierPanel());
        reportsButton.addActionListener(e -> showReportsPanel());
        settingsButton.addActionListener(e -> showSettingsPanel());
        themeButton.addActionListener(e -> showThemeDialog());
        logoutButton.addActionListener(e -> logout());
    }

    public void showLoginPanel() {
        cardLayout.show(mainPanel, "LOGIN");
        sidebarPanel.setVisible(false);
        setTitle("Grocery POS System - Login");
    }
    
    public void showDashboardPanel() {
        cardLayout.show(mainPanel, "DASHBOARD");
        dashboardPanel.refreshData();
        setTitle("Grocery POS System - Dashboard");
    }

    public void showAdminPanel() {
        if (!SessionManager.isAdmin() && !SessionManager.isManager()) {
            JOptionPane.showMessageDialog(this, "Access denied. Admin privileges required.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cardLayout.show(mainPanel, "ADMIN");
        adminPanel.refreshData();
        setTitle("Grocery POS System - Admin Panel");
    }

    public void showCashierPanel() {
        cardLayout.show(mainPanel, "CASHIER");
        cashierPanel.refreshData();
        setTitle("Grocery POS System - Cashier Panel");
    }

    public void showReportsPanel() {
        cardLayout.show(mainPanel, "REPORTS");
        reportsPanel.refreshData();
        setTitle("Grocery POS System - Reports");
    }

    public void showSettingsPanel() {
        cardLayout.show(mainPanel, "SETTINGS");
        setTitle("Grocery POS System - Settings");
    }

    public void loginSuccessful() {
        sidebarPanel.setVisible(true);
        userLabel.setText("Welcome, " + SessionManager.getCurrentUserName());
        showDashboardPanel(); // Default to dashboard after login
        ToastNotification.showSuccess(this, "Login successful! Welcome to Grocery POS");
    }

    public void logout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            SessionManager.clearSession();
            sidebarPanel.setVisible(false);
            userLabel.setText("Not Logged In");
            showLoginPanel();
        }
    }

    public void setStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
    }
    
    private void showThemeDialog() {
        ThemeManager.Theme[] themes = ThemeManager.getAllThemes();
        String[] themeNames = new String[themes.length];
        for (int i = 0; i < themes.length; i++) {
            themeNames[i] = themes[i].getDisplayName();
        }
        
        String selectedTheme = (String) JOptionPane.showInputDialog(
            this,
            "Choose a theme:",
            "Theme Selection",
            JOptionPane.QUESTION_MESSAGE,
            null,
            themeNames,
            ThemeManager.getCurrentTheme().getDisplayName()
        );
        
        if (selectedTheme != null) {
            ThemeManager.Theme theme = ThemeManager.getThemeByName(selectedTheme);
            ThemeManager.applyTheme(theme);
            ToastNotification.showInfo(this, "Theme changed to " + selectedTheme);
        }
    }

    public static void main(String[] args) {
        // Initialize theme manager
        ThemeManager.initialize();

        // Set application properties
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Grocery POS");

        SwingUtilities.invokeLater(() -> {
            try {
                new Application().setVisible(true);
            } catch (Exception e) {
                System.err.println("Failed to start application: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Failed to start application: " + e.getMessage(),
                    "Application Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
