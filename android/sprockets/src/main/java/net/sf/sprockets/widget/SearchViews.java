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
import android.widget.SearchView;

/**
 * Utility methods for working with SearchViews.
 */
public class SearchViews {
    private SearchViews() {
    }

    /**
     * Override the default background with a theme version.
     */
    public static SearchView setBackground(SearchView view, int drawableResId) {
        int id = view.getResources().getIdentifier("android:id/search_plate", null, null);
        if (id > 0) {
            View search = view.findViewById(id);
            if (search != null) {
                search.setBackgroundResource(drawableResId);
            }
        }
        return view;
    }
}
