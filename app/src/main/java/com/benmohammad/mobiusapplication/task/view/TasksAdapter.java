package com.benmohammad.mobiusapplication.task.view;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.benmohammad.mobiusapplication.R;
import com.benmohammad.mobiusapplication.task.view.TasksListViewData.TaskViewData;
import com.google.common.collect.ImmutableList;

import static androidx.core.util.Preconditions.checkNotNull;

public class TasksAdapter extends BaseAdapter {

    private ImmutableList<TaskViewData> mTasks;
    private TaskItemListener mItemListener;

    public void setItemListener(TaskItemListener listener) {
        this.mItemListener = listener;
    }

    public void replaceData(ImmutableList<TaskViewData> tasks) {
        mTasks = checkNotNull(tasks);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTasks == null ? 0 : mTasks.size();
    }

    @Override
    public TaskViewData getItem(int position) {
        return mTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.task_item, parent, false);
        }

        final TaskViewData task = getItem(position);
        TextView titleTv = rowView.findViewById(R.id.title);
        titleTv.setText(task.title());

        CheckBox completeCb = rowView.findViewById(R.id.complete);
        completeCb.setChecked(task.completed());

        //Drawable background = parent.getContext().getResources().getDrawable(task.backgroundDrawableId());
        //rowView.setBackgroundDrawable(background);

        completeCb.setOnClickListener(__ -> {
            if(mItemListener == null) return;

            if(!task.completed()) {
                mItemListener.onCompleteTaskClick(task.id());
            } else {
                mItemListener.onActiveTaskClick(task.id());
            }
        });

        rowView.setOnClickListener(__ -> {
            if(mItemListener != null) mItemListener.onTaskClicked(task.id());
        });

        return rowView;

    }


    public interface TaskItemListener {
        void onTaskClicked(String id);
        void onCompleteTaskClick(String id);
        void onActiveTaskClick(String id);
    }

}
