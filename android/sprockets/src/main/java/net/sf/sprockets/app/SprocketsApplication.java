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

package net.sf.sprockets.app;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;

/**
 * Provides static access to the application context and resources. This class (or a subclass) must
 * be specified in your application's manifest.
 */
public class SprocketsApplication extends Application {
    private static Context mContext;

    /**
     * Get the application context.
     *
     * @return null if this class (or a subclass) was not specified in the application's manifest
     */
    public static Context context() {
        return mContext;
    }

    /**
     * Get the application's Resources.
     *
     * @return null if this class (or a subclass) was not specified in the application's manifest
     */
    public static Resources res() {
        return mContext != null ? mContext.getResources() : null;
    }

    /**
     * Get the application's ContentResolver.
     *
     * @return null if this class (or a subclass) was not specified in the application's manifest
     */
    public static ContentResolver cr() {
        return mContext != null ? mContext.getContentResolver() : null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
