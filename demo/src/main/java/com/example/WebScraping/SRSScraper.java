package com.example.WebScraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
// import com.example.Entity.*; // Keep if you use Entity classes

import com.example.Entity.CalendarEvent;
import com.example.Entity.Importance;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SRSScraper {

    // URL constants
    private static final String STARS_BASE_URL = "https://stars.bilkent.edu.tr";
    private static final String SRS_LOGIN_URL = STARS_BASE_URL + "/accounts/login";
    private static final String SRS_SMS_VER_URL = STARS_BASE_URL + "/accounts/auth/verifySms";

    private HttpClient client;
    private CookieManager cookieManager;

    public SRSScraper() {
        this.cookieManager = new CookieManager();
        this.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        this.client = HttpClient.newBuilder()
                .cookieHandler(this.cookieManager)
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    // --- MASTER METHOD ---
    public boolean performFullLogin(String username, String password) {
        try {
            // Here we are sending login request using our method
            HttpResponse<String> response = sendLoginRequest(username, password);
            String responseHtml = response.body();

            // Here we are analyzing the result
            if (responseHtml.contains("verification code") || response.uri().toString().contains("verifySms")) {
                System.out.println("SMS Verification Required!");

                // Get code from user (Scanner logic here is for testing)
                Scanner scn = new Scanner(System.in);
                System.out.print("Enter SMS Code: ");
                String smsCode = scn.nextLine().trim();

                return verifySMSCode(responseHtml, smsCode);
            }
            // If we get here, login failed
            else {
                System.err.println("Login failed. Invalid credentials or unknown page.");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // This method manages the login process
    private HttpResponse<String> sendLoginRequest(String ID, String password) throws Exception {
        System.out.println("Fetching SRS Login Page...");

        // Here we are sending a get request to the login page, so that we see the login
        // page.
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://stars.bilkent.edu.tr/srs/"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        String html = getResponse.body();
        Document doc = Jsoup.parse(html);

        Map<String, String> formData = new HashMap<>();

        // Find every <input> tag that is hidden, these fields are required for post
        // request.
        for (Element input : doc.select("input[type=hidden]")) {
            String name = input.attr("name");
            String value = input.attr("value");

            if (!name.isEmpty()) {
                formData.put(name, value);
            }
        }

        // We are gathering data for formating to send a post request
        formData.put("LoginForm[username]", ID);
        formData.put("LoginForm[password]", password);
        formData.put("yt0", "Login"); // The button name

        String formBody = buildFormData(formData);

        // Here we are sending the post request
        System.out.println("2. Sending Credentials...");
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(SRS_LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();

        return client.send(postRequest, HttpResponse.BodyHandlers.ofString());
    }

    // This method handles the SMS verification process
    private boolean verifySMSCode(String smsPageHtml, String smsCode) throws Exception {
        System.out.println("3. Verifying SMS Code...");

        Document doc = Jsoup.parse(smsPageHtml);

        // We are finding all hidden inputs again
        Map<String, String> formData = new HashMap<>();

        for (Element input : doc.select("input[type=hidden]")) {
            String name = input.attr("name");
            String value = input.attr("value");

            if (!name.isEmpty()) {
                formData.put(name, value);
            }
        }

        // We are adding the users SMS code
        formData.put("SmsVerifyForm[verifyCode]", smsCode);
        // We are adding the Verify button name
        formData.put("yt0", "Verify");

        String formBody = buildFormData(formData);

        // Here we are sending the post request
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(SRS_SMS_VER_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Referer", SRS_SMS_VER_URL)
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        // We are checking if the login was successful
        if (response.body().contains("STARS::SRS") || response.body().contains("xenon")) {
            System.out.println("SMS Verified Successfully! We are in!");

            return true;
        } else {
            System.err.println("SMS Verification Failed.");
            return false;
        }
    }

    // This method fetches exams from the v2 system
    public ArrayList<CalendarEvent> fetchExams() {
        ArrayList<CalendarEvent> examEvents = new ArrayList<>();

        try {
            System.out.println("Fetching Exams...");
            String examsUrl = "https://stars.bilkent.edu.tr/srs-v2/exams/finals";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(examsUrl))
                    .header("Referer", "https://stars.bilkent.edu.tr/srs/")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (!response.body().contains("Exams/Activities")) {
                System.err.println(">> Failed to reach Exams page.");
                return examEvents;
            }

            Document doc = Jsoup.parse(response.body());

            // We are getting the table directly
            Element table = doc.selectFirst("table");
            if (table == null) {
                System.err.println(">> No table found in the HTML!");
                return examEvents;
            }

            // We are selecting all rows (skip tbody check to be safe)
            org.jsoup.select.Elements rows = table.select("tr");

            // Iterate through the rows
            for (Element row : rows) {
                // We are selecting both td and th (some tables use th for body cells)
                org.jsoup.select.Elements cols = row.select("td, th");

                if (cols.size() < 5) {
                    continue;
                }

                // Extracting raw strings
                String courseCode = cols.get(1).text(); // "MATH 132 - 002"
                String type = cols.get(2).text(); // "Midterm"
                String name = cols.get(3).text(); // "MT1"
                String rawTime = cols.get(4).text(); // "16.10.2025 19:30 - 21:30"

                // Sanitize: text() might contain non-breaking spaces (\u00A0) which break
                // parsing
                rawTime = rawTime.replace("\u00A0", " ").trim();

                // 2. Parse Dates
                LocalDateTime[] times = parseExamTime(rawTime);
                if (times == null)
                    continue; // Skip if date format is weird

                // 3. Create a Descriptive Title
                // e.g., "MATH 132 - Midterm (MT1)"
                String eventTitle = courseCode.split("-")[0].trim() + " - " + type + " (" + name + ")";

                // 4. Create and Add the Object
                CalendarEvent event = new CalendarEvent(eventTitle, times[0], times[1], Importance.MUST);

                examEvents.add(event);
                System.out.println(">> Added Event: " + eventTitle + " on " + times[0]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return examEvents;
    }

    // Helper (Unchanged)
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
     * Helper to parse string: "16.10.2025 19:30 - 21:30"
     * Returns an array where [0] is Start Time and [1] is End Time
     */
    private LocalDateTime[] parseExamTime(String rawString) {
        try {
            // rawString format: "dd.MM.yyyy HH:mm - HH:mm"
            // Example: "16.10.2025 19:30 - 21:30"

            String[] parts = rawString.split(" - ");
            String dateAndStart = parts[0]; // "16.10.2025 19:30"
            String endTimeOnly = parts[1]; // "21:30"

            // 1. Parse Start Time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime start = LocalDateTime.parse(dateAndStart, formatter);

            // 2. Parse End Time
            // We take the date from the 'start' object and combine it with the new time
            String[] endParts = endTimeOnly.split(":");
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            LocalDateTime end = start.withHour(endHour).withMinute(endMinute);

            return new LocalDateTime[] { start, end };

        } catch (Exception e) {
            // System.err.println(">> Could not parse date: " + rawString);
            return null;
        }
    }
}