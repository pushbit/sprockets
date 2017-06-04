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

import static org.immutables.value.Value.Style.ImplementationVisibility.PACKAGE;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.immutables.gson.Gson;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Modifiable;
import org.immutables.value.Value.Style;

import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;

import okhttp3.Call;
import okhttp3.Response;

/**
 * <p>
 * Downloads a
 * <a href="https://developers.google.com/maps/documentation/streetview/" target="_blank">Google
 * Street View Image</a> by lat/long or location name. For example:
 * </p>
 * <pre>{@code
 * try (Response image = streetView.image(
 *         Params.create().location("18 Rue Cujas, Paris, France"))) {
 *     if (image.isSuccessful()) {
 *         readImage(image.body().source());
 *     }
 * }
 * }</pre>
 *
 * @since 4.0.0
 */
@Gson.TypeAdapters(fieldNamingStrategy = true)
public interface StreetView {
	/**
	 * Get a Street View image Call.
	 */
	Call image(Params params);

	/**
	 * Download a Street View image to the file.
	 *
	 * @throws IOException
	 *             if there is a problem communicating with the Google Street View Image API service
	 *             or writing the file
	 */
	Response image(Params params, File file) throws IOException;

	/**
	 * Get Street View image metadata.
	 *
	 * @throws IOException
	 *             if there is a problem communicating with the Google Street View Image API service
	 */
	Metadata metadata(Params params) throws IOException;

	/**
	 * <p>
	 * Parameters for the Google Street View Image API service. Example usage:
	 * </p>
	 * <pre>{@code
	 * Params.create().location("empire state bldg")
	 *         .pitch(30).fov(100).width(360).height(480);
	 * }</pre>
	 */
	@Modifiable
	@Style(typeModifiable = "StreetView*", get = "*", set = "*")
	public abstract static class Params {
		public static final String REQUEST_IMAGE = "";
		public static final String REQUEST_METADATA = "/metadata";

		Params() {
		}

		/**
		 * Mutable instance where values can be set.
		 */
		public static StreetViewParams create() {
			return StreetViewParams.create();
		}

		/**
		 * Used with {@link #longitude() longitude} to get the closest image.
		 */
		@Default
		public double latitude() {
			return Double.NEGATIVE_INFINITY;
		}

		/**
		 * Used with {@link #latitude() latitude} to get the closest image.
		 */
		@Default
		public double longitude() {
			return Double.NEGATIVE_INFINITY;
		}

		/**
		 * Address, landmark name, or other description of the place to get the closest image of. If
		 * latitude and longitude are set, this value will be ignored.
		 */
		@Nullable
		public abstract String location();

		/**
		 * Panorama ID previously retrieved from another service, such as
		 * {@link StreetView#metadata(Params) Street View Image Metadata}.
		 */
		@Nullable
		public abstract String pano();

		/**
		 * Compass heading for the camera. By default, the camera points at the specified location.
		 */
		@Default
		public int heading() {
			return Integer.MIN_VALUE;
		}

		/**
		 * Camera angle which can be tilted up or down from the default of 0 degrees (which is
		 * usually flat horizontal).
		 */
		@Default
		public int pitch() {
			return Integer.MIN_VALUE;
		}

		/**
		 * Field of view which can be zoomed in (down to 10 degrees) or out (up to 120 degrees).
		 * Default value: 90.
		 */
		@Default
		public int fov() {
			return 0;
		}

		/**
		 * Width of the image to get in pixels. May be from 16 to 640. Default value: 640.
		 */
		@Default
		public int width() {
			return 640;
		}

		/**
		 * Height of the image to get in pixels. May be from 16 to 640. Default value: 640.
		 */
		@Default
		public int height() {
			return 640;
		}

		/**
		 * Get a URL for the request.
		 *
		 * @param request
		 *            must be one of the {@code REQUEST} constants in this class
		 */
		public String format(String request, GoogleApiAuth auth) {
			String domain = "https://maps.googleapis.com"; // inserted after any URL signing
			String path = "/maps/api/streetview";
			StringBuilder s = new StringBuilder(domain.length() + path.length() + 256);
			s.append(path).append(request).append("?key=").append(auth.browserKey());
			String pano = pano();
			if (!Strings.isNullOrEmpty(pano)) {
				s.append("&pano=").append(pano);
			} else {
				double lat = latitude();
				double lon = longitude();
				String location = location();
				if (lat > Double.NEGATIVE_INFINITY && lon > Double.NEGATIVE_INFINITY) {
					s.append("&location=").append(lat).append(',').append(lon);
				} else if (!Strings.isNullOrEmpty(location)) {
					try {
						s.append("&location=").append(URLEncoder.encode(location, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						throw new UnsupportedOperationException("UTF-8 encoding not supported?", e);
					}
				}
			}
			if (request.equals(REQUEST_IMAGE)) { // irrelevant for metadata requests
				int heading = heading();
				if (heading > Integer.MIN_VALUE) {
					s.append("&heading=").append(heading);
				}
				int pitch = pitch();
				if (pitch > Integer.MIN_VALUE) {
					s.append("&pitch=").append(pitch);
				}
				int fov = fov();
				if (fov > 0) {
					s.append("&fov=").append(fov);
				}
				s.append("&size=").append(width()).append('x').append(height());
			}
			String secret = auth.urlSigningSecret();
			if (!Strings.isNullOrEmpty(secret)) {
				String signature = sign(s.toString(), secret); // sign current path
				s.append("&signature=").append(signature); // note: don't inline
			}
			return s.insert(0, domain).toString();
		}

		private static String sign(String url, String secret) {
			BaseEncoding base64 = BaseEncoding.base64Url();
			try {
				Mac mac = Mac.getInstance("HmacSHA1");
				mac.init(new SecretKeySpec(base64.decode(secret), "HmacSHA1"));
				return base64.encode(mac.doFinal(url.getBytes()));
			} catch (NoSuchAlgorithmException e) {
				throw new UnsupportedOperationException("HmacSHA1 algorithm not supported?!", e);
			} catch (InvalidKeyException e) {
				throw new UnsupportedOperationException("key is inappropriate for the MAC?!", e);
			}
		}
	}

	/**
	 * Google Street View Image API metadata for the availability and details of a Street View
	 * image.
	 */
	@Immutable
	@Style(visibility = PACKAGE)
	public abstract static class Metadata {
		public static final String STATUS_OK = "OK";
		public static final String STATUS_ZERO_RESULTS = "ZERO_RESULTS";
		public static final String STATUS_NOT_FOUND = "NOT_FOUND";
		public static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
		public static final String STATUS_REQUEST_DENIED = "REQUEST_DENIED";
		public static final String STATUS_INVALID_REQUEST = "INVALID_REQUEST";
		public static final String STATUS_UNKNOWN_ERROR = "UNKNOWN_ERROR";

		Metadata() {
		}

		/**
		 * Indicates the success or failure of the request. Can be compared to the {@code STATUS}
		 * constants in this class.
		 */
		public abstract String status();

		/**
		 * Panorama ID which can be used in a Street View image {@link Params#pano() request}.
		 */
		@Nullable
		public abstract String panoId();

		/**
		 * Geo coordinates of the location in the image.
		 */
		@Nullable
		public abstract Location location();

		/**
		 * When the photo was taken.
		 */
		@Nullable
		public abstract String date();

		@Nullable
		public abstract String copyright();

		/**
		 * Geo coordinates.
		 */
		@Immutable
		@Style(visibility = PACKAGE)
		public abstract static class Location {
			public abstract double lat();

			public abstract double lng();
		}
	}
}
