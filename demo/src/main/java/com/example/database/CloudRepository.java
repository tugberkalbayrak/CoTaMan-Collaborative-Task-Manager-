package com.example.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import com.example.Entity.AcademicFile;
import com.example.Entity.CalendarEvent;
import com.example.Entity.Group;
import com.example.Entity.User;
import com.example.Entity.Visibility;
import com.example.ui.components.Task; // Task import edildi

public class CloudRepository {

    private final MongoCollection<User> userCollection;
    private final MongoCollection<Document> rawUserCollection;

    private final MongoCollection<Group> groupCollection;
    private final MongoCollection<Document> rawGroupCollection; // Manuel kayıt için

    private final MongoCollection<CalendarEvent> eventCollection;
    private final MongoCollection<AcademicFile> fileCollection;

    public CloudRepository() {
        MongoDatabase db = MongoConnectionManager.getInstance().getDatabase();

        // User
        this.userCollection = db.getCollection("users", User.class);
        this.rawUserCollection = db.getCollection("users");

        // Group
        this.groupCollection = db.getCollection("groups", Group.class);
        this.rawGroupCollection = db.getCollection("groups");

        // Event & File
        this.eventCollection = db.getCollection("events", CalendarEvent.class);
        this.fileCollection = db.getCollection("files", AcademicFile.class);
    }

    // --- GRUP METOTLARI ---

    public void createGroup(Group group) {
        try {
            Document doc = new Document();
            doc.append("_id", new ObjectId());
            doc.append("groupName", group.getGroupName());
            doc.append("courseCode", group.getCourseCode());
            doc.append("memberIds", group.getMemberIds());
            doc.append("groupArchive", new ArrayList<>());
            doc.append("tasks", new ArrayList<>()); // Task listesi boş başlasın

            rawGroupCollection.insertOne(doc);
            System.out.println("✅ Grup (Manuel) oluşturuldu: " + group.getGroupName());
        } catch (Exception e) {
            System.out.println("❌ Grup oluşturma hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Group> getGroupsForUser(ObjectId userId) {
        List<Group> groups = new ArrayList<>();
        groupCollection.find(Filters.in("memberIds", userId)).into(groups);
        return groups;
    }

    // --- USER METOTLARI ---

    public void saveUser(User user) {
        User existing = getUserByEmail(user.getEmail());
        if (existing == null) {
            Document doc = new Document();
            doc.append("_id", new ObjectId());
            doc.append("fullName", user.getFullName());
            doc.append("email", user.getEmail());
            doc.append("password", user.getPassword());
            doc.append("bilkentId", user.getBilkentId());
            doc.append("schedule", new ArrayList<>());
            doc.append("enrolledGroups", new ArrayList<>());
            doc.append("friends", new ArrayList<>());

            rawUserCollection.insertOne(doc);
            System.out.println("Kullanıcı (Manuel) kaydedildi: " + user.getEmail());
        } else {
            System.out.println("HATA: Bu email zaten kayıtlı!");
        }
    }

    public User getUserByEmail(String email) {
        return userCollection.find(Filters.eq("email", email)).first();
    }

    public User getUserById(ObjectId id) {
        return userCollection.find(Filters.eq("_id", id)).first();
    }

    // --- CALENDAR & FILE METOTLARI ---

    public void saveEvent(CalendarEvent event) {
        eventCollection.insertOne(event);
        System.out.println("Etkinlik eklendi: " + event.getTitle());
    }

    public List<CalendarEvent> getEventsForUser(ObjectId userId) {
        List<CalendarEvent> events = new ArrayList<>();
        eventCollection.find(Filters.eq("owner._id", userId)).into(events);
        return events;
    }

    public void saveFileMetadata(AcademicFile file) {
        fileCollection.insertOne(file);
    }

    public List<AcademicFile> getPublicFiles() {
        List<AcademicFile> files = new ArrayList<>();
        fileCollection.find(Filters.eq("visibility", Visibility.PUBLIC)).into(files);
        return files;
    }

    public void addTaskToGroup(ObjectId groupId, com.example.ui.components.Task task) {
        try {
            Document taskDoc = new Document()
                    .append("name", task.getName())
                    .append("owner", task.getOwner())
                    .append("dueDate", task.getDueDate())
                    .append("status", task.getStatus())
                    .append("color", task.getColor());

            // "$push" komutu ile listeye yeni görev ekliyoruz
            rawGroupCollection.updateOne(
                    Filters.eq("_id", groupId),
                    new Document("$push", new Document("tasks", taskDoc)));
            System.out.println("✅ Görev veritabanına eklendi: " + task.getName());
        } catch (Exception e) {
            System.out.println("❌ Görev ekleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
}