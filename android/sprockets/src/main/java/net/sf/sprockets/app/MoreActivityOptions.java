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

package net.sf.sprockets.app;

import android.app.ActivityOptions;
import android.view.View;

/**
 * Utility methods for working with ActivityOptions.
 *
 * @since 2.1.0
 */
public class MoreActivityOptions {
    private MoreActivityOptions() {
    }

    /**
     * Scale the new Activity from the View to its full size.
     *
     * @see ActivityOptions#makeScaleUpAnimation(View, int, int, int, int)
     */
    public static ActivityOptions makeScaleUpAnimation(View source) {
        return ActivityOptions.makeScaleUpAnimation(
                source, 0, 0, source.getWidth(), source.getHeight());
    }
}
