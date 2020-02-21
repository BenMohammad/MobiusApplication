package com.benmohammad.mobiusapplication.taskdetail;

import com.benmohammad.mobiusapplication.data.Task;
import com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailEffect;
import com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailEvent;
import com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailLogic;
import com.spotify.mobius.MobiusLoop;
import com.spotify.mobius.android.AndroidLogger;
import com.spotify.mobius.android.MobiusAndroid;
import com.spotify.mobius.rx2.RxMobius;

import io.reactivex.ObservableTransformer;

public class TaskDetailInjector {

    public static MobiusLoop.Controller<Task, TaskDetailEvent> createController(
            ObservableTransformer<TaskDetailEffect, TaskDetailEvent> effectHandlers, Task defaultModel
    ) {
        return MobiusAndroid.controller(createLoop(effectHandlers), defaultModel);
    }

    private static MobiusLoop.Factory<Task, TaskDetailEvent, TaskDetailEffect> createLoop(
            ObservableTransformer<TaskDetailEffect, TaskDetailEvent> effectHandlers
    ) {
        return RxMobius.loop(TaskDetailLogic::update, effectHandlers)
                .logger(AndroidLogger.tag("TaskDetail"));
    }
}
