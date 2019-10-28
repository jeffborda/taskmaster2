package com.jeffborda.taskmaster2.models;

import androidx.room.TypeConverter;

import static com.jeffborda.taskmaster2.models.TaskState.ASSIGNED;
import static com.jeffborda.taskmaster2.models.TaskState.COMPLETE;
import static com.jeffborda.taskmaster2.models.TaskState.IN_PROGRESS;
import static com.jeffborda.taskmaster2.models.TaskState.NEW;

// Need this class to store TaskState as an enum inside the Room database
// This is an excellent StackOverflow post about how to do this:
// https://stackoverflow.com/questions/44498616/android-architecture-components-using-enums
class TaskStateConverter {

    @TypeConverter
    public static TaskState toStatus(int taskState) {
        if (taskState == NEW.getStatusCode()) {
            return NEW;
        } else if (taskState == ASSIGNED.getStatusCode()) {
            return ASSIGNED;
        } else if (taskState == IN_PROGRESS.getStatusCode()) {
            return IN_PROGRESS;
        } else if (taskState == COMPLETE.getStatusCode()) {
            return COMPLETE;
        } else {
            throw new IllegalArgumentException("Could not recognize status");
        }
    }

    @TypeConverter
    public static int toInt(TaskState state) {
        return state.getStatusCode();
    }

}
