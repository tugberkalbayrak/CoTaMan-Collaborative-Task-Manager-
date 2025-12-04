package com.example;

public enum Importance {
    TRIVIA(1), OPTIONAL(2), MUST(3);
    private final int weight;
    Importance(int weight) { this.weight = weight; }
    public int getWeight() { return weight; }
}

enum Visibility {
    PRIVATE, GROUP, PUBLIC
}

enum FileType {
    SYLLABUS, LECTURE_NOTE, PAST_EXAM
}