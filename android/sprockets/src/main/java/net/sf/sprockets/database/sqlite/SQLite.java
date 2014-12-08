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

package net.sf.sprockets.database.sqlite;

import org.apache.commons.lang.time.FastDateFormat;

import java.text.Normalizer;
import java.util.regex.Pattern;

import static java.text.Normalizer.Form.NFD;
import static java.util.Locale.US;
import static org.apache.commons.lang.time.DateUtils.UTC_TIME_ZONE;

/**
 * Utility methods for working with SQLite.
 */
public class SQLite {
    private SQLite() {
    }

    /**
     * Get an {@link #alias_(String) aliased} projection element for converting the datetime
     * column value to epoch milliseconds.
     */
    public static String millis(String column) {
        return "strftime('%s', " + column + ") * 1000 AS " + aliased_(column);
    }

    /**
     * Get UTC date and time for now, in the format {@code YYYY-MM-DD hh:mm:ss}.
     */
    public static String datetime() {
        return datetime(System.currentTimeMillis());
    }

    /**
     * UTC datetime in ISO format.
     */
    private static FastDateFormat sFormat;

    /**
     * Get UTC date and time for the milliseconds since the epoch, in the format
     * {@code YYYY-MM-DD hh:mm:ss}.
     */
    public static String datetime(long millis) {
        if (sFormat == null) {
            sFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss", UTC_TIME_ZONE, US);
        }
        return sFormat.format(millis);
    }

    /**
     * Get a projection element that aliases the column to the same name without the table prefix.
     *
     * @return e.g. {@code table.column AS column}
     */
    public static String alias(String column) {
        return column + " AS " + aliased(column);
    }

    /**
     * Get the name that the column was {@link #alias(String) aliased} to.
     */
    public static String aliased(String column) {
        int i = column.lastIndexOf('.');
        return i >= 0 ? column.substring(i + 1) : column;
    }

    /**
     * Get a projection element that aliases the column to the same name, except with underscores
     * replacing dots.
     */
    public static String alias_(String column) {
        return column + " AS " + aliased_(column);
    }

    /**
     * Get the name that the column was {@link #alias_(String) aliased} to.
     */
    public static String aliased_(String column) {
        return column.replace('.', '_');
    }

    private static Pattern sDiacritics;

    /**
     * Remove diacritics from the string and convert it to upper case.
     */
    public static String normalise(String s) {
        if (sDiacritics == null) {
            sDiacritics = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        }
        return sDiacritics.matcher(Normalizer.normalize(s, NFD)).replaceAll("").toUpperCase(US);
    }
}
