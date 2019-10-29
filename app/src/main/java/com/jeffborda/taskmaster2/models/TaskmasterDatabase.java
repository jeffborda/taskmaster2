package com.jeffborda.taskmaster2.models;

import androidx.room.Database;
import androidx.room.RoomDatabase;

//Added the 'exportSchema' to prevent an error
@Database(entities = {Task.class}, exportSchema = false, version = 1)
public abstract class TaskmasterDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
