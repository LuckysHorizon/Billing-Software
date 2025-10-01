package com.grocerypos.ui.panels;

import com.grocerypos.Application;
import com.grocerypos.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Settings panel for application configuration
 */
public class SettingsPanel extends JPanel {
    private Application parent;
    private JLabel userInfoLabel;
    private JLabel roleLabel;
    private JButton changePasswordButton;
    private JButton logoutButton;
    private JLabel appInfoLabel;

    public SettingsPanel(Application parent) {
        this.parent = parent;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        userInfoLabel = new JLabel("User: " + SessionManager.getCurrentUserName());
        userInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        userInfoLabel.setForeground(new Color(0, 120, 215));
        
        roleLabel = new JLabel("Role: " + SessionManager.getCurrentUserRole());
        roleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        roleLabel.setForeground(Color.GRAY);
        
        changePasswordButton = new JButton("Change Password");
        changePasswordButton.setBackground(new Color(0, 120, 215));
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setFocusPainted(false);
        changePasswordButton.setPreferredSize(new Dimension(150, 35));
        
        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(100, 35));
        
        appInfoLabel = new JLabel("<html><center>" +
            "<h2>Grocery POS System</h2>" +
            "<p>Version 1.0.0</p>" +
            "<p>Built with Java Swing + MySQL</p>" +
            "<p>Features: Billing, Inventory, Reports</p>" +
            "</center></html>");
        appInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        appInfoLabel.setForeground(Color.GRAY);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        
        // Main content panel
        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // User info section
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
        userPanel.setBackground(Color.WHITE);
        
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        userInfoPanel.setBackground(Color.WHITE);
        userInfoPanel.add(userInfoLabel);
        userInfoPanel.add(roleLabel);
        
        JPanel userButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        userButtonPanel.setBackground(Color.WHITE);
        userButtonPanel.add(changePasswordButton);
        userButtonPanel.add(logoutButton);
        
        userPanel.add(userInfoPanel, BorderLayout.CENTER);
        userPanel.add(userButtonPanel, BorderLayout.SOUTH);
        
        // Application info section
        JPanel appPanel = new JPanel(new BorderLayout());
        appPanel.setBorder(BorderFactory.createTitledBorder("Application Information"));
        appPanel.setBackground(Color.WHITE);
        appPanel.add(appInfoLabel, BorderLayout.CENTER);
        
        // Add sections to main panel
        gbc.gridx = 0; gbc.gridy = 0;
        mainContentPanel.add(userPanel, gbc);
        
        gbc.gridy = 1;
        mainContentPanel.add(appPanel, gbc);
        
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        changePasswordButton.addActionListener(e -> changePassword());
        logoutButton.addActionListener(e -> logout());
    }

    private void changePassword() {
        JOptionPane.showMessageDialog(this, "Change password functionality will be implemented soon!", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        parent.setStatus("Change password functionality coming soon");
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            parent.logout();
        }
    }
}
