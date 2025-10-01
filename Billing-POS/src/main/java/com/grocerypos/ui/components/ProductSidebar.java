package com.grocerypos.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern product sidebar with quick access buttons and categories
 */
public class ProductSidebar extends GlassCard {
    private List<ProductButton> productButtons;
    private ProductSidebarListener listener;
    
    public interface ProductSidebarListener {
        void onProductSelected(String productName);
        void onCategorySelected(String category);
    }
    
    public ProductSidebar() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        productButtons = new ArrayList<>();
        
        // Sample categories and products (in real implementation, this would come from database)
        String[] categories = {"Electronics", "Groceries", "Clothing", "Books", "Home & Garden"};
        String[][] products = {
            {"iPhone 15", "Samsung Galaxy", "MacBook Pro", "iPad Air"},
            {"Milk", "Bread", "Eggs", "Cheese"},
            {"T-Shirt", "Jeans", "Sweater", "Shoes"},
            {"Novel", "Textbook", "Magazine", "Comic"},
            {"Plant", "Tool", "Furniture", "Decor"}
        };
        
        for (int i = 0; i < categories.length; i++) {
            addCategoryHeader(categories[i]);
            for (String product : products[i]) {
                addProductButton(product);
            }
            if (i < categories.length - 1) {
                addSeparator();
            }
        }
    }
    
    private void setupLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 0));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }
    
    private void addCategoryHeader(String category) {
        JLabel header = new JLabel(category);
        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        header.setForeground(new Color(0, 122, 255));
        header.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(header);
    }
    
    private void addProductButton(String productName) {
        ProductButton button = new ProductButton(productName);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        button.addActionListener(e -> {
            if (listener != null) {
                listener.onProductSelected(productName);
            }
        });
        productButtons.add(button);
        add(button);
        add(Box.createVerticalStrut(4));
    }
    
    private void addSeparator() {
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(0, 0, 0, 30));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(separator);
        add(Box.createVerticalStrut(8));
    }
    
    public void setListener(ProductSidebarListener listener) {
        this.listener = listener;
    }
    
    private static class ProductButton extends JButton {
        private boolean isHovered = false;
        
        public ProductButton(String text) {
            super(text);
            initializeButton();
        }
        
        private void initializeButton() {
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            setForeground(new Color(50, 50, 50));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
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
            
            if (isHovered) {
                g2.setColor(new Color(0, 122, 255, 30));
                g2.fillRoundRect(0, 0, w, h, 8, 8);
            }
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
