/*
 * Copyright 2015 pushbit <pushbit@gmail.com>
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

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.Swatch;

/**
 * Utility methods for working with Palettes.
 *
 * @since 2.1.0
 */
public class Palettes {
    /**
     * Maximum number of colors to use when {@link Palette#generate(Bitmap, int) generating} a
     * Palette.
     */
    public static final int MAX_COLORS = 32;

    private Palettes() {
    }

    /**
     * Get the Swatch in the Palette that has the highest {@link Swatch#getPopulation() population}.
     *
     * @return null if the Palette doesn't have any Swatches
     */
    public static Swatch getMostPopulousSwatch(Palette palette) {
        int highestPop = 0;
        Swatch mostPopulous = null;
        for (Swatch swatch : palette.getSwatches()) {
            int pop = swatch.getPopulation();
            if (pop > highestPop) {
                highestPop = pop;
                mostPopulous = swatch;
            }
        }
        return mostPopulous;
    }
}
