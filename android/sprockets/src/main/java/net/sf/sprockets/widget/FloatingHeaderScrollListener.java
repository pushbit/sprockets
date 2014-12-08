/*
 * Copyright 2013-2014 pushbit <pushbit@gmail.com>
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
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.ListView;

import net.sf.sprockets.content.res.Themes;
import net.sf.sprockets.view.Views;
import net.sf.sprockets.widget.ListScrollListeners.OnScrollApprover;

/**
 * Slides a View that floats above your list header(s) up and down along with the scrolling of the
 * list. When the floating View reaches the top of the list, it will stop and remain there while the
 * list continues to scroll beneath it. When scrolling back up to the top of the list, the floating
 * View will slide back down along with the list header(s) to its original position.
 * <p>
 * You must {@link ListView#addHeaderView(View) add} at least one header to your list. The floating
 * View must be initially positioned at the top of the list and then use
 * {@link View#setTranslationY(float)} (or android:translationY in XML) to move it down to the
 * desired starting location above the list header(s). As the list scrolls, the floating View's
 * translationY property will be adjusted to synchronise it with the list. In most cases, you will
 * probably want to include empty space in the list header where the floating View will overlay, so
 * that it will not obscure the list header or content.
 * </p>
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/java/net/sf/sprockets/sample/app/ui/FloatingHeaderActivity.java" target="_blank">Sample Usage</a>
 * </p>
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/res/layout/floating_header.xml" target="_blank">Sample Layout</a>
 * </p>
 */
public class FloatingHeaderScrollListener implements OnScrollListener {
    private final View mFloater;
    private final float mStartY;
    private int mMargin;
    private OnScrollApprover mApprover;

    /**
     * Slide the View that floats above your list header(s).
     *
     * @param floatingHeader translationY must already be set to the desired starting location
     */
    public FloatingHeaderScrollListener(View floatingHeader) {
        mFloater = floatingHeader;
        mStartY = floatingHeader.getTranslationY();
    }

    /**
     * Stop sliding the floating View when it reaches the ActionBar that is overlaying the list.
     */
    public FloatingHeaderScrollListener setActionBarOverlay(boolean actionBarOverlay) {
        mMargin = actionBarOverlay ? Themes.getActionBarSize(mFloater.getContext()) : 0;
        return this;
    }

    /**
     * Request approval from the approver before sliding the floating View.
     */
    public FloatingHeaderScrollListener setOnScrollApprover(OnScrollApprover approver) {
        mApprover = approver;
        return this;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int first, int visible, int total) {
        if (mApprover != null && !mApprover.onScroll(this, view, first, visible, total)) {
            return;
        }
        Adapter adapter = view.getAdapter();
        if (adapter == null) {
            return; // wait until it's set
        }
        ListView list = (ListView) view;
        if (first < list.getHeaderViewsCount()) { // headers visible, floater may move up or down
            int prev = 0; // move the height of the previous headers plus the topmost's scroll
            for (int i = first - 1; i >= 0; i--) {
                View header = adapter.getView(i, null, view);
                if (header.getMeasuredHeight() == 0) {
                    Views.measure(header, view);
                }
                prev += header.getMeasuredHeight() + list.getDividerHeight();
            }
            mFloater.setTranslationY(Math.max(mStartY - prev
                    + adapter.getView(first, null, view).getTop(), mMargin));
            view.setVerticalScrollBarEnabled(false); // don't show it disappearing behind floater
        } else {
            /*
             * headers have scrolled off the screen and floater should be at the top; this insurance
			 * is necessary because flings can cause loss of header 'top' property updates and the
			 * last value may suggest that a header is still on the screen when it's not
			 */
            mFloater.setTranslationY(mMargin);
            view.setVerticalScrollBarEnabled(true); // can let it emerge below the floater now
        }
    }
}
