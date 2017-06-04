/*
 * Copyright 2013-2016 pushbit <pushbit@gmail.com>
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

import static java.util.logging.Level.SEVERE;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Nullable;

import net.sf.sprockets.util.logging.Loggers;

/**
 * Utility methods for working with Connections.
 *
 * @since 1.0.0
 */
public class Connections {
	private Connections() {
	}

	/**
	 * If the connection is not in {@link Connection#getAutoCommit() auto-commit mode}, roll it back
	 * and put it in auto-commit mode before closing the connection.
	 *
	 * @param con
	 *            may be null
	 */
	public static void close(@Nullable Connection con) throws SQLException {
		if (con != null && !con.isClosed()) {
			if (!con.getAutoCommit()) {
				con.rollback();
				con.setAutoCommit(true);
			}
			con.close();
		}
	}

	/**
	 * {@link #close(Connection) Close} the connection and log any exceptions instead of throwing
	 * them.
	 *
	 * @param con
	 *            may be null
	 */
	public static void closeQuietly(@Nullable Connection con) {
		try {
			close(con);
		} catch (SQLException e) {
			Loggers.get(Connections.class).log(SEVERE, "closing connection", e);
		}
	}
}
