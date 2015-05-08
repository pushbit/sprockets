/*
 * Copyright 2013-2015 pushbit <pushbit@gmail.com>
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

package net.sf.sprockets.sql;

import static java.text.Normalizer.Form.NFD;
import static java.util.Locale.US;
import static net.sf.sprockets.util.Geos.LATITUDE_DEGREE_KM;
import static net.sf.sprockets.util.Geos.LATITUDE_DEGREE_MI;
import static net.sf.sprockets.util.MeasureUnit.MILE;
import static org.apache.commons.lang.time.DateUtils.UTC_TIME_ZONE;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

import net.sf.sprockets.util.Geos;
import net.sf.sprockets.util.MeasureUnit;

import org.apache.commons.lang.time.FastDateFormat;

/**
 * Utility methods for working with SQLite.
 * 
 * @since 2.2.0
 */
public class SQLite {
	private SQLite() {
	}

	/**
	 * Get a result column that aliases the column to the same name without the table prefix.
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
	 * Get a result column that aliases the column to the same name, except with underscores
	 * replacing dots.
	 *
	 * @return e.g. {@code table.column AS table_column}
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

	/**
	 * Get an {@link #aliased_(String) aliased} count(column).
	 * 
	 * @since 2.4.0
	 */
	public static String count(String column) {
		return func("count", column);
	}

	/**
	 * Get an {@link #aliased_(String) aliased} min(column).
	 * 
	 * @since 2.4.0
	 */
	public static String min(String column) {
		return func("min", column);
	}

	/**
	 * Get an {@link #aliased_(String) aliased} max(column).
	 * 
	 * @since 2.4.0
	 */
	public static String max(String column) {
		return func("max", column);
	}

	/**
	 * Get an {@link #aliased_(String) aliased} avg(column).
	 * 
	 * @since 2.4.0
	 */
	public static String avg(String column) {
		return func("avg", column);
	}

	/**
	 * Get an {@link #aliased_(String) aliased} sum(column).
	 * 
	 * @since 2.4.0
	 */
	public static String sum(String column) {
		return func("sum", column);
	}

	/**
	 * Get an {@link #aliased_(String) aliased} total(column).
	 * 
	 * @since 2.4.0
	 */
	public static String total(String column) {
		return func("total", column);
	}

	/**
	 * Get an {@link #aliased_(String) aliased} group_concat(column) with a comma separator.
	 * 
	 * @since 2.4.0
	 */
	public static String groupConcat(String column) {
		return func("group_concat", column);
	}

	/**
	 * Get an {@link #aliased_(String) aliased} group_concat(column) with the separator.
	 * 
	 * @since 2.4.0
	 */
	public static String groupConcat(String column, String separator) {
		return "group_concat(" + column + ", '" + separator + "') AS " + aliased_(column);
	}

	/**
	 * Get an {@link #aliased_(String) aliased} func(column).
	 */
	private static String func(String func, String column) {
		return func + '(' + column + ") AS " + aliased_(column);
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
	 * Get an {@link #aliased_(String) aliased} result column that converts the datetime column
	 * value to epoch milliseconds.
	 */
	public static String millis(String column) {
		return "strftime('%s', " + column + ") * 1000 AS " + aliased_(column);
	}

	/**
	 * Get an {@link #aliased_(String) aliased} result column that applies the aggregate function to
	 * the datetime column and converts the result to epoch milliseconds.
	 * 
	 * @since 2.4.0
	 */
	public static String millis(String function, String column) {
		return "strftime('%s', " + function + '(' + column + ")) * 1000 AS " + aliased_(column);
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

	/**
	 * Get a result column for the squared distance from the row coordinates to the supplied
	 * coordinates. SQLite doesn't have a square root core function, so this must be applied in Java
	 * when reading the result column value.
	 * 
	 * @param latitudeCosineColumn
	 *            see {@link Geos#cos(double)}
	 * @param unit
	 *            KILOMETER or MILE
	 * @param alias
	 *            result column name
	 */
	public static String distance(String latitudeColumn, String longitudeColumn,
			String latitudeCosineColumn, double latitude, double longitude, MeasureUnit unit,
			String alias) {
		/*
		 * adapted from the "Improved approximate distance" section at https://web.archive.org/web
		 * /20130316112756/http://www.meridianworlddata.com/Distance-calculation.asp
		 */
		String lats = "(%s * (%s - %s))";
		String lons = "(%s * (%s - %s) * %s)";
		String sql = lats + " * " + lats + " + " + lons + " * " + lons + " AS %s";
		double degree = unit == MILE ? LATITUDE_DEGREE_MI : LATITUDE_DEGREE_KM;
		return String.format((Locale) null, sql, degree, latitude, latitudeColumn, degree,
				latitude, latitudeColumn, degree, longitude, longitudeColumn, latitudeCosineColumn,
				degree, longitude, longitudeColumn, latitudeCosineColumn, alias);
	}
}
