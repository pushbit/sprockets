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

package net.sf.sprockets.view;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.UNSPECIFIED;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Utility methods for working with Views.
 */
public class Views {
    private Views() {
    }

    /**
     * Make the view visible if it isn't already.
     */
    public static View visible(View view) {
        return visibility(view, VISIBLE);
    }

    /**
     * Make the view invisible if it isn't already.
     */
    public static View invisible(View view) {
        return visibility(view, INVISIBLE);
    }

    /**
     * Make the view gone if it isn't already.
     */
    public static View gone(View view) {
        return visibility(view, GONE);
    }

    private static View visibility(View view, int visibility) {
        if (view != null && view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
        return view;
    }

    /**
     * {@link View#measure(int, int) Measure} the View without any parent constraints. You can then
     * call {@link View#getMeasuredWidth()} and {@link View#getMeasuredHeight()}.
     *
     * @param view must have {@link LayoutParams}
     */
    public static View measure(View view) {
        return measure(view, null);
    }

    /**
     * {@link View#measure(int, int) Measure} the View in its parent. You can then call
     * {@link View#getMeasuredWidth()} and {@link View#getMeasuredHeight()}.
     *
     * @param view   must have {@link LayoutParams}
     * @param parent must already be measured
     */
    public static View measure(View view, ViewGroup parent) {
        LayoutParams p = view.getLayoutParams();
        int w = parent != null && p.width == MATCH_PARENT ? parent.getMeasuredWidth() : p.width;
        int h = parent != null && p.height == MATCH_PARENT ? parent.getMeasuredHeight() : p.height;
        return measure(view, w, h);
    }

    private static View measure(View view, int width, int height) {
        int w = MeasureSpec.makeMeasureSpec(width, width == WRAP_CONTENT ? UNSPECIFIED : EXACTLY);
        int h = MeasureSpec.makeMeasureSpec(height, height == WRAP_CONTENT ? UNSPECIFIED : EXACTLY);
        view.measure(w, h);
        return view;
    }
}
