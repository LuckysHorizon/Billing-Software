package com.grocerypos.ui.components;

// Using simple Swing animations instead of Trident

import javax.swing.*;
import java.awt.*;
// removed unused ActionEvent/ActionListener imports

/**
 * Toast notification component with animations
 */
public class ToastNotification extends JWindow {
    public enum Type {
        SUCCESS("✅", new Color(34, 197, 94), new Color(240, 253, 244)),
        WARNING("⚠️", new Color(245, 158, 11), new Color(255, 251, 235)),
        ERROR("❌", new Color(239, 68, 68), new Color(254, 242, 242)),
        INFO("ℹ️", new Color(59, 130, 246), new Color(239, 246, 255));
        
        private final String icon;
        private final Color foreground;
        private final Color background;
        
        Type(String icon, Color foreground, Color background) {
            this.icon = icon;
            this.foreground = foreground;
            this.background = background;
        }
        
        public String getIcon() { return icon; }
        public Color getForeground() { return foreground; }
        public Color getBackground() { return background; }
    }
    
    private JLabel iconLabel;
    private JLabel messageLabel;
    private Timer fadeInTimer;
    private Timer fadeOutTimer;
    private Timer autoHideTimer;
    private float currentOpacity = 0.0f;
    
    public ToastNotification(Window parent, String message, Type type) {
        super(parent);
        initializeComponents(message, type);
        setupLayout();
        setupAnimations();
    }
    
    private void initializeComponents(String message, Type type) {
        setLayout(new BorderLayout(10, 10));
        setBackground(type.getBackground());
        // JWindow doesn't support setBorder, we'll handle styling in the panel
        
        iconLabel = new JLabel(type.getIcon());
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        iconLabel.setForeground(type.getForeground());
        
        messageLabel = new JLabel(message);
        messageLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        messageLabel.setForeground(type.getForeground());
        
        // Add close button
        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        closeButton.setForeground(type.getForeground());
        closeButton.setBackground(new Color(0, 0, 0, 0));
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> hideToast());
        
        add(iconLabel, BorderLayout.WEST);
        add(messageLabel, BorderLayout.CENTER);
        add(closeButton, BorderLayout.EAST);
    }
    
    private void setupLayout() {
        JPanel container = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(new Color(255,255,255,220));
                g2.fillRoundRect(0, 0, w, h, 16, 16);
                g2.setColor(new Color(0,0,0,30));
                g2.drawRoundRect(0, 0, w-1, h-1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 10));
        container.add(iconLabel, BorderLayout.WEST);
        container.add(messageLabel, BorderLayout.CENTER);
        // Build content safely; avoid removing components from JWindow directly
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(container, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getParent());
        if (getParent() != null) {
            Point parentLocation = getParent().getLocationOnScreen();
            setLocation(parentLocation.x + getParent().getWidth() - getWidth() - 24,
                       parentLocation.y + 24);
        }
    }
    
    private void setupAnimations() {
        // Fade in animation
        fadeInTimer = new Timer(20, e -> {
            currentOpacity += 0.1f;
            if (currentOpacity >= 1.0f) {
                currentOpacity = 1.0f;
                fadeInTimer.stop();
            }
            setOpacity(currentOpacity);
        });
        
        // Fade out animation
        fadeOutTimer = new Timer(20, e -> {
            currentOpacity -= 0.1f;
            if (currentOpacity <= 0.0f) {
                currentOpacity = 0.0f;
                fadeOutTimer.stop();
                dispose();
            }
            setOpacity(currentOpacity);
        });
        
        // Auto hide timer
        autoHideTimer = new Timer(3000, e -> hideToast());
    }
    
    public void showToast() {
        currentOpacity = 0.0f;
        setOpacity(0.0f);
        setVisible(true);
        fadeInTimer.start();
        autoHideTimer.start();
    }
    
    public void hideToast() {
        autoHideTimer.stop();
        fadeInTimer.stop();
        fadeOutTimer.start();
    }
    
    // Static method to show toast notifications
    public static void show(Window parent, String message, Type type) {
        SwingUtilities.invokeLater(() -> {
            ToastNotification toast = new ToastNotification(parent, message, type);
            toast.showToast();
        });
    }
    
    public static void showSuccess(Window parent, String message) {
        show(parent, message, Type.SUCCESS);
    }
    
    public static void showWarning(Window parent, String message) {
        show(parent, message, Type.WARNING);
    }
    
    public static void showError(Window parent, String message) {
        show(parent, message, Type.ERROR);
    }
    
    public static void showInfo(Window parent, String message) {
        show(parent, message, Type.INFO);
    }
}
