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

package net.sf.sprockets.content.res;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

/**
 * Utility methods for working with theme attributes.
 */
public class Themes {
    private Themes() {
    }

    /**
     * Get the ActionBar height in the Context's theme.
     */
    public static int getActionBarSize(Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int size = a.getDimensionPixelSize(0, 0);
        a.recycle();
        return size;
    }

    /**
     * Get the ActionBar background in the Context's theme.
     *
     * @return null if a background is not defined
     */
    public static Drawable getActionBarBackground(Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.actionBarStyle});
        int id = a.getResourceId(0, 0);
        a.recycle();
        if (id > 0) {
            a = context.obtainStyledAttributes(id, new int[]{android.R.attr.background});
            Drawable background = a.getDrawable(0);
            a.recycle();
            return background;
        }
        return null;
    }
}
