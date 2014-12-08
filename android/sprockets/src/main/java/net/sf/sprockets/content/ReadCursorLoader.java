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

package net.sf.sprockets.content;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import net.sf.sprockets.database.ReadCursor;

/**
 * Wraps the loaded cursor in a {@link ReadCursor}.
 */
public class ReadCursorLoader extends CursorWrapperLoader<ReadCursor> {
    public ReadCursorLoader(Context context) {
        super(context);
    }

    public ReadCursorLoader(Context context, Uri uri, String[] projection, String selection,
                            String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected ReadCursor wrap(Cursor cursor) {
        return new ReadCursor(cursor);
    }
}
