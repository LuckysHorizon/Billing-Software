package com.grocerypos.ui;

import com.grocerypos.dao.UserDAO;
import com.grocerypos.model.User;
import com.grocerypos.util.PasswordUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 * Login frame for user authentication
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    public LoginFrame() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setTitle("Grocery POS - Login");
        
        // Prefer showing the newer LoginWindow; keep this for backward compatibility
        
        // Debug output
        System.out.println("Login window created and should be visible");
    }

    private void initializeComponents() {
        // Create components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        statusLabel = new JLabel("Enter your credentials to login");

        // Set component properties
        usernameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        passwordField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        loginButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        cancelButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statusLabel.setForeground(Color.BLUE);

        // Set button properties
        loginButton.setPreferredSize(new Dimension(120, 36));
        cancelButton.setPreferredSize(new Dimension(120, 36));
        loginButton.putClientProperty("JButton.buttonType", "roundRect");
        cancelButton.putClientProperty("JButton.buttonType", "roundRect");
        loginButton.putClientProperty("JComponent.roundRect", true);
        cancelButton.putClientProperty("JComponent.roundRect", true);
        loginButton.putClientProperty("JButton.focusedBackground", new Color(0,122,255));
        loginButton.setBackground(new Color(0,122,255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);

        // Set field properties
        usernameField.putClientProperty("JComponent.roundRect", true);
        passwordField.putClientProperty("JComponent.roundRect", true);
        usernameField.putClientProperty("JTextComponent.showClearButton", true);
        passwordField.putClientProperty("JTextComponent.showClearButton", true);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Create main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Grocery POS System");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 120, 215));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Billing & Inventory Management");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(usernameLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(passwordLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        // Status label
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(statusLabel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Add some padding
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void setupEventHandlers() {
        // Login button action
        loginButton.addActionListener(e -> performLogin());

        // Cancel button action
        cancelButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Exit Application",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

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
                    com.grocerypos.util.SessionManager.setCurrentUser(user);
                    
                    // Close login frame and show main window
                    SwingUtilities.invokeLater(() -> {
                        dispose();
                        SwingUtilities.invokeLater(() -> {
                            try {
                                // Prefer modern Application shell to avoid duplicate windows
                                com.grocerypos.Application app = new com.grocerypos.Application();
                                app.setVisible(true);
                            } catch (Exception e) {
                                System.err.println("Failed to show main window: " + e.getMessage());
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(null, 
                                    "Failed to show main window: " + e.getMessage(),
                                    "Application Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    });
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
