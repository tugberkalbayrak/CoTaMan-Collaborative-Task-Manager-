package com.example.WebScraping;

import com.example.Entity.CalendarEvent; // Import your entity
import java.util.ArrayList;
import java.util.Scanner;

public class TestScraper {

    public static void main(String[] args) {
        // 1. Setup
        SRSScraper scraper = new SRSScraper();
        Scanner scanner = new Scanner(System.in);

        // 2. Get Credentials safely
        System.out.println("=== SRS Scraper Test ===");
        System.out.print("Enter Bilkent ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter Password: ");
        String pass = scanner.nextLine();

        try {
            // 3. Perform Login (Includes SMS step)
            System.out.println("\n>> Attempting Login...");
            boolean loginSuccess = scraper.performFullLogin(id, pass);

            if (loginSuccess) {
                System.out.println(">> Login Success! Now fetching data...\n");

                // 4. Fetch and Print Exams
                System.out.println("--- FETCHING EXAMS ---");
                ArrayList<CalendarEvent> exams = scraper.fetchExams();

                if (exams.isEmpty()) {
                    System.out.println("No exams found (or parsing failed).");
                } else {
                    System.out.println("Found " + exams.size() + " exams:");
                    for (CalendarEvent exam : exams) {
                        System.out.println("------------------------------------------------");
                        // Assuming your CalendarEvent has these getters or a toString()
                        System.out.println("Title:       " + exam.getTitle());
                        System.out.println("Start Time:  " + exam.getStartTime());
                        System.out.println("End Time:    " + exam.getEndTime());
                    }
                    System.out.println("------------------------------------------------");
                }
            } else {
                System.err.println("Login Failed. Cannot fetch exams.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}