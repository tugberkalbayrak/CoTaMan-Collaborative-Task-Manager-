package com.example.WebScraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.example.Entity.*;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoodleScraper implements IDataFetcher {

    // NOTE: Bilkent Moodle URLs change by academic year. 
    // You might need to update this URL to the current active semester!
    private static final String MOODLE_BASE_URL = "https://moodle.bilkent.edu.tr/2025-2026-fall"; 
    private static final String LOGIN_URL = MOODLE_BASE_URL + "/login/index.php";

    private HttpClient client;
    private CookieManager cookieManager;
    private boolean isLoggedIn = false;

    public MoodleScraper() {
        // 1. Setup the Cookie Manager 
        // This acts like a browser's memory, storing the "Session ID" automatically.
        this.cookieManager = new CookieManager();
        this.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        // 2. Build the Client
        this.client = HttpClient.newBuilder()
                .cookieHandler(this.cookieManager)
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Connects to Moodle by performing the full login handshake.
     * [cite: 161]
     */
    public boolean connect(String username, String password) {
        try {
            System.out.println("Fetching login token from Moodle...");
            
            // Step A: GET the login page first to find the "logintoken"
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(LOGIN_URL))
                    .GET()
                    .build();

            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            String loginPageHtml = getResponse.body();

            // Parse HTML to find the hidden <input name="logintoken" value="...">
            Document doc = Jsoup.parse(loginPageHtml);
            Element tokenInput = doc.selectFirst("input[name=logintoken]");
            
            if (tokenInput == null) {
                System.err.println("Could not find login token. Moodle structure might have changed.");
                return false;
            }

            String loginToken = tokenInput.val();
            System.out.println("Token found: " + loginToken.substring(0, 10) + "...");

            // Step B: POST the credentials + token
            // We need to format data as: username=abc&password=123&logintoken=xyz
            Map<String, String> formData = new HashMap<>();
            formData.put("username", username);
            formData.put("password", password);
            formData.put("logintoken", loginToken);
            
            String formBody = buildFormData(formData);

            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(URI.create(LOGIN_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formBody))
                    .build();

            System.out.println("Sending login request...");
            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

            // Step C: Verify success
            // If login fails, Moodle usually keeps you on the login page containing "Invalid login"
            if (postResponse.body().contains("Dashboard") || postResponse.body().contains("My courses")) {
                this.isLoggedIn = true;
                System.out.println("Login Successful! User is authenticated.");
                return true;
            } else {
                System.err.println("Login Failed. Check credentials.");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<CalendarEvent> fetchEvents() {
        ArrayList<CalendarEvent> moodleEvents = new ArrayList<>();

        if (!isLoggedIn) {
            System.err.println("Cannot fetch events: Not logged in.");
            return moodleEvents;
        }

        try {
            System.out.println("Fetching 'Upcoming Events' from Moodle...");
            
            // 1. Request the "Upcoming Events" page specifically
            // This page lists all deadlines in a clean format
            String calendarUrl = MOODLE_BASE_URL + "/calendar/view.php?view=upcoming";
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(calendarUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Document doc = Jsoup.parse(response.body());

            // 2. Find the Event Cards
            // Moodle usually wraps events in divs with class "event" or "calendar_event_item"
            // Note: Selectors might need tweaking if Bilkent uses a custom theme.
            // We look for any div that looks like an event card.
            org.jsoup.select.Elements eventCards = doc.select(".event"); 

            if (eventCards.isEmpty()) {
                // Fallback: Try the "m-t-1" class which is common in some Moodle themes
                eventCards = doc.select("div[data-region='event-item']");
            }

            System.out.println("Found " + eventCards.size() + " raw event elements.");

            for (Element card : eventCards) {
                String title = card.select("h3.name").text(); // Title usually in h3
                String dateText = card.select(".date").text(); // Date usually in a class .date
                String courseName = card.select(".course").text(); // Course name if available
                
                // If title is empty, skip (it might be a layout div)
                if (title.isEmpty()) continue;

                CalendarEvent event = new CalendarEvent();
                event.setTitle(title + " (" + courseName + ")");
                event.setLocation("Moodle Submission");
                event.setImportance(Importance.MUST); // Assignments are critical!
                
                System.out.println("   (Debug) Raw Moodle Date: " + dateText); // Let's see what we got
                event.setStartTime(parseMoodleDate(dateText));
                event.setEndTime(event.getStartTime().plusHours(1)); // Default 1 hour duration
                
                moodleEvents.add(event);
                System.out.println("Scraped Assignment: " + event.getTitle() + " Due: " + event.getStartTime());
                
                moodleEvents.add(event);
                System.out.println("Scraped Assignment: " + title);
            }

        } catch (Exception e) {
            System.err.println("Error fetching Moodle events: " + e.getMessage());
            e.printStackTrace();
        }

        return moodleEvents;
    }

    @Override
    public ArrayList<AcademicFile> fetchFiles() {
        return new ArrayList<>();
    }

    // Helper to turn a Map into "key=value&key2=value2" string
    private String buildFormData(Map<String, String> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.length() > 0) builder.append("&");
            builder.append(java.net.URLEncoder.encode(entry.getKey(), java.nio.charset.StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(java.net.URLEncoder.encode(entry.getValue(), java.nio.charset.StandardCharsets.UTF_8));
        }
        return builder.toString();
    }

    /**
     * Parses Moodle date formats like "Friday, 12 December, 17:00"
     * or "Yesterday, 14:00"
     */
    private LocalDateTime parseMoodleDate(String rawDate) {
        try {
            // 1. Clean the string
            // Input: "Friday, 12 December, 17:00"
            // Remove the day name if present (anything before the first comma)
            String cleanDate = rawDate;
            if (rawDate.contains(",")) {
                // If it splits into 3 parts (Day, Date, Time), take the last two
                // Example: "Friday, 12 December, 17:00" -> " 12 December, 17:00"
                int firstComma = rawDate.indexOf(",");
                cleanDate = rawDate.substring(firstComma + 1).trim();
            }

            // 2. Handle "Tomorrow" / "Yesterday" special cases
            if (cleanDate.toLowerCase().startsWith("tomorrow")) {
                String timePart = cleanDate.split(",")[1].trim(); // "17:00"
                java.time.LocalTime time = java.time.LocalTime.parse(timePart, java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                return LocalDateTime.now().plusDays(1).with(time);
            }

            // 3. Standard Parse: "12 December, 17:00"
            // We append the current year because Moodle often hides it
            int currentYear = java.time.Year.now().getValue();
            String dateWithYear = cleanDate + " " + currentYear; // "12 December, 17:00 2025"
            
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("d MMMM, HH:mm yyyy", java.util.Locale.ENGLISH);
            
            return LocalDateTime.parse(dateWithYear, formatter);

        } catch (Exception e) {
            System.err.println("Could not parse date: '" + rawDate + "' -> Using NOW.");
            return LocalDateTime.now();
        }
    }
}
