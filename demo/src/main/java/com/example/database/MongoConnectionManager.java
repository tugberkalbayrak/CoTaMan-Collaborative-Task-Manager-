package com.example.database;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import java.io.InputStream;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.jsr310.Jsr310CodecProvider;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnectionManager {
    private static MongoConnectionManager instance;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private MongoConnectionManager() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {

            // LÜTFEN KENDİ BAĞLANTI LİNKİNİ BURAYA KOY
            String connectionString = "mongodb+srv://admin:sifre123@cotaman.2gv2vue.mongodb.net/?appName=Cotaman";
            String dbName = "Cotaman";

            // --- POJO AYARI: Otomatik tanıma açık ---
            PojoCodecProvider pojoProvider = PojoCodecProvider.builder()
                    .automatic(true)
                    .build();

            Jsr310CodecProvider timeProvider = new Jsr310CodecProvider();

            CodecRegistry pojoCodecRegistry = fromProviders(timeProvider, pojoProvider);
            CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    pojoCodecRegistry);

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .codecRegistry(codecRegistry)
                    .build();

            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(dbName);
            System.out.println(">> MongoDB Bağlantısı Başarılı!");

        } catch (Exception ex) {
            System.out.println("HATA: Bağlantı kurulamadı!");
            ex.printStackTrace();
        }
    }

    public static MongoConnectionManager getInstance() {
        if (instance == null) {
            instance = new MongoConnectionManager();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
