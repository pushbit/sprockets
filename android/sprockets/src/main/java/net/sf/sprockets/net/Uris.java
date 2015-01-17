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

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.BaseColumns;

import com.google.common.base.Joiner;

import net.sf.sprockets.content.Content;

import java.util.List;

import static android.provider.BaseColumns._ID;
import static net.sf.sprockets.content.Content.CALLER_IS_SYNCADAPTER;
import static net.sf.sprockets.content.Content.LIMIT;
import static net.sf.sprockets.content.Content.NOTIFY_CHANGE;

/**
 * Utility methods for working with Uris.
 */
public class Uris {
    private Uris() {
    }

    /**
     * Append the cursor's {@link BaseColumns#_ID _ID} value to the URI.
     */
    public static Uri appendId(Uri uri, Cursor cursor) {
        return ContentUris.withAppendedId(uri, cursor.getLong(cursor.getColumnIndex(_ID)));
    }

    /**
     * Append a {@link Content#LIMIT limit} query parameter to the URI.
     */
    public static Uri limit(Uri uri, String limit) {
        return uri.buildUpon().appendQueryParameter(LIMIT, limit).build();
    }

    /**
     * Append a {@link Content#NOTIFY_CHANGE notify_change} query parameter to the URI.
     *
     * @since 2.1.0
     */
    public static Uri notifyChange(Uri uri, boolean notify) {
        return notifyChange(uri, 0L, notify);
    }

    /**
     * Append the ID, if it is greater than zero, and a {@link Content#NOTIFY_CHANGE notify_change}
     * query parameter to the URI.
     *
     * @since 2.1.0
     */
    public static Uri notifyChange(Uri uri, long id, boolean notify) {
        Builder builder = uri.buildUpon();
        if (id > 0) {
            ContentUris.appendId(builder, id);
        }
        return builder.appendQueryParameter(NOTIFY_CHANGE, notify ? "1" : "0").build();
    }

    /**
     * Append a {@link Content#CALLER_IS_SYNCADAPTER caller_is_syncadapter} query parameter to the
     * URI.
     */
    public static Uri callerIsSyncAdapter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(CALLER_IS_SYNCADAPTER, "1").build();
    }

    /**
     * Get a {@code mailto} Uri with the headers. Any parameter can be null and it will be skipped.
     * The subject and body will be encoded.
     */
    public static Uri mailto(List<String> to, List<String> cc, List<String> bcc, String subject,
                             String body) {
        String encSubject = Uri.encode(subject);
        String encBody = Uri.encode(body);
        StringBuilder ssp = new StringBuilder((to != null ? to.size() * 34 : 0)
                + (cc != null ? cc.size() * 34 : 0) + (bcc != null ? bcc.size() * 34 : 0)
                + (encSubject != null ? encSubject.length() : 0)
                + (encBody != null ? encBody.length() : 0));
        Joiner joiner = Joiner.on(',');
        if (to != null) {
            joiner.appendTo(ssp, to);
        }
        boolean queryStart = true;
        if (cc != null) {
            joiner.appendTo(ssp.append(queryStart ? '?' : '&').append("cc="), cc);
            queryStart = false;
        }
        if (bcc != null) {
            joiner.appendTo(ssp.append(queryStart ? '?' : '&').append("bcc="), bcc);
            queryStart = false;
        }
        if (encSubject != null) {
            ssp.append(queryStart ? '?' : '&').append("subject=").append(encSubject);
            queryStart = false;
        }
        if (encBody != null) {
            ssp.append(queryStart ? '?' : '&').append("body=").append(encBody);
        }
        return new Builder().scheme("mailto").encodedOpaquePart(ssp.toString()).build();
    }
}
