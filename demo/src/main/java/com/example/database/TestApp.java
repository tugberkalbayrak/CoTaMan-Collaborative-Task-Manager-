package com.example.database;

import com.example.Entity.Visibility;

import java.io.File;
import java.awt.Desktop;
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
            LocalDateTime.now().plusHours(1),
            Importance.OPTIONAL
        );
        repo.saveEvent(ders);

        
        List<CalendarEvent> zeynepHocaninDersleri = repo.getEventsForUser(u1.getId());
        System.out.println(">> Zeynep Hocanın Dersleri: " + zeynepHocaninDersleri);

       
        AcademicFile notlar = new AcademicFile("osurdum notlari", "files/notes/", u1, FileType.LECTURE_NOTE, Visibility.PUBLIC);
        
        repo.saveFileMetadata(notlar);

        AcademicFile yeniNot = new AcademicFile();
        yeniNot.setFileName("yeni not");
        yeniNot.setDiskPath("C:\\Users\\lekol\\Downloads\\İstanbul şehri (2).pdf");
        yeniNot.setUploader(u1);
        repo.saveFileMetadata(yeniNot);

        CalendarScraper scraper = new CalendarScraper();

        //List<DateInfo> tarihler = scraper.fetchDates();
        //repo.saveAllDates(tarihler);

        System.out.println("--- Test Tamamlandı ---");

        AcademicFile secilenDosya = repo.getFileByName(yeniNot.getFileName());
        String dosyaYolu = secilenDosya.getDiskPath();
        File dosya = new File(dosyaYolu);
        
        if (dosya.exists()) {
        try {
            // İşletim sisteminin varsayılan uygulamasıyla açar (PDF ise PDF okuyucu, Resim ise Fotoğraflar vb.)
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(dosya); 
                System.out.println("Dosya açılıyor: " + dosya.getName());
            } else {
                System.out.println("Bu sistemde dosya açma işlemi desteklenmiyor.");
            }
        } catch (Exception e) {
            System.out.println("Dosya açılırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    } else {
        System.out.println("HATA: Dosya diskte bulunamadı! Yol: " + dosyaYolu);
    }   
    }
}