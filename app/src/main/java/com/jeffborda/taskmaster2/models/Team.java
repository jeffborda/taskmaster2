package com.jeffborda.taskmaster2.models;

import java.util.LinkedList;
import java.util.List;

public class Team {

    String id;
    String title;
    List<Task> teamTasks;

    public Team() { }

    public Team(String title) {
        this.title = title;
        this.teamTasks = new LinkedList<>();
    }
}
