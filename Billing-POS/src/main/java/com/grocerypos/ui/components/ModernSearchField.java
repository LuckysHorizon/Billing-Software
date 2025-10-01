package com.grocerypos.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Modern search field with macOS-inspired styling and live search suggestions
 */
public class ModernSearchField extends JTextField {
    private boolean isFocused = false;
    private String placeholderText = "Search products...";
    private JPopupMenu suggestionMenu;
    private Timer searchTimer;
    
    public ModernSearchField() {
        initializeField();
    }
    
    public ModernSearchField(String placeholder) {
        this.placeholderText = placeholder;
        initializeField();
    }
    
    private void initializeField() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        setForeground(new Color(50, 50, 50));
        setPreferredSize(new Dimension(360, 40));
        
        // Focus listener for animations
        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                isFocused = true;
                repaint();
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
        
        // Debounced search timer
        searchTimer = new Timer(300, e -> {
            searchTimer.stop();
            performSearch();
        });
        
        getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                searchTimer.restart();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                searchTimer.restart();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                searchTimer.restart();
            }
        });
        
        // Initialize suggestion menu
        suggestionMenu = new JPopupMenu();
        suggestionMenu.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        // Background with glass effect
        Color bgColor = isFocused ? 
            new Color(255, 255, 255, 245) : 
            new Color(255, 255, 255, 215);
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, w, h, 12, 12);
        
        // Soft drop shadow for premium feel
        g2.setColor(new Color(0, 0, 0, isFocused ? 40 : 20));
        g2.fillRoundRect(2, 3, w, h, 12, 12);
        
        // Focus border
        if (isFocused) {
            g2.setColor(new Color(0, 122, 255, 150));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, w - 3, h - 3, 12, 12);
        } else {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
        }
        
        g2.dispose();
        
        // Draw text
        super.paintComponent(g);
        
        // Draw placeholder if empty
        if (getText().isEmpty() && !isFocused) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(150, 150, 150));
            g2d.setFont(getFont());
            FontMetrics fm = g2d.getFontMetrics();
            int y = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(placeholderText, 16, y);
            g2d.dispose();
        }
    }
    
    private void performSearch() {
        String query = getText().trim();
        if (query.length() >= 2) {
            // This would typically trigger a search in the parent component
            // For now, we'll just show a placeholder suggestion
            showSuggestions(query);
        } else {
            hideSuggestions();
        }
    }
    
    private void showSuggestions(String query) {
        suggestionMenu.removeAll();
        
        // Add sample suggestions (in real implementation, this would come from search results)
        String[] suggestions = {
            "Apple iPhone 15 Pro",
            "Samsung Galaxy S24",
            "MacBook Pro M3",
            "iPad Air 5th Gen"
        };
        
        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().contains(query.toLowerCase())) {
                JMenuItem item = new JMenuItem(suggestion);
                item.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
                item.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
                item.addActionListener(e -> {
                    setText(suggestion);
                    hideSuggestions();
                    transferFocus();
                });
                suggestionMenu.add(item);
            }
        }
        
        if (suggestionMenu.getComponentCount() > 0) {
            suggestionMenu.show(this, 0, getHeight());
        }
    }
    
    private void hideSuggestions() {
        suggestionMenu.setVisible(false);
    }
    
    @Override
    public void setText(String text) {
        super.setText(text);
        hideSuggestions();
    }
}
