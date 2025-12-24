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
import com.example.ui.components.Task;
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

    public void addTask(Group group, String taskName, String date, String status) {
        if (group == null || currentUser == null)
            return;

        com.example.ui.components.Task newTask = new com.example.ui.components.Task(
                taskName,
                currentUser.getFullName(),
                date,
                status,
                "#E67E22");

        if (group.getTasks() == null)
            group.setTasks(new ArrayList<>());
        group.getTasks().add(newTask);

        boolean success = repository.addTaskToGroup(group.getId(), newTask);
        if (success) {
            com.example.ui.components.NotificationManager.showSuccess("Task Added",
                    "Task '" + taskName + "' added to group.");
        } else {
            com.example.ui.components.NotificationManager.showError("Error", "Failed to add task to database.");
        }
    }

    public void createGroup(String n, String c) {
        if (currentUser == null)
            return;
        Group g = new Group(n, c);
        g.getMemberIds().add(currentUser.getId());
        boolean success = repository.createGroup(g);
        if (success) {
            com.example.ui.components.NotificationManager.showSuccess("Group Created",
                    "Group '" + n + "' created successfully.");
        } else {
            com.example.ui.components.NotificationManager.showError("Error", "Failed to create group.");
        }
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

    public boolean login(String email, String password) {
        com.example.ui.components.NotificationManager.showInfo("Login Process", "Processing login...");
        User user = repository.getUserByEmail(email);

        if (user == null) {
            com.example.ui.components.NotificationManager.showError("Login Error", "User not found.");
            return false;
        }

        if (user.getPassword() != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            return true;
        } else {
            com.example.ui.components.NotificationManager.showError("Login Error", "Incorrect password.");
            return false;
        }
    }

    public boolean register(String name, String email, String password, String moodleUser, String moodlePass) {
        if (repository.getUserByEmail(email) != null)
            return false;

        User newUser = new User();
        newUser.setFullName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setMoodleUsername(moodleUser);
        newUser.setMoodlePassword(moodlePass);

        repository.saveUser(newUser);
        return true;
    }

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

    public String addEvent(String title, String startIso, String endIso, Importance imp) {
        if (currentUser == null)
            return "User not logged in";

        try {
            LocalDateTime start = LocalDateTime.parse(startIso);
            LocalDateTime end = LocalDateTime.parse(endIso);

            CalendarEvent newEvent = new CalendarEvent(currentUser, title, "Manual", start, end, imp);

            List<CalendarEvent> existingEvents = getUserEvents();
            for (CalendarEvent existing : existingEvents) {

                if (existing.overlaps(newEvent)) {
                    return "Conflict detected! You already have '" + existing.getTitle() + "' at this time.";
                }
            }

            repository.saveEvent(newEvent);
            return "Success";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public List<AcademicFile> getPublicFiles() {
        return repository.getPublicFiles();
    }

    public void uploadFile(String n, String filePath, String c, String t, String v) {
        if (currentUser == null)
            return;

        Visibility visibility = Visibility.PUBLIC;
        if (v != null) {
            if (v.contains("Group"))
                visibility = Visibility.GROUP;
            else if (v.contains("Private"))
                visibility = Visibility.PRIVATE;
        }

        FileType fileType = (t != null && t.equals("Syllabus")) ? FileType.SYLLABUS : FileType.LECTURE_NOTE;

        repository.saveFileMetadata(new AcademicFile(n, filePath, currentUser, fileType, visibility, c));
        com.example.ui.components.NotificationManager.showSuccess("File Uploaded",
                "File '" + n + "' uploaded successfully.");
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

        IntelligentMeetingSchedular scheduler = new IntelligentMeetingSchedular();

        return scheduler.findCommonSlots(
                group,
                Duration.ofHours(1),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7));
    }

    public void scheduleMeeting(Group group, String timeString) {
        if (group == null || timeString == null)
            return;

        com.example.ui.components.NotificationManager.showInfo("Scheduling", "Scheduling meeting for: " + timeString);

        try {
            String[] parts = timeString.split(" - ");
            String dateStr = parts[0];
            String startStr = parts[1];
            String endStr = parts[2];

            DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

            LocalDateTime start = LocalDateTime.parse(dateStr + " " + startStr, dateTimeFmt);
            LocalDateTime end = LocalDateTime.parse(dateStr + " " + endStr, dateTimeFmt);

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
            com.example.ui.components.NotificationManager.showSuccess("Meeting Scheduled",
                    "Meeting (" + startStr + "-" + endStr + ") scheduled for group!");

        } catch (Exception e) {
            com.example.ui.components.NotificationManager.showError("Date Error", "Format error: " + e.getMessage());
        }
    }

    public String addFriend(String friendEmail) {
        if (currentUser == null)
            return "Not logged in!";
        if (friendEmail.equals(currentUser.getEmail()))
            return "You cannot add yourself.";

        User friend = repository.getUserByEmail(friendEmail);
        if (friend == null)
            return "User not found.";

        if (currentUser.getFriends().contains(friend.getId())) {
            return "Already friends.";
        }

        boolean success = repository.addFriend(currentUser.getId(), friend.getId());
        if (success) {
            currentUser.getFriends().add(friend.getId());
            com.example.ui.components.NotificationManager.showSuccess("Friend Added",
                    friend.getFullName() + " added as friend.");
            return "Success! " + friend.getFullName() + " added as friend.";
        }
        com.example.ui.components.NotificationManager.showError("Error", "Failed to add friend.");
        return "An error occurred.";
    }

    public List<User> getFriendsList() {
        List<User> friendList = new ArrayList<>();
        if (currentUser == null)
            return friendList;

        for (ObjectId friendId : currentUser.getFriends()) {
            User u = repository.getUserById(friendId);
            if (u != null)
                friendList.add(u);
        }
        return friendList;
    }

    public String addMemberToGroup(Group group, User friend) {
        if (group == null || friend == null)
            return "Error: Invalid selection.";

        if (group.getMemberIds().contains(friend.getId())) {
            return "User is already in the group.";
        }

        boolean success = repository.addMemberToGroup(group.getId(), friend.getId());

        if (success) {

            group.getMemberIds().add(friend.getId());
            if (group.getMembers() != null)
                group.getMembers().add(friend);

            com.example.ui.components.NotificationManager.showSuccess("Member Added",
                    friend.getFullName() + " added to group.");
            return "Success! " + friend.getFullName() + " added.";
        } else {
            com.example.ui.components.NotificationManager.showError("Error", "Failed to add member.");
            return "Database error occurred.";
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

    public void updateTaskStatus(Group group, String taskName, String newStatus, String newColor) {
        if (group == null)
            return;

        boolean success = repository.updateTaskStatus(group.getId(), taskName, newStatus, newColor);

        if (success) {
            com.example.ui.components.NotificationManager.showSuccess("Task Updated",
                    "Task updated: " + taskName + " -> " + newStatus);

        } else {
            com.example.ui.components.NotificationManager.showError("Update Failed", "Task could not be updated!");
        }
    }

    public void deleteTask(Group group, com.example.ui.components.Task task) {
        if (group == null || task == null)
            return;

        boolean success = repository.deleteTaskFromGroup(group.getId(), task.getName());

        if (success && group.getTasks() != null) {
            group.getTasks().remove(task);
            com.example.ui.components.NotificationManager.showSuccess("Task Deleted",
                    "Task '" + task.getName() + "' deleted.");
        } else {
            com.example.ui.components.NotificationManager.showError("Error", "Failed to delete task.");
        }
    }

    public void deleteEvent(CalendarEvent event) {
        if (event == null || event.getEventId() == null)
            return;
        if (repository.deleteEvent(event.getEventId())) {
            com.example.ui.components.NotificationManager.showSuccess("Event Deleted", "Event deleted successfully.");
        } else {
            com.example.ui.components.NotificationManager.showError("Error", "Failed to delete event.");
        }
    }

    public List<AcademicFile> searchFiles(List<AcademicFile> sourceList, String query) {
        if (query == null || query.isEmpty())
            return sourceList;

        String lowerQuery = query.toLowerCase();
        List<AcademicFile> filtered = new ArrayList<>();

        for (AcademicFile file : sourceList) {

            boolean nameMatch = file.getFileName().toLowerCase().contains(lowerQuery);
            boolean courseMatch = (file.getCourseCode() != null
                    && file.getCourseCode().toLowerCase().contains(lowerQuery));

            if (nameMatch || courseMatch) {
                filtered.add(file);
            }
        }
        return filtered;
    }
}