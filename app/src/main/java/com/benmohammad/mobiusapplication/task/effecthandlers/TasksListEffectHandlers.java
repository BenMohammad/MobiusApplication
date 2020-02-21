package com.benmohammad.mobiusapplication.task.effecthandlers;

import android.content.Context;

import com.benmohammad.mobiusapplication.data.Task;
import com.benmohammad.mobiusapplication.data.source.TasksDataSource;
import com.benmohammad.mobiusapplication.data.source.local.TasksLocalDataSource;
import com.benmohammad.mobiusapplication.data.source.remote.TasksRemoteDataSource;
import com.benmohammad.mobiusapplication.task.domain.TasksListEffect;
import com.benmohammad.mobiusapplication.task.domain.TasksListEvent;
import com.benmohammad.mobiusapplication.task.view.TasksListViewActions;
import com.benmohammad.mobiusapplication.util.Either;
import com.benmohammad.mobiusapplication.util.schedulers.SchedulerProvider;
import com.google.common.collect.ImmutableList;
import com.spotify.mobius.rx2.RxMobius;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static com.benmohammad.mobiusapplication.task.domain.TasksListEffect.*;
import static com.benmohammad.mobiusapplication.task.domain.TasksListEvent.taskRefreshed;
import static com.benmohammad.mobiusapplication.task.domain.TasksListEvent.tasksLoaded;
import static com.benmohammad.mobiusapplication.task.domain.TasksListEvent.tasksLoadingFailed;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public class TasksListEffectHandlers {

    public static ObservableTransformer<TasksListEffect, TasksListEvent> createEffectHandler(
            Context context,
            TasksListViewActions view,
            Action showAddTask,
            Consumer<Task> showTaskDetails
    ) {
        TasksRemoteDataSource remoteSource = TasksRemoteDataSource.getInstance();
        TasksLocalDataSource localSource = TasksLocalDataSource.getInstance(context, SchedulerProvider.getInstance());

        return RxMobius.<TasksListEffect, TasksListEvent> subtypeEffectHandler()
                .addTransformer(RefreshTasks.class, refreshTaskHandler(remoteSource, localSource))
                .addTransformer(LoadTasks.class, loadTasksHandler(localSource))
                .addConsumer(SaveTask.class, saveTasksHandler(remoteSource, localSource))
                .addConsumer(DeleteTasks.class, deleteTasksHandler(remoteSource, localSource))
                .addConsumer(ShowFeedback.class, showFeedbackHandler(view), mainThread())
                .addConsumer(NavigateToTaskDetails.class, navigateToDetailsHandler(showTaskDetails), mainThread())
                .addAction(StartTaskCreationFlow.class, showAddTask, mainThread())

               .build();
    }

    static ObservableTransformer<RefreshTasks, TasksListEvent> refreshTaskHandler(TasksDataSource remoteSource, TasksDataSource localSource) {
        Single<TasksListEvent> refreshTasksOperation =
                remoteSource
                .getTasks()
                .singleOrError()
                .map(Either::<Throwable, List<Task>> right)
                .onErrorReturn(Either::left)
                .flatMap(
                        either ->   either.map(
                                left -> Single.just(tasksLoadingFailed()),
                                right -> Observable.fromIterable(right.value())
                                .concatMapCompletable(
                                        t -> Completable.fromAction(
                                                () -> localSource.saveTask(t)))
                                .andThen(Single.just(taskRefreshed()))
                                .onErrorReturnItem(tasksLoadingFailed())));

                                return refreshTasks -> refreshTasks.flatMapSingle(__ -> refreshTasksOperation);

    }

    static ObservableTransformer<LoadTasks, TasksListEvent> loadTasksHandler(TasksDataSource dataSource) {
        return loadTasks -> loadTasks.flatMap(
                effect ->
                        dataSource
                            .getTasks()
                            .toObservable()
                            .take(1)
                            .map(tasks -> tasksLoaded(ImmutableList.copyOf(tasks)))
                            .onErrorReturnItem(tasksLoadingFailed()));
    }


    static Consumer<SaveTask> saveTasksHandler(TasksDataSource remoteDataSource, TasksDataSource localDataSource) {
        return saveTaskEffect -> {
            remoteDataSource.saveTask(saveTaskEffect.task());
            localDataSource.saveTask(saveTaskEffect.task());
        };
    }

    static Consumer<DeleteTasks> deleteTasksHandler(TasksDataSource remoteDataSource, TasksDataSource localDataSource) {
        return deleteTasks -> {
            for(Task task : deleteTasks.tasks()) {
                remoteDataSource.deleteTask(task.id());
                localDataSource.deleteTask(task.id());
            }
        };
    }


    static Consumer<ShowFeedback> showFeedbackHandler(TasksListViewActions view) {
        return showFeedback -> {
            switch (showFeedback.feedbackType()) {
                case SAVED_SUCCESSFULLY:
                    view.showSuccessfullySavedMessage();
                    break;
                case MARKED_ACTIVE:
                    view.showTaskMarkedActive();
                    break;
                case MARKED_COMPLETE:
                    view.showTaskMarkedComplete();
                    break;
                case CLEARED_COMPLETED:
                    view.showCompleteTaskCleared();
                    break;
                case LOADING_ERROR:
                    view.showLoadingTaskError();
                    break;
            }
        };
    }

    static Consumer<NavigateToTaskDetails> navigateToDetailsHandler(Consumer<Task> command) {
        return navigateEffect -> command.accept(navigateEffect.task());
    }

}


