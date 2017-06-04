/*
 * Copyright 2016-2017 pushbit <pushbit@gmail.com>
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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

/**
 * Utility methods for working with OkHttp.
 *
 * @since 4.0.0
 */
@Singleton
public class OkHttp {
	private final Call.Factory mCaller;

	@Inject
	public OkHttp(Call.Factory caller) {
		mCaller = caller;
	}

	/**
	 * Get a GET request for the URL.
	 */
	public static Request request(String url) {
		return request(url, (String[]) null);
	}

	/**
	 * Get a GET request for the URL and headers.
	 *
	 * @param headers
	 *            length must be a multiple of two: {@code String name, String value, ...}
	 */
	public static Request request(String url, String... headers) {
		Request.Builder request = new Request.Builder().url(url);
		if (headers != null) {
			int length = headers.length;
			checkArgument(length % 2 == 0, "headers length must be a multiple of two");
			for (int i = 0; i < length; i += 2) {
				request.addHeader(headers[i], headers[i + 1]);
			}
		}
		return request.build();
	}

	/**
	 * Get a Call for a GET request for the URL.
	 */
	public Call call(String url) {
		return call(url, (String[]) null);
	}

	/**
	 * Get a Call for a GET request for the URL and headers.
	 */
	public Call call(String url, String... headers) {
		return mCaller.newCall(request(url, headers));
	}

	/**
	 * Get a response to a GET request for the URL.
	 */
	public Response response(String url) throws IOException {
		return response(url, (String[]) null);
	}

	/**
	 * Get a response to a GET request for the URL and headers.
	 */
	public Response response(String url, String... headers) throws IOException {
		return call(url, headers).execute();
	}

	/**
	 * Download the resource at the URL and write it to the file.
	 *
	 * @return Response whose body has already been consumed and closed
	 */
	public Response download(String url, File destination) throws IOException {
		return download(url, destination, (String[]) null);
	}

	/**
	 * Download the resource at the URL with the headers and write it to the file.
	 *
	 * @return Response whose body has already been consumed and closed
	 */
	public Response download(String url, File destination, String... headers) throws IOException {
		try (Response resp = response(url, headers)) {
			if (resp.isSuccessful()) {
				try (BufferedSink sink = Okio.buffer(Okio.sink(destination))) {
					resp.body().source().readAll(sink);
				}
			}
			return resp;
		}
	}
}
