package com.example.WebScraping;

import java.util.ArrayList;
import com.example.Entity.*;

/*Defines the standard methods that any scraping class must provide. */

public interface IDataFetcher {
    /*Scrapes and returns a list of deadlines, exams, or lectures. */
    ArrayList<CalendarEvent> fetchEvents();

    /*Scrapes and returns course materials (PDFs, slides). */
    ArrayList<AcademicFile> fetchFiles();
} 
