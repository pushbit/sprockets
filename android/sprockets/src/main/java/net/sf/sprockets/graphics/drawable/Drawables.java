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

package net.sf.sprockets.graphics.drawable;

import android.graphics.drawable.ColorDrawable;

import net.sf.sprockets.graphics.Colors;

/**
 * Utility methods for working with Drawables.
 */
public class Drawables {
    private Drawables() {
    }

    /**
     * Get a random color.
     */
    public static ColorDrawable randomColor() {
        return color(Colors.random());
    }

    /**
     * Get a random color from the lighter half.
     */
    public static ColorDrawable lightColor() {
        return color(Colors.light());
    }

    /**
     * Get a random color from the lightest quarter.
     */
    public static ColorDrawable lighterColor() {
        return color(Colors.lighter());
    }

    /**
     * Get a random color from the lightest eighth.
     */
    public static ColorDrawable lightestColor() {
        return color(Colors.lightest());
    }

    /**
     * Get a random color from the darker half.
     */
    public static ColorDrawable darkColor() {
        return color(Colors.dark());
    }

    /**
     * Get a random color from the darkest quarter.
     */
    public static ColorDrawable darkerColor() {
        return color(Colors.darker());
    }

    /**
     * Get a random color from the darkest eighth.
     */
    public static ColorDrawable darkestColor() {
        return color(Colors.darkest());
    }

    private static ColorDrawable color(int color) {
        return new ColorDrawable(color);
    }
}
