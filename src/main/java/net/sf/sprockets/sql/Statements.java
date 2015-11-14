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

import static java.util.Collections.EMPTY_LIST;
import static org.apache.commons.lang.ArrayUtils.EMPTY_INT_ARRAY;
import static org.apache.commons.lang.ArrayUtils.EMPTY_LONG_ARRAY;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.primitives.ArrayIntList;
import org.apache.commons.collections.primitives.ArrayLongList;

/**
 * Utility methods for working with Statements.
 * 
 * @since 1.1.0
 */
public class Statements {
	private Statements() {
	}

	/**
	 * Set the statement parameters, starting at 1, in the order of the params.
	 * 
	 * @since 3.0.0
	 */
	public static PreparedStatement setInts(PreparedStatement stmt, int... params)
			throws SQLException {
		return setInts(1, stmt, params);
	}

	/**
	 * Set the statement parameters, starting at the index, in the order of the params.
	 * 
	 * @since 3.0.0
	 */
	public static PreparedStatement setInts(int index, PreparedStatement stmt, int... params)
			throws SQLException {
		return set(index, stmt, params, null, null);
	}

	/**
	 * Set the statement parameters, starting at 1, in the order of the params.
	 * 
	 * @since 3.0.0
	 */
	public static PreparedStatement setLongs(PreparedStatement stmt, long... params)
			throws SQLException {
		return setLongs(1, stmt, params);
	}

	/**
	 * Set the statement parameters, starting at the index, in the order of the params.
	 * 
	 * @since 3.0.0
	 */
	public static PreparedStatement setLongs(int index, PreparedStatement stmt, long... params)
			throws SQLException {
		return set(index, stmt, null, params, null);
	}

	/**
	 * Set the statement parameters, starting at 1, in the order of the params.
	 * 
	 * @since 3.0.0
	 */
	public static PreparedStatement setStrings(PreparedStatement stmt, String... params)
			throws SQLException {
		return setStrings(1, stmt, params);
	}

	/**
	 * Set the statement parameters, starting at the index, in the order of the params.
	 * 
	 * @since 3.0.0
	 */
	public static PreparedStatement setStrings(int index, PreparedStatement stmt, String... params)
			throws SQLException {
		return set(index, stmt, null, null, params);
	}

	private static PreparedStatement set(int index, PreparedStatement stmt,
			int[] ints, long[] longs, String[] strings) throws SQLException {
		int length = ints != null ? ints.length : longs != null ? longs.length : strings.length;
		for (int i = 0; i < length; i++) {
			if (ints != null) {
				stmt.setInt(index + i, ints[i]);
			} else if (longs != null) {
				stmt.setLong(index + i, longs[i]);
			} else {
				stmt.setString(index + i, strings[i]);
			}
		}
		return stmt;
	}

	/**
	 * Execute the query, get the int value in the first row and column of the result set, and close
	 * the statement.
	 * 
	 * @param stmt
	 *            must already have parameters set
	 * @return {@link Integer#MIN_VALUE} if the result set is empty
	 */
	public static int firstInt(PreparedStatement stmt) throws SQLException {
		ResultSet rs = stmt.executeQuery();
		int i = rs.next() ? rs.getInt(1) : Integer.MIN_VALUE;
		stmt.close();
		return i;
	}

	/**
	 * Execute the query, get the long value in the first row and column of the result set, and
	 * close the statement.
	 * 
	 * @param stmt
	 *            must already have parameters set
	 * @return {@link Long#MIN_VALUE} if the result set is empty
	 */
	public static long firstLong(PreparedStatement stmt) throws SQLException {
		ResultSet rs = stmt.executeQuery();
		long l = rs.next() ? rs.getLong(1) : Long.MIN_VALUE;
		stmt.close();
		return l;
	}

	/**
	 * Execute the query, get the String value in the first row and column of the result set, and
	 * close the statement.
	 * 
	 * @param stmt
	 *            must already have parameters set
	 * @return null if the result set is empty
	 * @since 3.0.0
	 */
	public static String firstString(PreparedStatement stmt) throws SQLException {
		ResultSet rs = stmt.executeQuery();
		String s = rs.next() ? rs.getString(1) : null;
		stmt.close();
		return s;
	}

	/**
	 * Execute the query, get the int values in the first row of the result set, and close the
	 * statement.
	 * 
	 * @param stmt
	 *            must already have parameters set
	 * @since 3.0.0
	 */
	public static int[] firstIntRow(PreparedStatement stmt) throws SQLException {
		return (int[]) firstRow(stmt, Integer.class);
	}

	/**
	 * Execute the query, get the long values in the first row of the result set, and close the
	 * statement.
	 * 
	 * @param stmt
	 *            must already have parameters set
	 * @since 1.4.0
	 */
	public static long[] firstLongRow(PreparedStatement stmt) throws SQLException {
		return (long[]) firstRow(stmt, Long.class);
	}

	/**
	 * Execute the query, get the String values in the first row of the result set, and close the
	 * statement.
	 * 
	 * @param stmt
	 *            must already have parameters set
	 * @since 3.0.0
	 */
	@SuppressWarnings("unchecked")
	public static List<String> firstStringRow(PreparedStatement stmt) throws SQLException {
		return (List<String>) firstRow(stmt, String.class);
	}

	private static Object firstRow(PreparedStatement stmt, Class<?> cls) throws SQLException {
		Object row = null;
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			int cols = rs.getMetaData().getColumnCount();
			int[] ints = cls == Integer.class ? new int[cols] : null;
			long[] longs = cls == Long.class ? new long[cols] : null;
			List<String> strings = cls == String.class ? new ArrayList<String>(cols) : null;
			for (int i = 0; i < cols; i++) {
				if (cls == Integer.class) {
					ints[i] = rs.getInt(i + 1);
				} else if (cls == Long.class) {
					longs[i] = rs.getLong(i + 1);
				} else {
					strings.add(rs.getString(i + 1));
				}
			}
			row = ints != null ? ints : longs != null ? longs : strings;
		} else {
			row = cls == Integer.class ? EMPTY_INT_ARRAY
					: cls == Long.class ? EMPTY_LONG_ARRAY : EMPTY_LIST;
		}
		stmt.close();
		return row;
	}

	/**
	 * Execute the query, get the int values in the first column of the result set, and close the
	 * statement.
	 * 
	 * @param stmt
	 *            must already have parameters set
	 * @since 3.0.0
	 */
	public static int[] allInts(PreparedStatement stmt) throws SQLException {
		return (int[]) all(stmt, Integer.class);
	}

	/**
	 * Execute the query, get the long values in the first column of the result set, and close the
	 * statement.
	 * 
	 * @param stmt
	 *            must already have parameters set
	 * @since 1.4.0
	 */
	public static long[] allLongs(PreparedStatement stmt) throws SQLException {
		return (long[]) all(stmt, Long.class);
	}

	/**
	 * Execute the query, get the String values in the first column of the result set, and close the
	 * statement.
	 * 
	 * @param stmt
	 *            must already have parameters set
	 * @since 1.5.0
	 */
	@SuppressWarnings("unchecked")
	public static List<String> allStrings(PreparedStatement stmt) throws SQLException {
		return (List<String>) all(stmt, String.class);
	}

	private static Object all(PreparedStatement stmt, Class<?> cls) throws SQLException {
		Object all = null;
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			ArrayIntList ints = cls == Integer.class ? new ArrayIntList() : null;
			ArrayLongList longs = cls == Long.class ? new ArrayLongList() : null;
			List<String> strings = cls == String.class ? new ArrayList<String>() : null;
			do {
				if (cls == Integer.class) {
					ints.add(rs.getInt(1));
				} else if (cls == Long.class) {
					longs.add(rs.getLong(1));
				} else {
					strings.add(rs.getString(1));
				}
			} while (rs.next());
			all = ints != null ? ints.toArray() : longs != null ? longs.toArray() : strings;
		} else {
			all = cls == Integer.class ? EMPTY_INT_ARRAY
					: cls == Long.class ? EMPTY_LONG_ARRAY : EMPTY_LIST;
		}
		stmt.close();
		return all;
	}

	/**
	 * Execute the insert statement, get the first generated key as an int, and close the statement.
	 * 
	 * @param stmt
	 *            must have been created with {@link Statement#RETURN_GENERATED_KEYS} and already
	 *            have parameters set
	 * @return 0 if the statement did not generate any keys
	 * @since 3.0.0
	 */
	public static int firstIntKey(PreparedStatement stmt) throws SQLException {
		stmt.execute();
		ResultSet rs = stmt.getGeneratedKeys();
		int key = rs.next() ? rs.getInt(1) : 0;
		stmt.close();
		return key;
	}

	/**
	 * Execute the insert statement, get the first generated key as a long, and close the statement.
	 * 
	 * @param stmt
	 *            must have been created with {@link Statement#RETURN_GENERATED_KEYS} and already
	 *            have parameters set
	 * @return 0 if the statement did not generate any keys
	 */
	public static long firstLongKey(PreparedStatement stmt) throws SQLException {
		stmt.execute();
		ResultSet rs = stmt.getGeneratedKeys();
		long key = rs.next() ? rs.getLong(1) : 0L;
		stmt.close();
		return key;
	}

	/**
	 * Execute the insert, update, or delete statement, get the number of rows affected, and close
	 * the statement.
	 * 
	 * @param stmt
	 *            must already have parameters set
	 * @since 1.4.0
	 */
	public static int update(PreparedStatement stmt) throws SQLException {
		int rows = stmt.executeUpdate();
		stmt.close();
		return rows;
	}

	/**
	 * Execute the {@link PreparedStatement#addBatch() batches}, get the number of rows affected in
	 * each batch, and close the statement.
	 * 
	 * @param stmt
	 *            must already have batches added
	 * @since 1.4.0
	 */
	public static int[] batch(PreparedStatement stmt) throws SQLException {
		int[] rows = EMPTY_INT_ARRAY;
		if (stmt != null) {
			rows = stmt.executeBatch();
			stmt.close();
		}
		return rows;
	}
}
