package com.benmohammad.mobiusapplication.task.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ScrollChildSwipeRefreshLayout extends SwipeRefreshLayout {

    private View mScrollUpChild;



    public ScrollChildSwipeRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public ScrollChildSwipeRefreshLayout(@NonNull Context context, AttributeSet sattrs) {
        super(context, sattrs);
    }

    @Override
    public boolean canChildScrollUp() {
        if(mScrollUpChild != null) {
            return ViewCompat.canScrollVertically(mScrollUpChild, -1);
        }
        return super.canChildScrollUp();
    }

    public void setScrollUpChild(View view) {
        mScrollUpChild = view;
    }
}
