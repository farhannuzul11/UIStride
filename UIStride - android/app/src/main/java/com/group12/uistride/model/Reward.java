package com.group12.uistride.model;

public class Reward {
    private Long id;
    private String name;
    private int pointsRequired;
    private String description;
    private int quantity;
    private String createdAt;

    public Reward(Long id, String name, int pointsRequired, String description, int quantity, String createdAt) {
        this.id = id;
        this.name = name;
        this.pointsRequired = pointsRequired;
        this.description = description;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPointsRequired() {
        return pointsRequired;
    }
    public void setPointsRequired(int pointsRequired) {
        this.pointsRequired = pointsRequired;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


}