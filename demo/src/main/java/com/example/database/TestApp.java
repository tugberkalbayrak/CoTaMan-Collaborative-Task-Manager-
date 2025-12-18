package com.example.database;

import java.time.LocalDateTime;
import java.util.List;

import com.example.Entity.AcademicFile;
import com.example.Entity.CalendarEvent;
import com.example.Entity.DateInfo;
import com.example.Entity.FileType;
import com.example.Entity.Importance;
import com.example.Entity.User;
import com.example.WebScraping.CalendarScraper;

public class TestApp {
    public static void main(String[] args) {
        System.out.println("--- CoTaMan Tam Sistem Testi ---");

        CloudRepository repo = new CloudRepository();


        User u1 = new User("224030303" ,"Zeynep Hoca", "zeynep@bilkent.edu.tr");
        repo.saveUser(u1); 

       
        CalendarEvent ders = new CalendarEvent(
            u1, 
            "CS102 Dersi", 
            "B-Z01", 
             
            LocalDateTime.now(), 
            LocalDateTime.now().plusHours(2),Importance.OPTIONAL
        );
        repo.saveEvent(ders);

        
        List<CalendarEvent> zeynepHocaninDersleri = repo.getEventsForUser(u1.getId());
        System.out.println(">> Zeynep Hocanın Dersleri: " + zeynepHocaninDersleri);

       
        AcademicFile notlar = new AcademicFile("osurdum notlari", "files/notes/", u1, FileType.LECTURE_NOTE, null);
        
        repo.saveFileMetadata(notlar);

        CalendarScraper scraper = new CalendarScraper();

        List<DateInfo> tarihler = scraper.fetchDates();
        repo.saveAllDates(tarihler);

        System.out.println("--- Test Tamamlandı ---");
    }
}