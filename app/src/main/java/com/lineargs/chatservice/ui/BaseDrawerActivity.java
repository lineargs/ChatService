package com.lineargs.chatservice.ui;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.lineargs.chatservice.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adds Navigation Drawer to {@link BaseActivity}
 */
public abstract class BaseDrawerActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigationView)
    NavigationView navigationView;

    /**
     * Helper method for setting up the Navigation Drawer
     */
    public void setupNavDrawer() {
        ButterKnife.bind(this);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                onNavItemClicked(item.getItemId());
                return false;
            }
        });
    }

    /**
     * If the Navigation Drawer is open it will close it or if it is not will
     * perform onBackPressed()
     */
    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
            return;
        }
        super.onBackPressed();
    }

    /**
     * Simple switch statement schecking if we are already displaying
     * the correct activity
     *
     * @param itemId Navigation menu item
     */
    private void onNavItemClicked(int itemId) {
        Intent intent = null;
        switch (itemId) {
            case R.id.nav_home:
                if (this instanceof HomeActivity) {
                    break;
                }
                intent = new Intent(this, HomeActivity.class);
                break;
            case R.id.nav_profile:
                intent = new Intent(this, ProfileActivity.class);
                break;
            case R.id.nav_sign_out:
                //Sign out
                AuthUI.getInstance().signOut(this);
                break;
        }

        if (intent != null) {
            startNavDrawerItem(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Fires up the intent
     *
     * @param intent Intent
     */
    private void startNavDrawerItem(Intent intent) {
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * @return true if Navigation Drawer is open
     */
    public boolean isNavDrawerOpen() {
        return drawerLayout.isDrawerOpen(navigationView);
    }

    /**
     * Highlights the given position. Activities listed should call
     * this method in {@link #onStart()}
     *
     * @param menuItemId Navigation menu item
     */
    public void setDrawerSelectedItem(@IdRes int menuItemId) {
        navigationView.setCheckedItem(menuItemId);
    }

    /**
     * Sets the toolbar navigation icon
     */
    public void setDrawerIndicatorEnabled() {
        toolbar.setNavigationIcon(R.drawable.icon_round_menu_white);
    }

    /**
     * Opens the Navigation Drawer
     */
    public void openNavDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    /**
     * Closes the Navigation Drawer
     */
    public void closeNavDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Toggles the Navigation Drawer
     *
     * @param item MenuItem
     * @return true / false
     */
    public boolean toggleDrawer(MenuItem item) {
        if (item != null && item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                closeNavDrawer();
            } else {
                openNavDrawer();
            }
            return true;
        }
        return false;
    }
}
