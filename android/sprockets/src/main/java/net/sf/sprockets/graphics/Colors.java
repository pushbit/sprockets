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

package net.sf.sprockets.graphics;

import android.graphics.Color;

import java.util.Random;

/**
 * Utility methods for working with colors.
 */
public class Colors {
    private static final Random sRandom = new Random();
    private static final int FULL = 256;
    private static final int HALF = FULL / 2;
    private static final int QUARTER = HALF / 2;
    private static final int EIGHTH = QUARTER / 2;

    private Colors() {
    }

    /**
     * Get a random color.
     */
    public static int random() {
        return color(FULL, 0);
    }

    /**
     * Get a random color from the lighter half.
     */
    public static int light() {
        return light(HALF);
    }

    /**
     * Get a random color from the lightest quarter.
     */
    public static int lighter() {
        return light(QUARTER);
    }

    /**
     * Get a random color from the lightest eighth.
     */
    public static int lightest() {
        return light(EIGHTH);
    }

    /**
     * Get a random color from the top lightest.
     */
    private static int light(int top) {
        return color(top, FULL - top);
    }

    /**
     * Get a random color from the darker half.
     */
    public static int dark() {
        return dark(HALF);
    }

    /**
     * Get a random color from the darkest quarter.
     */
    public static int darker() {
        return dark(QUARTER);
    }

    /**
     * Get a random color from the darkest eighth.
     */
    public static int darkest() {
        return dark(EIGHTH);
    }

    /**
     * Get a random color from the bottom darkest.
     */
    private static int dark(int bottom) {
        return color(bottom, 0);
    }

    /**
     * Get a color with variance between 0 (inclusive) and the max (exclusive) above the base.
     */
    private static int color(int max, int base) {
        return Color.rgb(random(max, base), random(max, base), random(max, base));
    }

    private static int random(int max, int base) {
        return sRandom.nextInt(max) + base;
    }
}
