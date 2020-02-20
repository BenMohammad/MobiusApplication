package com.benmohammad.mobiusapplication.data.source.remote;

import androidx.annotation.NonNull;

import com.benmohammad.mobiusapplication.data.Task;
import com.benmohammad.mobiusapplication.data.TaskDetails;
import com.benmohammad.mobiusapplication.data.source.TasksDataSource;
import com.google.common.base.Optional;

import java.nio.channels.FileLock;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

public class TasksRemoteDataSource implements TasksDataSource {

    private static TasksRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 3000;

    private static final Map<String, Task> TASKS_SERVICE_DATA;


    static  {
        TASKS_SERVICE_DATA = new LinkedHashMap<>();
        addTask("1234", "Build tower in Pisa", "Ground looks good, no foundation work required.");
        addTask("4321", "Finish bridge in Tacoma", "Found awesome girders at half the cost!");

    }

    public static TasksRemoteDataSource getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TasksRemoteDataSource();
        }

        return INSTANCE;
    }

    private TasksRemoteDataSource() {}

    private static void addTask(String is, String title, String description) {
        Task newTask = Task.create(is, TaskDetails.create(title, description));
        TASKS_SERVICE_DATA.put(newTask.id(), newTask);
    }


    @Override
    public Flowable<List<Task>> getTasks() {
        return Flowable.fromIterable(TASKS_SERVICE_DATA.values())
                .delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS)
                .toList()
                .toFlowable();
    }

    @Override
    public Flowable<Optional<Task>> getTask(@NonNull String taskId) {
        final Task task = TASKS_SERVICE_DATA.get(taskId);
        if(task != null) {
            return Flowable.just(Optional.of(task));
        } else {
            return Flowable.empty();
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        TASKS_SERVICE_DATA.put(task.id(), task);
    }

    @Override
    public void deleteAllTask() {
        TASKS_SERVICE_DATA.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }
}
