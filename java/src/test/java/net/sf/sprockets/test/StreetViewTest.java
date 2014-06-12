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

package net.sf.sprockets.test;

import static net.sf.sprockets.google.StreetView.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import net.sf.sprockets.google.StreetView;
import net.sf.sprockets.google.StreetView.Params;
import net.sf.sprockets.google.StreetView.Response;

import org.junit.Test;

public class StreetViewTest {
	@Test
	public void testImage() throws IOException {
		Response<InputStream> image = StreetView.image(new Params()
				.location("18 Rue Cujas, Paris, France"));
		assertEquals(OK, image.getStatus());
		InputStream in = image.getResult();
		assertNotNull(in);
		byte[] b = new byte[8192];
		while (in.read(b) != -1) {
		}
		in.close();
	}
}
