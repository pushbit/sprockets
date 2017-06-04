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

import static net.sf.sprockets.google.StreetView.Params.REQUEST_IMAGE;
import static net.sf.sprockets.google.StreetView.Params.REQUEST_METADATA;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.sf.sprockets.google.StreetView.Params;

public class StreetViewParamsTest {
	private StreetViewParams mParams;
	private ImmutableGoogleApiAuth.Builder mAuth;

	@Before
	public void resetBefore() {
		mParams = Params.create().heading(7).pitch(11).fov(13).width(17).height(19);
		mAuth = GoogleApiAuth.builder().browserKey("testkey").serverKey("");
	}

	@Test
	public void testLatLonFormat() {
		mParams.latitude(3.0).longitude(5.0).location("ignored location");
		String expected = "https://maps.googleapis.com/maps/api/streetview?key=testkey"
				+ "&location=3.0,5.0&heading=7&pitch=11&fov=13&size=17x19";
		assertEquals(expected, mParams.format(REQUEST_IMAGE, mAuth.build()));
	}

	@Test
	public void testLocationFormat() {
		mParams.location("test location");
		String expected = "https://maps.googleapis.com/maps/api/streetview?key=testkey"
				+ "&location=test+location&heading=7&pitch=11&fov=13&size=17x19";
		assertEquals(expected, mParams.format(REQUEST_IMAGE, mAuth.build()));
	}

	@Test
	public void testPanoFormat() {
		mParams.pano("test-pano");
		String expected = "https://maps.googleapis.com/maps/api/streetview?key=testkey"
				+ "&pano=test-pano&heading=7&pitch=11&fov=13&size=17x19";
		assertEquals(expected, mParams.format(REQUEST_IMAGE, mAuth.build()));
	}

	@Test
	public void testSignatureFormat() {
		mAuth.urlSigningSecret("testsecret");
		String expected = "https://maps.googleapis.com/maps/api/streetview?key=testkey"
				+ "&heading=7&pitch=11&fov=13&size=17x19&signature=l9OlfUEXLc2-tNuH2K1i2D6cPZs=";
		assertEquals(expected, mParams.format(REQUEST_IMAGE, mAuth.build()));
	}

	@Test
	public void testMetadataFormat() {
		Params params = Params.create().location("test location");
		String expected = "https://maps.googleapis.com/maps/api/streetview/metadata?key=testkey"
				+ "&location=test+location";
		assertEquals(expected, params.format(REQUEST_METADATA, mAuth.build()));
	}
}
