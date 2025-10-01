package com.grocerypos.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

/**
 * Modern invoice summary card with glass morphism effects
 */
public class InvoiceSummaryCard extends GlassCard {
    private JLabel subtotalLabel;
    private JLabel gstLabel;
    private JLabel discountLabel;
    private JLabel totalLabel;
    private JLabel itemCountLabel;
    private ModernButton checkoutButton;
    private ModernButton discountEditButton;
    
    public InvoiceSummaryCard() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        // Labels with modern typography
        subtotalLabel = createSummaryLabel("Subtotal", "₹0.00");
        gstLabel = createSummaryLabel("GST", "₹0.00");
        discountLabel = createSummaryLabel("Discount", "0%");
        totalLabel = createSummaryLabel("Total", "₹0.00");
        itemCountLabel = createSummaryLabel("Items", "0");
        
        // Checkout button
        checkoutButton = new ModernButton("Checkout");
        checkoutButton.setPreferredSize(new Dimension(200, 50));
        checkoutButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        // Edit discount button for quick access
        discountEditButton = new ModernButton("Edit Discount", new Color(255, 193, 7));
        discountEditButton.setForeground(new Color(40, 40, 40));
        discountEditButton.setPreferredSize(new Dimension(200, 40));
    }
    
    private JLabel createSummaryLabel(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        labelComponent.setForeground(new Color(100, 100, 100));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        valueComponent.setForeground(new Color(50, 50, 50));
        valueComponent.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panel.add(labelComponent, BorderLayout.WEST);
        panel.add(valueComponent, BorderLayout.EAST);
        
        // Return the value component for easy updates
        return valueComponent;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, 16));
        setPreferredSize(new Dimension(280, 0));
        
        // Summary panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setOpaque(false);
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        
        // Add all summary items
        summaryPanel.add(createSummaryRow("Items", itemCountLabel));
        summaryPanel.add(Box.createVerticalStrut(8));
        summaryPanel.add(createSummaryRow("Subtotal", subtotalLabel));
        summaryPanel.add(Box.createVerticalStrut(4));
        summaryPanel.add(createSummaryRow("GST", gstLabel));
        summaryPanel.add(Box.createVerticalStrut(4));
        summaryPanel.add(createSummaryRow("Discount", discountLabel));
        summaryPanel.add(Box.createVerticalStrut(12));
        
        // Total row with special styling
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0, 0, 0, 30)),
            BorderFactory.createEmptyBorder(8, 0, 0, 0)
        ));
        
        JLabel totalLabelText = new JLabel("Total");
        totalLabelText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        totalLabelText.setForeground(new Color(0, 122, 255));
        
        totalLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        totalLabel.setForeground(new Color(0, 122, 255));
        
        totalRow.add(totalLabelText, BorderLayout.WEST);
        totalRow.add(totalLabel, BorderLayout.EAST);
        
        summaryPanel.add(totalRow);
        
        add(summaryPanel, BorderLayout.CENTER);
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.add(discountEditButton);
        actions.add(Box.createVerticalStrut(8));
        actions.add(checkoutButton);
        add(actions, BorderLayout.SOUTH);
    }
    
    private JPanel createSummaryRow(String label, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        labelComponent.setForeground(new Color(100, 100, 100));
        
        row.add(labelComponent, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        
        return row;
    }
    
    public void updateSummary(BigDecimal subtotal, BigDecimal gst, BigDecimal discount, 
                           BigDecimal total, int itemCount) {
        subtotalLabel.setText("₹" + String.format("%.2f", subtotal));
        gstLabel.setText("₹" + String.format("%.2f", gst));
        discountLabel.setText(discount.stripTrailingZeros().toPlainString() + "%");
        totalLabel.setText("₹" + String.format("%.2f", total));
        itemCountLabel.setText(String.valueOf(itemCount));
    }
    
    public void addCheckoutListener(ActionListener listener) {
        checkoutButton.addActionListener(listener);
    }
    
    public void addEditDiscountListener(ActionListener listener) {
        discountEditButton.addActionListener(listener);
    }
    
    public void setCheckoutEnabled(boolean enabled) {
        checkoutButton.setEnabled(enabled);
    }
}
