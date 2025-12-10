package com.example;

import java.util.ArrayList;

/*Defines the standard methods that any scraping class must provide. */

public interface IDataFetcher {
    /*Scrapes and returns a list of deadlines, exams, or lectures. */
    ArrayList<CalendarEvent> fetchEvents();

    /*Scrapes and returns course materials (PDFs, slides). */
    ArrayList<AcademicFile> fetchFiles();
} 
