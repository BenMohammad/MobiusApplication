package com.benmohammad.mobiusapplication.taskdetail.view;

import android.view.View;

import com.benmohammad.mobiusapplication.data.Task;
import com.benmohammad.mobiusapplication.data.TaskDetails;

import static com.benmohammad.mobiusapplication.taskdetail.view.TaskDetailViewData.*;
import static com.google.common.base.Strings.isNullOrEmpty;

public class TaskDetailViewDataMapper {

    public static TaskDetailViewData taskToTaskViewData(Task task) {
        TaskDetails details = task.details();
        String title = details.title();
        String description = details.description();
        return TaskDetailViewData.builder()
                .title(TextViewData.create(isNullOrEmpty(title) ? View.GONE : View.VISIBLE, title ))
                .description(TextViewData.create(isNullOrEmpty(description) ? View.GONE : View.VISIBLE, description))
                .completedChecked(details.completed())
                .build();
    }
}
