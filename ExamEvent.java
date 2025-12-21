package com.example.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExamEvent extends CalendarEvent {
    private List<String> topics;
    private List<String> recommendedMaterialLinks;

    public ExamEvent(User owner, String title, String location, LocalDateTime start, Importance imp) { 
        super(owner, title, location, start, start.plusHours(2), imp); 
        this.topics = new ArrayList<>();
        this.recommendedMaterialLinks = new ArrayList<>();
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public void addTopic(String topic) {
        if (this.topics == null) {
            this.topics = new ArrayList<>();
        }
        this.topics.add(topic);
    }

    public List<String> getRecommendedMaterialLinks() {
        return recommendedMaterialLinks;
    }

    public void setRecommendedMaterialLinks(List<String> recommendedMaterialLinks) {
        this.recommendedMaterialLinks = recommendedMaterialLinks;
    }

    public void addRecommendedMaterialLink(String link) {
        if (this.recommendedMaterialLinks == null) {
            this.recommendedMaterialLinks = new ArrayList<>();
        }
        this.recommendedMaterialLinks.add(link);
    }
}