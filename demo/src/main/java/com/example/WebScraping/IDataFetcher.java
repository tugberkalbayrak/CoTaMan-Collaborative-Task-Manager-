package com.example.WebScraping;

import java.util.ArrayList;
import com.example.Entity.*;

public interface IDataFetcher {
     
    ArrayList<CalendarEvent> fetchEvents();

ArrayList<AcademicFile> fetchFiles();
} 
