package com.hei.td5webservice.entity;

public class CreateStockMovement {
    private String unit;
    private double quantity;
    private String type;

    public CreateStockMovement() {}

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}