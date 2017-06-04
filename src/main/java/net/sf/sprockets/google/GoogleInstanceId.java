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

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sf.sprockets.okhttp.OkHttp;
import okhttp3.Response;

/**
 * Connects to the Google Instance ID service with the provided client and Google API key.
 *
 * @since 4.0.0
 */
@Singleton
public class GoogleInstanceId implements InstanceId {
	private final OkHttp mClient;
	private final String mKey;
	private final Gson mGson =
			new GsonBuilder().registerTypeAdapterFactory(new GsonAdaptersInstanceId()).create();

	@Inject
	public GoogleInstanceId(OkHttp client, GoogleApiAuth auth) {
		mClient = client;
		mKey = "key=" + auth.serverKey();
	}

	@Override
	public Info info(String token) throws IOException {
		try (Response resp = mClient.response("https://iid.googleapis.com/iid/info/" + token,
				"Authorization", mKey)) {
			return resp.isSuccessful() ? mGson.fromJson(resp.body().charStream(), Info.class)
					: Info.forStatusCode(resp.code());
		}
	}
}
