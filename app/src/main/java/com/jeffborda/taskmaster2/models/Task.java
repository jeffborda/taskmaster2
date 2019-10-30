package com.jeffborda.taskmaster2.models;

import com.amazonaws.amplify.generated.graphql.ListTasksQuery;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String description;
    @TypeConverters(TaskStateConverter.class)
    private TaskState taskState;

    // If TaskState is not taken in the constructor, it is set to NEW
    @Ignore /* This tells Room to ignore this constructor */
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.taskState = TaskState.NEW;
    }

    // Alternate constructor to take in a TaskState
    @Ignore /* This tells Room to ignore this constructor */
    public Task(String title, String description, TaskState taskState) {
        this.title = title;
        this.description = description;
        this.taskState = taskState;
    }

    public Task(ListTasksQuery.Item item) {
        this.title = item.title();
        this.description = item.description();
        this.taskState = TaskStateConverter.toStatus(item.taskState().ordinal());
    }

    public Task() { }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public TaskState getTaskState() {
        return this.taskState;
    }

    public void setId(long id) {
        this.id = id;
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
