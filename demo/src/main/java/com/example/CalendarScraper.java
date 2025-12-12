package com.example;

import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarScraper implements IDataFetcher {

    // The target URL for the English version of the calendar
    private static final String CALENDAR_URL = "https://w3.bilkent.edu.tr/bilkent/academic-calendar/";

    // Keywords to filter only the critical events you asked for
    private static final String[] CRITICAL_KEYWORDS = {
            "Holiday", "Registration", "Deadline", "Add", "Drop",
            "Withdraw", "Grades Announced", "Classes Begin", "Final"
    };

    @Override
    public ArrayList<CalendarEvent> fetchEvents() {
        ArrayList<CalendarEvent> criticalEvents = new ArrayList<>();

        try {
            // 1. Connect and Download HTML
            Document doc = Jsoup.connect(CALENDAR_URL).get();

            // 2. Select the rows from the table
            // We select 'tr' elements inside the 'table'
            Elements rows = doc.select("table tr");

            for (Element row : rows) {
                // Bilkent's table usually has 2 columns: Date | Description
                Elements columns = row.select("td");

                if (columns.size() >= 2) {
                    String dateText = columns.get(0).text();
                    String description = columns.get(1).text();

                    // 3. Filter: Check if this row is one of our critical dates
                    if (isCritical(description)) {

                        // 4. Create the Event object
                        // Note: We use a helper method to handle date ranges
                        CalendarEvent newEvent = new CalendarEvent();
                        newEvent.setTitle(description);
                        newEvent.setImportance(Importance.MUST); // Academic dates are high priority
                        newEvent.setLocation("Bilkent"); // Default location

                        // Parse the date (e.g., "29 October 2024")
                        LocalDateTime eventDate = parseDate(dateText);
                        newEvent.setStartTime(eventDate);
                        newEvent.setEndTime(eventDate.plusHours(1)); // Default duration

                        criticalEvents.add(newEvent);
                        System.out.println("Scraped: " + description + " on " + eventDate.toLocalDate());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error scraping calendar: " + e.getMessage());
            e.printStackTrace();
        }

        return criticalEvents;
    }

    // --- Helper Methods ---

    /**
     * Checks if the event description matches our list of critical keywords.
     */
    private boolean isCritical(String description) {
        for (String keyword : CRITICAL_KEYWORDS) {
            if (description.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses Bilkent's date format (e.g., "15 September 2025").
     * Uses English Locale since we scrape the English page.
     */
    private LocalDateTime parseDate(String rawDate) {
        try {
            // Clean up the string (remove day names like ", Tuesday")
            // Input: "15 September 2025, Monday" -> Output: "15 September 2025"
            String cleanDate = rawDate.split(",")[0].trim();

            // If it's a range like "7-13 September", we just take the start date for
            // simplicity
            if (cleanDate.contains("-")) {
                cleanDate = cleanDate.split("-")[0].trim() + " " + cleanDate.split(" ")[1] + " "
                        + cleanDate.split(" ")[2];
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
            LocalDate date = LocalDate.parse(cleanDate, formatter);
            return date.atStartOfDay(); // Default to 00:00 time
        } catch (Exception e) {
            // Fallback for unexpected formats
            return LocalDateTime.now();
        }
    }

    /*
     * Returns null or an empty list, as the academic calendar page does not contain
     * downloadable files.
     */
    public ArrayList<AcademicFile> fetchFiles() {
        return null;
    }
}
