package com.grocerypos;

// import removed: FlatLightLaf not directly used
import com.grocerypos.ui.components.GlassSidebar;
import com.grocerypos.ui.components.MacTopBar;
import com.grocerypos.database.DBUtil;
import com.grocerypos.ui.panels.*;
import com.grocerypos.ui.components.ToastNotification;
import com.grocerypos.util.SessionManager;
import com.grocerypos.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
// unused imports removed

/**
 * Main application class with CardLayout navigation
 */
public class Application extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel sidebarPanel;
    private MacTopBar topBar;
    // Deprecated user label; status bar provides feedback
    // private JLabel userLabel;
    private JLabel statusLabel;
    
    // Panels
    private LoginPanel loginPanel;
    private AdminPanel adminPanel;
    private CashierPanel cashierPanel;
    private ReportsPanel reportsPanel;
    private DashboardPanel dashboardPanel;
    private SettingsPanel settingsPanel;
    
    // Legacy navigation buttons removed; navigation handled by GlassSidebar

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
        
        // Create top bar and sidebar
        createTopBar();
        createSidebar();
        
        // Add components to frame
        add(topBar, BorderLayout.NORTH);
        add(sidebarPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        
        // Create status bar
        createStatusBar();
    }

    private void createSidebar() {
        GlassSidebar glass = new GlassSidebar();
        glass.setListener(new GlassSidebar.SidebarListener() {
            @Override
            public void onNavigate(String key) {
                switch (key) {
                    case "DASHBOARD":
                        showDashboardPanel();
                        break;
                    case "PRODUCTS":
                        showAdminPanel();
                        break;
                    case "CUSTOMERS":
                        showAdminPanel();
                        break;
                    case "CASHIER":
                        showCashierPanel();
                        break;
                    case "REPORTS":
                        showReportsPanel();
                        break;
                    case "SETTINGS":
                        showSettingsPanel();
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onThemeChanged(com.grocerypos.util.ThemeManager.Theme theme) {
                // Already applied in component; keep hook for future persistence
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        wrapper.add(glass, BorderLayout.CENTER);

        sidebarPanel = wrapper;
        sidebarPanel.setPreferredSize(new Dimension(260, 0));
        sidebarPanel.setVisible(false);
    }

    private void createTopBar() {
        topBar = new MacTopBar("Grocery POS");
    }

    // removed legacy sidebar button builder; replaced by GlassSidebar

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
        // Navigation is provided by GlassSidebar listener; no additional handlers needed here
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
        // Optionally reflect user in status bar
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
            showLoginPanel();
        }
    }

    public void setStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
    }
    
    @SuppressWarnings("unused")
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
