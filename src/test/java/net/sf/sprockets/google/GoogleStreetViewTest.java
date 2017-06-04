/*
 * Copyright 2013-2017 pushbit <pushbit@gmail.com>
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

import static net.sf.sprockets.google.StreetView.Metadata.STATUS_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sf.sprockets.google.StreetView.Params;
import net.sf.sprockets.okhttp.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.Okio;

public class GoogleStreetViewTest {
	private final GoogleStreetView mStreetView =
			new GoogleStreetView(new OkHttp(new OkHttpClient()), GoogleApiAuth.builder()
					.browserKey(System.getProperty("google_browser_key")).serverKey("").build());
	private final Params mParams = Params.create().location("18 Rue Cujas, Paris, France");

	@Test
	public void testImageResponse() throws IOException {
		try (Response image = mStreetView.image(mParams).execute()) {
			assertTrue(image.isSuccessful());
			image.body().source().readAll(Okio.blackhole());
		}
	}

	@Test
	public void testImageDownload() throws IOException {
		File file = File.createTempFile(getClass().getSimpleName(), null);
		file.deleteOnExit();
		assertTrue(mStreetView.image(mParams, file).isSuccessful());
		assertTrue(file.length() > 0);
	}

	@Test
	public void testMetadata() throws IOException {
		assertEquals(STATUS_OK, mStreetView.metadata(mParams).status());
	}
}
