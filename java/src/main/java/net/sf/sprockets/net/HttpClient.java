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

package net.sf.sprockets.net;

import static java.util.logging.Level.WARNING;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import net.sf.sprockets.Sprockets;
import net.sf.sprockets.util.logging.Loggers;

import com.squareup.okhttp.OkHttpClient;

/**
 * Provides HTTP connections using the client specified in the {@link Sprockets library settings}.
 * 
 * @since 1.1.0
 */
public class HttpClient {
	private static final Logger sLog = Loggers.get(HttpClient.class);
	/** Null if using the standard library client */
	private static final OkHttpClient sClient;
	static {
		String client = Sprockets.getConfig().getString("network.http-client");
		if ("java.net".equals(client)) {
			sClient = null;
		} else {
			if (!"okhttp".equals(client)) {
				sLog.log(WARNING, "Unknown http-client: {0}, using default okhttp", client);
			}
			sClient = new OkHttpClient();
		}
	}

	private HttpClient() {
	}

	/**
	 * Get a connection to the URL.
	 */
	public static HttpURLConnection openConnection(URL url) throws IOException {
		return sClient != null ? sClient.open(url) : (HttpURLConnection) url.openConnection();
	}
}
