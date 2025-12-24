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

private static final String MOODLE_BASE_URL = "https://moodle.bilkent.edu.tr/2025-2026-fall";
    private static final String LOGIN_URL = MOODLE_BASE_URL + "/login/index.php";

    private HttpClient client;
    private CookieManager cookieManager;
    private boolean isLoggedIn = false;

    public MoodleScraper() {

this.cookieManager = new CookieManager();
        this.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

this.client = HttpClient.newBuilder()
                .cookieHandler(this.cookieManager)
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

public boolean connect(String username, String password) {
        try {
            System.out.println("Fetching login token from Moodle...");

HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(LOGIN_URL))
                    .GET()
                    .build();

            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            String loginPageHtml = getResponse.body();

Document doc = Jsoup.parse(loginPageHtml);
            Element tokenInput = doc.selectFirst("input[name=logintoken]");

            if (tokenInput == null) {
                System.err.println("Could not find login token. Moodle structure might have changed.");
                return false;
            }

            String loginToken = tokenInput.val();
            System.out.println("Token found: " + loginToken.substring(0, 10) + "...");

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

String calendarUrl = MOODLE_BASE_URL + "/calendar/view.php?view=upcoming";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(calendarUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Document doc = Jsoup.parse(response.body());

org.jsoup.select.Elements eventCards = doc.select(".event");

            if (eventCards.isEmpty()) {
                 
                eventCards = doc.select("div[data-region='event-item']");
            }

            System.out.println("Found " + eventCards.size() + " raw event elements.");

            for (Element card : eventCards) {
                String title = card.select("h3.name").text();  
                String dateText = card.select(".date").text();  
                String courseName = card.select(".course").text();  

if (title.isEmpty())
                    continue;

                CalendarEvent event = new CalendarEvent();
                event.setTitle(title + " (" + courseName + ")");
                event.setLocation("Moodle Submission");
                event.setImportance(Importance.MUST);  

                System.out.println("   (Debug) Raw Moodle Date: " + dateText);  

LocalDateTime deadline = parseMoodleDate(dateText);
                event.setEndTime(deadline);
                event.setStartTime(deadline.minusHours(1));  

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

private LocalDateTime parseMoodleDate(String rawDate) {
        try {

String cleanDate = rawDate;
            if (rawDate.contains(",")) {

int firstComma = rawDate.indexOf(",");
                cleanDate = rawDate.substring(firstComma + 1).trim();
            }

if (cleanDate.toLowerCase().startsWith("tomorrow")) {
                String timePart = cleanDate.split(",")[1].trim();  
                java.time.LocalTime time = java.time.LocalTime.parse(timePart,
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                return LocalDateTime.now().plusDays(1).with(time);
            }

int currentYear = java.time.Year.now().getValue();
            String dateWithYear = cleanDate + " " + currentYear;  

            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("d MMMM, HH:mm yyyy", java.util.Locale.ENGLISH);

            return LocalDateTime.parse(dateWithYear, formatter);

        } catch (Exception e) {
            System.err.println("Could not parse date: '" + rawDate + "' -> Using NOW.");
            return LocalDateTime.now();
        }
    }
}