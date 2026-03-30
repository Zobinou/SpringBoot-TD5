package com.hei.td5webservice.entity;

public class StockMovement {
    private String unit;
    private double value;

    public StockMovement() {}

    public StockMovement(String unit, double value) {
        this.unit = unit;
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
}