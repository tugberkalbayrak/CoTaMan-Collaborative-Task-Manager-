package com.example;

import java.time.LocalDateTime;
import java.util.List;

public class ExamEvent extends CalendarEvent {
    private List<String> topics;
    private List<String> recommendedMaterialLinks;

    public ExamEvent(String title, LocalDateTime start, Importance imp) { super(title, start, start.plusHours(2), imp); }
}
