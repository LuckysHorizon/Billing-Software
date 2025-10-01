package com.grocerypos.ui;

import com.grocerypos.model.Bill;
import com.grocerypos.ui.components.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Modern payment dialog with glass morphism effects and smooth animations
 */
public class PaymentDialog extends JDialog {
    private JLabel totalLabel;
    private ModernSearchField amountReceivedField;
    private JLabel changeLabel;
    private JComboBox<Bill.PaymentMethod> paymentMethodCombo;
    private JToggleButton cashToggle;
    private JToggleButton cardToggle;
    private JToggleButton upiToggle;
    private ModernButton processButton;
    private ModernButton cancelButton;
    
    private BigDecimal totalAmount;
    private boolean paymentSuccessful = false;
    private Bill.PaymentMethod selectedPaymentMethod;

    public PaymentDialog(JFrame parent, BigDecimal totalAmount) {
        super(parent, "Payment", true);
        this.totalAmount = totalAmount;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDialog();
    }

    private void initializeComponents() {
        // Total amount label with modern styling
        totalLabel = new JLabel("Total Amount: ₹" + String.format("%.2f", totalAmount));
        totalLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        totalLabel.setForeground(new Color(0, 122, 255));
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Modern amount input field
        amountReceivedField = new ModernSearchField("Enter amount received");
        amountReceivedField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        
        // Change label
        changeLabel = new JLabel("Change: ₹0.00");
        changeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        changeLabel.setForeground(new Color(34, 197, 94));
        changeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Payment method combo
        paymentMethodCombo = new JComboBox<>(Bill.PaymentMethod.values());
        paymentMethodCombo.setSelectedItem(Bill.PaymentMethod.CASH);
        paymentMethodCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        // Segmented payment buttons with modern styling
        cashToggle = createSegmentedButton("Cash", true, false);
        cardToggle = createSegmentedButton("Card", false, false);
        upiToggle = createSegmentedButton("UPI", false, true);
        
        ButtonGroup pmGroup = new ButtonGroup();
        pmGroup.add(cashToggle);
        pmGroup.add(cardToggle);
        pmGroup.add(upiToggle);
        cashToggle.setSelected(true);
        
        // Modern action buttons
        processButton = new ModernButton("Process Payment", new Color(34, 197, 94));
        processButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        processButton.setPreferredSize(new Dimension(200, 50));
        
        cancelButton = new ModernButton("Cancel", new Color(220, 53, 69));
        cancelButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        cancelButton.setPreferredSize(new Dimension(120, 40));
    }
    
    private JToggleButton createSegmentedButton(String text, boolean left, boolean right) {
        JToggleButton button = new JToggleButton(text);
        button.putClientProperty("JButton.buttonType", "segmented");
        button.putClientProperty("JButton.segmentPosition", left ? "first" : right ? "last" : "middle");
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(240, 242, 247));
        
        // Main content panel with glass card
        GlassCard mainCard = new GlassCard();
        mainCard.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16);
        
        // Total amount
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainCard.add(totalLabel, gbc);
        
        // Payment method segmented control
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel paymentLabel = new JLabel("Payment Method:");
        paymentLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        paymentLabel.setForeground(new Color(100, 100, 100));
        mainCard.add(paymentLabel, gbc);
        
        JPanel segmented = new JPanel(new GridLayout(1, 3, 2, 0));
        segmented.setOpaque(false);
        segmented.add(cashToggle);
        segmented.add(cardToggle);
        segmented.add(upiToggle);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainCard.add(segmented, gbc);
        
        // Amount received
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        JLabel amountLabel = new JLabel("Amount Received:");
        amountLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        amountLabel.setForeground(new Color(100, 100, 100));
        mainCard.add(amountLabel, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainCard.add(amountReceivedField, gbc);
        
        // Change
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        mainCard.add(changeLabel, gbc);
        
        add(mainCard, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 16));
        buttonPanel.setOpaque(false);
        buttonPanel.add(cancelButton);
        buttonPanel.add(processButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // Amount received field listener
        amountReceivedField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateChange(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateChange(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateChange(); }
        });
        
        // Segmented selection sync to combo
        cashToggle.addActionListener(e -> {
            paymentMethodCombo.setSelectedItem(Bill.PaymentMethod.CASH);
            updateChange();
        });
        cardToggle.addActionListener(e -> {
            paymentMethodCombo.setSelectedItem(Bill.PaymentMethod.CARD);
            updateChange();
        });
        upiToggle.addActionListener(e -> {
            paymentMethodCombo.setSelectedItem(Bill.PaymentMethod.UPI);
            updateChange();
        });
        
        // Payment method change
        paymentMethodCombo.addActionListener(e -> {
            Bill.PaymentMethod method = (Bill.PaymentMethod) paymentMethodCombo.getSelectedItem();
            if (method == Bill.PaymentMethod.CASH) {
                amountReceivedField.setEnabled(true);
                amountReceivedField.setText("");
                cashToggle.setSelected(true);
            } else {
                amountReceivedField.setEnabled(false);
                amountReceivedField.setText(String.format("%.2f", totalAmount));
                updateChange();
                if (method == Bill.PaymentMethod.CARD) cardToggle.setSelected(true);
                if (method == Bill.PaymentMethod.UPI) upiToggle.setSelected(true);
            }
        });
        
        // Process button
        processButton.addActionListener(e -> processPayment());
        
        // Cancel button
        cancelButton.addActionListener(e -> {
            paymentSuccessful = false;
            dispose();
        });
        
        // Enter key on amount field
        amountReceivedField.addActionListener(e -> processPayment());
    }

    private void setupDialog() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Apply glass effect
        getRootPane().putClientProperty("JComponent.roundRect", true);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add smooth animations
        AnimationUtils.fadeIn(this, 200);
        
        // Keyboard shortcuts
        getRootPane().setDefaultButton(processButton);
        getRootPane().registerKeyboardAction(e -> dispose(),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Set focus to amount field
        SwingUtilities.invokeLater(() -> amountReceivedField.requestFocus());
    }

    private void updateChange() {
        try {
            if (paymentMethodCombo.getSelectedItem() == Bill.PaymentMethod.CASH) {
                String amountText = amountReceivedField.getText().trim();
                if (!amountText.isEmpty()) {
                    BigDecimal amountReceived = new BigDecimal(amountText);
                    BigDecimal change = amountReceived.subtract(totalAmount);
                    changeLabel.setText("Change: ₹" + String.format("%.2f", change));
                    processButton.setEnabled(amountReceived.compareTo(totalAmount) >= 0);
                    
                    // Color coding for change
                    if (change.compareTo(BigDecimal.ZERO) >= 0) {
                        changeLabel.setForeground(new Color(34, 197, 94)); // Green
                    } else {
                        changeLabel.setForeground(new Color(239, 68, 68)); // Red
                    }
                } else {
                    changeLabel.setText("Change: ₹0.00");
                    changeLabel.setForeground(new Color(100, 100, 100));
                    processButton.setEnabled(false);
                }
            } else {
                changeLabel.setText("Change: ₹0.00");
                changeLabel.setForeground(new Color(100, 100, 100));
                processButton.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("Change: Invalid amount");
            changeLabel.setForeground(new Color(239, 68, 68));
            processButton.setEnabled(false);
        }
    }

    private void processPayment() {
        try {
            Bill.PaymentMethod method = (Bill.PaymentMethod) paymentMethodCombo.getSelectedItem();
            
            if (method == Bill.PaymentMethod.CASH) {
                String amountText = amountReceivedField.getText().trim();
                if (amountText.isEmpty()) {
                    ToastNotification.showWarning(this, "Please enter amount received");
                    return;
                }
                
                BigDecimal amountReceived = new BigDecimal(amountText);
                if (amountReceived.compareTo(totalAmount) < 0) {
                    ToastNotification.showWarning(this, "Amount received is less than total amount");
                    return;
                }
            }
            
            selectedPaymentMethod = method;
            paymentSuccessful = true;
            
            // Show success animation
            ToastNotification.showSuccess(this, "Payment processed successfully!");
            
            // Small delay before closing
            Timer timer = new Timer(1000, e -> dispose());
            timer.setRepeats(false);
            timer.start();
            
        } catch (NumberFormatException e) {
            ToastNotification.showError(this, "Invalid amount format");
        }
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    public Bill.PaymentMethod getPaymentMethod() {
        return selectedPaymentMethod;
    }
}