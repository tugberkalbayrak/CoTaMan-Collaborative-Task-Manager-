package com.example.Services.ArchiveService.CalendarService;

import java.util.ArrayList;
import java.util.List;
import com.example.Entity.*;

public class RecommendationSystem {

    public List<AcademicFile> getSuggestedMaterials(ExamEvent exam, Group group) {
        List<String> topics = exam.getTopics();
        List<AcademicFile> suggestions = new ArrayList<>();
        List<AcademicFile> groupFiles = group.getGroupArchive();

        if (groupFiles != null && topics != null) {
            for (AcademicFile file : groupFiles) {
                for (String topic : topics) {
                    if (file.getFileName().toLowerCase().contains(topic.toLowerCase())) {
                        suggestions.add(file);
                        break;
                    }
                }
            }
        }
        return suggestions;
    }
}