package com.example.ui.components;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class Task {
    @BsonProperty("name")
    private String name;

    @BsonProperty("owner")
    private String owner;

    @BsonProperty("dueDate")
    private String dueDate;

    @BsonProperty("status")
    private String status;

    @BsonProperty("color")
    private String color;

    // BU BOŞ CONSTRUCTOR ŞART! (Yoksa veritabanından okunmaz)
    public Task() {
    }

    public Task(String name, String owner, String dueDate, String status, String color) {
        this.name = name;
        this.owner = owner;
        this.dueDate = dueDate;
        this.status = status;
        this.color = color;
    }

    // Getter & Setter'lar
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}