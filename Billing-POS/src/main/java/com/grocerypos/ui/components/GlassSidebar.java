package com.grocerypos.ui.components;

import com.grocerypos.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Translucent frosted-glass sidebar with macOS-inspired styling and
 * segmented theme toggle at the bottom.
 */
public class GlassSidebar extends JPanel {
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private final JToggleButton lightToggle = new JToggleButton("Light");
    private final JToggleButton darkToggle = new JToggleButton("Dark");
    private SidebarListener listener;

    public interface SidebarListener {
        void onNavigate(String key);
        void onThemeChanged(ThemeManager.Theme theme);
    }

    public GlassSidebar() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(240, 0));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));

        addNav(nav, "DASHBOARD", "Dashboard");
        addNav(nav, "PRODUCTS", "Products");
        addNav(nav, "CUSTOMERS", "Customers");
        addNav(nav, "CASHIER", "Billing");
        addNav(nav, "REPORTS", "Reports");
        addNav(nav, "SETTINGS", "Settings");

        add(nav, BorderLayout.CENTER);

        JPanel bottom = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        bottom.setOpaque(false);
        bottom.setLayout(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 8, 4, 8));

        JPanel segmented = new JPanel(new GridLayout(1, 2, 1, 0));
        segmented.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        group.add(lightToggle);
        group.add(darkToggle);
        styleSegment(lightToggle, true, false);
        styleSegment(darkToggle, false, true);
        lightToggle.setSelected(ThemeManager.getCurrentTheme() == ThemeManager.Theme.MAC_LIGHT);
        darkToggle.setSelected(ThemeManager.getCurrentTheme() == ThemeManager.Theme.MAC_DARK);

        lightToggle.addActionListener(e -> changeTheme(ThemeManager.Theme.MAC_LIGHT));
        darkToggle.addActionListener(e -> changeTheme(ThemeManager.Theme.MAC_DARK));

        segmented.add(lightToggle);
        segmented.add(darkToggle);
        bottom.add(segmented, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);
    }

    private void changeTheme(ThemeManager.Theme theme) {
        ThemeManager.applyTheme(theme);
        if (listener != null) listener.onThemeChanged(theme);
    }

    private void addNav(JPanel parent, String key, String label) {
        JButton btn = createNavButton(label);
        btn.addActionListener(e -> {
            if (listener != null) listener.onNavigate(key);
        });
        parent.add(btn);
        parent.add(Box.createVerticalStrut(6));
        navButtons.put(key, btn);
    }

    private JButton createNavButton(String text) {
        JButton b = new JButton(text);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.putClientProperty("JButton.buttonType", "toolBarButton");
        b.putClientProperty("JComponent.roundRect", true);
        b.putClientProperty("JComponent.sizeVariant", "regular");
        b.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(0,0,0,20));
                b.setOpaque(true);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setOpaque(false);
                b.repaint();
            }
        });
        return b;
    }

    private void styleSegment(AbstractButton b, boolean left, boolean right) {
        b.putClientProperty("JButton.buttonType", "segmented");
        b.putClientProperty("JButton.segmentPosition", left ? "first" : right ? "last" : "middle");
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }

    public void setListener(SidebarListener listener) {
        this.listener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        // Frosted glass background
        Color base = new Color(255, 255, 255, 160);
        g2.setColor(base);
        g2.fillRoundRect(0, 0, w, h, 24, 24);

        // Inner light
        GradientPaint gp = new GradientPaint(0, 0, new Color(255,255,255,200), 0, h, new Color(255,255,255,120));
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, w, h, 24, 24);

        // Subtle border
        g2.setColor(new Color(0,0,0,25));
        g2.drawRoundRect(0, 0, w-1, h-1, 24, 24);

        g2.dispose();
        super.paintComponent(g);
    }
}


