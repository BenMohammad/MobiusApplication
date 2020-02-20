package com.benmohammad.mobiusapplication.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.benmohammad.mobiusapplication.data.Task;
import com.benmohammad.mobiusapplication.data.TaskDetails;
import com.benmohammad.mobiusapplication.data.source.TasksDataSource;
import com.benmohammad.mobiusapplication.data.source.local.TasksPersistenceContract.TaskEntry;
import com.benmohammad.mobiusapplication.util.schedulers.BaseSchedulerProvider;
import com.google.common.base.Optional;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;

import static androidx.core.util.Preconditions.checkNotNull;


public class TasksLocalDataSource implements TasksDataSource {

    @Nullable private static TasksLocalDataSource INSTANCE;
    @Nullable private final BriteDatabase mDatabaseHelper;
    @Nullable private Function<Cursor, Task> mTaskMapperFunction;

    private TasksLocalDataSource(@NonNull Context context, @NonNull BaseSchedulerProvider schedulerProvider) {
        checkNotNull(context, "Contexxt cannot be null");
        checkNotNull(schedulerProvider, "SchedulerProvider cannot be null");
        TasksDbHelper dbHelper = new TasksDbHelper(context);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        mDatabaseHelper = sqlBrite.wrapDatabaseHelper(dbHelper, schedulerProvider.io());
        mTaskMapperFunction = this::getTask;
    }

    @NonNull
    private Task getTask(@NonNull Cursor c) {
        String itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
        String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
        String description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
        boolean completed = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
        TaskDetails details =
                TaskDetails.builder().title(title).description(description).completed(completed).build();
        return Task.create(itemId, details);
    }

    public static TasksLocalDataSource getInstance(@NonNull Context context, @NonNull BaseSchedulerProvider schedulerProvider) {
        if(INSTANCE == null) {
            INSTANCE = new TasksLocalDataSource(context, schedulerProvider);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }



    @Override
    public Flowable<List<Task>> getTasks() {
        String[] projection = {
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_COMPLETED
        };

        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", projection), TaskEntry.TABLE_NAME);
        return mDatabaseHelper
                .createQuery(TaskEntry.TABLE_NAME, sql)
                .mapToList(mTaskMapperFunction)
                .toFlowable(BackpressureStrategy.BUFFER);

    }

    @Override
    public Flowable<Optional<Task>> getTask(@NonNull String taskId) {
        String[] projection = {
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_COMPLETED
        };

        String sql = String.format(
                "SELECT %s FROM %s WHERE %s LIKE ?",
                        TextUtils.join(",", projection), TaskEntry.TABLE_NAME, TaskEntry.COLUMN_NAME_ENTRY_ID);
                return mDatabaseHelper
                        .createQuery(TaskEntry.TABLE_NAME, sql, taskId)
                        .mapToOneOrDefault(
                                cursor -> Optional.of(mTaskMapperFunction.apply(cursor)), Optional.<Task> absent())
                        .toFlowable(BackpressureStrategy.BUFFER);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_ENTRY_ID, task.id());
        values.put(TaskEntry.COLUMN_NAME_TITLE, task.details().title());
        values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, task.details().description());
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, task.details().completed());
        mDatabaseHelper.insert(TaskEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void deleteAllTask() {
        mDatabaseHelper.delete(TaskEntry.TABLE_NAME, null);
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        mDatabaseHelper.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    }
}
