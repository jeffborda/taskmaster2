package com.jeffborda.taskmaster2.models;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class}, version = 1)
public abstract class TaskmasterDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
