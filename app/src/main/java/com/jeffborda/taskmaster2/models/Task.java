package com.jeffborda.taskmaster2.models;

import androidx.annotation.NonNull;

public class Task {

    private String title;
    private String description;
    private TaskState taskState;

    // If TaskState is not taken in the constructor, it is set to NEW
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.taskState = TaskState.NEW;
    }

    // Alternate constructor to take in a TaskState
    public Task(String title, String description, TaskState taskState) {
        this.title = title;
        this.description = description;
        this.taskState = taskState;
    }

    public Task() { }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public TaskState getTaskState() {
        return this.taskState;
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    @Override
    @NonNull
    public String toString() {
        return String.format("TITLE: %s DESCRIPTION: %s STATE: %s", this.title, this.description, this.taskState);
    }
}
