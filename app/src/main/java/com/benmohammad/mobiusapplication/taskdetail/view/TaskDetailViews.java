package com.benmohammad.mobiusapplication.taskdetail.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.benmohammad.mobiusapplication.R;
import com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.spotify.mobius.Connectable;
import com.spotify.mobius.Connection;
import com.spotify.mobius.ConnectionLimitExceededException;
import com.spotify.mobius.functions.Consumer;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailEvent.activateTaskRequested;
import static com.benmohammad.mobiusapplication.taskdetail.domain.TaskDetailEvent.completeTaskRequested;

public class TaskDetailViews implements TaskDetailViewActions, Connectable<TaskDetailViewData, TaskDetailEvent> {

    private FloatingActionButton mFab;
    private final Observable<TaskDetailEvent> mMenuEvents;
    private TextView mDetailTitle;
    private TextView mDetailDescription;
    private CheckBox mDetailCompleteStatus;
    private View mRootView;

    public TaskDetailViews(
            LayoutInflater inflater,
            ViewGroup parent,
            FloatingActionButton fab,
            Observable<TaskDetailEvent> menuEvents
    ) {
        mRootView = inflater.inflate(R.layout.taskdetail_frag, parent, false);
        mMenuEvents = menuEvents;
        mDetailTitle = mRootView.findViewById(R.id.task_detail_title);
        mDetailDescription = mRootView.findViewById(R.id.task_detail_description);
        mDetailCompleteStatus = mRootView.findViewById(R.id.task_detail_complete);
        mFab = fab;
    }

    public View getRootView() {
        return mRootView;
    }

    @Override
    public void showTaskMarkedComplete() {
        Snackbar.make(mRootView, R.string.task_marked_complete, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showTaskMarkedActive() {
        Snackbar.make(mRootView, R.string.task_marked_active, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showTaskSavingFailed() {
        Snackbar.make(mRootView, "Failed to Save", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showTaskDeletionFailed() {
        Snackbar.make(mRootView, "Failed to Delete", Snackbar.LENGTH_SHORT).show();
    }

    private void render(TaskDetailViewData viewData) {
        mDetailCompleteStatus.setChecked(viewData.completedChecked());
        bindTextViewData(mDetailTitle, viewData.title());
        bindTextViewData(mDetailDescription, viewData.description());
    }

    private void bindTextViewData(TextView textView, TaskDetailViewData.TextViewData viewData) {
        textView.setVisibility(viewData.visibility());
        textView.setText(viewData.text());
    }


    @Nonnull
    @Override
    public Connection<TaskDetailViewData> connect(Consumer<TaskDetailEvent> output) throws ConnectionLimitExceededException {
        mFab.setOnClickListener(__ -> output.accept(TaskDetailEvent.editTaskRequested()));

        mDetailCompleteStatus.setOnCheckedChangeListener(
                ((buttonView, isChecked) -> {
                    if(isChecked) {
                        output.accept(completeTaskRequested());
                    } else {
                        output.accept(activateTaskRequested());
                    }
                })
        );

        Disposable disposable =
                mMenuEvents
                .retry()
                .subscribe(
                        output::accept,
                        t -> Log.e(TaskDetailEvent.class.getSimpleName(), "Menu events seem to fail!!")
                );

        return new Connection<TaskDetailViewData>() {
            @Override
            public void accept(TaskDetailViewData value) {
                render(value);
            }

            @Override
            public void dispose() {
                disposable.dispose();
                mFab.setOnClickListener(null);
                mDetailCompleteStatus.setOnCheckedChangeListener(null);
            }
        };

    }
}
