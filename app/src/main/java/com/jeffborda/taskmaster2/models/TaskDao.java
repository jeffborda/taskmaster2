package com.jeffborda.taskmaster2.models;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task ORDER BY id DESC")
    List<Task> getAll();

    @Query("SELECT * FROM task WHERE title =:title")
    List<Task> getTasksByTitle(String title);

    @Query("SELECT * FROM task WHERE id =:id")
    Task getTaskById(long id);

    @Insert
    void addTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);
}
