package com.example.WebScraping;

import java.util.ArrayList;
import com.example.Entity.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarScraper {

private static final String CALENDAR_URL = "https://w3.bilkent.edu.tr/bilkent/academic-calendar/";

private static final String[] CRITICAL_KEYWORDS = {
            "Holiday", "Registration", "Deadline", "Add", "Drop",
            "Withdraw", "Grades Announced", "Classes Begin", "Final"
    };

    public ArrayList<DateInfo> fetchDates() {
        ArrayList<DateInfo> criticalDates = new ArrayList<DateInfo>();

        try {
             
            Document doc = Jsoup.connect(CALENDAR_URL).get();

Elements rows = doc.select("table tr");

            for (Element row : rows) {
                 
                Elements columns = row.select("td");

                if (columns.size() >= 2) {
                    String dateText = columns.get(0).text();
                    String description = columns.get(1).text();

if (isCritical(description)) {

DateInfo newDateInfo = new DateInfo();
                        newDateInfo.setDescription(description);

LocalDate eventDate = parseDate(dateText);
                        newDateInfo.setDate(eventDate);

                        criticalDates.add(newDateInfo);
                        System.out.println("Scraped: " + description + " on " + eventDate.toString());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error scraping calendar: " + e.getMessage());
            e.printStackTrace();
        }

        return criticalDates;
    }

private boolean isCritical(String description) {
        for (String keyword : CRITICAL_KEYWORDS) {
            if (description.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

private LocalDate parseDate(String rawDate) {
        try {

String cleanDate = rawDate.split(",")[0].trim();

if (cleanDate.contains("-")) {
                cleanDate = cleanDate.split("-")[0].trim() + " " + cleanDate.split(" ")[1] + " "
                        + cleanDate.split(" ")[2];
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
            LocalDate date = LocalDate.parse(cleanDate, formatter);
            return date;
        } catch (Exception e) {
             
            return LocalDate.now();
        }
    }

public ArrayList<AcademicFile> fetchFiles() {
        return null;
    }
}
