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

package net.sf.sprockets.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import net.sf.sprockets.content.Managers;

/**
 * Utility methods for working with Displays.
 */
public class Displays {
    private Displays() {
    }

    /**
     * Get the {@link Display#getSize(Point) size} of the display.
     */
    public static Point getSize(Context context) {
        return getSize(Managers.window(context));
    }

    /**
     * Get the {@link Display#getSize(Point) size} of the display.
     */
    public static Point getSize(Activity activity) {
        return getSize(activity.getWindowManager());
    }

    private static Point getSize(WindowManager manager) {
        Point size = new Point();
        manager.getDefaultDisplay().getSize(size);
        return size;
    }
}
