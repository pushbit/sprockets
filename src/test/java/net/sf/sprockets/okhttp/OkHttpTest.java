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

package net.sf.sprockets.okhttp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpTest {
	private final OkHttp mClient = new OkHttp(new OkHttpClient());
	private final String mUrl = "http://example.com/";
	private final String mHeaderName = "Test-Header";
	private final String mHeaderValue = "test value";

	@Test
	public void testRequest() {
		assertEquals(mUrl, OkHttp.request(mUrl).url().toString());
	}

	@Test
	public void testRequestWithHeaders() {
		Request request = OkHttp.request(mUrl, mHeaderName, mHeaderValue);
		assertEquals(mUrl, request.url().toString());
		assertEquals(mHeaderValue, request.header(mHeaderName));
	}

	@Test
	public void testCall() {
		assertEquals(mUrl, mClient.call(mUrl).request().url().toString());
	}

	@Test
	public void testResponse() throws IOException {
		try (Response resp = mClient.response(mUrl)) {
			assertTrue(resp.isSuccessful());
		}
	}

	@Test
	public void testDownload() throws IOException {
		File file = File.createTempFile(getClass().getSimpleName(), null);
		file.deleteOnExit();
		assertTrue(mClient.download(mUrl, file).isSuccessful());
		assertTrue(file.length() > 0);
	}
}
