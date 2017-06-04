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

package net.sf.sprockets.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UrlsTest {
	@Test
	public void testIsHttp() {
		assertTrue(Urls.isHttp("http://example.com"));
		assertTrue(Urls.isHttp("https://example.com"));
		assertFalse(Urls.isHttp("example.com"));
	}

	@Test
	public void testAddHttp() {
		assertEquals("http://example.com", Urls.addHttp("example.com"));
		assertEquals("http://example.com", Urls.addHttp("http://example.com"));
		assertEquals("https://example.com", Urls.addHttp("https://example.com"));
	}
}
