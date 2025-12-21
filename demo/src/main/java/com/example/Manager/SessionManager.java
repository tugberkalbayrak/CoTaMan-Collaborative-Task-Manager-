package com.example.Manager;

import com.example.database.CloudRepository;
import com.example.Entity.User;
import com.example.Entity.Group;
import com.example.Entity.AcademicFile;
import com.example.Entity.CalendarEvent;
import com.example.Entity.Importance;
import com.example.Entity.FileType;
import com.example.Entity.Visibility;
import com.example.Entity.WeeklyLecture;
import com.example.WebScraping.SRSScraper;
import com.example.Handlers.AuthenticationHandler;
import com.example.ui.components.Task; // Task import

import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private static SessionManager instance;
    private CloudRepository repository;
    private AuthenticationHandler authHandler;
    private User currentUser;
    private SRSScraper activeScraper;

    private SessionManager() {
        this.repository = new CloudRepository();
        this.authHandler = new AuthenticationHandler();
    }

    public static SessionManager getInstance() {
        if (instance == null)
            instance = new SessionManager();
        return instance;
    }

    // --- YENİ: GÖREV EKLEME ---
    public void addTask(Group group, String taskName, String date, String status) {
        if (group == null || currentUser == null)
            return;

        // Yeni görev oluştur
        com.example.ui.components.Task newTask = new com.example.ui.components.Task(
                taskName,
                currentUser.getFullName(),
                date,
                status,
                "#E67E22" // Turuncu
        );

        // 1. Ekrandaki listeye ekle (Anında görünsün diye)
        if (group.getTasks() == null)
            group.setTasks(new ArrayList<>());
        group.getTasks().add(newTask);

        // 2. Veritabanına kaydet (Kalıcı olsun diye)
        repository.addTaskToGroup(group.getId(), newTask);
    }
    // --- GRUP & ARKADAŞ ---

    public void createGroup(String n, String c) {
        if (currentUser == null)
            return;
        Group g = new Group(n, c);
        g.getMemberIds().add(currentUser.getId());
        repository.createGroup(g);
    }

    public List<Group> getUserGroups() {
        return currentUser == null ? new ArrayList<>() : repository.getGroupsForUser(currentUser.getId());
    }

    public List<User> getGroupMembers(Group g) {
        List<User> l = new ArrayList<>();
        if (g != null)
            for (ObjectId id : g.getMemberIds()) {
                User u = repository.getUserById(id);
                if (u != null)
                    l.add(u);
            }
        return l;
    }

    public Group getGroupByName(String n) {
        for (Group g : getUserGroups())
            if (g.getGroupName().equals(n))
                return g;
        return null;
    }

    // --- LOGIN & REGISTER ---

    public boolean login(String email, String password) {
        System.out.println("--- LOGIN İŞLEMİ ---");
        User user = repository.getUserByEmail(email);

        if (user == null) {
            System.out.println("HATA: Kullanıcı bulunamadı.");
            return false;
        }

        if (user.getPassword() != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            System.out.println("Giriş Başarılı: " + user.getFullName());
            return true;
        } else {
            System.out.println("HATA: Şifre yanlış.");
            return false;
        }
    }

    public boolean register(String name, String email, String password) {
        if (repository.getUserByEmail(email) != null)
            return false;

        User newUser = new User();
        newUser.setFullName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);

        repository.saveUser(newUser);
        return true;
    }

    // --- SRS & TAKVİM ---

    public void startSrsLogin(String id, String pass, java.util.function.Consumer<Integer> callback) {
        new Thread(() -> {
            activeScraper = new SRSScraper();
            int result = activeScraper.startLogin(id, pass);
            javafx.application.Platform.runLater(() -> callback.accept(result));
        }).start();
    }

    public void verifySmsAndFetch(String smsCode, Runnable onSuccess, Runnable onFail) {
        if (activeScraper == null)
            return;
        new Thread(() -> {
            if (activeScraper.verifySmsCode(smsCode)) {
                List<CalendarEvent> srsExams = activeScraper.fetchExams();
                for (CalendarEvent e : srsExams) {
                    e.setOwner(currentUser);
                    repository.saveEvent(e);
                }
                List<WeeklyLecture> lectures = activeScraper.fetchWeeklySchedule();
                List<CalendarEvent> converted = convertLecturesToEvents(lectures);
                for (CalendarEvent e : converted) {
                    repository.saveEvent(e);
                }
                javafx.application.Platform.runLater(onSuccess);
            } else {
                javafx.application.Platform.runLater(onFail);
            }
        }).start();
    }

    private List<CalendarEvent> convertLecturesToEvents(List<WeeklyLecture> lectures) {
        List<CalendarEvent> events = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusWeeks(4);
        LocalDate semesterEndDate = LocalDate.of(2025, 12, 25);
        for (int i = 0; i < 20; i++) {
            LocalDate weekBaseDate = startDate.plusWeeks(i);
            for (WeeklyLecture lecture : lectures) {
                LocalDate lectureDate = weekBaseDate.with(TemporalAdjusters.nextOrSame(lecture.getDay()));
                if (lectureDate.isAfter(semesterEndDate))
                    continue;
                LocalDateTime start = LocalDateTime.of(lectureDate, lecture.getStartTime());
                LocalDateTime end = LocalDateTime.of(lectureDate, lecture.getEndTime());
                events.add(new CalendarEvent(currentUser, lecture.getCourseCode() + " (" + lecture.getType() + ")",
                        lecture.getRoom(), start, end, Importance.MUST));
            }
        }
        return events;
    }

    public List<CalendarEvent> getUserEvents() {
        if (currentUser == null)
            return List.of();
        return repository.getEventsForUser(currentUser.getId());
    }

    public void addEvent(String title, String s, String e, Importance i) {
        if (currentUser == null)
            return;
        try {
            repository.saveEvent(
                    new CalendarEvent(currentUser, title, "Manual", LocalDateTime.parse(s), LocalDateTime.parse(e), i));
        } catch (Exception x) {
        }
    }

    public List<AcademicFile> getPublicFiles() {
        return repository.getPublicFiles();
    }

    public void uploadFile(String n, String c, String t, String v) {
        if (currentUser == null)
            return;
        repository.saveFileMetadata(new AcademicFile(n, "path", currentUser,
                t.equals("Syllabus") ? FileType.SYLLABUS : FileType.LECTURE_NOTE,
                v.contains("Group") ? Visibility.GROUP : Visibility.PUBLIC));
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public CloudRepository getRepository() {
        return repository;
    }
}