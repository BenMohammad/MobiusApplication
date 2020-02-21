package com.benmohammad.mobiusapplication.task.view;

import android.view.View;

import com.benmohammad.mobiusapplication.R;
import com.benmohammad.mobiusapplication.task.domain.TasksFilterType;
import com.benmohammad.mobiusapplication.task.view.TasksListViewData.EmptyTaskViewData;

public class EmptyTasksViewDataMapper {

    public static EmptyTaskViewData createEmptyTaskViewData(TasksFilterType filter) {
        EmptyTaskViewData.Builder builder = EmptyTaskViewData.builder();
        switch (filter) {
            case ACTIVE_TASKS:
                return builder
                        .addViewVisibility(View.GONE)
                        .title(R.string.no_tasks_active)
                        .icon(R.drawable.ic_check_circle_24dp)
                        .build();

            case COMPLETED_TASKS:
                return builder
                        .addViewVisibility(View.GONE)
                        .title(R.string.no_tasks_completed)
                        .icon(R.drawable.ic_verified_user_24dp)
                        .build();
                default:
                    return builder
                            .addViewVisibility(View.VISIBLE)
                            .title(R.string.no_tasks_all)
                            .icon(R.drawable.ic_assignment_turned_in_24dp)
                            .build();
        }
    }
}
