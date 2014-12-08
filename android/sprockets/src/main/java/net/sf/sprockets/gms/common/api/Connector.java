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

package net.sf.sprockets.gms.common.api;

import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

/**
 * Connects to Google Play Services and logs any failures.
 */
public abstract class Connector implements ConnectionCallbacks, OnConnectionFailedListener {
    private static final String TAG = Connector.class.getSimpleName();

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "couldn't connect to Google Play Services: "
                + GooglePlayServicesUtil.getErrorString(result.getErrorCode()));
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }
}
