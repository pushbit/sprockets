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

package net.sf.sprockets.app.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;

import com.google.common.base.Supplier;

import net.sf.sprockets.app.NavigationDrawerToggle;
import net.sf.sprockets.view.ActionModePresenter;

import java.util.Set;

import static android.view.Gravity.START;
import static com.google.common.base.Preconditions.checkState;

/**
 * Manages the ActionBar during navigation drawer events according to the
 * <a href="http://developer.android.com/design/patterns/navigation-drawer.html"
 * target="_blank">Android design guidelines</a>. See {@link NavigationDrawerToggle} for details
 * on how the ActionBar is affected and your responsibilities in {@code onCreateOptionsMenu}
 * methods.
 * <p>
 * Subclasses must call {@link #setDrawerLayout(DrawerLayout) setDrawerLayout} before
 * {@link #onPostCreate(Bundle) onPostCreate} runs.
 * </p>
 * <p>
 * If your Activity includes components that may start an {@link ActionMode}, they should implement
 * {@link ActionModePresenter} and be provided in {@link #getActionModePresenters()}.
 * </p>
 */
public abstract class NavigationDrawerActivity extends SprocketsActivity implements DrawerListener {
    private DrawerLayout mLayout;
    private NavigationDrawerToggle mToggle;

    /**
     * Listen for drawer events on the DrawerLayout.
     */
    public NavigationDrawerActivity setDrawerLayout(DrawerLayout layout) {
        mLayout = layout;
        mToggle = new NavigationDrawerToggle(this, layout)
                .setActionModePresentersSupplier(new Presenters()).setDrawerListener(this);
        return this;
    }

    /**
     * Get the previously set DrawerLayout.
     */
    public DrawerLayout getDrawerLayout() {
        return mLayout;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        checkState(mToggle != null, "you must call setDrawerLayout before onPostCreate runs");
        mToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /**
     * Override to provide any components that should be asked to hide their ActionMode when the
     * navigation drawer is opened.
     *
     * @return null when there aren't any ActionModePresenters
     */
    public Set<ActionModePresenter> getActionModePresenters() {
        return null;
    }

    /**
     * After the next navigation drawer close, wait before restoring the ActionBar state.
     */
    public NavigationDrawerActivity setOneTimeDrawerActionDelay(long millis) {
        mToggle.setOneTimeDrawerActionDelay(millis);
        return this;
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }

    @Override
    public void onDrawerClosed(View drawerView) {
    }

    @Override
    public void onBackPressed() {
        if (mLayout.isDrawerOpen(START)) {
            mLayout.closeDrawer(START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Supplies any ActionModePresenters from a subclass.
     */
    private class Presenters implements Supplier<Set<ActionModePresenter>> {
        @Override
        public Set<ActionModePresenter> get() {
            return getActionModePresenters();
        }
    }
}
