package com.benmohammad.mobiusapplication.data;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Task {

    public abstract String id();

    public abstract TaskDetails details();

    public Task withDetails(TaskDetails taskDetails) {
        return create(id(), details());
    }

    public static Task create(String id, TaskDetails taskDetails) {
        return new AutoValue_Task(id, taskDetails);
    }

    public Task complete() {
        return withDetails(details().withCompleted(true));
    }

    public Task activate() {
        return withDetails(details().withCompleted(false));
    }
}
