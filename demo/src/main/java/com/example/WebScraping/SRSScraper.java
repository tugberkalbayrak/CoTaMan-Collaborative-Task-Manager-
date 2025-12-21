package com.example.WebScraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.example.Entity.CalendarEvent;
import com.example.Entity.Importance;
import com.example.Entity.WeeklyLecture;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SRSScraper {

    private static final String STARS_BASE_URL = "https://stars.bilkent.edu.tr";
    private static final String SRS_LOGIN_URL = STARS_BASE_URL + "/accounts/login";
    private static final String SRS_SMS_VER_URL = STARS_BASE_URL + "/accounts/auth/verifySms";

    private HttpClient client;
    private CookieManager cookieManager;
    private String lastResponseHtml; // SMS sayfasındaki tokenları saklamak için

    public SRSScraper() {
        this.cookieManager = new CookieManager();
        this.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.client = HttpClient.newBuilder()
                .cookieHandler(this.cookieManager)
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    // --- ADIM 1: KULLANICI ADI ŞİFRE GÖNDER ---
    // Dönüş Değeri:
    // 0 = Başarısız
    // 1 = Başarılı (Direkt girdi - nadirdir)
    // 2 = SMS Gerekli (En olası durum)
    public int startLogin(String username, String password) {
        try {
            HttpResponse<String> response = sendLoginRequest(username, password);
            this.lastResponseHtml = response.body();

            if (lastResponseHtml.contains("verification code") || response.uri().toString().contains("verifySms")) {
                return 2; // SMS GEREKLİ
            } else if (lastResponseHtml.contains("Welcome") || lastResponseHtml.contains("Log Out")) {
                return 1; // DİREKT GİRİŞ
            } else {
                return 0; // HATA
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // --- ADIM 2: SMS KODUNU GÖNDER ---
    public boolean verifySmsCode(String smsCode) {
        try {
            return verifySMSCodeInternal(this.lastResponseHtml, smsCode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- YARDIMCI METOTLAR ---

    private HttpResponse<String> sendLoginRequest(String ID, String password) throws Exception {
        HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create("https://stars.bilkent.edu.tr/srs/")).GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        Document doc = Jsoup.parse(getResponse.body());
        Map<String, String> formData = new HashMap<>();
        for (Element input : doc.select("input[type=hidden]")) {
            if (!input.attr("name").isEmpty())
                formData.put(input.attr("name"), input.attr("value"));
        }

        formData.put("LoginForm[username]", ID);
        formData.put("LoginForm[password]", password);
        formData.put("yt0", "Login");

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(SRS_LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(buildFormData(formData)))
                .build();

        return client.send(postRequest, HttpResponse.BodyHandlers.ofString());
    }

    private boolean verifySMSCodeInternal(String smsPageHtml, String smsCode) throws Exception {
        Document doc = Jsoup.parse(smsPageHtml);
        Map<String, String> formData = new HashMap<>();
        for (Element input : doc.select("input[type=hidden]")) {
            if (!input.attr("name").isEmpty())
                formData.put(input.attr("name"), input.attr("value"));
        }

        formData.put("SmsVerifyForm[verifyCode]", smsCode);
        formData.put("yt0", "Verify");

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(SRS_SMS_VER_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Referer", SRS_SMS_VER_URL)
                .POST(HttpRequest.BodyPublishers.ofString(buildFormData(formData)))
                .build();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        return response.body().contains("STARS::SRS") || response.body().contains("xenon");
    }

    // --- VERİ ÇEKME ---

    public ArrayList<WeeklyLecture> fetchWeeklySchedule() {
        ArrayList<WeeklyLecture> schedule = new ArrayList<>();
        try {
            String scheduleUrl = "https://stars.bilkent.edu.tr/srs-v2/schedule/index/weekly";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(scheduleUrl)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Document doc = Jsoup.parse(response.body());
            Element table = doc.selectFirst("table");
            if (table == null)
                return schedule;

            int ROWS = 20;
            int COLS = 8;
            boolean[][] occupied = new boolean[ROWS][COLS];
            org.jsoup.select.Elements rows = doc.select("table tbody tr");
            DayOfWeek[] days = { null, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY };

            int rowIndex = 0;
            for (Element row : rows) {
                org.jsoup.select.Elements cells = row.select("td, th");
                String timeSlotText = "";
                int currentCellPtr = 0;

                for (int col = 0; col < COLS; col++) {
                    if (occupied[rowIndex][col])
                        continue;
                    if (currentCellPtr >= cells.size())
                        break;

                    Element cell = cells.get(currentCellPtr++);
                    int rowSpan = 1;
                    try {
                        rowSpan = Integer.parseInt(cell.attr("rowspan"));
                    } catch (NumberFormatException e) {
                    }

                    for (int r = 0; r < rowSpan; r++) {
                        if (rowIndex + r < ROWS)
                            occupied[rowIndex + r][col] = true;
                    }

                    if (col == 0) {
                        timeSlotText = cell.text().trim();
                    } else {
                        String text = cell.text().trim();
                        if (!text.isEmpty() && timeSlotText.contains("-")) {
                            LocalTime[] slotTimes = parseTimeSlot(timeSlotText);
                            if (slotTimes != null) {
                                WeeklyLecture lecture = new WeeklyLecture(
                                        text.split("Face-to-face")[0].trim(),
                                        text.contains("B-") ? "B-..." : "Unknown",
                                        "Lecture",
                                        days[col], slotTimes[0], slotTimes[0].plusMinutes(rowSpan * 60 - 10));
                                schedule.add(lecture);
                            }
                        }
                    }
                }
                rowIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schedule;
    }

    public ArrayList<CalendarEvent> fetchExams() {
        ArrayList<CalendarEvent> examEvents = new ArrayList<>();
        try {
            String examsUrl = "https://stars.bilkent.edu.tr/srs-v2/exams/finals";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(examsUrl))
                    .header("Referer", "https://stars.bilkent.edu.tr/srs/").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (!response.body().contains("Exams/Activities"))
                return examEvents;

            Document doc = Jsoup.parse(response.body());
            Element table = doc.selectFirst("table");
            if (table == null)
                return examEvents;

            for (Element row : table.select("tr")) {
                org.jsoup.select.Elements cols = row.select("td, th");
                if (cols.size() < 5)
                    continue;

                String courseCode = cols.get(1).text();
                String type = cols.get(2).text();
                String rawTime = cols.get(4).text().replace("\u00A0", " ").trim();

                LocalDateTime[] times = parseExamTime(rawTime);
                if (times == null)
                    continue;

                CalendarEvent event = new CalendarEvent();
                event.setTitle(courseCode + " - " + type);
                event.setImportance(Importance.MUST);
                event.setStartTime(times[0]);
                event.setEndTime(times[1]);
                examEvents.add(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return examEvents;
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

    private LocalTime[] parseTimeSlot(String raw) {
        try {
            String[] parts = raw.split("-");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            return new LocalTime[] { LocalTime.parse(parts[0].trim(), fmt), LocalTime.parse(parts[1].trim(), fmt) };
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime[] parseExamTime(String rawString) {
        try {
            String[] parts = rawString.split(" - ");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime start = LocalDateTime.parse(parts[0], fmt);
            String[] endParts = parts[1].split(":");
            LocalDateTime end = start.withHour(Integer.parseInt(endParts[0])).withMinute(Integer.parseInt(endParts[1]));
            return new LocalDateTime[] { start, end };
        } catch (Exception e) {
            return null;
        }
    }
}