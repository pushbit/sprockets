/*
 * Copyright 2013-2014 pushbit <pushbit@gmail.com>
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
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.logging.Level.INFO;
import static net.sf.sprockets.google.StreetView.Response.Status.INVALID_REQUEST;
import static net.sf.sprockets.google.StreetView.Response.Status.OK;
import static net.sf.sprockets.google.StreetView.Response.Status.OVER_QUERY_LIMIT;
import static net.sf.sprockets.google.StreetView.Response.Status.UNKNOWN_ERROR;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.logging.Logger;

import net.sf.sprockets.Sprockets;
import net.sf.sprockets.net.HttpClient;
import net.sf.sprockets.util.logging.Loggers;

import org.apache.commons.configuration.Configuration;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

/**
 * <p>
 * Method for downloading a <a href="https://developers.google.com/maps/documentation/streetview/"
 * target="_blank">Google Street View Image</a> by supplying a lat/long or location name. For
 * example:
 * </p>
 * 
 * <pre>{@code
 * Response<InputStream> image = StreetView.image(new Params()
 *         .location("18 Rue Cujas, Paris, France").size(480, 360));
 * Status status = image.getStatus();
 * InputStream in = image.getResult();
 * 
 * if (status == Status.OK && in != null) {
 *     readImage(in);
 * } else {
 *     System.out.println("error: " + status);
 * }
 * 
 * if (in != null) {
 *     in.close();
 * }
 * }</pre>
 * 
 * @since 1.0.0
 */
public class StreetView {
	private static final Logger sLog = Loggers.get(StreetView.class);
	private static final String URL = "https://maps.googleapis.com/maps/api/streetview?";

	private StreetView() {
	}

	/**
	 * Download a street view image. Always {@link InputStream#close() close} the stream when
	 * finished.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>One of:
	 * <ul>
	 * <li>{@link Params#location(double, double) location lat/long}</li>
	 * <li>{@link Params#location(String) location name}</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#heading(int) heading}</li>
	 * <li>{@link Params#pitch(int) pitch}</li>
	 * <li>{@link Params#fov(int) fov}</li>
	 * <li>{@link Params#size(int, int) size}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Street View Image API service
	 */
	public static Response<InputStream> image(Params params) throws IOException {
		return new ImageResponse(HttpClient.openConnection(params.format()));
	}

	/**
	 * <p>
	 * Parameters for the Google Street View Image API service. All methods return their instance so
	 * that calls can be chained. For example:
	 * </p>
	 * 
	 * <pre>{@code
	 * Params p = new Params().location("empire state bldg")
	 *         .pitch(30).fov(100).size(360, 480);
	 * }</pre>
	 */
	public static class Params {
		private double mLat = Double.NEGATIVE_INFINITY;
		private double mLong = Double.NEGATIVE_INFINITY;
		private String mLocation;
		private int mHeading = Integer.MIN_VALUE;
		private int mPitch = Integer.MIN_VALUE;
		private int mFov;
		private int mWidth = 320;
		private int mHeight = 320;

		/**
		 * Get the image closest to this latitude and longitude.
		 */
		public Params location(double latitude, double longitude) {
			mLat = latitude;
			mLong = longitude;
			return this;
		}

		/**
		 * Get the image closest to this location. May be an address, landmark, etc. If
		 * {@link #location(double, double)} is called, this value will be ignored.
		 */
		public Params location(String location) {
			mLocation = location;
			return this;
		}

		/**
		 * Get the image at this compass heading. By default, the camera points at the specified
		 * location.
		 */
		public Params heading(int degrees) {
			mHeading = degrees;
			return this;
		}

		/**
		 * Angle the camera up or down from the default of 0 degrees (which is usually flat
		 * horizontal).
		 */
		public Params pitch(int degrees) {
			mPitch = degrees;
			return this;
		}

		/**
		 * Adjust the field of view to zoom in (down to 10 degrees) or out (up to 120 degrees). The
		 * default FOV is 90 degrees.
		 */
		public Params fov(int degrees) {
			mFov = degrees;
			return this;
		}

		/**
		 * Get an image with this many pixels. Valid sizes range from 16x16 to 640x640. The default
		 * size is 320x320.
		 */
		public Params size(int width, int height) {
			mWidth = width;
			mHeight = height;
			return this;
		}

		/**
		 * Get a URL for this request.
		 */
		public String format() {
			StringBuilder s = new StringBuilder(URL.length() + 256);
			Configuration config = Sprockets.getConfig();
			boolean sensor = config.getBoolean("hardware.location");
			s.append(URL).append("sensor=").append(sensor);
			if (config.getBoolean("google.street-view.use-api-key")) {
				String key = config.getString("google.api-key");
				checkState(!Strings.isNullOrEmpty(key), "google.api-key not set");
				s.append("&key=").append(key);
			}
			if (mLat > Double.NEGATIVE_INFINITY && mLong > Double.NEGATIVE_INFINITY) {
				s.append("&location=").append(mLat).append(',').append(mLong);
			} else if (!Strings.isNullOrEmpty(mLocation)) {
				try {
					s.append("&location=").append(URLEncoder.encode(mLocation, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("UTF-8 encoding isn't supported?!", e);
				}
			}
			if (mHeading > Integer.MIN_VALUE) {
				s.append("&heading=").append(mHeading);
			}
			if (mPitch > Integer.MIN_VALUE) {
				s.append("&pitch=").append(mPitch);
			}
			if (mFov > 0) {
				s.append("&fov=").append(mFov);
			}
			return s.append("&size=").append(mWidth).append('x').append(mHeight).toString();
		}

		/**
		 * Clear any set parameters so that this instance can be re-used for a new request.
		 * 
		 * @since 1.1.0
		 */
		public Params clear() {
			mLat = Double.NEGATIVE_INFINITY;
			mLong = Double.NEGATIVE_INFINITY;
			mLocation = null;
			mHeading = Integer.MIN_VALUE;
			mPitch = Integer.MIN_VALUE;
			mFov = 0;
			mWidth = 320;
			mHeight = 320;
			return this;
		}

		@Override
		public int hashCode() {
			return Objects
					.hashCode(mLat, mLong, mLocation, mHeading, mPitch, mFov, mWidth, mHeight);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null) {
				if (this == obj) {
					return true;
				} else if (obj instanceof Params) {
					Params o = (Params) obj;
					return mLat == o.mLat && mLong == o.mLong
							&& Objects.equal(mLocation, o.mLocation) && mHeading == o.mHeading
							&& mPitch == o.mPitch && mFov == o.mFov && mWidth == o.mWidth
							&& mHeight == o.mHeight;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			boolean l = mLat != Double.NEGATIVE_INFINITY && mLong != Double.NEGATIVE_INFINITY;
			return MoreObjects.toStringHelper(this)
					.add("location", l ? mLat + "," + mLong : mLocation)
					.add("heading", mHeading != Integer.MIN_VALUE ? mHeading : null)
					.add("pitch", mPitch != Integer.MIN_VALUE ? mPitch : null)
					.add("fov", mFov != 0 ? mFov : null).add("size", mWidth + "x" + mHeight)
					.omitNullValues().toString();
		}
	}

	/**
	 * Result from one of the {@link StreetView} methods.
	 * 
	 * @param <T>
	 *            type of result returned in the response
	 */
	public static class Response<T> {
		/**
		 * Indications of the success or failure of the request.
		 */
		public enum Status {
			OK, OVER_QUERY_LIMIT, INVALID_REQUEST, UNKNOWN_ERROR
		}

		Status mStatus;
		T mResult;
		private int mHash;

		private Response() {
		}

		/**
		 * Indication of the success or failure of the request.
		 */
		public Status getStatus() {
			return mStatus;
		}

		/**
		 * Check the {@link StreetView} method signature for the specific type of result it returns.
		 * Can be null if there was a problem with the request.
		 */
		public T getResult() {
			return mResult;
		}

		@Override
		public int hashCode() {
			if (mHash == 0) {
				mHash = Objects.hashCode(mStatus, mResult);
			}
			return mHash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null) {
				if (this == obj) {
					return true;
				} else if (obj instanceof Response) {
					Response<?> o = (Response<?>) obj;
					return mStatus == o.mStatus && Objects.equal(mResult, o.mResult);
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("status", mStatus).add("result", mResult)
					.omitNullValues().toString();
		}
	}

	/**
	 * Image bitstream for reading. Always {@link InputStream#close() close} the stream when
	 * finished.
	 */
	private static class ImageResponse extends Response<InputStream> {
		/**
		 * Get the InputStream from the connection response.
		 */
		private ImageResponse(HttpURLConnection con) throws IOException {
			switch (con.getResponseCode()) {
			case HTTP_OK:
				mStatus = OK;
				mResult = con.getInputStream();
				break;
			case HTTP_BAD_REQUEST:
				mStatus = INVALID_REQUEST;
				break;
			case HTTP_FORBIDDEN:
				mStatus = OVER_QUERY_LIMIT;
				mResult = con.getInputStream(); // "quota has been exceeded" image
				break;
			default:
				mStatus = UNKNOWN_ERROR;
				sLog.log(INFO, "Unexpected response code: {0}", con.getResponseCode());
			}
		}
	}
}
