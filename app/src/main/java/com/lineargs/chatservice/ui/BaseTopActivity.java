package com.lineargs.chatservice.ui;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;

public abstract class BaseTopActivity extends BaseDrawerActivity {

    @Override
    protected void setupActionBar() {
        super.setupActionBar();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setupNavDrawer() {
        super.setupNavDrawer();
        /* Shows a drawer indicator */
        setDrawerIndicatorEnabled();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggleDrawer(item) || super.onOptionsItemSelected(item);
    }
}
