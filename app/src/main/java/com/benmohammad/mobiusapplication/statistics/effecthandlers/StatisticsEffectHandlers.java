package com.benmohammad.mobiusapplication.statistics.effecthandlers;

import android.content.Context;

import com.benmohammad.mobiusapplication.data.source.local.TasksLocalDataSource;
import com.benmohammad.mobiusapplication.statistics.domain.StatisticsEffect;
import com.benmohammad.mobiusapplication.statistics.domain.StatisticsEffect.LoadTasks;
import com.benmohammad.mobiusapplication.statistics.domain.StatisticsEvent;
import com.benmohammad.mobiusapplication.util.schedulers.SchedulerProvider;
import com.google.common.collect.ImmutableList;
import com.spotify.mobius.rx2.RxMobius;

import io.reactivex.ObservableTransformer;

import static com.benmohammad.mobiusapplication.statistics.domain.StatisticsEvent.tasksLoadingFailed;

public class StatisticsEffectHandlers {

    public static ObservableTransformer<StatisticsEffect, StatisticsEvent> createEffectHandller(
            Context context
    ) {
        TasksLocalDataSource localSource =
                TasksLocalDataSource.getInstance(context, SchedulerProvider.getInstance());
        return RxMobius.<StatisticsEffect, StatisticsEvent>subtypeEffectHandler()
                .addTransformer(LoadTasks.class, loadTasksHandler(localSource))
                .build();
    }

    private static ObservableTransformer<LoadTasks, StatisticsEvent> loadTasksHandler(
            TasksLocalDataSource localSource
    ) {
        return effects ->
                effects.flatMap(
                        loadTasks ->
                                localSource
                        .getTasks()
                        .toObservable()
                        .take(1)
                        .map(ImmutableList::copyOf)
                        .map(StatisticsEvent::tasksLoaded)
                        .onErrorReturnItem(tasksLoadingFailed()));
    }
}
