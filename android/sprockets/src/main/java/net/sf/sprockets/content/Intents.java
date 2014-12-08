/*
 * Copyright 2013 pushbit <pushbit@gmail.com>
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

package net.sf.sprockets.content;

import android.content.Context;
import android.content.Intent;

import static android.content.pm.PackageManager.MATCH_DEFAULT_ONLY;

/**
 * Utility methods for working with Intents.
 */
public class Intents {
    private Intents() {
    }

    /**
     * True if the Intent can be resolved to an Activity.
     */
    public static boolean hasActivity(Context context, Intent intent) {
        return context.getPackageManager()
                .queryIntentActivities(intent, MATCH_DEFAULT_ONLY).size() > 0;
    }
}
