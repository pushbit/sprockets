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

import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Cursor with additional methods that can simplify interaction.
 */
public class EasyCursor extends CursorWrapper {
    public EasyCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Returns the value of the requested column as a byte array.
     *
     * @throws IllegalArgumentException if the column does not exist
     */
    public byte[] getBlob(String columnName) {
        return getBlob(getColumnIndexOrThrow(columnName));
    }

    /**
     * Returns the value of the requested column as a String.
     *
     * @throws IllegalArgumentException if the column does not exist
     */
    public String getString(String columnName) {
        return getString(getColumnIndexOrThrow(columnName));
    }

    /**
     * Retrieves the requested column text and stores it in the buffer provided. If the buffer size
     * is not sufficient, a new char buffer will be allocated and assigned to CharArrayBuffer.data.
     *
     * @throws IllegalArgumentException if the column does not exist
     */
    public void copyStringToBuffer(String columnName, CharArrayBuffer buffer) {
        copyStringToBuffer(getColumnIndexOrThrow(columnName), buffer);
    }

    /**
     * Returns the value of the requested column as a short.
     *
     * @throws IllegalArgumentException if the column does not exist
     */
    public short getShort(String columnName) {
        return getShort(getColumnIndexOrThrow(columnName));
    }

    /**
     * Returns the value of the requested column as an int.
     *
     * @throws IllegalArgumentException if the column does not exist
     */
    public int getInt(String columnName) {
        return getInt(getColumnIndexOrThrow(columnName));
    }

    /**
     * Returns the value of the requested column as a long.
     *
     * @throws IllegalArgumentException if the column does not exist
     */
    public long getLong(String columnName) {
        return getLong(getColumnIndexOrThrow(columnName));
    }

    /**
     * Returns the value of the requested column as a float.
     *
     * @throws IllegalArgumentException if the column does not exist
     */
    public float getFloat(String columnName) {
        return getFloat(getColumnIndexOrThrow(columnName));
    }

    /**
     * Returns the value of the requested column as a double.
     *
     * @throws IllegalArgumentException if the column does not exist
     */
    public double getDouble(String columnName) {
        return getDouble(getColumnIndexOrThrow(columnName));
    }

    /**
     * Returns data type of the given column's value.
     *
     * @throws IllegalArgumentException if the column does not exist
     * @see #getType(int)
     */
    public int getType(String columnName) {
        return getType(getColumnIndexOrThrow(columnName));
    }

    /**
     * Returns true if the value in the indicated column is null.
     *
     * @throws IllegalArgumentException if the column does not exist
     */
    public boolean isNull(String columnName) {
        return isNull(getColumnIndexOrThrow(columnName));
    }
}
