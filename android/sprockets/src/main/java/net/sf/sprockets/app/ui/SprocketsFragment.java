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
import android.app.Fragment;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import android.widget.AbsListView;

import net.sf.sprockets.util.SparseArrays;
import net.sf.sprockets.view.ActionModePresenter;

import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.Icicle;

/**
 * {@link ButterKnife#inject(Object, View) Injects} content Views, saves the instance state of
 * {@link Icicle} fields and restores them when recreated.
 * <p>
 * If you have an AbsListView that creates an {@link ActionMode} when items are checked, you can
 * provide it in {@link #getAbsListView()} and its ActionMode will be hidden and restored as
 * requested (e.g. in a {@link NavigationDrawerActivity}).
 * </p>
 */
public abstract class SprocketsFragment extends Fragment implements ActionModePresenter {
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
        onCreate(this, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewCreated(this, view, savedInstanceState);
    }

    /**
     * Shortcut to {@link #getActivity()}, casting to your assignment type.
     */
    public <T extends Activity> T a() {
        return (T) a;
    }

    /**
     * Override to provide an AbsListView that creates an {@link ActionMode} when items are checked.
     * Its ActionMode will be hidden and restored as requested.
     */
    public AbsListView getAbsListView() {
        return null;
    }

    @Override
    public boolean hideActionMode() {
        AbsListView view = getAbsListView();
        if (view != null && view.getCheckedItemCount() > 0) {
            Object[] result = hideActionMode(view);
            mCheckedItemPos = (int[]) result[1];
            return (Boolean) result[0];
        }
        return false;
    }

    @Override
    public boolean restoreActionMode() {
        AbsListView view = getAbsListView();
        if (view != null && mCheckedItemPos != null) {
            boolean checked = restoreActionMode(view, mCheckedItemPos);
            mCheckedItemPos = null; // don't restore again
            return checked;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onSaveInstanceState(this, outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onDestroyView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        a = null;
    }

    /**
     * Restore the instance state of {@link Icicle} fields.
     */
    static void onCreate(Fragment frag, Bundle savedInstanceState) {
        Icepick.restoreInstanceState(frag, savedInstanceState);
    }

    /**
     * {@link ButterKnife#inject(Object, View) Inject} content Views.
     */
    static void onViewCreated(Fragment frag, View view, Bundle savedInstanceState) {
        ButterKnife.inject(frag, view);
    }

    /**
     * Uncheck any checked items.
     *
     * @return true if any positions were unchecked, the unchecked item positions
     */
    static Object[] hideActionMode(AbsListView view) {
        int[] checkedItemPos = SparseArrays.trueKeys(view.getCheckedItemPositions());
        return new Object[]{check(view, checkedItemPos, false), checkedItemPos};
    }

    /**
     * Check the items.
     *
     * @return true if any positions were checked
     */
    static boolean restoreActionMode(AbsListView view, int[] checkedItemPos) {
        return check(view, checkedItemPos, true);
    }

    /**
     * Check or uncheck the positions.
     *
     * @return true if any positions were checked or unchecked
     */
    private static boolean check(AbsListView view, int[] checkedItemPos, boolean check) {
        for (int pos : checkedItemPos) {
            view.setItemChecked(pos, check);
        }
        return checkedItemPos.length > 0;
    }

    /**
     * Save the instance state of {@link Icicle} fields.
     */
    static void onSaveInstanceState(Fragment frag, Bundle outState) {
        Icepick.saveInstanceState(frag, outState);
    }

    /**
     * {@link ButterKnife#reset(Object) Reset} content Views.
     */
    static void onDestroyView(Fragment frag) {
        ButterKnife.reset(frag);
    }
}
