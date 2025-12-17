package com.example.database;

import org.bson.types.ObjectId;

public class User {
    private ObjectId id;
    private String name;
    private String email;

    public User() {} 

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "User{name='" + name + "', email='" + email + "'}";
    }
}
