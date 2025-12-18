package com.example.Handlers;

import com.example.database.CloudRepository;
import com.example.Entity.User; // DB User entity

public class AuthenticationHandler {

    private CloudRepository repository;

    public AuthenticationHandler() {
        this.repository = new CloudRepository();
    }

    public boolean onLoginClick(String email, String password) {
        if (email == null || password == null) return false;

        User user = repository.getUserByEmail(email);
        
        if (user != null) {
            System.out.println("Login Success: " + user.getFullName());
            return true;
        }
        return false;
    }

    public void onRegisterClick(String name, String surname, String id, String email, String password) {
        User newUser = new User(id, name + " " + surname, email);
        // Note: Add logic to save ID/Password if User entity supports it
        repository.saveUser(newUser);
        System.out.println("User Registered: " + email);
    }
}