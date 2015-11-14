/*
 * Copyright 2013-2015 pushbit <pushbit@gmail.com>
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

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import javax.annotation.Nullable;

import net.sf.sprockets.Sprockets;
import net.sf.sprockets.net.HttpClient;

import org.apache.commons.configuration.Configuration;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Modifiable;
import org.immutables.value.Value.Style;

import com.google.common.base.Strings;

/**
 * <p>
 * Method for downloading a <a href="https://developers.google.com/maps/documentation/streetview/"
 * target="_blank">Google Street View Image</a> by supplying lat/long or location name. For example:
 * </p>
 * 
 * <pre>{@code
 * Response<InputStream> image = StreetView.image(Params.create()
 *         .location("18 Rue Cujas, Paris, France").width(480).height(360));
 * 
 * int status = image.getStatus();
 * InputStream in = image.getResult();
 * 
 * if (status == HTTP_OK && in != null) {
 *     readImage(in);
 * } else {
 *     System.out.println("error, HTTP status code: " + status);
 * }
 * 
 * Closeables.closeQuietly(in);
 * }</pre>
 * 
 * @since 1.0.0
 */
public class StreetView {
	private StreetView() {
	}

	/**
	 * Download a Street View image. Always {@link InputStream#close() close} the stream when
	 * finished.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>One of:
	 * <ul>
	 * <li>{@link Params#latitude() latitude} and {@link Params#longitude() longitude}</li>
	 * <li>{@link Params#location() location name}</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#heading() heading}</li>
	 * <li>{@link Params#pitch() pitch}</li>
	 * <li>{@link Params#fov() fov}</li>
	 * <li>{@link Params#width() width}</li>
	 * <li>{@link Params#height() height}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Street View Image API service
	 */
	public static Response<InputStream> image(Params params) throws IOException {
		return ImageResponse.from(HttpClient.openConnection(params.format()));
	}

	/**
	 * <p>
	 * Parameters for the Google Street View Image API service. Example usage:
	 * </p>
	 * 
	 * <pre>{@code
	 * Params.create().location("empire state bldg")
	 *         .pitch(30).fov(100).width(360).height(480);
	 * }</pre>
	 */
	@Modifiable
	@Style(typeModifiable = "StreetView*", create = "new", get = "*", set = "*")
	public static abstract class Params {
		Params() {
		}

		/**
		 * Mutable instance where values can be set.
		 * 
		 * @since 3.0.0
		 */
		public static StreetViewParams create() {
			return new StreetViewParams();
		}

		/**
		 * Used with {@link #longitude() longitude} to get the closest image.
		 * 
		 * @since 3.0.0
		 */
		@Default
		public double latitude() {
			return Double.NEGATIVE_INFINITY;
		}

		/**
		 * Used with {@link #latitude() latitude} to get the closest image.
		 * 
		 * @since 3.0.0
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
		 * Width of the image to get in pixels. May be from 16 to 640. Default value: 320.
		 * 
		 * @since 3.0.0
		 */
		@Default
		public int width() {
			return 320;
		}

		/**
		 * Height of the image to get in pixels. May be from 16 to 640. Default value: 320.
		 * 
		 * @since 3.0.0
		 */
		@Default
		public int height() {
			return 320;
		}

		/**
		 * Get a URL for this request.
		 */
		public String format() {
			String url = "https://maps.googleapis.com/maps/api/streetview?";
			StringBuilder s = new StringBuilder(url.length() + 256);
			s.append(url);
			Configuration config = Sprockets.getConfig();
			if (config.getBoolean("google.street-view.use-api-key")) {
				String key = config.getString("google.api-key");
				checkState(!Strings.isNullOrEmpty(key), "google.api-key not set");
				s.append("&key=").append(key);
			}
			double lat = latitude();
			double lon = longitude();
			String location = location();
			if (lat > Double.NEGATIVE_INFINITY && lon > Double.NEGATIVE_INFINITY) {
				s.append("&location=").append(lat).append(',').append(lon);
			} else if (!Strings.isNullOrEmpty(location)) {
				try {
					s.append("&location=").append(URLEncoder.encode(location, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("UTF-8 encoding isn't supported?!", e);
				}
			}
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
			return s.append("&size=").append(width()).append('x').append(height()).toString();
		}
	}

	/**
	 * Result from one of the {@link StreetView} methods.
	 * 
	 * @param <T>
	 *            type of result returned in the response
	 */
	public static abstract class Response<T> {
		Response() {
		}

		/**
		 * Indication of the success or failure of the request.
		 * 
		 * @return one of the {@link HttpURLConnection} HTTP_* constants or -1 if the response is
		 *         not valid
		 */
		public abstract int getStatus();

		/**
		 * Check the {@link StreetView} method signature for the specific type of result it returns.
		 * 
		 * @return null if there was a problem with the request
		 */
		@Nullable
		public abstract T getResult();
	}

	/**
	 * Image bitstream for reading. Always {@link InputStream#close() close} the stream when
	 * finished.
	 */
	@Immutable
	static abstract class ImageResponse extends Response<InputStream> {
		static ImageResponse from(HttpURLConnection con) throws IOException {
			return ImmutableImageResponse.builder().status(con.getResponseCode())
					.result(con.getInputStream()).build();
		}
	}
}
