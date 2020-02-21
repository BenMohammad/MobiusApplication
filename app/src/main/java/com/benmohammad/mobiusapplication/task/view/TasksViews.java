package com.benmohammad.mobiusapplication.task.view;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.benmohammad.mobiusapplication.R;
import com.benmohammad.mobiusapplication.task.domain.TasksListEvent;
import com.benmohammad.mobiusapplication.task.view.TasksListViewData.TaskViewData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.ImmutableList;
import com.spotify.mobius.Connectable;
import com.spotify.mobius.Connection;
import com.spotify.mobius.ConnectionLimitExceededException;
import com.spotify.mobius.functions.Consumer;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static com.benmohammad.mobiusapplication.task.domain.TasksListEvent.navigateToTaskDetailsRequested;
import static com.benmohammad.mobiusapplication.task.domain.TasksListEvent.newTaskClicked;
import static com.benmohammad.mobiusapplication.task.domain.TasksListEvent.refreshRequested;
import static com.benmohammad.mobiusapplication.task.domain.TasksListEvent.taskMarkedActive;
import static com.benmohammad.mobiusapplication.task.domain.TasksListEvent.taskMarkedComplete;

public class TasksViews implements TasksListViewActions, Connectable<TasksListViewData, TasksListEvent> {

    private final View mRoot;
    private final ScrollChildSwipeRefreshLayout swipeRefreshlLayout;
    private final FloatingActionButton mFab;
    private final Observable<TasksListEvent> menuEvents;

    private TasksAdapter mListAdapter;
    private View mNoTasksView;
    private ImageView mNoTaskIcon;
    private TextView mNoTasksMainView;
    private TextView mNoTaskAddView;
    private LinearLayout mTasksView;
    private TextView mFilteringLabelView;

    public TasksViews(
            LayoutInflater inflater,
            ViewGroup parent,
            FloatingActionButton fab,
            Observable<TasksListEvent> menuEvents
    ) {
        this.menuEvents = menuEvents;
        mRoot = inflater.inflate(R.layout.tasks_frag, parent, false);
        mListAdapter = new TasksAdapter();
        ListView listView = mRoot.findViewById(R.id.tasks_list);
        listView.setAdapter(mListAdapter);
        mFilteringLabelView = mRoot.findViewById(R.id.filteringLabel);
        mTasksView = mRoot.findViewById(R.id.tasksLL);

        mNoTasksView = mRoot.findViewById(R.id.noTasks);
        mNoTaskIcon = mRoot.findViewById(R.id.noTasksIcon);
        mNoTasksMainView = mRoot.findViewById(R.id.noTasksMain);
        mNoTaskAddView = mRoot.findViewById(R.id.noTasksAdd);
        fab.setImageResource(R.drawable.ic_add);
        mFab = fab;
        swipeRefreshlLayout = mRoot.findViewById(R.id.refresh_layout);
        swipeRefreshlLayout.setColorSchemeColors(
                ContextCompat.getColor(mRoot.getContext(), R.color.colorPrimary),
                ContextCompat.getColor(mRoot.getContext(), R.color.colorAccent),
                ContextCompat.getColor(mRoot.getContext(), R.color.colorPrimaryDark));
        swipeRefreshlLayout.setScrollUpChild(listView);
    }

    public View getRootView() {
        return mRoot;
    }


    @Override
    public void showTaskMarkedComplete() {
        showMessage(R.string.task_marked_complete);
    }

    @Override
    public void showTaskMarkedActive() {
        showMessage(R.string.task_marked_active);
    }

    @Override
    public void showCompleteTaskCleared() {
        showMessage(R.string.completed_tasks_cleared);
    }

    @Override
    public void showLoadingTaskError() {
        showMessage(R.string.loading_tasks_error);
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(R.string.successfully_saved_task_message);
    }

    private void showMessage(int messageRes) {
        Snackbar.make(mRoot, messageRes, Snackbar.LENGTH_SHORT).show();
    }

    @Nonnull
    @Override
    public Connection<TasksListViewData> connect(Consumer<TasksListEvent> output) throws ConnectionLimitExceededException {
        addUiListeners(output);
        Disposable disposable = menuEvents.subscribe(output::accept);

        return new Connection<TasksListViewData>() {
            @Override
            public void accept(TasksListViewData value) {
                render(value);
            }

            @Override
            public void dispose() {
                disposable.dispose();
                mNoTaskAddView.setOnClickListener(null);
                mFab.setOnClickListener(null);
                swipeRefreshlLayout.setOnRefreshListener(null);
                mListAdapter.setItemListener(null);
            }
        };
    }

    private void render(TasksListViewData value) {
        swipeRefreshlLayout.setRefreshing(value.loading());
        mFilteringLabelView.setText(value.filterLabel());
        value
                .viewState()
                .match(
                        awaiting -> showNoTasksViewState(),
                        emptyTasks -> showEmptyTaskState(emptyTasks.viewData()),
                        hasTasks -> showTasks(hasTasks.taskViewData()));

    }


    private void addUiListeners(Consumer<TasksListEvent> output) {
        mNoTaskAddView.setOnClickListener(__ -> output.accept(newTaskClicked()));
        mFab.setOnClickListener(__ -> output.accept(newTaskClicked()));
        swipeRefreshlLayout.setOnRefreshListener(() -> output.accept(refreshRequested()));
        mListAdapter.setItemListener(
                new TasksAdapter.TaskItemListener() {
                    @Override
                    public void onTaskClicked(String id) {
                        output.accept(navigateToTaskDetailsRequested(id));
                    }

                    @Override
                    public void onCompleteTaskClick(String id) {
                        output.accept(taskMarkedComplete(id));
                    }

                    @Override
                    public void onActiveTaskClick(String id) {
                        output.accept(taskMarkedActive(id));
                    }
                }
        );
    }

    private void showEmptyTaskState(TasksListViewData.EmptyTaskViewData vd) {
        mTasksView.setVisibility(View.GONE);
        mNoTasksView.setVisibility(View.GONE);
        mNoTasksMainView.setText(vd.title());
        mNoTaskIcon.setImageResource(vd.icon());
        mNoTaskAddView.setVisibility(vd.addViewVisibility());
    }


    private void showNoTasksViewState() {
        mTasksView.setVisibility(View.GONE);
        mNoTasksView.setVisibility(View.GONE);
    }

    private void showTasks(ImmutableList<TaskViewData> tasks) {
        mListAdapter.replaceData(tasks);
        mTasksView.setVisibility(View.VISIBLE);
        mNoTasksView.setVisibility(View.GONE);
    }

}