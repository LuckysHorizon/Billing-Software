package com.grocerypos.ui.components;

// Using simple Swing animations instead of Trident

import javax.swing.*;
import java.awt.*;

/**
 * Animated loading spinner component
 */
public class LoadingSpinner extends JComponent {
    private int rotation = 0;
    private Timer rotationTimer;
    private Color spinnerColor = new Color(59, 130, 246);
    private int size = 40;
    private int strokeWidth = 4;
    
    public LoadingSpinner() {
        initializeAnimations();
    }
    
    public LoadingSpinner(int size) {
        this.size = size;
        initializeAnimations();
    }
    
    public LoadingSpinner(int size, Color color) {
        this.size = size;
        this.spinnerColor = color;
        initializeAnimations();
    }
    
    private void initializeAnimations() {
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
        
        // Create rotation animation
        rotationTimer = new Timer(50, e -> {
            rotation = (rotation + 10) % 360;
            repaint();
        });
    }
    
    public void start() {
        rotationTimer.start();
    }
    
    public void stop() {
        rotationTimer.stop();
    }
    
    public void setRotation(int rotation) {
        this.rotation = rotation;
        repaint();
    }
    
    public int getRotation() {
        return rotation;
    }
    
    public void setSpinnerColor(Color color) {
        this.spinnerColor = color;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 2 - strokeWidth;
        
        // Draw spinner arc
        g2d.setColor(spinnerColor);
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        int startAngle = rotation;
        int arcAngle = 270; // 3/4 circle
        
        g2d.drawArc(centerX - radius, centerY - radius, 
                   radius * 2, radius * 2, 
                   startAngle, arcAngle);
        
        g2d.dispose();
    }
    
    // Static method to show loading dialog
    public static JDialog showLoadingDialog(Window parent, String message) {
        JDialog dialog = new JDialog(parent, "Loading", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setUndecorated(true);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);
        
        LoadingSpinner spinner = new LoadingSpinner(50);
        spinner.start();
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(spinner, BorderLayout.CENTER);
        panel.add(messageLabel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        
        return dialog;
    }
}
