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

package net.sf.sprockets.gms.maps;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds.Builder;

import net.sf.sprockets.R;
import net.sf.sprockets.location.Locations;

/**
 * Utility methods for working with GoogleMaps.
 *
 * @since 2.1.0
 */
public class GoogleMaps {
    private GoogleMaps() {
    }

    /**
     * Move to the user's most recent location and zoom to the default level.
     */
    public static void moveCameraToMyLocation(Context context, GoogleMap map) {
        moveCameraToMyLocation(context, map, 15.0f);
    }

    /**
     * Move and zoom to the user's most recent location.
     *
     * @see CameraUpdateFactory#newLatLngZoom(LatLng, float)
     */
    public static void moveCameraToMyLocation(Context context, final GoogleMap map,
                                              final float zoom) {
        Locations.requestLast(context, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), zoom));
            }
        });
    }

    /**
     * If the position is not visible on the map, animate the camera to include it.
     */
    public static void animateCameraToIncludePosition(Context context, GoogleMap map,
                                                      LatLng position) {
        animateCameraToIncludePosition(context, map, position, 0L);
    }

    /**
     * If the position is not visible on the map, animate the camera after the delay to include it.
     */
    public static void animateCameraToIncludePosition(final Context context, final GoogleMap map,
                                                      final LatLng position, long delay) {
        if (!map.getProjection().getVisibleRegion().latLngBounds.contains(position)) {
            if (delay > 0) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doAnimateCameraToIncludePosition(context, map, position);
                    }
                }, delay);
            } else {
                doAnimateCameraToIncludePosition(context, map, position);
            }
        }
    }

    private static void doAnimateCameraToIncludePosition(Context context, GoogleMap map,
                                                         LatLng position) {
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(
                new Builder().include(map.getCameraPosition().target).include(position).build(),
                context.getResources().getDimensionPixelOffset(R.dimen.min_touch_size)));
    }
}
