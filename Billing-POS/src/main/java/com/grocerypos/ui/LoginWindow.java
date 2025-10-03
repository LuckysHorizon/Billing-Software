package com.grocerypos.ui;

import com.grocerypos.dao.UserDAO;
import com.grocerypos.model.User;
import com.grocerypos.util.PasswordUtil;
import com.grocerypos.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.prefs.Preferences;

/**
 * macOS glass-morphism login window with Remember Me and Forgot Password stub
 */
public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberMe;
    private JButton loginButton;
    private JButton forgotPasswordButton;
    private JLabel statusLabel;
    private final Preferences prefs = Preferences.userRoot().node("com.grocerypos.login");

    public LoginWindow() {
        setTitle("Grocery POS - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 360);
        setLocationRelativeTo(null);
        setResizable(false);
        ThemeManager.applyTheme(ThemeManager.getCurrentTheme());

        initUI();
        loadRemembered();
    }

    private void initUI() {
        JPanel root = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240,242,247));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(24,24,24,24));

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(0,0,0,30));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Grocery POS", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        title.setForeground(new Color(0,122,255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        card.add(title, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        card.add(new JLabel("Username"), gbc);
        usernameField = new JTextField(18);
        gbc.gridx = 1;
        card.add(usernameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        card.add(new JLabel("Password"), gbc);
        passwordField = new JPasswordField(18);
        gbc.gridx = 1;
        card.add(passwordField, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        rememberMe = new JCheckBox("Remember me");
        card.add(rememberMe, gbc);

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 1;
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0,122,255));
        loginButton.setForeground(Color.WHITE);
        card.add(loginButton, gbc);

        gbc.gridx = 1;
        forgotPasswordButton = new JButton("Forgot Password?");
        card.add(forgotPasswordButton, gbc);

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        card.add(statusLabel, gbc);

        root.add(card, new GridBagConstraints());
        setContentPane(root);

        loginButton.addActionListener(e -> doLogin());
        forgotPasswordButton.addActionListener(e -> onForgot());
        getRootPane().setDefaultButton(loginButton);
    }

    private void loadRemembered() {
        String savedUser = prefs.get("username", "");
        if (!savedUser.isEmpty()) {
            usernameField.setText(savedUser);
            rememberMe.setSelected(true);
        }
    }

    private void onForgot() {
        JOptionPane.showMessageDialog(this, "Please contact admin to reset your password.", "Forgot Password", JOptionPane.INFORMATION_MESSAGE);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Enter username and password");
            return;
        }
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findByUsername(username);
            if (user != null && PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                if (rememberMe.isSelected()) {
                    prefs.put("username", username);
                } else {
                    prefs.remove("username");
                }
                com.grocerypos.util.SessionManager.setCurrentUser(user);
                dispose();
                new com.grocerypos.Application().setVisible(true);
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Invalid credentials");
            }
        } catch (SQLException ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Database error: " + ex.getMessage());
        }
    }
}


