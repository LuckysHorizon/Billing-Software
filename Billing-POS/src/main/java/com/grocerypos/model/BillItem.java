package com.grocerypos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * BillItem model class representing individual items in a bill
 */
public class BillItem {
    private int id;
    private int billId;
    private int itemId;
    private String itemName;
    private String itemBarcode;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal gstPercentage;
    private BigDecimal gstAmount;
    private BigDecimal lineTotal;
    private LocalDateTime createdAt;

    // Constructors
    public BillItem() {}

    public BillItem(int billId, int itemId, int quantity, BigDecimal unitPrice, BigDecimal gstPercentage) {
        this.billId = billId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.gstPercentage = gstPercentage;
        this.discountPercentage = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        calculateTotals();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemBarcode() {
        return itemBarcode;
    }

    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(BigDecimal gstPercentage) {
        this.gstPercentage = gstPercentage;
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public void calculateTotals() {
        // Initialize null fields with safe defaults
        if (unitPrice == null) unitPrice = BigDecimal.ZERO;
        if (gstPercentage == null) gstPercentage = BigDecimal.ZERO;
        if (discountPercentage == null) discountPercentage = BigDecimal.ZERO;
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
        
        if (quantity <= 0) {
            lineTotal = BigDecimal.ZERO;
            gstAmount = BigDecimal.ZERO;
            return;
        }

        // Calculate subtotal before discount
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        
        // Calculate discount amount
        if (discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = subtotal.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
        } else {
            discountAmount = BigDecimal.ZERO;
        }
        
        // Calculate subtotal after discount
        BigDecimal subtotalAfterDiscount = subtotal.subtract(discountAmount);
        
        // Calculate GST amount
        if (gstPercentage.compareTo(BigDecimal.ZERO) > 0) {
            gstAmount = subtotalAfterDiscount.multiply(gstPercentage).divide(BigDecimal.valueOf(100));
        } else {
            gstAmount = BigDecimal.ZERO;
        }
        
        // Calculate line total
        lineTotal = subtotalAfterDiscount.add(gstAmount);
    }

    public void setQuantityAndRecalculate(int quantity) {
        this.quantity = quantity;
        calculateTotals();
    }

    public void setDiscountPercentageAndRecalculate(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
        calculateTotals();
    }

    @Override
    public String toString() {
        return "BillItem{" +
                "id=" + id +
                ", billId=" + billId +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", lineTotal=" + lineTotal +
                '}';
    }
}
