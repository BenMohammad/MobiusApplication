package com.benmohammad.mobiusapplication.task.view;

public interface TasksListViewActions {

    void showTaskMarkedComplete();

    void showTaskMarkedActive();

    void showCompleteTaskCleared();

    void showLoadingTaskError();

    void showSuccessfullySavedMessage();
}
