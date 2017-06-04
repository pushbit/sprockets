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

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
import static net.sf.sprockets.google.StreetView.Params.REQUEST_IMAGE;
import static net.sf.sprockets.google.StreetView.Params.REQUEST_METADATA;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sf.sprockets.okhttp.OkHttp;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Connects to the Google Street View Image service with the provided client and authentication
 * values.
 *
 * @since 4.0.0
 */
@Singleton
public class GoogleStreetView implements StreetView {
	private final OkHttp mClient;
	private final GoogleApiAuth mAuth;
	private Gson mGson;

	@Inject
	public GoogleStreetView(OkHttp client, GoogleApiAuth auth) {
		mClient = client;
		mAuth = auth;
	}

	@Override
	public Call image(Params params) {
		return mClient.call(params.format(REQUEST_IMAGE, mAuth));
	}

	@Override
	public Response image(Params params, File file) throws IOException {
		return mClient.download(params.format(REQUEST_IMAGE, mAuth), file);
	}

	@Override
	public Metadata metadata(Params params) throws IOException {
		if (mGson == null) {
			mGson = new GsonBuilder().registerTypeAdapterFactory(new GsonAdaptersStreetView())
					.setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
		}
		try (Response resp = mClient.response(params.format(REQUEST_METADATA, mAuth))) {
			return mGson.fromJson(resp.body().charStream(), Metadata.class);
		}
	}
}
