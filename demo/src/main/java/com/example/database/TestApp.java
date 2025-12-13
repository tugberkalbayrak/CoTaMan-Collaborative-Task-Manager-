package com.example.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class TestApp {
    public static void main(String[] args) {
        System.out.println("--- CoTaMan Test Başlıyor ---");

        // 1. Veritabanını al
        MongoDatabase db = MongoConnectionManager.getInstance().getDatabase();

        // 2. Tabloyu (Collection) al
        MongoCollection<User> userCollection = db.getCollection("users", User.class);

        // 3. Veri Ekle
        User yeniUser = new User("Test Kullanicisi", "osurdum");
        userCollection.insertOne(yeniUser);
        System.out.println("Kayıt Eklendi: " + yeniUser);
    }
}
