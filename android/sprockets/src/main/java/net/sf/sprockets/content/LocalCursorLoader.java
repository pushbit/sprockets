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

package net.sf.sprockets.content;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import net.sf.sprockets.database.EasyCursor;
import net.sf.sprockets.location.Locations;

import java.util.concurrent.CountDownLatch;

import static com.google.android.gms.common.ConnectionResult.SUCCESS;

/**
 * Provides the current location to subclasses before performing the cursor query.
 */
public abstract class LocalCursorLoader extends EasyCursorLoader {
    private static final String TAG = LocalCursorLoader.class.getSimpleName();

    private int mPriority = -1;
    /**
     * Wait for location update.
     */
    private final CountDownLatch mLatch = new CountDownLatch(1);

    /**
     * By default, the best most recent location or, if none are available, the current coarse
     * location will be provided to {@link #onLocation(Location)}.
     */
    public LocalCursorLoader(Context context) {
        super(context);
    }

    /**
     * By default, the best most recent location or, if none are available, the current coarse
     * location will be provided to {@link #onLocation(Location)}.
     */
    public LocalCursorLoader(Context context, Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    /**
     * Request the current location with the specific priority.
     *
     * @param priority must be one of the {@link LocationRequest} PRIORITY constants
     */
    public LocalCursorLoader priority(int priority) {
        mPriority = priority;
        return this;
    }

    @Override
    public EasyCursor loadInBackground() {
        int status = mPriority == -1 ? Locations.requestLast(getContext(), new Listener())
                : Locations.requestCurrent(getContext(), mPriority, new Listener());
        if (status == SUCCESS) {
            try {
                mLatch.await();
            } catch (InterruptedException e) {
                Log.e(TAG, "interrupted while waiting for location", e);
                onLocation(null);
            }
        } else {
            onLocation(null);
        }
        return super.loadInBackground();
    }

    /**
     * The current location has been found.
     *
     * @param location null if there was a problem getting the current location
     */
    protected abstract void onLocation(Location location);

    /**
     * Provides the location to the subclass and then resumes loading the cursor.
     */
    private class Listener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            onLocation(location);
            mLatch.countDown();
        }
    }
}
