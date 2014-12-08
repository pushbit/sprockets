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

package net.sf.sprockets.net;

import android.content.Context;
import android.net.NetworkInfo;

import net.sf.sprockets.content.Managers;

/**
 * Utility methods for working with network connectivity.
 */
public class Network {
    private Network() {
    }

    /**
     * True if any network interfaces are connected.
     */
    public static boolean isConnected(Context context) {
        NetworkInfo[] all = Managers.connectivity(context).getAllNetworkInfo();
        if (all != null) {
            for (NetworkInfo net : all) {
                if (net.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
}
