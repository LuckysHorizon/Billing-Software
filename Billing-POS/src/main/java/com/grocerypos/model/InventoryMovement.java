package com.grocerypos.model;

import java.time.LocalDateTime;

/**
 * InventoryMovement model class for tracking stock changes
 */
public class InventoryMovement {
    private int id;
    private int itemId;
    private String itemName;
    private MovementType movementType;
    private int quantityChange;
    private int previousStock;
    private int newStock;
    private Integer referenceId;
    private ReferenceType referenceType;
    private String notes;
    private int userId;
    private String userName;
    private LocalDateTime createdAt;

    public enum MovementType {
        PURCHASE, SALE, ADJUSTMENT, RETURN, DAMAGE
    }

    public enum ReferenceType {
        BILL, PURCHASE, ADJUSTMENT
    }

    // Constructors
    public InventoryMovement() {}

    public InventoryMovement(int itemId, MovementType movementType, int quantityChange, 
                           int previousStock, int newStock, int userId) {
        this.itemId = itemId;
        this.movementType = movementType;
        this.quantityChange = quantityChange;
        this.previousStock = previousStock;
        this.newStock = newStock;
        this.userId = userId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public int getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(int quantityChange) {
        this.quantityChange = quantityChange;
    }

    public int getPreviousStock() {
        return previousStock;
    }

    public void setPreviousStock(int previousStock) {
        this.previousStock = previousStock;
    }

    public int getNewStock() {
        return newStock;
    }

    public void setNewStock(int newStock) {
        this.newStock = newStock;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "InventoryMovement{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", movementType=" + movementType +
                ", quantityChange=" + quantityChange +
                ", previousStock=" + previousStock +
                ", newStock=" + newStock +
                ", referenceId=" + referenceId +
                ", referenceType=" + referenceType +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
}
