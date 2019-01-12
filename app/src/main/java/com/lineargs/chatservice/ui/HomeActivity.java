package com.lineargs.chatservice.ui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lineargs.chatservice.R;
import com.lineargs.chatservice.ui.BaseTopActivity;

import butterknife.OnClick;

public class HomeActivity extends BaseTopActivity {

    //TODO Initialize the UI afer sign in
    //TODO Breakdown the UI after user signs out
    //TODO Firebase AuthUI StateListener

    public static final String MESSAGES = "messages";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupActionBar();
        setupNavDrawer();
    }

    @Override
    protected void setupActionBar() {
        super.setupActionBar();
        setTitle(getString(R.string.home_activity_title));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.home_activity_title));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setDrawerSelectedItem(R.id.nav_home);
    }

    @OnClick(R.id.button_chat)
    public void onClickMessages() {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.putExtra(MESSAGES, MESSAGES);
        startActivity(intent);
    }
}
