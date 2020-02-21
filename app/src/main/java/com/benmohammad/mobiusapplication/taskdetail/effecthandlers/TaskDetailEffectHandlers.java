package com.benmohammad.mobiusapplication.taskdetail.effecthandlers;

import android.content.Context;

import com.benmohammad.mobiusapplication.data.Task;
import com.benmohammad.mobiusapplication.data.source.TasksDataSource;
import com.benmohammad.mobiusapplication.data.source.local.TasksLocalDataSource;
import com.benmohammad.mobiusapplication.data.source.remote.TasksRemoteDataSource;
import com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailEffect;
import com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailEvent;
import com.benmohammad.mobiusapplication.taskdetail.view.TaskDetailViewActions;
import com.benmohammad.mobiusapplication.util.schedulers.SchedulerProvider;
import com.spotify.mobius.rx2.RxMobius;

import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailEffect.*;
import static com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailEvent.taskDeleted;
import static com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailEvent.taskDeletionFailed;
import static com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailEvent.taskMarkedActive;
import static com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailEvent.taskMarkedComplete;
import static com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailEvent.taskSaveFailed;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public class TaskDetailEffectHandlers {

    public static ObservableTransformer<TaskDetailEffect, TaskDetailEvent> createEffectHandlers(
        TaskDetailViewActions view, Context context, Action dismiss, Consumer<Task> launchEditor
    ) {
        TasksRemoteDataSource remoteSource = TasksRemoteDataSource.getInstance();
        TasksLocalDataSource localSource  = TasksLocalDataSource.getInstance(context, SchedulerProvider.getInstance());
        return RxMobius.<TaskDetailEffect, TaskDetailEvent> subtypeEffectHandler()
                .addFunction(DeleteTask.class, deleteTaskHandler(remoteSource, localSource))
                .addFunction(SaveTask.class, saveTaskHandler(remoteSource, localSource))
                .addAction(NotifyTaskMarkedComplete.class, view::showTaskMarkedComplete, mainThread())
                .addAction(NotifyTaskMarkedActive.class, view::showTaskMarkedActive, mainThread())
                .addAction(NotifyTaskDeletionFailed.class, view::showTaskDeletionFailed, mainThread())
                .addAction(NotifyTasSaveFailed.class, view::showTaskSavingfailed, mainThread())
                .addConsumer(OpenTaskEditor.class, openTaskEditorHandler(launchEditor), mainThread())
                .addAction(Exit.class, dismiss, mainThread())
                .build();
    }

    private static Consumer<OpenTaskEditor> openTaskEditorHandler(
            Consumer<Task> launchEditorCommand) {
        return openTaskEditor -> launchEditorCommand.accept(openTaskEditor.task());
    }

    private static Function<SaveTask, TaskDetailEvent> saveTaskHandler(
            TasksDataSource remoteSource, TasksDataSource localSource) {
        return saveTask -> {
            try {
                remoteSource.saveTask(saveTask.task());
                localSource.saveTask(saveTask.task());
                return saveTask.task().details().completed() ? taskMarkedComplete() : taskMarkedActive();
            } catch(Exception e) {
                return taskSaveFailed();
            }
        };
    }

    private static Function<DeleteTask, TaskDetailEvent> deleteTaskHandler(
            TasksDataSource remoteSource, TasksDataSource localSource
    ) {
        return deleteTask -> {
            try {
                remoteSource.deleteTask(deleteTask.task().id());
                localSource.deleteTask(deleteTask.task().id());
                return taskDeleted();
            } catch(Exception e) {
                return taskDeletionFailed();
            }
        };
    }

}
