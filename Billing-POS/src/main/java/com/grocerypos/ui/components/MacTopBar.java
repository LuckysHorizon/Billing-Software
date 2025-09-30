package com.grocerypos.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * macOS-inspired top navigation bar with traffic-light window controls
 * and subtle aqua gradient background.
 */
public class MacTopBar extends JPanel {
    private final JButton closeButton;
    private final JButton minimizeButton;
    private final JButton maximizeButton;
    private final JLabel titleLabel;

    public MacTopBar(String appTitle) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel leftControls = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };
        leftControls.setOpaque(false);
        leftControls.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));

        closeButton = createTrafficLight(new Color(255, 95, 86));
        minimizeButton = createTrafficLight(new Color(255, 189, 46));
        maximizeButton = createTrafficLight(new Color(39, 201, 63));

        leftControls.add(closeButton);
        leftControls.add(minimizeButton);
        leftControls.add(maximizeButton);

        titleLabel = new JLabel(appTitle);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.putClientProperty("FlatLaf.style", "font: medium 14");

        add(leftControls, BorderLayout.WEST);
        add(titleLabel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(0, 44));
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    }

    private JButton createTrafficLight(Color color) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(14, 14));
        btn.setMinimumSize(new Dimension(14, 14));
        btn.setMaximumSize(new Dimension(14, 14));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.putClientProperty("JComponent.sizeVariant", "small");

        btn = new RoundColorButton(color);
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        // Soft aqua-inspired gradient
        Color c1 = new Color(245, 246, 250);
        Color c2 = new Color(255, 255, 255);
        GradientPaint gp = new GradientPaint(0, 0, c1, 0, h, c2);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, w, h, 16, 16);

        // Separator shadow at bottom
        g2.setColor(new Color(0, 0, 0, 20));
        g2.drawLine(0, h - 1, w, h - 1);

        g2.dispose();
        super.paintComponent(g);
    }

    private static class RoundColorButton extends JButton {
        private final Color baseColor;

        public RoundColorButton(Color color) {
            this.baseColor = color;
            setBorder(BorderFactory.createEmptyBorder());
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setPreferredSize(new Dimension(14, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int d = Math.min(getWidth(), getHeight());
            int x = (getWidth() - d) / 2;
            int y = (getHeight() - d) / 2;
            Color inner = baseColor;
            Color outer = baseColor.brighter();
            RadialGradientPaint rg = new RadialGradientPaint(new Point(x + d/2, y + d/2), d/2f,
                    new float[]{0f, 1f}, new Color[]{outer, inner});
            g2.setPaint(rg);
            g2.fillOval(x, y, d, d);
            g2.setColor(new Color(0,0,0,40));
            g2.drawOval(x, y, d-1, d-1);
            g2.dispose();
        }
    }
}


