package com.benmohammad.mobiusapplication.data;

import android.os.Bundle;

import static androidx.core.util.Preconditions.checkNotNull;

public class TaskBundlePacker {

    private static class TaskDetailsBundleIdentifiers {
        static final String TITLE = "task_title";
        static final String DESCRIPTION = "task_description";
        static final String STATUS = "task_status";
    }

    private static class TaskBundleIdentifiers {
        static final String ID = "task_id";
        static final String DETAILS = "task_details";
    }

    public static Bundle taskToBundle(Task task) {
        Bundle b = new Bundle();
        b.putString(TaskBundleIdentifiers.ID, task.id());
        b.putBundle(TaskBundleIdentifiers.DETAILS, taskDetailsToBundle(task.details()));
        return b;
    }

    public static Task taskFromBundle(Bundle b) {
        return Task.create(
                checkNotNull(b.getString(TaskBundleIdentifiers.ID)),
                taskDetailsFromBundle(checkNotNull(b.getBundle(TaskBundleIdentifiers.DETAILS)))
        );
    }

    public static Bundle taskDetailsToBundle(TaskDetails taskDetails) {
        Bundle b = new Bundle();
        b.putString(TaskDetailsBundleIdentifiers.TITLE, taskDetails.title());
        b.putString(TaskDetailsBundleIdentifiers.DESCRIPTION, taskDetails.description());
        b.putBoolean(TaskDetailsBundleIdentifiers.STATUS, taskDetails.completed());
        return b;
    }

    public static TaskDetails taskDetailsFromBundle(Bundle b) {
        String title = checkNotNull(b.getString(TaskDetailsBundleIdentifiers.TITLE));
        String description = checkNotNull(b.getString(TaskDetailsBundleIdentifiers.DESCRIPTION));
        return TaskDetails.builder()
                .title(title)
                .description(description)
                .completed(b.getBoolean(TaskDetailsBundleIdentifiers.STATUS))
                .build();
    }
}
