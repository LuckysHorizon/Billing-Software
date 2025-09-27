package com.grocerypos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Bill model class representing a sales transaction
 */
public class Bill {
    private int id;
    private String billNumber;
    private String customerName;
    private String customerPhone;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private int cashierId;
    private String cashierName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<BillItem> billItems;

    public enum PaymentMethod {
        CASH, CARD, UPI, ONLINE
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, REFUNDED
    }

    // Constructors
    public Bill() {
        this.billItems = new ArrayList<>();
        this.subtotal = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.gstAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.paymentMethod = PaymentMethod.CASH;
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    public Bill(String billNumber, int cashierId) {
        this();
        this.billNumber = billNumber;
        this.cashierId = cashierId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getCashierId() {
        return cashierId;
    }

    public void setCashierId(int cashierId) {
        this.cashierId = cashierId;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<BillItem> getBillItems() {
        return billItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }

    // Helper methods
    public void addBillItem(BillItem billItem) {
        this.billItems.add(billItem);
        recalculateTotals();
    }

    public void removeBillItem(BillItem billItem) {
        this.billItems.remove(billItem);
        recalculateTotals();
    }

    public void recalculateTotals() {
        this.subtotal = BigDecimal.ZERO;
        this.gstAmount = BigDecimal.ZERO;
        
        for (BillItem item : billItems) {
            this.subtotal = this.subtotal.add(item.getLineTotal());
            this.gstAmount = this.gstAmount.add(item.getGstAmount());
        }
        
        this.totalAmount = this.subtotal.add(this.gstAmount).subtract(this.discountAmount);
    }

    public int getTotalItems() {
        return billItems.stream().mapToInt(BillItem::getQuantity).sum();
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", billNumber='" + billNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", totalAmount=" + totalAmount +
                ", paymentMethod=" + paymentMethod +
                ", paymentStatus=" + paymentStatus +
                ", cashierId=" + cashierId +
                '}';
    }
}
