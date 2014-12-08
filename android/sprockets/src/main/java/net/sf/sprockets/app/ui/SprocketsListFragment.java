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

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import net.sf.sprockets.view.ActionModePresenter;

import butterknife.ButterKnife;
import icepick.Icicle;

/**
 * {@link ButterKnife#inject(Object, View) Injects} content Views, saves the instance state of
 * {@link Icicle} fields and restores them when recreated, hides and restores the list's ActionMode
 * as requested (e.g. in a {@link NavigationDrawerActivity}).
 */
public abstract class SprocketsListFragment extends ListFragment implements ActionModePresenter {
    /**
     * Shortcut to {@link #getActivity()}.
     */
    protected Activity a;
    @Icicle
    int[] mCheckedItemPos;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        a = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SprocketsFragment.onCreate(this, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SprocketsFragment.onViewCreated(this, view, savedInstanceState);
    }

    /**
     * Shortcut to {@link #getActivity()}, casting to your assignment type.
     */
    public <T extends Activity> T a() {
        return (T) a;
    }

    @Override
    public boolean hideActionMode() {
        ListView view = getListView();
        if (view.getCheckedItemCount() > 0) {
            Object[] result = SprocketsFragment.hideActionMode(view);
            mCheckedItemPos = (int[]) result[1];
            return (Boolean) result[0];
        }
        return false;
    }

    @Override
    public boolean restoreActionMode() {
        if (mCheckedItemPos != null) {
            boolean checked = SprocketsFragment.restoreActionMode(getListView(), mCheckedItemPos);
            mCheckedItemPos = null; // don't restore again
            return checked;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SprocketsFragment.onSaveInstanceState(this, outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SprocketsFragment.onDestroyView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        a = null;
    }
}
