package com.grocerypos.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Reusable frosted glass panel with rounded corners and soft shadow,
 * inspired by macOS Big Sur widgets.
 */
public class GlassCard extends JPanel {
    private int arc = 20;

    public GlassCard() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    }

    public void setArc(int arc) {
        this.arc = arc;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        // Background layers for glass effect
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRoundRect(0, 0, w, h, arc, arc);
        GradientPaint gp = new GradientPaint(0, 0, new Color(255,255,255,230), 0, h, new Color(255,255,255,160));
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        // Soft border
        g2.setColor(new Color(0,0,0,28));
        g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}


