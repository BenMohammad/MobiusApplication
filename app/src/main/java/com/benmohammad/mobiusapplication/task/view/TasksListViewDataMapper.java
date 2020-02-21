package com.benmohammad.mobiusapplication.task.view;

import androidx.annotation.Nullable;

import com.benmohammad.mobiusapplication.R;
import com.benmohammad.mobiusapplication.data.Task;
import com.benmohammad.mobiusapplication.task.domain.TaskFilters;
import com.benmohammad.mobiusapplication.task.domain.TasksFilterType;
import com.benmohammad.mobiusapplication.task.domain.TasksListModel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import static com.benmohammad.mobiusapplication.task.view.EmptyTasksViewDataMapper.createEmptyTaskViewData;
import static com.benmohammad.mobiusapplication.task.view.ViewState.awaitingTasks;
import static com.benmohammad.mobiusapplication.task.view.ViewState.emptyTasks;
import static com.benmohammad.mobiusapplication.task.view.ViewState.hasTasks;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.*;

public class TasksListViewDataMapper {

    public static TasksListViewData tasksListModelToViewData(TasksListModel model) {
        return TasksListViewData.builder()
                .loading(model.loading())
                .filterLabel(getFilterLabel(model.filter()))
                .viewState(getViewState(model.tasks(), model.filter()))
                .build();
    }

    private static ViewState getViewState(@Nullable ImmutableList<Task> tasks, TasksFilterType filter) {
        if(tasks == null) return awaitingTasks();
        ImmutableList<Task> filteredtasks = TaskFilters.filterTasks(tasks, filter);
        if(filteredtasks.isEmpty()) {
            return emptyTasks(createEmptyTaskViewData(filter));
        } else {
            return hasTasks(copyOf(transform(filteredtasks, TaskViewDataMapper::createTasksViewData)));
        }
    }


    private static int getFilterLabel(TasksFilterType filterType) {
        switch (filterType) {
            case ALL_TASKS:
                return R.string.label_active;

            case COMPLETED_TASKS:
                return R.string.label_completed;

                default:
                    return R.string.label_all;
        }
    }

}
