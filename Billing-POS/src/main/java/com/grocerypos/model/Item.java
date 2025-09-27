package com.grocerypos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Item model class representing products in the inventory
 */
public class Item {
    private int id;
    private String barcode;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal costPrice;
    private BigDecimal gstPercentage;
    private int stockQuantity;
    private int minStockLevel;
    private String unit;
    private String category;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Item() {
        // Initialize with safe defaults to prevent null pointer exceptions
        this.price = BigDecimal.ZERO;
        this.costPrice = BigDecimal.ZERO;
        this.gstPercentage = BigDecimal.ZERO;
        this.stockQuantity = 0;
        this.minStockLevel = 5;
        this.unit = "pcs";
        this.isActive = true;
    }

    public Item(String barcode, String name, BigDecimal price, BigDecimal gstPercentage) {
        this.barcode = barcode;
        this.name = name;
        this.price = price != null ? price : BigDecimal.ZERO;
        this.gstPercentage = gstPercentage != null ? gstPercentage : BigDecimal.ZERO;
        this.costPrice = BigDecimal.ZERO;
        this.stockQuantity = 0;
        this.minStockLevel = 5;
        this.unit = "pcs";
        this.isActive = true;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(BigDecimal gstPercentage) {
        this.gstPercentage = gstPercentage;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(int minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    // Helper methods
    public boolean isLowStock() {
        return stockQuantity <= minStockLevel;
    }

    public BigDecimal getGstAmount() {
        if (gstPercentage == null || price == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(gstPercentage).divide(BigDecimal.valueOf(100));
    }

    public BigDecimal getPriceWithGst() {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        return price.add(getGstAmount());
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", barcode='" + barcode + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", isActive=" + isActive +
                '}';
    }
}
