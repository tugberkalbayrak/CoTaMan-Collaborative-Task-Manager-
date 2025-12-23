package com.example.Entity;

public enum Importance {
    TRIVIA(1), OPTIONAL(2), MUST(3), CRITICAL(4);

    private final int weight;

    Importance(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
