package com.benmohammad.mobiusapplication.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.benmohammad.mobiusapplication.R;
import com.benmohammad.mobiusapplication.statistics.StatisticsActivity;
import com.google.android.material.navigation.NavigationView;

public class TasksActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_act);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if(navigationView != null) {
            setUpDrawerContent(navigationView);
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }


    private void setUpDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    switch(menuItem.getItemId()) {
                        case R.id.list_navigation_menu_item:

                            break;

                        case R.id.statistics_navigation_menu_item:
                            Intent intent = new Intent(TasksActivity.this, StatisticsActivity.class);
                            startActivity(intent);
                            break;

                            default:
                                break;
                    }

                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                }
        );
    }
}
