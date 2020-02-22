package com.benmohammad.mobiusapplication.addedittask.effecthandlers;

import android.content.Context;

import com.benmohammad.mobiusapplication.addedittask.domain.AddEditTaskEffect;
import com.benmohammad.mobiusapplication.addedittask.domain.AddEditTaskEffect.NotifyEmptyTaskNotAllowed;
import com.benmohammad.mobiusapplication.addedittask.domain.AddEditTaskEvent;
import com.benmohammad.mobiusapplication.data.Task;
import com.benmohammad.mobiusapplication.data.source.TasksDataSource;
import com.benmohammad.mobiusapplication.data.source.local.TasksLocalDataSource;
import com.benmohammad.mobiusapplication.data.source.remote.TasksRemoteDataSource;
import com.benmohammad.mobiusapplication.util.schedulers.SchedulerProvider;
import com.spotify.mobius.rx2.RxMobius;

import java.util.UUID;

import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;

import static com.benmohammad.mobiusapplication.addedittask.domain.AddEditTaskEffect.*;
import static com.benmohammad.mobiusapplication.addedittask.domain.AddEditTaskEvent.taskCreatedSuccessfully;
import static com.benmohammad.mobiusapplication.addedittask.domain.AddEditTaskEvent.taskCreationFailed;
import static com.benmohammad.mobiusapplication.addedittask.domain.AddEditTaskEvent.taskUpdateFailed;
import static com.benmohammad.mobiusapplication.addedittask.domain.AddEditTaskEvent.taskUpdatedSuccessfully;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public class AddEditTaskEffectHandlers {

    public static ObservableTransformer<AddEditTaskEffect, AddEditTaskEvent> createEffectHandlers(
            Context context, Action showTasksList, Action showEmptyTaskError
    ) {
        TasksRemoteDataSource remoteSource = TasksRemoteDataSource.getInstance();
        TasksLocalDataSource localSource = TasksLocalDataSource.getInstance(context ,SchedulerProvider.getInstance());

        return RxMobius.<AddEditTaskEffect, AddEditTaskEvent>subtypeEffectHandler()
                .addAction(NotifyEmptyTaskNotAllowed.class, showEmptyTaskError, mainThread())
                .addAction(Exit.class, showTasksList, mainThread())
                .addFunction(CreateTask.class, createTaskHandler(remoteSource, localSource))
                .addFunction(SaveTask.class, saveTaskHandler(remoteSource, localSource))
                .build();
    }

    static Function<CreateTask, AddEditTaskEvent> createTaskHandler(
            TasksDataSource remoteSource, TasksDataSource localSource
    ) {
        return createTaskEffect -> {
            Task task = Task.create(UUID.randomUUID().toString(), createTaskEffect.taskDetails());
            try {
                remoteSource.saveTask(task);
                localSource.saveTask(task);
                return taskCreatedSuccessfully();
            } catch (Exception e) {
                return taskCreationFailed("Failed to create Task");
            }
        };
    }


    static Function<SaveTask, AddEditTaskEvent> saveTaskHandler(
            TasksDataSource remoteSource, TasksDataSource localSource
    ) {
        return saveTask -> {
            try {
                remoteSource.saveTask(saveTask.task());
                localSource.saveTask(saveTask.task());
                return taskUpdatedSuccessfully();
            } catch (Exception e) {
                return taskUpdateFailed("Failed to update Task");
            }
        };
    }
}
