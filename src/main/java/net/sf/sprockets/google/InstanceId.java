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

import static java.util.Locale.US;

import java.io.IOException;

import javax.annotation.Nullable;

import org.immutables.gson.Gson;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;

import net.sf.sprockets.google.ImmutableInfo.Builder;

/**
 * Gets details for application instance ID tokens.
 *
 * @since 4.0.0
 */
@Gson.TypeAdapters
public interface InstanceId {
	/**
	 * Get the details for the application instance ID token.
	 *
	 * @throws IOException
	 *             if there is a problem communicating with the Google Instance ID service
	 */
	Info info(String token) throws IOException;

	/**
	 * Details for an application instance ID token.
	 */
	@Immutable
	public abstract static class Info {
		public static final String STATUS_OK = "OK";
		public static final String STATUS_BAD_REQUEST = "BAD_REQUEST";
		public static final String STATUS_UNAUTHORIZED = "UNAUTHORIZED";
		public static final String STATUS_FORBIDDEN = "FORBIDDEN";
		public static final String STATUS_NOT_FOUND = "NOT_FOUND";
		public static final String STATUS_SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
		public static final String STATUS_UNKNOWN_ERROR = "UNKNOWN_ERROR";
		public static final String ATTEST_ROOTED = "ATTEST_ROOTED";
		public static final String ATTEST_NOT_ROOTED = "ATTEST_NOT_ROOTED";
		public static final String ATTEST_UNKNOWN = "ATTEST_UNKNOWN";
		public static final String PLATFORM_ANDROID = "PLATFORM_ANDROID";
		public static final String PLATFORM_IOS = "PLATFORM_IOS";
		public static final String PLATFORM_CHROME = "PLATFORM_CHROME";

		Info() {
		}

		public static ImmutableInfo.Builder builder() {
			return ImmutableInfo.builder();
		}

		/**
		 * Indicates the success or failure of the request. Can be compared to the {@code STATUS}
		 * constants in this class.
		 */
		@Default
		public String status() {
			return STATUS_OK;
		}

		/**
		 * Project ID authorised to send to the token.
		 */
		@Nullable
		public abstract String authorizedEntity();

		/**
		 * Package name associated with the token.
		 */
		@Nullable
		public abstract String application();

		/**
		 * Version code of the application.
		 */
		@Nullable
		public abstract String applicationVersion();

		/**
		 * SHA-1 fingerprint for the signature applied to the package.
		 */
		@Nullable
		public abstract String appSigner();

		/**
		 * Lower case {@link #appSigner()} without colons.
		 */
		@Derived
		@Nullable
		public String appSignerShort() {
			String sig = appSigner();
			return sig != null ? sig.replace(":", "").toLowerCase(US) : null;
		}

		/**
		 * Indicates the device platform to which the token belongs. Can be compared to the
		 * {@code PLATFORM} constants in this class.
		 */
		@Nullable
		public abstract String platform();

		/**
		 * Indicates whether or not the device is rooted. Can be compared to the {@code ATTEST}
		 * constants in this class.
		 */
		@Nullable
		public abstract String attestStatus();

		/**
		 * True if this matches the expected properties.
		 *
		 * @param expected
		 *            null properties are ignored
		 */
		public boolean matches(Info expected) {
			return (expected.authorizedEntity() == null
					|| expected.authorizedEntity().equals(authorizedEntity()))
					&& (expected.application() == null
							|| expected.application().equals(application()))
					&& (expected.applicationVersion() == null
							|| expected.applicationVersion().equals(applicationVersion()))
					&& (expected.appSigner() == null
							|| expected.appSignerShort().equals(appSignerShort()))
					&& (expected.platform() == null || expected.platform().equals(platform()))
					&& (expected.attestStatus() == null
							|| expected.attestStatus().equals(attestStatus()));
		}

		static Info forStatusCode(int code) {
			Builder b = builder();
			if (code >= 200 && code < 300) {
				b.status(STATUS_OK);
			} else {
				switch (code) {
				case 400:
					b.status(STATUS_BAD_REQUEST);
					break;
				case 401:
					b.status(STATUS_UNAUTHORIZED);
					break;
				case 403:
					b.status(STATUS_FORBIDDEN);
					break;
				case 404:
					b.status(STATUS_NOT_FOUND);
					break;
				case 503:
					b.status(STATUS_SERVICE_UNAVAILABLE);
					break;
				default:
					b.status(STATUS_UNKNOWN_ERROR);
				}
			}
			return b.build();
		}
	}
}
