/*
 * Copyright 2014-2015 pushbit <pushbit@gmail.com>
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

import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.sf.sprockets.R;
import net.sf.sprockets.content.Intents;
import net.sf.sprockets.preference.Prefs;

import icepick.Icicle;

import static android.content.Intent.ACTION_VIEW;
import static net.sf.sprockets.preference.Prefs.RATED;
import static net.sf.sprockets.preference.Prefs.SPROCKETS;

/**
 * Google Play style (pre-Material Design) navigation drawer. Subclasses should call one of the
 * {@code setItems} methods, optionally one of the {@code setSelectedItem} methods,
 * and implement one of the {@code onListItemClick} methods.
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/java/net/sf/sprockets/sample/app/ui/NavigationDrawerFragment.java" target="_blank">Sample Implementation</a>
 * </p>
 */
public abstract class BaseNavigationDrawerFragment extends SprocketsListFragment {
    @Icicle
    int[] mItemResIds;
    @Icicle
    String[] mItems;
    @Icicle
    int mSelResId;
    @Icicle
    int mSelPos = -1;
    @Icicle
    boolean mSettings;
    @Icicle
    boolean mHelp;
    @Icicle
    boolean mFeedback;
    @Icicle
    boolean mRate;
    private View mRateView; // save ref so can be removed when clicked
    private ItemAdapter mAdapter;

    /**
     * Use each string from the array as a navigation drawer item.
     */
    public BaseNavigationDrawerFragment setItems(int arrayResId) {
        TypedArray array = getResources().obtainTypedArray(arrayResId);
        int length = array.length();
        mItemResIds = new int[length];
        mItems = new String[length];
        for (int i = 0; i < length; i++) {
            mItemResIds[i] = array.getResourceId(i, 0);
            mItems[i] = array.getString(i);
        }
        array.recycle();
        updateItems();
        return this;
    }

    /**
     * Use each element as a navigation drawer item.
     */
    public BaseNavigationDrawerFragment setItems(String[] items) {
        mItems = items;
        updateItems();
        return this;
    }

    private void updateItems() {
        if (mAdapter != null && mItems != null) {
            mAdapter.clear();
            mAdapter.addAll(mItems);
        }
    }

    /**
     * If your {@link #setItems(int) items array} is composed of references to string resources, you
     * may set the currently selected item by its resource ID.
     *
     * @param resId 0 for no selected item
     */
    public BaseNavigationDrawerFragment setSelectedItemResId(int resId) {
        mSelResId = resId;
        mSelPos = -1;
        updateItemViews();
        return this;
    }

    /**
     * Set the item at the position as the currently selected item.
     *
     * @param position -1 for no selected item
     */
    public BaseNavigationDrawerFragment setSelectedItemPosition(int position) {
        mSelPos = position;
        mSelResId = 0;
        updateItemViews();
        return this;
    }

    private void updateItemViews() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Show the Settings option at the bottom of the list. This must be called before
     * {@link #onViewCreated(View, Bundle) onViewCreated} executes. Default is false.
     * <p>
     * Listen for the click event in either of the {@code onListItemClick} methods, where
     * {@link View#getId()} or {@code resId == R.id.settings}.
     * </p>
     */
    public BaseNavigationDrawerFragment showSettings(boolean show) {
        mSettings = show;
        return this;
    }

    /**
     * Show the Help option at the bottom of the list. This must be called before
     * {@link #onViewCreated(View, Bundle) onViewCreated} executes. Default is false.
     * <p>
     * Listen for the click event in either of the {@code onListItemClick} methods, where
     * {@link View#getId()} or {@code resId == R.id.help}.
     * </p>
     */
    public BaseNavigationDrawerFragment showHelp(boolean show) {
        mHelp = show;
        return this;
    }

    /**
     * Show the Send Feedback option at the bottom of the list. This must be called before
     * {@link #onViewCreated(View, Bundle) onViewCreated} executes. Default is false.
     * <p>
     * Listen for the click event in either of the {@code onListItemClick} methods, where
     * {@link View#getId()} or {@code resId == R.id.feedback}.
     * </p>
     */
    public BaseNavigationDrawerFragment showFeedback(boolean show) {
        mFeedback = show;
        return this;
    }

    /**
     * Show the Rate option at the bottom of the list. This must be called before
     * {@link #onViewCreated(View, Bundle) onViewCreated} executes. Default is false.
     * <p>
     * On click the user will be taken to the app's Google Play page.  The Rate option will then
     * no longer display in the list.
     * </p>
     */
    public BaseNavigationDrawerFragment showRate(boolean show) {
        mRate = show;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = super.onCreateView(inflater, container, state);
        view.setBackgroundResource(R.color.navigation_drawer_background);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView list = getListView();
        list.setDivider(null);
        list.setDrawSelectorOnTop(true);
        LayoutInflater inf = a.getLayoutInflater();
        list.addFooterView(inf.inflate(R.layout.list_divider, list, false), null, false);
        if (mSettings) {
            list.addFooterView(inf.inflate(R.layout.navigation_drawer_settings, list, false));
        }
        if (mHelp) {
            list.addFooterView(inf.inflate(R.layout.navigation_drawer_help, list, false));
        }
        if (mFeedback) {
            list.addFooterView(inf.inflate(R.layout.navigation_drawer_feedback, list, false));
        }
        if (mRate && !Prefs.getBoolean(a, SPROCKETS, RATED)) {
            mRateView = inf.inflate(R.layout.navigation_drawer_rate, list, false);
            list.addFooterView(mRateView);
        }
        mAdapter = new ItemAdapter();
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        if (mItemResIds != null) {
            int resId = position < mItemResIds.length ? mItemResIds[position] : view.getId();
            if (resId > 0) {
                onListItemClick(list, view, position, id, resId);
            }
        }
    }

    /**
     * If your {@link #setItems(int) items array} is composed of references to string resources,
     * this method will be called with the resource ID of the item that was clicked.
     */
    public void onListItemClick(ListView list, View view, int position, long id, int resId) {
        if (resId == R.id.rate) {
            Intent intent =
                    new Intent(ACTION_VIEW, Uri.parse("market://details?id=" + a.getPackageName()));
            if (Intents.hasActivity(a, intent)) {
                startActivity(intent);
            }
            Prefs.putBoolean(a, SPROCKETS, RATED, true);
            list.removeFooterView(mRateView);
        }
    }

    /**
     * Highlights selected items.
     */
    private class ItemAdapter extends ArrayAdapter<String> {
        private ItemAdapter() {
            super(a, R.layout.navigation_drawer_item);
            if (mItems != null) {
                addAll(mItems);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            int style = mSelResId > 0 && mItemResIds != null && mItemResIds[position] == mSelResId
                    || mSelPos >= 0 && position == mSelPos
                    ? R.style.NavigationDrawerItem_Selected : R.style.NavigationDrawerItem;
            view.setTextAppearance(getContext(), style);
            return view;
        }
    }
}
