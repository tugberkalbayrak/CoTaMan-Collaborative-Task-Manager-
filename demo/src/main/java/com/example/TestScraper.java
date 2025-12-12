package com.example;
import java.util.ArrayList;

public class TestScraper {
    public static void main(String[] args) {
        System.out.println("⏳ Connecting to Bilkent Academic Calendar...");

        // 1. Create your scraper
        CalendarScraper scraper = new CalendarScraper();

        // 2. Ask it to fetch events
        ArrayList<CalendarEvent> events = scraper.fetchEvents();

        // 3. Print the results
        System.out.println("------------------------------------------------");
        System.out.println("✅ Found " + events.size() + " critical events:");
        System.out.println("------------------------------------------------");

        for (CalendarEvent event : events) {
            System.out.println("📅 DATE: " + event.getStartTime().toLocalDate());
            System.out.println("📌 EVENT: " + event.getTitle());
            System.out.println("------------------------------------------------");
        }
    }
}
