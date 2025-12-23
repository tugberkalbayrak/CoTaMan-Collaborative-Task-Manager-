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
import com.example.Services.ArchiveService.CalendarService.IntelligentMeetingSchedular;
import com.example.Entity.TimeSlot;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.example.Entity.Importance;

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

    // filePath parametresi eklenmiş GÜNCELLENMİŞ uploadFile metodu
    public void uploadFile(String n, String filePath, String c, String t, String v) {
        if (currentUser == null)
            return;
        // Görünürlük Ayarı
        Visibility visibility = Visibility.PUBLIC;
        if (v != null) {
            if (v.contains("Group"))
                visibility = Visibility.GROUP;
            else if (v.contains("Private"))
                visibility = Visibility.PRIVATE;
        }
        // Dosya Tipi Ayarı (Null kontrolü eklendi)
        // Eğer t null ise veya Syllabus değilse varsayılan olarak LECTURE_NOTE atanır.
        FileType fileType = (t != null && t.equals("Syllabus")) ? FileType.SYLLABUS : FileType.LECTURE_NOTE;
        // Dosya yolunu (filePath) AcademicFile içine kaydediyoruz
        repository.saveFileMetadata(new AcademicFile(n, filePath, currentUser, fileType, visibility, c));
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public CloudRepository getRepository() {
        return repository;
    }

    public List<TimeSlot> findCommonSlots(Group group) {
        if (group == null)
            return new ArrayList<>();

        // A) Üyelerin güncel takvimlerini çek
        List<User> membersWithSchedule = new ArrayList<>();
        for (ObjectId memberId : group.getMemberIds()) {
            User u = repository.getUserById(memberId);
            if (u != null) {
                List<CalendarEvent> events = repository.getEventsForUser(memberId);
                u.setSchedule(events);
                membersWithSchedule.add(u);
            }
        }
        group.setMembers(membersWithSchedule);

        // B) Algoritmayı Çalıştır
        IntelligentMeetingSchedular scheduler = new IntelligentMeetingSchedular();

        // 7 günlük tarama, en az 1 saatlik boşluklar
        return scheduler.findCommonSlots(
                group,
                Duration.ofHours(1),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7));
    }

    public void scheduleMeeting(Group group, String timeString) {
        if (group == null || timeString == null)
            return;

        System.out.println("Toplantı Planlanıyor: " + timeString);

        // Gelen Format: "25 Dec 2025 - 14:00 - 15:30"
        try {
            String[] parts = timeString.split(" - ");
            String dateStr = parts[0]; // 25 Dec 2025
            String startStr = parts[1]; // 14:00
            String endStr = parts[2]; // 15:30

            DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

            LocalDateTime start = LocalDateTime.parse(dateStr + " " + startStr, dateTimeFmt);
            LocalDateTime end = LocalDateTime.parse(dateStr + " " + endStr, dateTimeFmt);

            // Tüm üyelere dağıt
            for (ObjectId memberId : group.getMemberIds()) {
                User member = repository.getUserById(memberId);
                if (member != null) {
                    CalendarEvent meeting = new CalendarEvent(
                            member,
                            "Meeting: " + group.getGroupName(),
                            "Scheduled via CoTaMan",
                            start,
                            end,
                            Importance.MUST);
                    repository.saveEvent(meeting);
                }
            }
            System.out.println("✅ Toplantı (" + startStr + "-" + endStr + ") tüm gruba işlendi!");

        } catch (Exception e) {
            System.out.println("❌ Tarih formatı hatası: " + e.getMessage());
        }
    }

    public String addFriend(String friendEmail) {
        if (currentUser == null)
            return "Giriş yapılmamış!";
        if (friendEmail.equals(currentUser.getEmail()))
            return "Kendini ekleyemezsin.";

        User friend = repository.getUserByEmail(friendEmail);
        if (friend == null)
            return "Kullanıcı bulunamadı.";

        if (currentUser.getFriends().contains(friend.getId())) {
            return "Zaten arkadaşsınız.";
        }

        boolean success = repository.addFriend(currentUser.getId(), friend.getId());
        if (success) {
            currentUser.getFriends().add(friend.getId());
            return "Başarılı! " + friend.getFullName() + " arkadaş eklendi.";
        }
        return "Hata oluştu.";
    }

    public List<User> getFriendsList() {
        List<User> friendList = new ArrayList<>();
        if (currentUser == null)
            return friendList;

        // Kullanıcının arkadaş ID'lerini tek tek User nesnesine çevir
        for (ObjectId friendId : currentUser.getFriends()) {
            User u = repository.getUserById(friendId);
            if (u != null)
                friendList.add(u);
        }
        return friendList;
    }

    // 2. Seçilen Arkadaşı Gruba Ekle
    public String addMemberToGroup(Group group, User friend) {
        if (group == null || friend == null)
            return "Hata: Seçim geçersiz.";

        // Zaten üye mi?
        if (group.getMemberIds().contains(friend.getId())) {
            return "Bu kullanıcı zaten grupta ekli.";
        }

        // Veritabanına Yaz
        boolean success = repository.addMemberToGroup(group.getId(), friend.getId());

        if (success) {
            // Ekran anında güncellensin diye RAM'deki listeye de ekle
            group.getMemberIds().add(friend.getId());
            if (group.getMembers() != null)
                group.getMembers().add(friend);

            return "Başarılı! " + friend.getFullName() + " eklendi.";
        } else {
            return "Veritabanı hatası oluştu.";
        }
    }

    public List<AcademicFile> getPrivateFiles() {
        if (currentUser == null)
            return new ArrayList<>();
        return repository.getPrivateFiles(currentUser.getId());
    }

    public List<AcademicFile> getFilesByCourse(String courseCode) {
        return repository.getPublicFilesByCourse(courseCode);
    }
}