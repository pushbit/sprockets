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
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Sprockets. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sprockets.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import net.sf.sprockets.gms.common.api.Connector;

import static com.google.android.gms.common.ConnectionResult.SUCCESS;
import static com.google.android.gms.location.LocationServices.API;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

/**
 * Utility methods for working with Locations.
 */
public class Locations {
    private Locations() {
    }

    /**
     * Get the best most recent location or, if none are available, the current coarse location and
     * send it to the listener.
     *
     * @return {@link ConnectionResult#SUCCESS SUCCESS} if Google Play Services is available and an
     * attempt will be made to get the location and send it to the listener
     */
    public static int requestLast(Context context, LocationListener listener) {
        return requestCurrent(context, -1, listener);
    }

    /**
     * Get the current coarse location and send it to the listener.
     *
     * @return {@link ConnectionResult#SUCCESS SUCCESS} if Google Play Services is available and an
     * attempt will be made to get the location and send it to the listener
     */
    public static int requestCurrent(Context context, LocationListener listener) {
        return requestCurrent(context, 0, listener);
    }

    /**
     * Get the current location with the priority and send it to the listener.
     *
     * @param priority must be one of the {@link LocationRequest} PRIORITY constants
     * @return {@link ConnectionResult#SUCCESS SUCCESS} if Google Play Services is available and an
     * attempt will be made to get the location and send it to the listener
     */
    public static int requestCurrent(Context context, int priority, LocationListener listener) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (status == SUCCESS) {
            new Request(context, priority, listener);
        }
        return status;
    }

    /**
     * Get the last or current location and send it to the listener.
     */
    private static class Request extends Connector implements LocationListener {
        private final int mPriority;
        private final LocationListener mListener;
        private final GoogleApiClient mClient;

        /**
         * @param priority must be -1 for the last location, 0 for the current coarse location, or
         *                 one of the {@link LocationRequest} PRIORITY constants
         */
        private Request(Context context, int priority, LocationListener listener) {
            mPriority = priority;
            mListener = listener;
            mClient = new Builder(context.getApplicationContext(), this, this).addApi(API).build();
            mClient.connect();
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            if (mPriority == -1) { // try to get last location and return
                Location location = FusedLocationApi.getLastLocation(mClient);
                if (location != null) {
                    mListener.onLocationChanged(location);
                    mClient.disconnect();
                    return;
                }
            }
            /* get current location */
            LocationRequest req = LocationRequest.create().setNumUpdates(1);
            if (mPriority > 0) {
                req.setPriority(mPriority);
            }
            FusedLocationApi.requestLocationUpdates(mClient, req, this);
        }

        @Override
        public void onLocationChanged(Location location) {
            mListener.onLocationChanged(location);
            mClient.disconnect();
        }
    }
}
