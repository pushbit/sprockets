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

package net.sf.sprockets.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.primitives.ArrayLongList;
import org.apache.commons.collections.primitives.LongList;

/**
 * Utility methods for working with Statements.
 * 
 * @since 1.1.0
 */
public class Statements {
	private Statements() {
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
	 * Execute the query, get the long values in the first row of the result set, and close the
	 * statement.
	 * 
	 * @param stmt
	 *            must already have parameters set
	 * @return null if the result set is empty
	 * @since 1.4.0
	 */
	public static long[] firstLongRow(PreparedStatement stmt) throws SQLException {
		long[] l = null;
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			l = new long[rs.getMetaData().getColumnCount()];
			for (int i = 0; i < l.length; i++) {
				l[i] = rs.getLong(i + 1);
			}
		}
		stmt.close();
		return l;
	}

	/**
	 * Execute the query, get the long values in the first column of the result set, and close the
	 * statement.
	 * 
	 * @param stmt
	 *            must already have parameters set
	 * @return null if the result set is empty
	 * @since 1.4.0
	 */
	public static long[] allLongs(PreparedStatement stmt) throws SQLException {
		long[] l = null;
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			LongList list = new ArrayLongList();
			do {
				list.add(rs.getLong(1));
			} while (rs.next());
			l = list.toArray();
		}
		stmt.close();
		return l;
	}

	/**
	 * Execute the query, get the String values in the first column of the result set, and close the
	 * statement.
	 * 
	 * @param stmt
	 *            must already have parameters set
	 * @return null if the result set is empty
	 * @since 1.5.0
	 */
	public static List<String> allStrings(PreparedStatement stmt) throws SQLException {
		List<String> s = null;
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			s = new ArrayList<String>();
			do {
				s.add(rs.getString(1));
			} while (rs.next());
		}
		stmt.close();
		return s;
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
		long l = rs.next() ? rs.getLong(1) : 0L;
		stmt.close();
		return l;
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
	 * @return null if the statement is null
	 * @since 1.4.0
	 */
	public static int[] batch(PreparedStatement stmt) throws SQLException {
		int[] rows = null;
		if (stmt != null) {
			rows = stmt.executeBatch();
			stmt.close();
		}
		return rows;
	}
}
