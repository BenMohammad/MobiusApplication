package com.benmohammad.mobiusapplication.task;

import com.benmohammad.mobiusapplication.task.domain.TasksListEffect;
import com.benmohammad.mobiusapplication.task.domain.TasksListEvent;
import com.benmohammad.mobiusapplication.task.domain.TasksListLogic;
import com.benmohammad.mobiusapplication.task.domain.TasksListModel;
import com.spotify.mobius.EventSource;
import com.spotify.mobius.MobiusLoop;
import com.spotify.mobius.android.AndroidLogger;
import com.spotify.mobius.android.MobiusAndroid;
import com.spotify.mobius.rx2.RxMobius;

import io.reactivex.ObservableTransformer;

public class TasksInjector {

    public static MobiusLoop.Controller<TasksListModel, TasksListEvent> createController(
            ObservableTransformer<TasksListEffect, TasksListEvent> effecthandler,
            EventSource<TasksListEvent> eventSource,
            TasksListModel defaultModel
    ) {
        return MobiusAndroid.controller(createLoop(eventSource, effecthandler), defaultModel);
    }


    private static MobiusLoop.Factory<TasksListModel, TasksListEvent, TasksListEffect> createLoop(
            EventSource<TasksListEvent> eventSource,
            ObservableTransformer<TasksListEffect, TasksListEvent> effectHandler
    ) {
        return RxMobius.loop(TasksListLogic::update, effectHandler)
                .init(TasksListLogic::init)
                .eventSource(eventSource)
                .logger(AndroidLogger.tag("TasksList"));
    }

}
