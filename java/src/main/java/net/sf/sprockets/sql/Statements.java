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

package net.sf.sprockets.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
}
