/*
 * Copyright 2014 pushbit <pushbit@gmail.com>
 * 
 * This file is part of Sprockets.
 * 
 * Sprockets is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Sprockets is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Sprockets. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sprockets.app;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.view.View;

import com.google.common.base.Supplier;

import net.sf.sprockets.R;
import net.sf.sprockets.app.ui.NavigationDrawerActivity;
import net.sf.sprockets.view.ActionModePresenter;

import java.util.Set;

import static android.app.ActionBar.DISPLAY_SHOW_TITLE;
import static android.app.ActionBar.NAVIGATION_MODE_STANDARD;
import static android.view.Gravity.LEFT;
import static android.view.Gravity.START;

/**
 * Manages the ActionBar of an Activity that contains a navigation DrawerLayout according to the
 * <a href="http://developer.android.com/design/patterns/navigation-drawer.html"
 * target="_blank">Android design guidelines</a>. The standard navigation drawer indicator is
 * displayed, a shadow is applied to the navigation drawer, when the drawer is opened the ActionBar
 * navigation mode is set to {@link ActionBar#NAVIGATION_MODE_STANDARD standard} and the title is
 * changed to the app name, and when the drawer is closed the ActionBar is restored to its previous
 * state.
 * <p>
 * Opening and closing the navigation drawer also invalidates the options menu and your
 * {@code onCreateOptionsMenu} method(s) should only add Activity-specific items when the drawer is
 * {@link DrawerLayout#isDrawerOpen(int) not open}.
 * </p>
 * <p>
 * See {@link ActionBarDrawerToggle} for Activity callbacks that must call methods in this class. Or
 * your Activity may extend {@link NavigationDrawerActivity} and these calls will be handled for
 * you.
 * </p>
 */
public class NavigationDrawerToggle extends ActionBarDrawerToggle {
    private final Activity mActivity;
    private final DrawerLayout mLayout;
    private final CharSequence mApp;
    private final ActionBar mActionBar;
    private int mOptions;
    private int mMode;
    private CharSequence mTitle;
    private CharSequence mSubtitle;
    private Supplier<Set<ActionModePresenter>> mSupplier;
    private DrawerListener mListener;
    private long mDelay;

    /**
     * Manage the Activity's ActionBar and listen for navigation drawer events on the DrawerLayout.
     */
    public NavigationDrawerToggle(Activity activity, DrawerLayout layout) {
        super(activity, layout, R.drawable.ic_drawer, R.string.open_navigation_drawer,
                R.string.close_navigation_drawer);
        mActivity = activity;
        mLayout = layout;
        mApp = activity.getPackageManager().getApplicationLabel(activity.getApplicationInfo());
        mActionBar = activity.getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        layout.setDrawerShadow(R.drawable.drawer_shadow, START);
        layout.setDrawerListener(this);
    }

    /**
     * When the navigation drawer is opened, ask the ActionModePresenters to hide any ActionMode.
     * When the navigation drawer is closed, ask them to restore any ActionMode that was hidden.
     *
     * @param supplier may supply null when there are no ActionModePresenters
     */
    public NavigationDrawerToggle setActionModePresentersSupplier(
            Supplier<Set<ActionModePresenter>> supplier) {
        mSupplier = supplier;
        return this;
    }

    /**
     * Forward navigation drawer events to the listener after they are processed here.
     */
    public NavigationDrawerToggle setDrawerListener(DrawerListener listener) {
        mListener = listener;
        return this;
    }

    /**
     * After the next navigation drawer close, wait before restoring the ActionBar state.
     */
    public NavigationDrawerToggle setOneTimeDrawerActionDelay(long millis) {
        mDelay = millis;
        return this;
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        super.onDrawerStateChanged(newState);
        if (mListener != null) {
            mListener.onDrawerStateChanged(newState);
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);
        if (mListener != null) {
            mListener.onDrawerSlide(drawerView, slideOffset);
        }
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        if (isNavigationDrawer(drawerView)) {
            saveActionBar();
            mActivity.invalidateOptionsMenu();
        }
        if (mListener != null) {
            mListener.onDrawerOpened(drawerView);
        }
    }

    /**
     * Save the current ActionBar state and then only display the app label.
     */
    private void saveActionBar() {
        /* save ActionBar state */
        mOptions = mActionBar.getDisplayOptions();
        mMode = mActionBar.getNavigationMode();
        mTitle = mActivity.getTitle();
        mSubtitle = mActionBar.getSubtitle();
        /* only show app label */
        mActionBar.setDisplayOptions(DISPLAY_SHOW_TITLE, DISPLAY_SHOW_TITLE);
        mActionBar.setNavigationMode(NAVIGATION_MODE_STANDARD);
        mActivity.setTitle(mApp);
        mActionBar.setSubtitle(null);
        hideActionMode(true);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        if (isNavigationDrawer(drawerView)) {
            if (mDelay > 0) {
                drawerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        restoreActionModeAndBar();
                    }
                }, mDelay);
                mDelay = 0L;
            } else {
                restoreActionModeAndBar();
            }
        }
        if (mListener != null) {
            mListener.onDrawerClosed(drawerView);
        }
    }

    /**
     * Restore any hidden ActionMode and then restore the ActionBar.
     */
    private void restoreActionModeAndBar() {
        if (hideActionMode(false)) { // delay to avoid flickering
            mLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    restoreActionBar();
                }
            }, 300L);
        } else {
            restoreActionBar();
        }
    }

    /**
     * Restore the saved ActionBar state.
     */
    private void restoreActionBar() {
        mActionBar.setDisplayOptions(mOptions);
        mActionBar.setNavigationMode(mMode);
        mActivity.setTitle(mTitle);
        mActionBar.setSubtitle(mSubtitle);
        mActivity.invalidateOptionsMenu();
    }

    /**
     * True if the view is the navigation drawer.
     */
    private boolean isNavigationDrawer(View view) {
        return (((LayoutParams) view.getLayoutParams()).gravity & LEFT) == LEFT; // includes START
    }

    /**
     * Ask any ActionModePresenters to hide or restore their ActionMode.
     *
     * @return true if any ActionMode was hidden or restored
     */
    private boolean hideActionMode(boolean hide) {
        boolean changed = false;
        Set<ActionModePresenter> presenters = mSupplier != null ? mSupplier.get() : null;
        if (presenters != null) {
            for (ActionModePresenter presenter : presenters) {
                boolean change = hide ? presenter.hideActionMode() : presenter.restoreActionMode();
                if (!changed && change) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    @Override
    public void syncState() {
        super.syncState();
        if (mLayout.isDrawerOpen(START)) {
            saveActionBar();
        }
    }
}
