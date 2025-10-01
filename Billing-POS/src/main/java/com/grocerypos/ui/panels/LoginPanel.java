package com.grocerypos.ui.panels;

import com.grocerypos.Application;
import com.grocerypos.dao.UserDAO;
import com.grocerypos.model.User;
import com.grocerypos.util.PasswordUtil;
import com.grocerypos.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 * Login panel with modern design
 */
public class LoginPanel extends JPanel {
    private Application parent;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginPanel(Application parent) {
        this.parent = parent;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        statusLabel = new JLabel("Enter your credentials to login");

        // Set component properties
        usernameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        passwordField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        loginButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statusLabel.setForeground(Color.BLUE);

        // Set button properties
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);

        // Set field properties
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Main content panel
        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Title
        JLabel titleLabel = new JLabel("Grocery POS System");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        titleLabel.setForeground(new Color(0, 120, 215));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainContentPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Billing & Inventory Management");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        mainContentPanel.add(subtitleLabel, gbc);

        // Login form
        JPanel loginFormPanel = new JPanel(new GridBagLayout());
        loginFormPanel.setBackground(Color.WHITE);
        loginFormPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 10, 10, 10);
        loginFormPanel.add(usernameLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        loginFormPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        loginFormPanel.add(passwordLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        loginFormPanel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginFormPanel.add(loginButton, gbc);

        // Status label
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        loginFormPanel.add(statusLabel, gbc);

        // Add login form to main content
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainContentPanel.add(loginFormPanel, gbc);

        add(mainContentPanel, BorderLayout.CENTER);

        // Add some padding
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void setupEventHandlers() {
        loginButton.addActionListener(e -> performLogin());

        // Enter key handling
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };

        usernameField.addKeyListener(enterKeyAdapter);
        passwordField.addKeyListener(enterKeyAdapter);

        // Focus handling
        usernameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                usernameField.selectAll();
            }
        });

        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordField.selectAll();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            statusLabel.setForeground(Color.RED);
            return;
        }

        // Disable login button to prevent multiple clicks
        loginButton.setEnabled(false);
        statusLabel.setText("Authenticating...");
        statusLabel.setForeground(Color.BLUE);

        // Perform authentication in a separate thread
        SwingUtilities.invokeLater(() -> {
            try {
                UserDAO userDAO = new UserDAO();
                User user = userDAO.findByUsername(username);

                // Debug output
                System.out.println("Login attempt for username: " + username);
                if (user != null) {
                    System.out.println("User found: " + user.getUsername());
                    System.out.println("Password hash: " + user.getPasswordHash());
                    System.out.println("Password verification: " + PasswordUtil.verifyPassword(password, user.getPasswordHash()));
                } else {
                    System.out.println("User not found!");
                }

                if (user != null && PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                    // Login successful
                    statusLabel.setText("Login successful!");
                    statusLabel.setForeground(Color.GREEN);
                    
                    // Store current user in session
                    SessionManager.setCurrentUser(user);
                    
                    // Notify parent application
                    parent.loginSuccessful();
                } else {
                    // Login failed
                    statusLabel.setText("Invalid username or password");
                    statusLabel.setForeground(Color.RED);
                    passwordField.setText("");
                    passwordField.requestFocus();
                }
            } catch (SQLException e) {
                statusLabel.setText("Database error: " + e.getMessage());
                statusLabel.setForeground(Color.RED);
                System.err.println("Login error: " + e.getMessage());
            } finally {
                loginButton.setEnabled(true);
            }
        });
    }
}
