package com.example.database;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class CloudRepository {
    
    private final MongoCollection<User> userCollection;
    private final MongoCollection<Group> groupCollection;
    private final MongoCollection<Event> eventCollection;
    private final MongoCollection<ArchiveFile> fileCollection;

    public CloudRepository() {
        
        MongoDatabase db = MongoConnectionManager.getInstance().getDatabase();
        
        
        this.userCollection = db.getCollection("users", User.class);
        this.groupCollection = db.getCollection("groups", Group.class);
        this.eventCollection = db.getCollection("events", Event.class);
        this.fileCollection = db.getCollection("files", ArchiveFile.class);
    }

   
    
    public void saveUser(User user) {
        
        User existing = getUserByEmail(user.getEmail());
        if (existing == null) {
            userCollection.insertOne(user);
            System.out.println("Kullanıcı kaydedildi: " + user.getName());
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

    
    public void saveEvent(Event event) {
        eventCollection.insertOne(event);
        System.out.println("Etkinlik eklendi: " + event.getTitle());
    }

    
    public List<Event> getEventsForUser(ObjectId userId) {
        List<Event> events = new ArrayList<>();
       
        eventCollection.find(Filters.eq("ownerId", userId)).into(events);
        return events;
    }

  
    public void deleteEvent(ObjectId eventId) {
        eventCollection.deleteOne(Filters.eq("_id", eventId));
        System.out.println("Etkinlik silindi.");
    }

    
    public void createGroup(Group group) {
        groupCollection.insertOne(group);
        System.out.println("Grup oluşturuldu: " + group.getName());
    }

   
    public void addMemberToGroup(ObjectId groupId, ObjectId newMemberId) {
        groupCollection.updateOne(
            Filters.eq("_id", groupId),
            Updates.push("memberIds", newMemberId) 
        );
        System.out.println("Üye gruba eklendi.");
    }

    
    public List<Group> getGroupsForUser(ObjectId userId) {
        List<Group> groups = new ArrayList<>();
        
        groupCollection.find(Filters.in("memberIds", userId)).into(groups);
        return groups;
    }

    

    public void saveFileMetadata(ArchiveFile file) {
        fileCollection.insertOne(file);
    }

    
    public List<ArchiveFile> getPublicFiles() {
        List<ArchiveFile> files = new ArrayList<>();
        fileCollection.find(Filters.eq("visibility", Visibility.PUBLIC)).into(files);
        return files;
    }

   
    public List<ArchiveFile> getFilesUploadedByUser(ObjectId uploaderId) {
        List<ArchiveFile> files = new ArrayList<>();
        fileCollection.find(Filters.eq("uploaderId", uploaderId)).into(files);
        return files;
    }
}