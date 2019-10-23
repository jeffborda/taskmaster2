package com.jeffborda.taskmaster2.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TaskTest {

    private Task testTask;
    @Before
    public void setUp() {
        testTask = new Task("This is a title", "This is a description");
    }

    @Test
    public void getTitleTest() {
        assertEquals("This is a title", testTask.getTitle());
    }

    @Test
    public void getDescriptionTest() {
        assertEquals("This is a description", testTask.getDescription());
    }

    @Test
    public void getTaskStateTest() {
        assertEquals(TaskState.NEW, testTask.getTaskState());
    }

    @Test
    public void setTitleTest() {
        testTask.setTitle("New title");
        assertEquals("New title", testTask.getTitle());
    }

    @Test
    public void setDescriptionTest() {
        testTask.setDescription("New description");
        assertEquals("New description", testTask.getDescription());
    }

    @Test
    public void setTestTaskTest() {
        testTask.setTaskState(TaskState.ASSIGNED);
        assertEquals(TaskState.ASSIGNED, testTask.getTaskState());
    }

    @Test
    public void toStringTest() {
        Task task = new Task("title", "description");
        String expected = "TITLE: title DESCRIPTION: description STATE: NEW";
        assertEquals(expected, task.toString());
    }
}