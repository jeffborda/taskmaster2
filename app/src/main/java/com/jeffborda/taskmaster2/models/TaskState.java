package com.jeffborda.taskmaster2.models;

public enum TaskState {
    NEW(0), ASSIGNED(1), IN_PROGRESS(2), COMPLETE(3);

    // This instance variable and the methods are implemented to enable storing in Room database
    // RE: https://stackoverflow.com/questions/44498616/android-architecture-components-using-enums
    private int statusCode;

    TaskState(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

}
