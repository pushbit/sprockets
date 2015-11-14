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
		return alias(column, aliased(column));
	}

	/**
	 * Get an aliased result column.
	 *
	 * @since 2.6.0
	 */
	public static String alias(String column, String alias) {
		return column + " AS " + alias;
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
	 * Get an {@link #aliased(String) aliased} count(column).
	 * 
	 * @since 2.4.0
	 */
	public static String count(String column) {
		return count(column, null);
	}

	/**
	 * Get an aliased count(column).
	 * 
	 * @since 2.6.0
	 */
	public static String count(String column, String alias) {
		return func("count", column, alias);
	}

	/**
	 * Get an {@link #aliased(String) aliased} min(column).
	 * 
	 * @since 2.4.0
	 */
	public static String min(String column) {
		return min(column, null);
	}

	/**
	 * Get an aliased min(column).
	 * 
	 * @since 2.6.0
	 */
	public static String min(String column, String alias) {
		return func("min", column, alias);
	}

	/**
	 * Get an {@link #aliased(String) aliased} max(column).
	 * 
	 * @since 2.4.0
	 */
	public static String max(String column) {
		return max(column, null);
	}

	/**
	 * Get an aliased max(column).
	 * 
	 * @since 2.6.0
	 */
	public static String max(String column, String alias) {
		return func("max", column, alias);
	}

	/**
	 * Get an {@link #aliased(String) aliased} avg(column).
	 * 
	 * @since 2.4.0
	 */
	public static String avg(String column) {
		return avg(column, null);
	}

	/**
	 * Get an aliased avg(column).
	 * 
	 * @since 2.6.0
	 */
	public static String avg(String column, String alias) {
		return func("avg", column, alias);
	}

	/**
	 * Get an {@link #aliased(String) aliased} sum(column).
	 * 
	 * @since 2.4.0
	 */
	public static String sum(String column) {
		return sum(column, null);
	}

	/**
	 * Get an aliased sum(column).
	 * 
	 * @since 2.6.0
	 */
	public static String sum(String column, String alias) {
		return func("sum", column, alias);
	}

	/**
	 * Get an {@link #aliased(String) aliased} total(column).
	 * 
	 * @since 2.4.0
	 */
	public static String total(String column) {
		return total(column, null);
	}

	/**
	 * Get an aliased total(column).
	 * 
	 * @since 2.6.0
	 */
	public static String total(String column, String alias) {
		return func("total", column, alias);
	}

	/**
	 * Get an {@link #aliased(String) aliased} group_concat(column) with a comma separator.
	 * 
	 * @since 2.4.0
	 */
	public static String groupConcat(String column) {
		return groupConcat(column, ",");
	}

	/**
	 * Get an {@link #aliased(String) aliased} group_concat(column) with the separator.
	 * 
	 * @since 2.4.0
	 */
	public static String groupConcat(String column, String separator) {
		return groupConcat(column, separator, aliased(column));
	}

	/**
	 * Get an aliased group_concat(column) with the separator.
	 * 
	 * @since 2.6.0
	 */
	public static String groupConcat(String column, String separator, String alias) {
		return "group_concat(" + column + ", '" + separator + "') AS " + alias;
	}

	/**
	 * Get an aliased func(column).
	 * 
	 * @param alias
	 *            may be null for a default alias
	 */
	private static String func(String func, String column, String alias) {
		if (alias == null) {
			alias = aliased(column);
		}
		return func + '(' + column + ") AS " + alias;
	}

	/**
	 * Get an {@link #aliased(String) aliased} result column that converts the datetime column value
	 * to epoch milliseconds.
	 */
	public static String millis(String column) {
		return millis(null, column);
	}

	/**
	 * Get an {@link #aliased(String) aliased} result column that applies the aggregate function to
	 * the datetime column and converts the result to epoch milliseconds.
	 * 
	 * @since 2.4.0
	 */
	public static String millis(String function, String column) {
		return millis(function, column, aliased(column));
	}

	/**
	 * Get an aliased result column that applies the aggregate function to the datetime column and
	 * converts the result to epoch milliseconds.
	 * 
	 * @param function
	 *            can be null to not apply a function
	 * @since 3.0.0
	 */
	public static String millis(String function, String column, String alias) {
		StringBuilder s = new StringBuilder(96).append("strftime('%s', ");
		if (function != null) {
			s.append(function).append('(').append(column).append(')');
		} else {
			s.append(column);
		}
		return s.append(") * 1000 AS ").append(alias).toString();
	}

	/**
	 * Get UTC date and time for now, in the format {@code YYYY-MM-DD hh:mm:ss}.
	 */
	public static String datetime() {
		return datetime(System.currentTimeMillis());
	}

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
	 * Adapted from <a href=
	 * "https://web.archive.org/web/20130316112756/http://www.meridianworlddata.com/Distance-calculation.asp"
	 * >Improved approximate distance</a>.
	 */
	private static final String sDistance = "(%s * (%s - %s)) * (%s * (%s - %s)) + "
			+ "(%s * (%s - %s) * %s) * (%s * (%s - %s) * %s) AS %s";

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
		double degree = unit == MILE ? LATITUDE_DEGREE_MI : LATITUDE_DEGREE_KM;
		return String.format((Locale) null, sDistance, degree, latitude, latitudeColumn, degree,
				latitude, latitudeColumn, degree, longitude, longitudeColumn, latitudeCosineColumn,
				degree, longitude, longitudeColumn, latitudeCosineColumn, alias);
	}

	/**
	 * Get an {@code IN} operator for the column and values.
	 * 
	 * @return column IN (values[0],...,values[n])
	 * @since 2.6.0
	 */
	public static StringBuilder in(String column, long[] values) {
		return in(column, values, new StringBuilder(64));
	}

	/**
	 * Append an {@code IN} operator for the column and values.
	 * 
	 * @return column IN (values[0],...,values[n])
	 * @since 2.6.0
	 */
	public static StringBuilder in(String column, long[] values, StringBuilder s) {
		return in(column, values, null, s);
	}

	/**
	 * Get an {@code IN} operator for the column and values. The values will be escaped if
	 * necessary.
	 * 
	 * @return column IN (values[0],...,values[n])
	 * @since 2.6.0
	 */
	public static StringBuilder in(String column, String[] values) {
		return in(column, values, new StringBuilder(64));
	}

	/**
	 * Append an {@code IN} operator for the column and values. The values will be escaped if
	 * necessary.
	 * 
	 * @return column IN (values[0],...,values[n])
	 * @since 2.6.0
	 */
	public static StringBuilder in(String column, String[] values, StringBuilder s) {
		return in(column, null, values, s);
	}

	private static StringBuilder in(String column, long[] longValues, String[] stringValues,
			StringBuilder s) {
		s.append(column).append(" IN (");
		boolean longs = longValues != null;
		for (int i = 0, length = longs ? longValues.length : stringValues.length; i < length; i++) {
			if (i > 0) {
				s.append(',');
			}
			if (longs) {
				s.append(longValues[i]);
			} else {
				appendEscapedSQLString(s, stringValues[i]);
			}
		}
		return s.append(')');
	}

	/**
	 * Appends an SQL string to the given StringBuilder, including the opening and closing single
	 * quotes. Any single quotes internal to sqlString will be escaped.
	 * <p>
	 * Copied from android.database.DatabaseUtils.
	 * </p>
	 *
	 * @param sb
	 *            the StringBuilder that the SQL string will be appended to
	 * @param sqlString
	 *            the raw string to be appended, which may contain single quotes
	 */
	private static StringBuilder appendEscapedSQLString(StringBuilder sb, String sqlString) {
		sb.append('\'');
		if (sqlString.indexOf('\'') != -1) {
			for (int i = 0, length = sqlString.length(); i < length; i++) {
				char c = sqlString.charAt(i);
				if (c == '\'') {
					sb.append('\'');
				}
				sb.append(c);
			}
		} else {
			sb.append(sqlString);
		}
		sb.append('\'');
		return sb;
	}
}
