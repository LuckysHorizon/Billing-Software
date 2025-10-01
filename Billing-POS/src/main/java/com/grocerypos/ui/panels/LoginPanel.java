package com.grocerypos.ui.panels;

import com.grocerypos.Application;
import com.grocerypos.dao.UserDAO;
import com.grocerypos.model.User;
import com.grocerypos.util.PasswordUtil;
import com.grocerypos.util.SessionManager;

import javax.swing.*;
import java.awt.*;
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
        setBackground(new Color(245, 245, 245));

        // Gradient sky-like background
        JPanel gradientPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(210, 235, 248), 0, h, new Color(245, 247, 250));
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
        };
        gradientPanel.setOpaque(true);

        // Main content panel (card)
        JPanel mainContentPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                // Frosted white card
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, w, h, 28, 28);
                // Soft border
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 28, 28);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(36, 36, 36, 36));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Title
        JLabel titleLabel = new JLabel("🍏 Grocery POS");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        titleLabel.setForeground(new Color(0, 122, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainContentPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        gbc.gridy = 1;
        mainContentPanel.add(subtitleLabel, gbc);

        // Login form
        JPanel loginFormPanel = new JPanel(new GridBagLayout());
        loginFormPanel.setBackground(new Color(245, 245, 245));
        loginFormPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 10, 10, 10);
        loginFormPanel.add(usernameLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        usernameField.setMaximumSize(new Dimension(280, 40));
        usernameField.putClientProperty("JComponent.roundRect", true);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        loginFormPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        loginFormPanel.add(passwordLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        passwordField.setMaximumSize(new Dimension(280, 40));
        passwordField.putClientProperty("JComponent.roundRect", true);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        loginFormPanel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginButton.setBackground(new Color(0, 122, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        loginButton.putClientProperty("JButton.buttonType", "roundRect");
        loginFormPanel.add(loginButton, gbc);

        // Status label
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        loginFormPanel.add(statusLabel, gbc);

        // Add login form to main content
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainContentPanel.add(loginFormPanel, gbc);

        GridBagConstraints root = new GridBagConstraints();
        root.gridx = 0; root.gridy = 0; root.weightx = 1; root.weighty = 1; root.anchor = GridBagConstraints.CENTER;
        root.fill = GridBagConstraints.NONE;
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setOpaque(false);
        mainContentPanel.setPreferredSize(new Dimension(520, 540));
        cardWrapper.add(mainContentPanel);

        gradientPanel.add(cardWrapper, root);
        add(gradientPanel, BorderLayout.CENTER);

        // Add some padding
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
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
