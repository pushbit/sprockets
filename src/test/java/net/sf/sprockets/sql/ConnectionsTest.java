/*
 * Copyright 2017 pushbit <pushbit@gmail.com>
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

import static java.util.logging.Level.OFF;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;
import org.mockito.Mock;

import net.sf.sprockets.test.SprocketsTest;
import net.sf.sprockets.util.logging.Loggers;

public class ConnectionsTest extends SprocketsTest {
	@Mock
	private Connection mCon;

	@Test
	public void testClose() throws SQLException {
		Connections.close(mCon);
		verify(mCon).rollback();
		verify(mCon).setAutoCommit(true);
		verify(mCon).close();

		Connections.close(null);
	}

	@Test
	public void testCloseQuietly() throws SQLException {
		Connections.closeQuietly(mCon);

		Loggers.get(Connections.class).setLevel(OFF);
		doThrow(SQLException.class).when(mCon).close();
		Connections.closeQuietly(mCon);

		Connections.closeQuietly(null);
	}
}
