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
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Sprockets.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sprockets.database;

import android.database.Cursor;

/**
 * Cursor that tracks the read status of its rows. Call {@link #wasRead()} each time you access a
 * row. The first call for each row will return false and further calls will return true.
 */
public class ReadCursor extends EasyCursor {
    /**
     * True if the corresponding row has been read.
     */
    private final boolean[] mRead;

    /**
     * Track the read status of the cursor's rows.
     */
    public ReadCursor(Cursor cursor) {
        super(cursor);
        mRead = new boolean[getCount()];
    }

    /**
     * True if this method has been previously called for the current row. False if the cursor is
     * before the first row or after the last row.
     */
    public boolean wasRead() {
        int pos = getPosition();
        if (pos < 0 || pos >= mRead.length) {
            return false;
        }
        boolean read = mRead[pos];
        if (!read) {
            mRead[pos] = true;
        }
        return read;
    }
}
