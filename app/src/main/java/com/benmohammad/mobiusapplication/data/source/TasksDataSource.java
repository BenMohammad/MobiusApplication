package com.benmohammad.mobiusapplication.data.source;

import androidx.annotation.NonNull;

import com.benmohammad.mobiusapplication.data.Task;
import com.google.common.base.Optional;

import java.util.List;

import io.reactivex.Flowable;

public interface TasksDataSource {

    Flowable<List<Task>> getTasks();

    Flowable<Optional<Task>> getTask(@NonNull String taskId);

    void saveTask(@NonNull Task task);

    void deleteAllTask();

    void deleteTask(@NonNull String taskId);

}
