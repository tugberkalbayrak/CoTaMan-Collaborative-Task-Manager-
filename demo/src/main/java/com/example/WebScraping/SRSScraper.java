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
            // Step 1: Send Login Request
            HttpResponse<String> response = sendLoginRequest(username, password);
            String responseHtml = response.body();

            // Step 2: Analyze the result
            // Case A: Immediate Success (No SMS)
            if (responseHtml.contains("Welcome") || responseHtml.contains("Log Out")) {
                System.out.println(">> Login successful! No SMS needed.");
                return true;
            }
            // Case B: SMS Required
            // (Note: Check for unique keywords on the SMS page, like 'verification code' or
            // the specific form ID)
            else if (responseHtml.contains("verification code") || response.uri().toString().contains("verifySms")) {
                System.out.println(">> SMS Verification Required!");

                // Get code from user (Scanner logic here is fine for testing)
                Scanner scn = new Scanner(System.in);
                System.out.print("Enter SMS Code: ");
                String smsCode = scn.nextLine().trim();

                return verifySMSCode(responseHtml, smsCode);
            }
            // Case C: Failure
            else {
                System.err.println(">> Login failed. Invalid credentials or unknown page.");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- STEP 1: LOGIN ---
    private HttpResponse<String> sendLoginRequest(String ID, String password) throws Exception {
        System.out.println("1. Fetching SRS Login Page...");

        // A. GET the login page
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://stars.bilkent.edu.tr/srs/"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        String html = getResponse.body();
        Document doc = Jsoup.parse(html);

        // B. The "Vacuum Cleaner": Collect ALL hidden inputs automatically
        Map<String, String> formData = new HashMap<>();

        // Find every <input> tag that is hidden
        for (Element input : doc.select("input[type=hidden]")) {
            String name = input.attr("name");
            String value = input.attr("value");

            if (!name.isEmpty()) {
                formData.put(name, value);
                System.out.println(">> Found Hidden Token: " + name + " = " + value); // Debugging
            }
        }

        // C. Add the Credentials (User Input)
        // We already confirmed these names in your screenshot!
        formData.put("LoginForm[username]", ID);
        formData.put("LoginForm[password]", password);
        formData.put("yt0", "Login"); // The button name

        String formBody = buildFormData(formData);

        // D. Send POST
        System.out.println("2. Sending Credentials...");
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(SRS_LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();

        return client.send(postRequest, HttpResponse.BodyHandlers.ofString());
    }

    // --- STEP 2: SMS VERIFICATION ---
    private boolean verifySMSCode(String smsPageHtml, String smsCode) throws Exception {
        System.out.println("3. Verifying SMS Code...");

        Document doc = Jsoup.parse(smsPageHtml);

        // 1. The "Vacuum Cleaner": Collect ALL hidden inputs automatically
        // (This replaces the __VIEWSTATE code that was crashing)
        Map<String, String> formData = new HashMap<>();

        for (Element input : doc.select("input[type=hidden]")) {
            String name = input.attr("name");
            String value = input.attr("value");

            if (!name.isEmpty()) {
                formData.put(name, value);
                // Debugging: see what hidden token the SMS page uses
                System.out.println(">> SMS Page Hidden Token: " + name);
            }
        }

        // 2. Add the User's SMS Code
        // It is likely "SmsVerifyForm[verifyCode]" or just "verifyCode"
        formData.put("SmsVerifyForm[verifyCode]", smsCode);
        // 3. Add the Verify Button (if it has a name)
        // Check if the "Verify" button has a name like "yt0" or "submit"
        formData.put("yt0", "Verify");

        String formBody = buildFormData(formData);

        // 4. Send POST
        // IMPORTANT: Check the URL bar when you are on the SMS page.
        // Is it still /accounts/login or did it change to /accounts/auth/verifySms?
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(SRS_SMS_VER_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Referer", SRS_SMS_VER_URL)
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        // 5. Check Final Success
        if (response.body().contains("STARS::SRS") || response.body().contains("xenon")) {
            System.out.println(">> SMS Verified Successfully! We are in!");

            return true;
        } else {
            System.err.println(">> SMS Verification Failed.");

            // --- NEW DEBUGGING LINES ---
            System.out.println("--- SERVER RESPONSE TEXT ---");
            // This strips out all HTML tags and leaves just the words
            String cleanText = Jsoup.parse(response.body()).text();
            System.out.println(cleanText);
            System.out.println("----------------------------");
            // ---------------------------

            return false;
        }
    }

    /**
     * Phase 3: Fetching Exams from the v2 System
     * URL Source: https://stars.bilkent.edu.tr/srs-v2/exams/finals
     */
    public ArrayList<CalendarEvent> fetchExams() {
        ArrayList<CalendarEvent> examEvents = new ArrayList<>();

        try {
            System.out.println("5. Fetching Exams...");
            String examsUrl = "https://stars.bilkent.edu.tr/srs-v2/exams/finals";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(examsUrl))
                    .header("Referer", "https://stars.bilkent.edu.tr/srs/")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (!response.body().contains("Exams/Activities")) {
                System.err.println(">> Failed to reach Exams page. Body snippet: "
                        + response.body().substring(0, Math.min(500, response.body().length())));
                return examEvents;
            }

            Document doc = Jsoup.parse(response.body());
            System.out.println(">> Exams page parsed. Title: " + doc.title());

            // DEBUG: Print all tables found
            org.jsoup.select.Elements allTables = doc.select("table");
            System.out.println(">> Debug: Found " + allTables.size() + " tables.");
            for (Element t : allTables) {
                System.out.println("   - Table: Class='" + t.className() + "', ID='" + t.id() + "'");
            }

            // 1. Get the table directly (we know there is 1 or at least the first one is
            // likely it)
            Element table = doc.selectFirst("table");
            if (table == null) {
                System.err.println(">> No table found in the HTML!");
                return examEvents;
            }

            // DEBUG: Print start of table HTML to see structure
            String tableHtml = table.html();
            System.out.println(">> Table HTML snippet: "
                    + tableHtml.substring(0, Math.min(300, tableHtml.length())).replace("\n", " "));

            // 2. Select ALL rows (skip tbody check to be safe)
            org.jsoup.select.Elements rows = table.select("tr");
            System.out.println(">> Found " + rows.size() + " total rows in table.");

            // Iterate through the rows
            for (Element row : rows) {
                // Fix: Select both td and th (some tables use th for body cells)
                org.jsoup.select.Elements cols = row.select("td, th");

                if (cols.size() < 5) {
                    // It's likely a header row or empty
                    continue;
                }

                // 1. Extract Raw Strings
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
            System.err.println(">> Could not parse date: '" + rawString + "'");
            return null;
        }
    }
}