package com.grocerypos.ui.components;

import com.grocerypos.dao.ItemDAO;
import com.grocerypos.model.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Modern product sidebar with quick access buttons and categories
 */
public class ProductSidebar extends GlassCard {
    private List<ProductButton> productButtons;
    private ItemDAO itemDAO;
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
        try {
            itemDAO = new ItemDAO();
        } catch (Exception ignored) {}
        reloadFromDatabase();
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

    public void refreshData() {
        removeAll();
        productButtons.clear();
        reloadFromDatabase();
        revalidate();
        repaint();
    }

    private void reloadFromDatabase() {
        if (itemDAO == null) {
            // Fallback: minimal sample list
            String[] fallback = {"Quick Picks", "Groceries"};
            String[][] products = {{"Milk", "Bread", "Eggs"}, {"Sugar", "Rice"}};
            for (int i = 0; i < fallback.length; i++) {
                addCategoryHeader(fallback[i]);
                for (String p : products[i]) addProductButton(p);
                if (i < fallback.length - 1) addSeparator();
            }
            return;
        }

        try {
            List<String> categories = itemDAO.getAllCategories();
            if (categories == null) categories = new ArrayList<>();
            // Ensure Quick Picks appear first
            if (categories.contains("Quick Picks")) {
                categories.remove("Quick Picks");
                categories.add(0, "Quick Picks");
            }
            if (categories.isEmpty()) categories = Collections.singletonList("All");

            for (int i = 0; i < categories.size(); i++) {
                String category = categories.get(i);
                addCategoryHeader(category);
                List<Item> items = "All".equals(category) ? itemDAO.findAll() : itemDAO.findByCategory(category);
                for (Item item : items) {
                    addProductButton(item.getName());
                }
                if (i < categories.size() - 1) addSeparator();
            }
        } catch (SQLException e) {
            // Show minimal fallback if DB fails
            addCategoryHeader("Quick Picks");
            addProductButton("Milk");
            addProductButton("Bread");
        }
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
