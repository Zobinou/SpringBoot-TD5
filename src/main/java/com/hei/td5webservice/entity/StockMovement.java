package com.hei.td5webservice.entity;

import java.time.Instant;

public class StockMovement {
    private int id;
    private Instant createdAt;
    private String unit;
    private double quantity;
    private String type;

    public StockMovement() {}

    public StockMovement(int id, Instant createdAt, String unit, double quantity, String type) {
        this.id = id;
        this.createdAt = createdAt;
        this.unit = unit;
        this.quantity = quantity;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}