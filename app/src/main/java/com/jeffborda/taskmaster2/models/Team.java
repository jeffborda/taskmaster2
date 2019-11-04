package com.jeffborda.taskmaster2.models;

import java.util.LinkedList;
import java.util.List;

public class Team {

    String id;
    String title;
    List<Task> teamTasks;

    public Team() { }

    public Team(String id, String title) {
        this.id = id;
        this.title = title;
        this.teamTasks = new LinkedList<>();
    }

    @Override
    public String toString() {
        return this.title;
    }
}
