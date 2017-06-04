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

package net.sf.sprockets.google;

import static net.sf.sprockets.google.InstanceId.Info.STATUS_BAD_REQUEST;
import static net.sf.sprockets.google.InstanceId.Info.STATUS_FORBIDDEN;
import static net.sf.sprockets.google.InstanceId.Info.STATUS_NOT_FOUND;
import static net.sf.sprockets.google.InstanceId.Info.STATUS_OK;
import static net.sf.sprockets.google.InstanceId.Info.STATUS_SERVICE_UNAVAILABLE;
import static net.sf.sprockets.google.InstanceId.Info.STATUS_UNAUTHORIZED;
import static net.sf.sprockets.google.InstanceId.Info.STATUS_UNKNOWN_ERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sf.sprockets.google.InstanceId.Info;

public class InstanceIdInfoTest {
	@Test
	public void testVerify() {
		ImmutableInfo info = Info.builder().authorizedEntity("1234567890")
				.application("com.example.test").appSigner("abcdefghijklmnop").build();
		assertFalse(info.withAppSigner("qrstuvwxyz").matches(info));
		assertTrue(info.matches(info.withAppSigner("AB:CD:EF:GH:IJ:KL:MN:OP")));
	}

	@Test
	public void testForStatusCode() {
		assertEquals(STATUS_OK, Info.forStatusCode(200).status());
		assertEquals(STATUS_BAD_REQUEST, Info.forStatusCode(400).status());
		assertEquals(STATUS_UNAUTHORIZED, Info.forStatusCode(401).status());
		assertEquals(STATUS_FORBIDDEN, Info.forStatusCode(403).status());
		assertEquals(STATUS_NOT_FOUND, Info.forStatusCode(404).status());
		assertEquals(STATUS_SERVICE_UNAVAILABLE, Info.forStatusCode(503).status());
		assertEquals(STATUS_UNKNOWN_ERROR, Info.forStatusCode(999).status());
	}
}
