package com.lineargs.chatservice.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.lineargs.chatservice.R;

/**
 * Provides common functionality across all activities
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Implementers must call this in {@link #onCreate} after {@link #setContentView} if they want
     * to use the action bar. If want to set a title, might also want to @Override
     */
    protected void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
