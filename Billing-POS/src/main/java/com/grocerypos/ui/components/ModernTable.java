package com.grocerypos.ui.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern table with macOS-inspired styling, hover effects, and smooth animations
 */
public class ModernTable extends JTable {
    private Timer hoverTimer;
    private int hoveredRow = -1;
    private float hoverProgress = 0.0f;
    
    public ModernTable(DefaultTableModel model) {
        super(model);
        initializeTable();
    }
    
    private void initializeTable() {
        setOpaque(false);
        setShowGrid(false);
        setRowHeight(40);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        
        // Custom cell renderer for modern styling
        setDefaultRenderer(Object.class, new ModernCellRenderer());
        
        // Header styling
        getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        getTableHeader().setBackground(new Color(248, 249, 250));
        getTableHeader().setForeground(new Color(50, 50, 50));
        getTableHeader().setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        // Hover animation timer
        hoverTimer = new Timer(16, e -> {
            float target = hoveredRow != -1 ? 1.0f : 0.0f;
            hoverProgress += (target - hoverProgress) * 0.15f;
            if (Math.abs(target - hoverProgress) < 0.01f) {
                hoverProgress = target;
                hoverTimer.stop();
            }
            repaint();
        });
        
        // Mouse listener for hover effects
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    hoverTimer.start();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                hoverTimer.start();
            }
        });
        
        // Set alternating row colors
        setAlternateRowColor(new Color(248, 249, 250));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        // Glass background
        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRoundRect(0, 0, w, h, 16, 16);
        
        // Subtle border
        g2.setColor(new Color(0, 0, 0, 20));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    private class ModernCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Modern styling
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            
            if (isSelected) {
                setBackground(new Color(0, 122, 255, 100));
                setForeground(new Color(0, 122, 255));
            } else if (row == hoveredRow) {
                setBackground(new Color(0, 122, 255, (int)(20 * hoverProgress)));
                setForeground(new Color(50, 50, 50));
            } else if (row % 2 == 0) {
                setBackground(new Color(248, 249, 250));
                setForeground(new Color(50, 50, 50));
            } else {
                setBackground(Color.WHITE);
                setForeground(new Color(50, 50, 50));
            }
            
            return c;
        }
    }
    
    public void setAlternateRowColor(Color color) {
        putClientProperty("JTable.alternateRowColor", color);
    }
}
