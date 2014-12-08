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

package net.sf.sprockets.widget;

import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ListView;

import net.sf.sprockets.view.Views;
import net.sf.sprockets.widget.ListScrollListeners.ObservingScrollListener;
import net.sf.sprockets.widget.ListScrollListeners.OnScrollApprover;

/**
 * Synchronises the scrolling of a View with a ListView, at a speed relative to the list scrolling
 * speed.
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/java/net/sf/sprockets/sample/app/ui/ParallaxViewActivity.java" target="_blank">Sample Usage</a>
 * </p>
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/res/layout/parallax_view.xml" target="_blank">Sample Layout</a>
 * </p>
 */
public class ParallaxViewScrollListener extends ObservingScrollListener {
    private final View mView;
    private final float mStartY;
    private final float mSpeed;
    private OnScrollApprover mApprover;
    private int[] mHeights;
    private int mDividerHeight;
    private View[] mConverts;

    /**
     * Scroll the View at the speed relative to the list scrolling speed.
     *
     * @param speed e.g. 0.5f for half of the list scrolling speed
     */
    public ParallaxViewScrollListener(View view, float speed) {
        mView = view;
        mStartY = view.getTranslationY();
        mSpeed = speed;
    }

    /**
     * Request approval from the approver before scrolling the View.
     */
    public ParallaxViewScrollListener setOnScrollApprover(OnScrollApprover approver) {
        mApprover = approver;
        return this;
    }

    @Override
    public void onScrollStateChanged(final AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int first, int visible, int total) {
        if (mApprover != null && !mApprover.onScroll(this, view, first, visible, total)) {
            return;
        }
        super.onScroll(view, first, visible, total);
        Adapter adapter = view.getAdapter();
        if (adapter == null) {
            return; // wait until it's set
        }
        if (mHeights == null) {
            mHeights = new int[adapter.getCount()];
            mDividerHeight = ((ListView) view).getDividerHeight();
            mConverts = new View[adapter.getViewTypeCount()];
        }
        int prev = 0; // sum the heights of the previous Views
        for (int i = first - 1; i >= 0; i--) {
            if (mHeights[i] == 0) { // create and measure the View
                int type = adapter.getItemViewType(i); // reuse adapter View when possible
                View child = type >= 0 ? mConverts[type] = adapter.getView(i, mConverts[type], view)
                        : adapter.getView(i, null, view);
                mHeights[i] = Views.measure(child, view).getMeasuredHeight() + mDividerHeight;
            }
            prev += mHeights[i];
        }
        View child = view.getChildAt(0); // scroll height of previous Views plus topmost scroll
        if (child != null) { // can be on first call, apparently scrolling before child views added
            mView.setTranslationY(mStartY - (prev - child.getTop()) * mSpeed);
            if (mHeights[first] == 0) { // save topmost View height to use after it's off screen
                mHeights[first] = child.getHeight() + mDividerHeight;
            }
        }
    }

    @Override
    public void onChanged() {
        super.onChanged();
        mHeights = null;
    }
}
