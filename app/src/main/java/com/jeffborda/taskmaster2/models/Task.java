package com.jeffborda.taskmaster2.models;

public class Task {

    private String title;
    private String description;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("TITLE: %s. DESCRIPTION: %s", this.title, this.description);
    }
}
