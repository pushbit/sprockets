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

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

import static android.content.ContentResolver.SYNC_EXTRAS_EXPEDITED;
import static android.content.ContentResolver.SYNC_EXTRAS_MANUAL;

/**
 * Utility methods and constants for working with ContentResolvers.
 */
public class Content {
    /**
     * URI query parameter for requesting a limited number of rows returned by the query.
     */
    public static final String LIMIT = "limit_offset";
    /**
     * URI query parameter to specify if observers should be notified about the change.  Default is
     * true.
     *
     * @since 2.1.0
     */
    public static final String NOTIFY_CHANGE = "notify_change";
    /**
     * URI query parameter to specify that the caller is a sync adapter and the changes it makes do
     * not need to be synced to the network.
     */
    public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";
    /**
     * True if data should be downloaded.
     */
    public static final String SYNC_EXTRAS_DOWNLOAD = "download";

    private Content() {
    }

    /**
     * Request that a sync starts immediately.
     *
     * @param extras can be null
     * @see ContentResolver#requestSync(Account, String, Bundle)
     */
    public static void requestSyncNow(Account account, String authority, Bundle extras) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putBoolean(SYNC_EXTRAS_MANUAL, true);
        extras.putBoolean(SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(account, authority, extras);
    }
}
