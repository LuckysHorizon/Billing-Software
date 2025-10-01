package com.grocerypos.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern macOS-inspired button with glass morphism effects and smooth animations
 */
public class ModernButton extends JButton {
    private Color primaryColor = new Color(0, 122, 255);
    private Color hoverColor = new Color(0, 100, 220);
    private Color pressedColor = new Color(0, 80, 180);
    private boolean isHovered = false;
    private boolean isPressed = false;
    private Timer hoverTimer;
    private float hoverProgress = 0.0f;
    
    public ModernButton(String text) {
        super(text);
        initializeButton();
    }
    
    public ModernButton(String text, Color color) {
        super(text);
        this.primaryColor = color;
        this.hoverColor = color.darker();
        this.pressedColor = color.darker().darker();
        initializeButton();
    }
    
    private void initializeButton() {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        
        // Smooth hover animation
        hoverTimer = new Timer(16, e -> {
            float target = isHovered ? 1.0f : 0.0f;
            hoverProgress += (target - hoverProgress) * 0.2f;
            if (Math.abs(target - hoverProgress) < 0.01f) {
                hoverProgress = target;
                hoverTimer.stop();
            }
            repaint();
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                hoverTimer.start();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                hoverTimer.start();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        // Determine current color based on state
        Color currentColor = primaryColor;
        if (isPressed) {
            currentColor = pressedColor;
        } else if (isHovered) {
            currentColor = hoverColor;
        }
        
        // Glass morphism effect
        Color glassColor = new Color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), 
            (int)(200 + hoverProgress * 55));
        g2.setColor(glassColor);
        g2.fillRoundRect(0, 0, w, h, 16, 16);
        
        // Gradient overlay for depth
        GradientPaint gradient = new GradientPaint(0, 0, 
            new Color(255, 255, 255, (int)(30 + hoverProgress * 20)), 
            0, h, 
            new Color(255, 255, 255, (int)(10 + hoverProgress * 10)));
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, w, h, 16, 16);
        
        // Subtle border
        g2.setColor(new Color(0, 0, 0, (int)(20 + hoverProgress * 15)));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    public void setPrimaryColor(Color color) {
        this.primaryColor = color;
        this.hoverColor = color.darker();
        this.pressedColor = color.darker().darker();
        repaint();
    }
}
