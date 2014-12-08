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

package net.sf.sprockets.database;

import android.database.Cursor;

/**
 * Utility methods for working with Cursors.
 */
public class Cursors {
    private Cursors() {
    }

    /**
     * Get the number of rows in the cursor and then close it.
     */
    public static int count(Cursor cursor) {
        return count(cursor, true);
    }

    /**
     * Get the number of rows in the cursor.
     *
     * @param close true to close the cursor or false to leave it open
     */
    public static int count(Cursor cursor, boolean close) {
        int count = cursor.getCount();
        close(cursor, close);
        return count;
    }

    /**
     * Get the long value in the first row and column and then close the cursor.
     *
     * @return {@link Long#MIN_VALUE} if the cursor is empty
     */
    public static long firstLong(Cursor cursor) {
        return firstLong(cursor, true);
    }

    /**
     * Get the long value in the first row and column.
     *
     * @param close true to close the cursor or false to leave it open
     * @return {@link Long#MIN_VALUE} if the cursor is empty
     */
    public static long firstLong(Cursor cursor, boolean close) {
        long l = cursor.moveToFirst() ? cursor.getLong(0) : Long.MIN_VALUE;
        close(cursor, close);
        return l;
    }

    /**
     * Get the String value in the first row and column and then close the cursor.
     *
     * @return null if the cursor is empty
     */
    public static String firstString(Cursor cursor) {
        return firstString(cursor, true);
    }

    /**
     * Get the String value in the first row and column.
     *
     * @param close true to close the cursor or false to leave it open
     * @return null if the cursor is empty
     */
    public static String firstString(Cursor cursor, boolean close) {
        String s = cursor.moveToFirst() ? cursor.getString(0) : null;
        close(cursor, close);
        return s;
    }

    /**
     * Get all long values in the first column and then close the cursor.
     *
     * @return null if the cursor is empty
     */
    public static long[] allLongs(Cursor cursor) {
        return allLongs(cursor, true);
    }

    /**
     * Get all long values in the first column.
     *
     * @param close true to close the cursor or false to leave it open
     * @return null if the cursor is empty
     */
    public static long[] allLongs(Cursor cursor, boolean close) {
        long[] l = null;
        if (cursor.moveToFirst()) {
            l = new long[cursor.getCount()];
            do {
                l[cursor.getPosition()] = cursor.getLong(0);
            } while (cursor.moveToNext());
        }
        close(cursor, close);
        return l;
    }

    /**
     * Get all String values in the first column and then close the cursor.
     *
     * @return null if the cursor is empty
     */
    public static String[] allStrings(Cursor cursor) {
        return allStrings(cursor, true);
    }

    /**
     * Get all String values in the first column.
     *
     * @param close true to close the cursor or false to leave it open
     * @return null if the cursor is empty
     */
    public static String[] allStrings(Cursor cursor, boolean close) {
        String[] s = null;
        if (cursor.moveToFirst()) {
            s = new String[cursor.getCount()];
            do {
                s[cursor.getPosition()] = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        close(cursor, close);
        return s;
    }

    /**
     * Close the cursor if requested.
     */
    private static void close(Cursor cursor, boolean close) {
        if (close) {
            cursor.close();
        }
    }
}
