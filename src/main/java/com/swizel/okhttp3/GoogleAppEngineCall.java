/*
 * Copyright 2016-2017 Andrew Kelly
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.swizel.okhttp3;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpMethod;
import okio.Buffer;
import okio.BufferedSource;

/**
 * An implementation of the OkHttp3 Call interface that allows OkHttp to be used on Google App
 * Engine.
 */
class GoogleAppEngineCall implements Call {

	private static final String NO_ASYNC_MESSAGE =
			"Async callbacks should be performed using tasks/queues on App Engine along with the execute() instead of enqueue() method.";
	private static final ResponseBody EMPTY_BODY = new ResponseBody() {
		@Override
		public MediaType contentType() {
			return null;
		}

		@Override
		public long contentLength() {
			return 0;
		}

		@Override
		public BufferedSource source() {
			return new Buffer();
		}
	};
	private Request mRequest;
	private boolean mExecuted = false;
	private boolean mCancelled = false;

	GoogleAppEngineCall(Request request) {
		mRequest = request;
	}

	@Override
	public Request request() {
		return mRequest;
	}

	@Override
	public Response execute() throws IOException {
		synchronized (this) {
			if (mExecuted) {
				throw new IllegalStateException("Already Executed");
			}
			mExecuted = true;
		}
		if ("GET".equalsIgnoreCase(mRequest.method())
				|| "DELETE".equalsIgnoreCase(mRequest.method())
				|| "POST".equalsIgnoreCase(mRequest.method())
				|| "PATCH".equalsIgnoreCase(mRequest.method())
				|| "PUT".equalsIgnoreCase(mRequest.method())) {

			URL url = new URL(mRequest.url().url().toString());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			if ("PATCH".equals(mRequest.method())) {
				con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
				con.setRequestMethod("POST");
			} else {
				con.setRequestMethod(mRequest.method());
			}

			setHeaders(mRequest, url, con);

			// Send body if we're required to do so.
			if (HttpMethod.requiresRequestBody(mRequest.method())
					&& mRequest.body().contentLength() > 0) {

				Buffer payload = new Buffer();
				mRequest.body().writeTo(payload);

				con.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.write(payload.readByteArray());
				wr.flush();
				wr.close();
			}

			Response.Builder builder = parseResponse(con);
			return builder.build();

		} else {
			throw new RuntimeException("Unsupported HTTP method : " + mRequest.method());

		}
	}

	@Override
	public void enqueue(Callback responseCallback) {
		throw new RuntimeException(NO_ASYNC_MESSAGE);
	}

	@Override
	public void cancel() {
		mCancelled = true;
	}

	@Override
	public boolean isExecuted() {
		return mExecuted;
	}

	@Override
	public boolean isCanceled() {
		return mCancelled;
	}

	@Override
	public GoogleAppEngineCall clone() {
		return new GoogleAppEngineCall(request());
	}

	private void setHeaders(Request request, URL url, URLConnection con) {
		Headers headers = request.headers();
		for (String header : headers.names()) {
			// TODO_: Support multiple values/header
			con.setRequestProperty(header, headers.get(header));
		}

		// HttpsUrlConnection isn't supported on App Engine, so add a new Header to fix that.
		if (request.isHttps()) {
			int port = url.getPort();
			if (port == -1) {
				port = 443;
			}
			con.setRequestProperty("Host", url.getHost() + ":" + port);
		}
	}

	private Response.Builder parseResponse(HttpURLConnection connection) throws IOException {
		Response.Builder builder = new Response.Builder();
		builder.request(request());
		builder.protocol(Protocol.HTTP_1_1);
		builder.code(connection.getResponseCode());
		builder.message(String.valueOf(connection.getResponseMessage()));

		InputStream in = connection.getInputStream();
		if (in != null) {
			BufferedInputStream bis = new BufferedInputStream(in);
			byte[] buffer = new byte[8192];

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			int len = bis.read(buffer);
			while (len != -1) {
				baos.write(buffer, 0, len);
				len = bis.read(buffer);
			}
			in.close();

			builder.body(ResponseBody.create(MediaType.parse(connection.getContentType()),
					baos.toByteArray()));
		} else {
			builder.body(EMPTY_BODY);
		}

		return builder;
	}

}
