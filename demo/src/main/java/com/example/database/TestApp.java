package com.example.database;

import java.util.List;

import com.example.Entity.DateInfo;
import com.example.WebScraping.CalendarScraper;

public class TestApp {
    public static void main(String[] args) {
        System.out.println("--- CoTaMan Tam Sistem Testi ---");

        CloudRepository repo = new CloudRepository();

        
        User u1 = new User("Zeynep Hoca", "zeynep@bilkent.edu.tr");
        repo.saveUser(u1); 

       
        Event ders = new Event(
            u1.getId(), 
            "CS102 Dersi", 
            "B-Z01", 
            Importance.MUST, 
            "2025-10-10 08:30", 
            "2025-10-10 10:20"
        );
        repo.saveEvent(ders);

        
        List<Event> zeynepHocaninDersleri = repo.getEventsForUser(u1.getId());
        System.out.println(">> Zeynep Hocanın Dersleri: " + zeynepHocaninDersleri);

       
        ArchiveFile notlar = new ArchiveFile(
            "Hafta_1_Notlari.pdf", 
            "C:/Belgelerim/Notlar.pdf", 
            u1.getId(), 
            FileType.LECTURE_NOTES, 
            Visibility.PUBLIC
        );
        repo.saveFileMetadata(notlar);

        CalendarScraper scraper = new CalendarScraper();
        List<DateInfo> tarihler = scraper.fetchDates();
        repo.saveAllDates(tarihler);

        System.out.println("--- Test Tamamlandı ---");
    }
}