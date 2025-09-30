package com.grocerypos.ui;

import com.grocerypos.model.Bill;

import javax.swing.*;
import java.awt.*;
// removed unused ActionEvent/ActionListener imports
import java.math.BigDecimal;

/**
 * Payment dialog for checkout process
 */
public class PaymentDialog extends JDialog {
    private JLabel totalLabel;
    private JTextField amountReceivedField;
    private JLabel changeLabel;
    private JComboBox<Bill.PaymentMethod> paymentMethodCombo;
    private JToggleButton cashToggle;
    private JToggleButton cardToggle;
    private JToggleButton upiToggle;
    private JButton processButton;
    private JButton cancelButton;
    
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
        totalLabel = new JLabel("Total Amount: ₹" + String.format("%.2f", totalAmount));
        totalLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        totalLabel.setForeground(new Color(0, 120, 215));
        
        amountReceivedField = new JTextField(15);
        amountReceivedField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        amountReceivedField.setToolTipText("Enter amount received from customer");
        
        changeLabel = new JLabel("Change: ₹0.00");
        changeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        paymentMethodCombo = new JComboBox<>(Bill.PaymentMethod.values());
        paymentMethodCombo.setSelectedItem(Bill.PaymentMethod.CASH);
        paymentMethodCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        // Segmented payment buttons
        cashToggle = new JToggleButton("Cash");
        cardToggle = new JToggleButton("Card");
        upiToggle = new JToggleButton("UPI");
        ButtonGroup pmGroup = new ButtonGroup();
        pmGroup.add(cashToggle);
        pmGroup.add(cardToggle);
        pmGroup.add(upiToggle);
        cashToggle.setSelected(true);
        styleSegment(cashToggle, true, false);
        styleSegment(cardToggle, false, false);
        styleSegment(upiToggle, false, true);
        
        processButton = new JButton("Checkout");
        processButton.putClientProperty("JButton.buttonType", "roundRect");
        processButton.setBackground(new Color(0, 122, 255));
        processButton.setForeground(Color.WHITE);
        processButton.setFocusPainted(false);
        processButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Total amount
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(totalLabel, gbc);
        
        // Payment method segmented control
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Payment Method:"), gbc);
        JPanel segmented = new JPanel(new GridLayout(1, 3, 1, 0));
        segmented.setOpaque(false);
        segmented.add(cashToggle);
        segmented.add(cardToggle);
        segmented.add(upiToggle);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(segmented, gbc);
        
        // Amount received
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Amount Received:"), gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(amountReceivedField, gbc);
        
        // Change
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Change:"), gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(changeLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
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
        setSize(420, 320);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Set focus to amount field
        amountReceivedField.requestFocus();
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
                } else {
                    changeLabel.setText("Change: ₹0.00");
                    processButton.setEnabled(false);
                }
            } else {
                changeLabel.setText("Change: ₹0.00");
                processButton.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("Change: Invalid amount");
            processButton.setEnabled(false);
        }
    }

    private void processPayment() {
        try {
            Bill.PaymentMethod method = (Bill.PaymentMethod) paymentMethodCombo.getSelectedItem();
            
            if (method == Bill.PaymentMethod.CASH) {
                String amountText = amountReceivedField.getText().trim();
                if (amountText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter amount received", "Input Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                BigDecimal amountReceived = new BigDecimal(amountText);
                if (amountReceived.compareTo(totalAmount) < 0) {
                    JOptionPane.showMessageDialog(this, "Amount received is less than total amount", "Insufficient Amount", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            selectedPaymentMethod = method;
            paymentSuccessful = true;
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    public Bill.PaymentMethod getPaymentMethod() {
        return selectedPaymentMethod;
    }

    private void styleSegment(AbstractButton b, boolean left, boolean right) {
        b.putClientProperty("JButton.buttonType", "segmented");
        b.putClientProperty("JButton.segmentPosition", left ? "first" : right ? "last" : "middle");
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }
}
