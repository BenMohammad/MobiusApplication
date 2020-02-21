package com.benmohammad.mobiusapplication.task.view;

import com.benmohammad.mobiusapplication.R;
import com.benmohammad.mobiusapplication.data.Task;
import com.benmohammad.mobiusapplication.data.TaskDetails;
import com.google.common.base.Strings;

public class TaskViewDataMapper {
    public static TasksListViewData.TaskViewData createTasksViewData(Task task) {
        if(task == null) return null;
        return TasksListViewData.TaskViewData.create(
                getTitleForList(task.details()),
                task.details().completed(),
                task.details().completed() ?
                        R.drawable.list_completed_touch_feedback : R.drawable.touch_feedback,
                task.id());
    }

    private static String getTitleForList(TaskDetails taskDetails) {
        if(!Strings.isNullOrEmpty(taskDetails.title())) {
            return taskDetails.title();
        } else {
            return taskDetails.description();
        }
    }
}
