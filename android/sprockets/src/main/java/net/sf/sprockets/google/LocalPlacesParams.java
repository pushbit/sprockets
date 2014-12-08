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

package net.sf.sprockets.google;

import android.Manifest.permission;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Request;
import net.sf.sprockets.location.Locations;

import java.util.concurrent.CountDownLatch;

import static com.google.android.gms.common.ConnectionResult.SUCCESS;

/**
 * Params whose {@link #location(double, double) location} is automatically set to the current
 * location when supplied to one of the {@link Places} methods. The constructor and setter methods
 * do not block and can be called on the UI thread.
 * <p>
 * Requires the {@link permission#ACCESS_COARSE_LOCATION ACCESS_COARSE_LOCATION} (or
 * {@link permission#ACCESS_FINE_LOCATION FINE}) permission.
 * </p>
 */
public class LocalPlacesParams extends Params {
    private static final String TAG = LocalPlacesParams.class.getSimpleName();

    private final Context mContext;
    private int mPriority = -1;
    private boolean mRequired = true;
    /**
     * Has a location already been requested?
     */
    private boolean mRequested;
    /**
     * Wait for location update when required.
     */
    private final CountDownLatch mLatch = new CountDownLatch(1);

    /**
     * By default, the best most recent location or, if none are available, the current coarse
     * location will be set before sending the {@link Places} request.
     */
    public LocalPlacesParams(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * Request the current location with the specific priority.
     *
     * @param priority must be one of the {@link LocationRequest} PRIORITY constants
     */
    public LocalPlacesParams priority(int priority) {
        mPriority = priority;
        return this;
    }

    /**
     * Require a location before sending the request. If set to false, the request can be sent
     * immediately, possibly without a location, and when a location is received it will be set for
     * future requests. For example, this could be useful if you would like to send the first
     * autocomplete requests as soon as possible and allow the location to be added later.
     */
    public LocalPlacesParams required(boolean required) {
        mRequired = required;
        return this;
    }

    /**
     * Get a URL formatted for the type of request. If {@link #required(boolean) required} is true
     * (the default), this method will block until a location has been received.
     */
    @Override
    public String format(Request type) {
        if (!mRequested) { // only request location once
            mRequested = true;
            int status = mPriority == -1 ? Locations.requestLast(mContext, new Listener())
                    : Locations.requestCurrent(mContext, mPriority, new Listener());
            if (status == SUCCESS && mRequired) {
                try {
                    mLatch.await();
                } catch (InterruptedException e) {
                    Log.e(TAG, "interrupted while waiting for location", e);
                }
            }
        }
        return super.format(type);
    }

    /**
     * Sets the location and resumes the request.
     */
    private class Listener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            location(location.getLatitude(), location.getLongitude());
            mLatch.countDown();
        }
    }
}
