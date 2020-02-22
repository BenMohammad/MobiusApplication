package com.benmohammad.mobiusapplication.addedittask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.benmohammad.mobiusapplication.R;
import com.benmohammad.mobiusapplication.data.Task;
import com.benmohammad.mobiusapplication.data.TaskBundlePacker;
import com.benmohammad.mobiusapplication.util.ActivityUtils;

import static androidx.core.util.Preconditions.checkNotNull;

public class AddEditTaskActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TASK = 1;

    private ActionBar mActionBar;

    public static Intent editTask(Context c, Task task) {
        Intent i = new Intent(c, AddEditTaskActivity.class);
        i.putExtra("task_to_edit", TaskBundlePacker.taskToBundle(task));
        return i;
    }

    public static Intent addTask(Context c) {
        return new Intent(c, AddEditTaskActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_act);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = checkNotNull(getSupportActionBar());
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        AddEditTaskFragment addEditTaskFragment =
                (AddEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        Task task;
        Bundle extras = getIntent().getExtras();
        if(extras != null&& extras.containsKey("task_to_edit")) {
            Bundle bundleTask = checkNotNull(extras.getBundle("task_to_edit"));
            task = TaskBundlePacker.taskFromBundle(bundleTask);
            setToolbarTitle(task.id());
        } else {
            task = null;
            setToolbarTitle(null);
        }

        if(addEditTaskFragment == null) {
            addEditTaskFragment = task == null ? AddEditTaskFragment.newInstanceForTaskCreation()
                    : AddEditTaskFragment.newInstanceForTaskUpdate(task);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), addEditTaskFragment, R.id.contentFrame);

        }
    }

    private void setToolbarTitle(@Nullable String taskId) {
        if(taskId == null) {
            mActionBar.setTitle(R.string.add_task);
        } else {
            mActionBar.setTitle(R.string.edit_task);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
