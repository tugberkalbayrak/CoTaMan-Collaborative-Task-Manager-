package com.example.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import com.example.Entity.AcademicFile;
import com.example.Entity.CalendarEvent;
import com.example.Entity.WeeklyLecture;
import com.example.Entity.DateInfo;
import com.example.Entity.Group;
import com.example.Entity.User;
import com.example.Entity.Visibility;

public class CloudRepository {

    private final MongoCollection<User> userCollection;
    private final MongoCollection<Group> groupCollection;
    private final MongoCollection<CalendarEvent> eventCollection;
    private final MongoCollection<AcademicFile> fileCollection;

    // New Collections
    private final MongoCollection<CalendarEvent> calendarEventCollection;
    private final MongoCollection<WeeklyLecture> weeklyLectureCollection;
    private final MongoCollection<DateInfo> dateInfoCollection;

    public CloudRepository() {

        MongoDatabase db = MongoConnectionManager.getInstance().getDatabase();

        this.userCollection = db.getCollection("users", User.class);
        this.groupCollection = db.getCollection("groups", Group.class);
        this.eventCollection = db.getCollection("events", CalendarEvent.class);
        this.fileCollection = db.getCollection("files", AcademicFile.class);

        this.calendarEventCollection = db.getCollection("calendar_events", CalendarEvent.class);
        this.weeklyLectureCollection = db.getCollection("weekly_lectures", WeeklyLecture.class);
        this.dateInfoCollection = db.getCollection("date_infos", DateInfo.class);
    }

    public void saveUser(User user) {

        User existing = getUserByEmail(user.getEmail());
        if (existing == null) {
            userCollection.insertOne(user);
            System.out.println("Kullanıcı kaydedildi: " + user.getFullName());
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

    public void saveEvent(CalendarEvent event) {
        eventCollection.insertOne(event);
        System.out.println("Etkinlik eklendi: " + event.getTitle());
    }

    public List<CalendarEvent> getEventsForUser(ObjectId userId) {
        List<CalendarEvent> events = new ArrayList<>();

        eventCollection.find(Filters.eq("ownerId", userId)).into(events);
        return events;
    }

    public void deleteEvent(ObjectId eventId) {
        eventCollection.deleteOne(Filters.eq("_id", eventId));
        System.out.println("Etkinlik silindi.");
    }

    public void createGroup(Group group) {
        groupCollection.insertOne(group);
        System.out.println("Grup oluşturuldu: " + group.getGroupName());
    }

    public void addMemberToGroup(ObjectId groupId, ObjectId newMemberId) {
        groupCollection.updateOne(
                Filters.eq("_id", groupId),
                Updates.push("memberIds", newMemberId));
        System.out.println("Üye gruba eklendi.");
    }

    public List<Group> getGroupsForUser(ObjectId userId) {
        List<Group> groups = new ArrayList<>();

        groupCollection.find(Filters.in("memberIds", userId)).into(groups);
        return groups;
    }

    public void saveFileMetadata(AcademicFile file) {
        fileCollection.insertOne(file);
    }

    public List<AcademicFile> getPublicFiles() {
        List<AcademicFile> files = new ArrayList<>();
        fileCollection.find(Filters.eq("visibility", Visibility.PUBLIC)).into(files);
        return files;
    }

    public List<AcademicFile> getFilesUploadedByUser(ObjectId uploaderId) {
        List<AcademicFile> files = new ArrayList<>();
        fileCollection.find(Filters.eq("uploaderId", uploaderId)).into(files);
        return files;
    }

    public AcademicFile getFileById(ObjectId fileId) {
        return fileCollection.find(Filters.eq("_id", fileId)).first();
    }

    public AcademicFile getFileByName(String fileName) {
        return fileCollection.find(Filters.eq("fileName", fileName)).first();
    }

    // Tek bir tarihi kaydet
    

    // Toplu kaydetme (Scraper'dan gelen listeyi atmak için)
    public void saveAllDates(List<DateInfo> dateList) {
        if (dateList != null && !dateList.isEmpty()) {
            dateInfoCollection.insertMany(dateList);
            System.out.println(dateList.size() + " adet tarih veritabanına yüklendi.");
        }
    }
    
    // Tüm tarihleri getir
    public List<DateInfo> getAllDates() {
        List<DateInfo> list = new ArrayList<>();
        dateInfoCollection.find().into(list);
        return list;
    }

    // --- New Methods for Scraped Data ---

    public void saveCalendarEvent(CalendarEvent event) {
        calendarEventCollection.insertOne(event);
    }

    public void saveWeeklyLecture(WeeklyLecture lecture) {
        weeklyLectureCollection.insertOne(lecture);
    }

    public void saveDateInfo(DateInfo info) {
        dateInfoCollection.insertOne(info);
    }

    public List<CalendarEvent> getAllCalendarEvents() {
        List<CalendarEvent> list = new ArrayList<>();
        calendarEventCollection.find().into(list);
        return list;
    }

    public List<WeeklyLecture> getAllWeeklyLectures() {
        List<WeeklyLecture> list = new ArrayList<>();
        weeklyLectureCollection.find().into(list);
        return list;
    }

    public List<DateInfo> getAllDateInfos() {
        List<DateInfo> list = new ArrayList<>();
        dateInfoCollection.find().into(list);
        return list;
    }
}