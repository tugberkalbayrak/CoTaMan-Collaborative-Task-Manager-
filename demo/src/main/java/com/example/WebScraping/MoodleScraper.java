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

public class MoodleScraper {

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
            // If login fails, Moodle usually keeps you on the login page containing
            // "Invalid login"
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
            // Moodle usually wraps events in divs with class "event" or
            // "calendar_event_item"
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

                // --- DEBUG: If date is empty, try harder or log it ---
                if (dateText.isEmpty()) {
                    // Try alternative selectors common in Bilkent Moodle
                    dateText = card.select(".col-xs-11").text(); // Sometimes date is here text
                    if (dateText.isEmpty()) {
                        System.out.println("Wait! Date is empty. Card HTML: " + card.html());
                    }
                }

                String courseName = card.select(".course").text(); // Course name if available

                // If title is empty, skip (it might be a layout div)
                if (title.isEmpty())
                    continue;

                CalendarEvent event = new CalendarEvent();
                event.setTitle(title + " (" + courseName + ")");
                event.setLocation("Moodle Submission");
                event.setImportance(Importance.MUST); // Assignments are critical!

                System.out.println("   (Debug) Raw Moodle Date: " + dateText); // Let's see what we got
                event.setEndTime(parseMoodleDate(dateText));
                event.setStartTime(event.getEndTime().minusHours(1)); // Default 1 hour duration ending at deadline

                moodleEvents.add(event);
                System.out.println("Scraped Assignment: " + event.getTitle() + " Due: " + event.getStartTime());
            }

        } catch (Exception e) {
            System.err.println("Error fetching Moodle events: " + e.getMessage());
            e.printStackTrace();
        }

        return moodleEvents;
    }

    // Helper to turn a Map into "key=value&key2=value2" string
    private String buildFormData(Map<String, String> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.length() > 0)
                builder.append("&");
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
        System.out.println("DEBUG: Parsing Moodle Date from string: '" + rawDate + "'");
        try {
            String lower = rawDate.toLowerCase();

            // 1. Handle "Tomorrow" / "Yesterday" special cases FIRST
            if (lower.contains("tomorrow")) {
                // Format: "Tomorrow, 17:30"
                // Split by comma or space to find time
                String[] parts = rawDate.split(",");
                String timePart = parts[parts.length - 1].trim(); // Take the last part "17:30"
                java.time.LocalTime time = java.time.LocalTime.parse(timePart,
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                return LocalDateTime.now().plusDays(1).with(time);
            }

            if (lower.contains("yesterday")) {
                String[] parts = rawDate.split(",");
                String timePart = parts[parts.length - 1].trim();
                java.time.LocalTime time = java.time.LocalTime.parse(timePart,
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                return LocalDateTime.now().minusDays(1).with(time);
            }

            // 2. Standard Parse: "Friday, 12 December, 17:00" -> Clean day name
            String cleanDate = rawDate;
            if (rawDate.contains(",")) {
                // If it splits into 3 parts (Day, Date, Time), take the last two
                // Example: "Friday, 12 December, 17:00" -> " 12 December, 17:00"
                // But avoid cleaning if it looks like "12 December, 17:00" already
                // Count commas? Bilkent Moodle usually: "DayName, Day Month, Time" (2 commas)
                // Or "Day Month, Time" (1 comma) - Wait, "Sunday, 28 December, 23:59" has 2
                // commas.
                // "Tomorrow, 17:30" has 1 comma.

                // Strategy: If it has 2 commas, strip the first part.
                int commaCount = rawDate.length() - rawDate.replace(",", "").length();
                if (commaCount >= 2) {
                    int firstComma = rawDate.indexOf(",");
                    cleanDate = rawDate.substring(firstComma + 1).trim();
                }
            }

            // 3. Standard Parse
            // We append the current year because Moodle often hides it
            int currentYear = java.time.Year.now().getValue();
            // cleanDate is like "28 December, 23:59"
            String dateWithYear = cleanDate + " " + currentYear; // "28 December, 23:59 2025"

            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("d MMMM, HH:mm yyyy", java.util.Locale.ENGLISH);

            return LocalDateTime.parse(dateWithYear, formatter);

        } catch (Exception e) {
            System.err
                    .println("Could not parse date: '" + rawDate + "' -> Error: " + e.getMessage() + " -> Using NOW.");
            return LocalDateTime.now();
        }
    }
}