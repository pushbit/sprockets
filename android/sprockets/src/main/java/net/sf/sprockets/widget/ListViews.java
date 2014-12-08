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
import android.widget.Adapter;
import android.widget.ListView;

import net.sf.sprockets.view.Views;

/**
 * Utility methods for working with ListViews.
 */
public class ListViews {
    private ListViews() {
    }

    /**
     * Get the height, in pixels, of all adapter Views in the list. Any off screen Views will be
     * created by the list's Adapter and measured.
     */
    public static int getHeight(ListView view) {
        return getHeight(view, 0, view.getAdapter().getCount());
    }

    /**
     * Get the height, in pixels, of the list's adapter Views from the start position (inclusive) to
     * the end position (exclusive). Any off screen Views will be created by the list's Adapter and
     * measured.
     */
    public static int getHeight(ListView view, int start, int end) {
        return getHeight(view, start, end, Integer.MAX_VALUE);
    }

    /**
     * Get the height in pixels, up to the limit, of the list's adapter Views from the start
     * position (inclusive) to the end position (exclusive). Any off screen Views will be created by
     * the list's Adapter and measured.
     * <p>
     * The limit can be used to eliminate unnecessary View creation and measurement when you only
     * need to know if the adapter Views are at least a certain height.
     * </p>
     */
    public static int getHeight(ListView view, int start, int end, int limit) {
        int height = 0;
        int first = view.getFirstVisiblePosition();
        int last = view.getLastVisiblePosition();
        int dividerHeight = view.getDividerHeight();
        Adapter adapter = view.getAdapter();
        View[] converts = new View[adapter.getViewTypeCount()];
        for (int i = start; i < end; i++) {
            if (i >= first && i <= last) { // add height of visible View
                height += view.getChildAt(i - first).getHeight() + dividerHeight;
            } else { // get off screen View and add its measured height
                int type = adapter.getItemViewType(i); // reuse adapter View when possible
                View child = type >= 0 ? converts[type] = adapter.getView(i, converts[type], view)
                        : adapter.getView(i, null, view);
                height += Views.measure(child, view).getMeasuredHeight() + dividerHeight;
            }
            if (height >= limit) {
                return limit;
            }
        }
        return height;
    }
}
