package com.example.Entity;

import org.bson.types.ObjectId;
import java.time.LocalDate; // Tarih formatı için gerekli

public class DateInfo {
    // MongoDB ID
    private ObjectId id;
    
    private String description;
    private LocalDate date; // Artık String değil, LocalDate tutuyoruz

    // Boş Constructor (MongoDB için zorunlu)
    public DateInfo() {}

    // Veri doldurmak için kullandığımız yapıcı
    public DateInfo(String description, LocalDate date) {
        this.description = description;
        this.date = date;
    }

    // --- Getter ve Setter Metotları ---
    
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public String toString() {
        return date + ": " + description;
    }
}