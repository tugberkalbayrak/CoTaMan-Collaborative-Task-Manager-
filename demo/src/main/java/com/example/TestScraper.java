package com.example;

import java.util.ArrayList;

public class TestScraper {
    public static void main(String[] args) {
        MoodleScraper moodle = new MoodleScraper();

        // REPLACE WITH YOUR REAL CREDENTIALS TO TEST
        // BUT DO NOT SHARE THIS FILE AFTERWARDS
        boolean success = moodle.connect("22403411", "yeniYesil2520");

        if (success) {
            System.out.println("We are in! Ready to scrape assignments.");

            ArrayList<CalendarEvent> assignments = moodle.fetchEvents();
            System.out.println("Found " + assignments.size() + " assignments.");
        }
    }
}
