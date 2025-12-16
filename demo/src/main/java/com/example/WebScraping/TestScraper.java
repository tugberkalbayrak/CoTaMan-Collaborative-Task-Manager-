package com.example.WebScraping;

import java.util.ArrayList;
import com.example.Entity.*;

public class TestScraper {
    public static void main(String[] args) {
        SRSScraper SRS = new SRSScraper();
        // REPLACE WITH YOUR REAL CREDENTIALS TO TEST
        // BUT DO NOT SHARE THIS FILE AFTERWARDS
        boolean success = false;
        try {
            success = SRS.performFullLogin("22403411", "928K4G");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (success) {
            System.out.println("We are in! Ready to scrape assignments.");

            // ArrayList<CalendarEvent> assignments = moodle.fetchEvents();
            // System.out.println("Found " + assignments.size() + " assignments.");
        }
    }
}
