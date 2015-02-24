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

package net.sf.sprockets.graphics.drawable;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

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

    /**
     * Get a random color oval.
     *
     * @since 2.2.0
     */
    public static ShapeDrawable randomOval() {
        return oval(Colors.random());
    }

    /**
     * Get a random color oval from the lighter half.
     *
     * @since 2.2.0
     */
    public static ShapeDrawable lightOval() {
        return oval(Colors.light());
    }

    /**
     * Get a random color oval from the lightest quarter.
     *
     * @since 2.2.0
     */
    public static ShapeDrawable lighterOval() {
        return oval(Colors.lighter());
    }

    /**
     * Get a random color oval from the lightest eighth.
     *
     * @since 2.2.0
     */
    public static ShapeDrawable lightestOval() {
        return oval(Colors.lightest());
    }

    /**
     * Get a random color oval from the darker half.
     *
     * @since 2.2.0
     */
    public static ShapeDrawable darkOval() {
        return oval(Colors.dark());
    }

    /**
     * Get a random color oval from the darkest quarter.
     *
     * @since 2.2.0
     */
    public static ShapeDrawable darkerOval() {
        return oval(Colors.darker());
    }

    /**
     * Get a random color oval from the darkest eighth.
     *
     * @since 2.2.0
     */
    public static ShapeDrawable darkestOval() {
        return oval(Colors.darkest());
    }

    /**
     * Get a colored oval.
     *
     * @since 2.2.0
     */
    public static ShapeDrawable oval(int color) {
        ShapeDrawable d = new ShapeDrawable(new OvalShape());
        d.setIntrinsicWidth(-1);
        d.setIntrinsicHeight(-1);
        d.getPaint().setColor(color);
        return d;
    }
}
