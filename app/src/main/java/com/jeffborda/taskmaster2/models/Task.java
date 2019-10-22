package com.jeffborda.taskmaster2.models;

import androidx.annotation.NonNull;

public class Task {

    private String title;
    private String description;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task() { }

    String getTitle() {
        return this.title;
    }

    String getDescription() {
        return this.description;
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setDescription(String description) {
        this.description = description;
    }

    @Override
    @NonNull
    public String toString() {
        return String.format("TITLE: %s DESCRIPTION: %s", this.title, this.description);
    }
}
