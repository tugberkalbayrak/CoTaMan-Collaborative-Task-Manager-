package com.example.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.example.Entity.AcademicFile;
import com.example.Entity.CalendarEvent;
import com.example.Entity.Group;
import com.example.Entity.User;
import com.example.Entity.Visibility;

public class CloudRepository {

    private final MongoCollection<User> userCollection;
    private final MongoCollection<Document> rawUserCollection;

    private final MongoCollection<Group> groupCollection;
    private final MongoCollection<Document> rawGroupCollection;

    private final MongoCollection<CalendarEvent> eventCollection;
    private final MongoCollection<AcademicFile> fileCollection;

    private final MongoCollection<Document> rawCalendarCollection;

    public CloudRepository() {
        MongoDatabase db = MongoConnectionManager.getInstance().getDatabase();

        this.userCollection = db.getCollection("users", User.class);
        this.rawUserCollection = db.getCollection("users");

        this.groupCollection = db.getCollection("groups", Group.class);
        this.rawGroupCollection = db.getCollection("groups");

        this.eventCollection = db.getCollection("events", CalendarEvent.class);
        this.rawCalendarCollection = db.getCollection("events");

        this.fileCollection = db.getCollection("files", AcademicFile.class);
    }

    public boolean createGroup(Group group) {
        try {
            Document doc = new Document();
            doc.append("_id", new ObjectId());
            doc.append("groupName", group.getGroupName());
            doc.append("courseCode", group.getCourseCode());
            doc.append("memberIds", group.getMemberIds());
            doc.append("groupArchive", new ArrayList<>());
            doc.append("tasks", new ArrayList<>());

            rawGroupCollection.insertOne(doc);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Group> getGroupsForUser(ObjectId userId) {
        List<Group> groups = new ArrayList<>();
        groupCollection.find(Filters.in("memberIds", userId)).into(groups);
        return groups;
    }

    public boolean saveUser(User user) {
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
            return true;
        } else {
            return false;
        }
    }

    public User getUserByEmail(String email) {
        return userCollection.find(Filters.eq("email", email)).first();
    }

    public User getUserById(ObjectId id) {
        return userCollection.find(Filters.eq("_id", id)).first();
    }

    public void updateUser(User user) {
        try {
            Document updateDoc = new Document();
            updateDoc.append("fullName", user.getFullName());
            updateDoc.append("email", user.getEmail());
            updateDoc.append("bilkentId", user.getBilkentId());
            updateDoc.append("profilePhotoPath", user.getProfilePhotoPath());

            rawUserCollection.updateOne(Filters.eq("_id", user.getId()), new Document("$set", updateDoc));
            System.out.println("User updated: " + user.getFullName());
        } catch (Exception e) {
            System.out.println("User update error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public User getUserByBilkentId(String bilkentId) {
        return userCollection.find(Filters.eq("bilkentId", bilkentId)).first();
    }

    public void saveEvent(CalendarEvent event) {
        eventCollection.insertOne(event);
        System.out.println("Event added: " + event.getTitle());
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

    public boolean addTaskToGroup(ObjectId groupId, com.example.ui.components.Task task) {
        try {
            Document taskDoc = new Document()
                    .append("name", task.getName())
                    .append("owner", task.getOwner())
                    .append("dueDate", task.getDueDate())
                    .append("status", task.getStatus())
                    .append("color", task.getColor());

            rawGroupCollection.updateOne(
                    Filters.eq("_id", groupId),
                    new Document("$push", new Document("tasks", taskDoc)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void openFile(AcademicFile file) {
        if (file == null || file.getDiskPath() == null) {
            System.out.println("ERROR: File or file path is invalid.");
            return;
        }
        String path = file.getDiskPath();
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (path.startsWith("http://") || path.startsWith("https://")) {
                    desktop.browse(new URI(path));
                    System.out.println("Opening link in browser: " + path);
                } else {
                    File localFile = new File(path);
                    if (localFile.exists()) {
                        desktop.open(localFile);
                        System.out.println("Opening file: " + localFile.getName());
                    } else {
                        System.out.println("ERROR: File not found on disk! -> " + path);
                    }
                }
            } else {
                System.out.println("ERROR: File opening process not supported on this system.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while opening the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean addFriend(ObjectId userId, ObjectId friendId) {
        try {
            rawUserCollection.updateOne(
                    Filters.eq("_id", userId),
                    new Document("$addToSet", new Document("friends", friendId)));
            rawUserCollection.updateOne(
                    Filters.eq("_id", friendId),
                    new Document("$addToSet", new Document("friends", userId)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addMemberToGroup(ObjectId groupId, ObjectId newMemberId) {
        try {
            rawGroupCollection.updateOne(
                    Filters.eq("_id", groupId),
                    new Document("$addToSet", new Document("memberIds", newMemberId)));
            return true;
        } catch (Exception e) {
            System.out.println("Member addition error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<AcademicFile> getPrivateFiles(ObjectId userId) {
        List<AcademicFile> files = new ArrayList<>();
        fileCollection.find(
                Filters.and(
                        Filters.eq("uploader._id", userId),
                        Filters.eq("visibility", Visibility.PRIVATE)))
                .into(files);
        return files;
    }

    public List<AcademicFile> getPublicFilesByCourse(String courseCode) {
        List<AcademicFile> files = new ArrayList<>();
        fileCollection.find(
                Filters.and(
                        Filters.eq("visibility", Visibility.PUBLIC),
                        Filters.eq("courseCode", courseCode)))
                .into(files);
        return files;
    }

    public boolean updateTaskStatus(org.bson.types.ObjectId groupId, String taskName, String newStatus,
            String newColor) {
        try {
            rawGroupCollection.updateOne(
                    com.mongodb.client.model.Filters.and(
                            com.mongodb.client.model.Filters.eq("_id", groupId),
                            com.mongodb.client.model.Filters.eq("tasks.name", taskName)),
                    com.mongodb.client.model.Updates.combine(
                            com.mongodb.client.model.Updates.set("tasks.$.status", newStatus),
                            com.mongodb.client.model.Updates.set("tasks.$.color", newColor)));
            return true;
        } catch (Exception e) {
            System.out.println("Task update error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTaskFromGroup(org.bson.types.ObjectId groupId, String taskName) {
        try {
            rawGroupCollection.updateOne(
                    com.mongodb.client.model.Filters.eq("_id", groupId),
                    new org.bson.Document("$pull",
                            new org.bson.Document("tasks", new org.bson.Document("name", taskName))));
            System.out.println("Task deleted from database: " + taskName);
            return true;
        } catch (Exception e) {
            System.out.println("Deletion error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEvent(org.bson.types.ObjectId eventId) {
        try {
            rawCalendarCollection.deleteOne(com.mongodb.client.model.Filters.eq("_id", eventId));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
