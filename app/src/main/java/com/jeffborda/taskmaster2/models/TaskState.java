package com.jeffborda.taskmaster2.models;

public enum TaskState {
    NEW(0), ASSIGNED(1), IN_PROGRESS(2), COMPLETE(3);

    private int statusCode;

    TaskState(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

}
