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
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.Locale.ENGLISH;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.logging.Level.INFO;
import static net.sf.sprockets.google.Places.Field.ADDRESS;
import static net.sf.sprockets.google.Places.Field.EVENTS;
import static net.sf.sprockets.google.Places.Field.FORMATTED_ADDRESS;
import static net.sf.sprockets.google.Places.Field.FORMATTED_OPENING_HOURS;
import static net.sf.sprockets.google.Places.Field.FORMATTED_PHONE_NUMBER;
import static net.sf.sprockets.google.Places.Field.GEOMETRY;
import static net.sf.sprockets.google.Places.Field.ICON;
import static net.sf.sprockets.google.Places.Field.INTL_PHONE_NUMBER;
import static net.sf.sprockets.google.Places.Field.MATCHED_SUBSTRINGS;
import static net.sf.sprockets.google.Places.Field.NAME;
import static net.sf.sprockets.google.Places.Field.OPENING_HOURS;
import static net.sf.sprockets.google.Places.Field.OPEN_NOW;
import static net.sf.sprockets.google.Places.Field.PERMANENTLY_CLOSED;
import static net.sf.sprockets.google.Places.Field.PHOTOS;
import static net.sf.sprockets.google.Places.Field.PRICE_LEVEL;
import static net.sf.sprockets.google.Places.Field.RATING;
import static net.sf.sprockets.google.Places.Field.REVIEWS;
import static net.sf.sprockets.google.Places.Field.TERMS;
import static net.sf.sprockets.google.Places.Field.TYPES;
import static net.sf.sprockets.google.Places.Field.URL;
import static net.sf.sprockets.google.Places.Field.USER_RATINGS_TOTAL;
import static net.sf.sprockets.google.Places.Field.UTC_OFFSET;
import static net.sf.sprockets.google.Places.Field.VICINITY;
import static net.sf.sprockets.google.Places.Field.WEBSITE;
import static net.sf.sprockets.google.Places.Params.RankBy.DISTANCE;
import static net.sf.sprockets.google.Places.Request.AUTOCOMPLETE;
import static net.sf.sprockets.google.Places.Request.DETAILS;
import static net.sf.sprockets.google.Places.Request.NEARBY_SEARCH;
import static net.sf.sprockets.google.Places.Request.PHOTO;
import static net.sf.sprockets.google.Places.Request.QUERY_AUTOCOMPLETE;
import static net.sf.sprockets.google.Places.Request.RADAR_SEARCH;
import static net.sf.sprockets.google.Places.Request.TEXT_SEARCH;
import static net.sf.sprockets.google.Places.Response.Status.INVALID_REQUEST;
import static net.sf.sprockets.google.Places.Response.Status.NOT_MODIFIED;
import static net.sf.sprockets.google.Places.Response.Status.OK;
import static net.sf.sprockets.google.Places.Response.Status.OVER_QUERY_LIMIT;
import static net.sf.sprockets.google.Places.Response.Status.UNKNOWN_ERROR;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import net.sf.sprockets.Sprockets;
import net.sf.sprockets.google.Place.Photo;
import net.sf.sprockets.google.Place.Prediction;
import net.sf.sprockets.google.Places.Params.RankBy;
import net.sf.sprockets.lang.Maths;
import net.sf.sprockets.net.HttpClient;
import net.sf.sprockets.util.concurrent.Interruptibles;
import net.sf.sprockets.util.logging.Loggers;

import org.apache.commons.configuration.Configuration;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.google.common.io.Closeables;
import com.google.gson.stream.JsonReader;

/**
 * Methods for calling <a href="https://developers.google.com/places/" target="_blank">Google Places
 * API</a> services. All methods require a {@link Params Params} instance that defines the places to
 * search for. Most methods also allow you to specify any number of {@link Field Field}s that should
 * be populated in the results, which can reduce execution time and memory allocation when you are
 * not using all of the available fields. When no Fields are specified, all available are populated
 * in the {@link Response Response} results. {@link Params#maxResults(int) Params.maxResults(int)}
 * can be used to similar effect when you will only use a limited number of results.
 * <p>
 * Below is a simple example that prints the names and addresses of fish & chips shops that are
 * within 1 km of Big Ben in London and are currently open.
 * </p>
 * 
 * <pre>{@code
 * Response<List<Place>> resp = Places.nearbySearch(new Params()
 *         .location(51.500702, -0.124576).radius(1000).types("food")
 *         .keyword("fish & chips").openNow(), Field.NAME, Field.VICINITY);
 * Status status = resp.getStatus();
 * List<Place> places = resp.getResult();
 * 
 * if (status == Status.OK && places != null) {
 *     for (Place place : places) {
 *         System.out.println(place.getName() + " - " + place.getVicinity());
 *     }
 * } else if (status == Status.ZERO_RESULTS) {
 *     System.out.println("no results");
 * } else {
 *     System.out.println("error: " + status);
 * }
 * }</pre>
 */
public class Places {
	private static final Logger sLog = Loggers.get(Places.class);

	private Places() {
	}

	/**
	 * Types of requests that can be sent to the Google Places API service.
	 * 
	 * @since 1.0.0
	 */
	public enum Request {
		NEARBY_SEARCH("nearbysearch", true), TEXT_SEARCH("textsearch", true),
		RADAR_SEARCH("radarsearch", false), AUTOCOMPLETE("autocomplete", true),
		QUERY_AUTOCOMPLETE("queryautocomplete", true), DETAILS("details", true),
		PHOTO("photo", false);

		/** Full path to make the request, just add the query parameters. */
		private final String mUrl;

		/** True if the request supports the language parameter. */
		private final boolean mHasLang;

		Request(String path, boolean hasLang) {
			mUrl = "https://maps.googleapis.com/maps/api/place/" + path
					+ (!path.equals("photo") ? "/json?" : "?");
			mHasLang = hasLang;
		}
	}

	/**
	 * Get places that are near a location.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#location(double, double) location}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#radius(int) radius}</li>
	 * <li>{@link Params#name(String) name}</li>
	 * <li>{@link Params#keyword(String) keyword}</li>
	 * <li>{@link Params#types(String...) types}</li>
	 * <li>{@link Params#minPrice(int) minPrice}</li>
	 * <li>{@link Params#maxPrice(int) maxPrice}</li>
	 * <li>{@link Params#openNow() openNow}</li>
	 * <li>{@link Params#language(String) language}</li>
	 * <li>{@link Params#rankBy(RankBy) rankBy}</li>
	 * <li>{@link Params#pageToken(String) pageToken}</li>
	 * <li>{@link Params#filter(Predicate) filter}</li>
	 * <li>{@link Params#maxResults(int) maxResults}</li>
	 * </ul>
	 * <p>
	 * Available fields:
	 * </p>
	 * <ul>
	 * <li>{@link Field#ICON ICON}</li>
	 * <li>{@link Field#GEOMETRY GEOMETRY}</li>
	 * <li>{@link Field#NAME NAME}</li>
	 * <li>{@link Field#VICINITY VICINITY}</li>
	 * <li>{@link Field#TYPES TYPES}</li>
	 * <li>{@link Field#PRICE_LEVEL PRICE_LEVEL}</li>
	 * <li>{@link Field#RATING RATING}</li>
	 * <li>{@link Field#OPEN_NOW OPEN_NOW}</li>
	 * <li>{@link Field#PHOTOS PHOTOS}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 */
	public static Response<List<Place>> nearbySearch(Params params, Field... fields)
			throws IOException {
		return places(NEARBY_SEARCH, params, fields);
	}

	/**
	 * Get places based on a text query, for example "fish & chips in London".
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#query(String) query}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#location(double, double) location}</li>
	 * <li>{@link Params#radius(int) radius}</li>
	 * <li>{@link Params#types(String...) types}</li>
	 * <li>{@link Params#minPrice(int) minPrice}</li>
	 * <li>{@link Params#maxPrice(int) maxPrice}</li>
	 * <li>{@link Params#openNow() openNow}</li>
	 * <li>{@link Params#language(String) language}</li>
	 * <li>{@link Params#pageToken(String) pageToken}</li>
	 * <li>{@link Params#filter(Predicate) filter}</li>
	 * <li>{@link Params#maxResults(int) maxResults}</li>
	 * </ul>
	 * <p>
	 * Available fields:
	 * </p>
	 * <ul>
	 * <li>{@link Field#ICON ICON}</li>
	 * <li>{@link Field#GEOMETRY GEOMETRY}</li>
	 * <li>{@link Field#NAME NAME}</li>
	 * <li>{@link Field#FORMATTED_ADDRESS FORMATTED_ADDRESS}</li>
	 * <li>{@link Field#TYPES TYPES}</li>
	 * <li>{@link Field#PRICE_LEVEL PRICE_LEVEL}</li>
	 * <li>{@link Field#RATING RATING}</li>
	 * <li>{@link Field#OPEN_NOW OPEN_NOW}</li>
	 * <li>{@link Field#PHOTOS PHOTOS}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 */
	public static Response<List<Place>> textSearch(Params params, Field... fields)
			throws IOException {
		return places(TEXT_SEARCH, params, fields);
	}

	/**
	 * Get a large number of place locations for an area.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#location(double, double) location}</li>
	 * <li>At least one of:
	 * <ul>
	 * <li>{@link Params#name(String) name}</li>
	 * <li>{@link Params#keyword(String) keyword}</li>
	 * <li>{@link Params#types(String...) types}</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#radius(int) radius}</li>
	 * <li>{@link Params#minPrice(int) minPrice}</li>
	 * <li>{@link Params#maxPrice(int) maxPrice}</li>
	 * <li>{@link Params#openNow() openNow}</li>
	 * <li>{@link Params#filter(Predicate) filter}</li>
	 * <li>{@link Params#maxResults(int) maxResults}</li>
	 * </ul>
	 * <p>
	 * Available fields:
	 * </p>
	 * <ul>
	 * <li>{@link Field#GEOMETRY GEOMETRY}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 */
	public static Response<List<Place>> radarSearch(Params params, Field... fields)
			throws IOException {
		return places(RADAR_SEARCH, params, fields);
	}

	/**
	 * Get places that match a partial search term.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#query(String) query}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#location(double, double) location}</li>
	 * <li>{@link Params#radius(int) radius}</li>
	 * <li>{@link Params#offset(int) offset}</li>
	 * <li>{@link Params#types(String...) types}
	 * <ul>
	 * <li>One of:</li>
	 * <li>"geocode"</li>
	 * <li>"address"</li>
	 * <li>"establishment"</li>
	 * <li>"(regions)"</li>
	 * <li>"(cities)"</li>
	 * </ul>
	 * </li>
	 * <li>{@link Params#countries(String) countries}</li>
	 * <li>{@link Params#language(String) language}</li>
	 * <li>{@link Params#filter(Predicate) filter}</li>
	 * <li>{@link Params#maxResults(int) maxResults}</li>
	 * </ul>
	 * <p>
	 * Available fields:
	 * </p>
	 * <ul>
	 * <li>{@link Field#NAME NAME}</li>
	 * <li>{@link Field#TYPES TYPES}</li>
	 * <li>{@link Field#TERMS TERMS}</li>
	 * <li>{@link Field#MATCHED_SUBSTRINGS MATCHED_SUBSTRINGS}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 */
	public static Response<List<Prediction>> autocomplete(Params params, Field... fields)
			throws IOException {
		return predictions(AUTOCOMPLETE, params, fields);
	}

	/**
	 * Get suggested queries for a partial search query.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#query(String) query}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#location(double, double) location}</li>
	 * <li>{@link Params#radius(int) radius}</li>
	 * <li>{@link Params#offset(int) offset}</li>
	 * <li>{@link Params#language(String) language}</li>
	 * <li>{@link Params#filter(Predicate) filter}</li>
	 * <li>{@link Params#maxResults(int) maxResults}</li>
	 * </ul>
	 * <p>
	 * Available fields:
	 * </p>
	 * <ul>
	 * <li>{@link Field#NAME NAME}</li>
	 * <li>{@link Field#TYPES TYPES}</li>
	 * <li>{@link Field#TERMS TERMS}</li>
	 * <li>{@link Field#MATCHED_SUBSTRINGS MATCHED_SUBSTRINGS}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 */
	public static Response<List<Prediction>> queryAutocomplete(Params params, Field... fields)
			throws IOException {
		return predictions(QUERY_AUTOCOMPLETE, params, fields);
	}

	/**
	 * Get all data for a place. Normally this will be called after getting a
	 * {@link Place#getPlaceId() Place ID} from the results of a search or autocomplete method. The
	 * {@link Params#maxResults(int) maxResults} parameter can be used to limit the number of
	 * reviews and photos. For example, if maxResults == 1, then at most 1 review and 1 photo will
	 * be returned.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#placeId(String) placeId}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#language(String) language}</li>
	 * <li>{@link Params#maxResults(int) maxResults}</li>
	 * </ul>
	 * <p>
	 * Available fields:
	 * </p>
	 * <ul>
	 * <li>All except:
	 * <ul>
	 * <li>{@link Field#TERMS TERMS}</li>
	 * <li>{@link Field#MATCHED_SUBSTRINGS MATCHED_SUBSTRINGS}</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 */
	public static Response<Place> details(Params params, Field... fields) throws IOException {
		JsonReader in = reader(params.format(DETAILS));
		try {
			return new PlaceResponse(in, Field.bits(fields), params);
		} finally {
			Closeables.close(in, true);
		}
	}

	/**
	 * Download a place photo. Normally this will be called after getting a
	 * {@link Photo#getReference() photo reference} from the results of a search or details method.
	 * Always {@link InputStream#close() close} the stream when finished.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#reference(String) reference}</li>
	 * <li>One or both of:
	 * <ul>
	 * <li>{@link Params#maxWidth(int) maxWidth}</li>
	 * <li>{@link Params#maxHeight(int) maxHeight}</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#etag(String) etag}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 */
	public static Response<InputStream> photo(Params params) throws IOException {
		HttpURLConnection con = HttpClient.openConnection(params.format(PHOTO));
		if (!Strings.isNullOrEmpty(params.mEtag)) {
			con.setRequestProperty("If-None-Match", params.mEtag);
		}
		return new PhotoResponse(con);
	}

	/**
	 * Get places for the request.
	 */
	private static PlacesResponse places(Request type, Params params, Field[] fields)
			throws IOException {
		String url = params.format(type);
		JsonReader in = reader(url);
		try {
			int bits = Field.bits(fields);
			PlacesResponse resp = new PlacesResponse(in, bits, params);
			/* try request again if next page wasn't available yet */
			if (resp.getStatus() == INVALID_REQUEST
					&& (type == NEARBY_SEARCH || type == TEXT_SEARCH)
					&& !Strings.isNullOrEmpty(params.mPageToken)) {
				in.close();
				Interruptibles.sleep(2, SECONDS);
				in = reader(url);
				resp = new PlacesResponse(in, bits, params);
			}
			return resp;
		} finally {
			Closeables.close(in, true);
		}
	}

	/**
	 * Get predictions for the request.
	 */
	private static PredictionsResponse predictions(Request type, Params params, Field[] fields)
			throws IOException {
		JsonReader in = reader(params.format(type));
		try {
			return new PredictionsResponse(in, Field.bits(fields), params);
		} finally {
			Closeables.close(in, true);
		}
	}

	/**
	 * Get a reader for the URL.
	 */
	private static JsonReader reader(String url) throws IOException {
		URLConnection con = HttpClient.openConnection(url);
		return new JsonReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
	}

	/**
	 * <p>
	 * Parameters for Google Places API services. All methods return their instance so that calls
	 * can be chained. For example:
	 * </p>
	 * 
	 * <pre>{@code
	 * Params p = new Params().location(51.500702, -0.124576).radius(1000)
	 *         .types("food").keyword("fish & chips").openNow();
	 * }</pre>
	 */
	public static class Params {
		private String mPlaceId;
		private String mReference;
		private double mLat = Double.NEGATIVE_INFINITY;
		private double mLong = Double.NEGATIVE_INFINITY;
		private int mRadius;
		private String mName;
		private String mKeyword;
		private String mQuery;
		private int mOffset;
		private String[] mTypes;
		private int mMinPrice = -1;
		private int mMaxPrice = -1;
		private boolean mOpen;
		private String[] mCountries;
		private String mLanguage;
		private RankBy mRankBy;
		private String mPageToken;
		private Predicate<Place> mFilter;
		private int mMaxResults;
		private int mMaxWidth;
		private int mMaxHeight;
		private String mEtag;

		/**
		 * Get the place identified by the {@link Place.Id#getId() place ID}, as returned from a
		 * {@link Places} search, autocomplete, or details method.
		 * 
		 * @param placeId
		 *            from {@code Place.getPlaceId().getId()}
		 * @since 1.5.0
		 */
		public Params placeId(String placeId) {
			mPlaceId = placeId;
			return this;
		}

		/**
		 * Get the photo identified by the token, as returned from a {@link Places} search or
		 * details method.
		 */
		public Params reference(String reference) {
			mReference = reference;
			return this;
		}

		/**
		 * Search for places in the area of this latitude and longitude.
		 */
		public Params location(double latitude, double longitude) {
			mLat = latitude;
			mLong = longitude;
			return this;
		}

		/**
		 * Search for places within this many metres from the specified
		 * {@link #location(double, double) location}. Must be between 1 and 50000.
		 */
		public Params radius(int radius) {
			mRadius = radius;
			return this;
		}

		/**
		 * Search for places with this name.
		 */
		public Params name(String name) {
			mName = name;
			return this;
		}

		/**
		 * Search for places with this term in their content.
		 */
		public Params keyword(String keyword) {
			mKeyword = keyword;
			return this;
		}

		/**
		 * Search using this text.
		 */
		public Params query(String query) {
			mQuery = query;
			return this;
		}

		/**
		 * Only use the characters in the search text that are before this zero-based position. For
		 * example, if the search text is 'Sprocke' and the offset is 4, then the search will be
		 * performed with 'Spro'. The offset value is typically the position of the text caret.
		 */
		public Params offset(int offset) {
			mOffset = offset;
			return this;
		}

		/**
		 * Search for places that are at least one of these types. Calling this method multiple
		 * times will append to the list of types to match. Provide null to reset the list.
		 * 
		 * @see <a href="https://developers.google.com/places/supported_types"
		 *      target="_blank">Supported Place Types</a>
		 */
		public Params types(String... types) {
			if (types != null) {
				if (mTypes == null) {
					mTypes = types;
				} else {
					mTypes = ObjectArrays.concat(mTypes, types, String.class);
				}
			} else {
				mTypes = null;
			}
			return this;
		}

		/**
		 * Search for places with this price level or higher. Valid values are from 0 (least
		 * expensive) to 4 (most expensive).
		 */
		public Params minPrice(int minPrice) {
			mMinPrice = minPrice;
			return this;
		}

		/**
		 * Search for places with this price level or lower. Valid values are from 0 (least
		 * expensive) to 4 (most expensive).
		 */
		public Params maxPrice(int maxPrice) {
			mMaxPrice = maxPrice;
			return this;
		}

		/**
		 * Search for places that have specified opening hours and are open right now.
		 */
		public Params openNow() {
			mOpen = true;
			return this;
		}

		/**
		 * Search for places in this country. The value must be a two character ISO 3166-1 Alpha-2
		 * compatible country code, e.g. "GB". Currently only one country parameter is supported,
		 * but if support for multiple country parameters will be added in the future, then this
		 * will become a varargs param.
		 */
		public Params countries(String countries) {
			if (countries != null) {
				if (mCountries == null) {
					mCountries = new String[1];
				}
				mCountries[0] = countries;
			} else {
				mCountries = null;
			}
			return this;
		}

		/**
		 * Return results in this language, if possible. The value must be one of the supported
		 * language codes. If you do not call this method to specify a language, the default locale
		 * will be used.
		 * 
		 * @see <a href="https://developers.google.com/maps/faq#languagesupport"
		 *      target="_blank">Supported Languages</a>
		 */
		public Params language(String language) {
			mLanguage = language;
			return this;
		}

		/**
		 * Options for sorting the results.
		 */
		public enum RankBy {
			/** Sort by importance. */
			PROMINENCE,

			/**
			 * Sort by distance from the specified {@link Params#location(double, double) location}.
			 * When using this option, {@link Params#radius(int) radius} is ignored and one or more
			 * of {@link Params#name(String) name}, {@link Params#keyword(String) keyword}, or
			 * {@link Params#types(String...) types} is required.
			 */
			DISTANCE
		}

		/**
		 * Sort the results by this option. By default, results are sorted by
		 * {@link RankBy#PROMINENCE prominence}.
		 */
		public Params rankBy(RankBy option) {
			mRankBy = option;
			return this;
		}

		/**
		 * Get the next 20 results from a previous search. When this value is set, all other
		 * parameters are ignored.
		 */
		public Params pageToken(String pageToken) {
			mPageToken = pageToken;
			return this;
		}

		/**
		 * Only return places for which the filter returns true. Used only in search and
		 * autocomplete methods.
		 * 
		 * @see Place.Id.Filter
		 * @since 1.4.0
		 */
		public Params filter(Predicate<Place> filter) {
			mFilter = filter;
			return this;
		}

		/**
		 * Return this many results, at most.
		 */
		public Params maxResults(int maxResults) {
			mMaxResults = maxResults;
			return this;
		}

		/**
		 * If necessary, decrease the width of the image to be this many pixels. The original aspect
		 * ratio of the image will be preserved. The value must be between 1 and 1600.
		 */
		public Params maxWidth(int maxWidth) {
			mMaxWidth = maxWidth;
			return this;
		}

		/**
		 * If necessary, decrease the height of the image to be this many pixels. The original
		 * aspect ratio of the image will be preserved. The value must be between 1 and 1600.
		 */
		public Params maxHeight(int maxHeight) {
			mMaxHeight = maxHeight;
			return this;
		}

		/**
		 * Don't return the content if it has this ETag (which means it hasn't changed).
		 */
		public Params etag(String etag) {
			mEtag = etag;
			return this;
		}

		/** Append the types param with pipe symbols between the values. */
		private static Joiner sPipes;

		/**
		 * Get a URL formatted for the type of request.
		 * 
		 * @since 1.0.0
		 */
		public String format(Request type) {
			/* use alternate param names? */
			boolean alt = type == AUTOCOMPLETE || type == QUERY_AUTOCOMPLETE || type == PHOTO;
			StringBuilder s = new StringBuilder(type.mUrl.length() + 256);
			Configuration config = Sprockets.getConfig();
			String key = config.getString("google.api-key");
			checkState(!Strings.isNullOrEmpty(key), "google.api-key not set");
			s.append(type.mUrl).append("key=").append(key);

			if (!Strings.isNullOrEmpty(mPageToken)) {
				return s.append("&pagetoken=").append(mPageToken).toString();
			}
			if (!Strings.isNullOrEmpty(mPlaceId)) {
				s.append("&placeid=").append(mPlaceId);
			} else if (!Strings.isNullOrEmpty(mReference)) {
				s.append(alt ? "&photoreference=" : "&reference=").append(mReference);
			}
			if (mLat > Double.NEGATIVE_INFINITY && mLong > Double.NEGATIVE_INFINITY) {
				s.append("&location=").append(mLat).append(',').append(mLong);
				if (mRankBy != DISTANCE) {
					if (mRadius <= 0) {
						mRadius = 50000;
					}
					s.append("&radius=").append(mRadius);
				}
			}
			try {
				if (!Strings.isNullOrEmpty(mName)) {
					s.append("&name=").append(URLEncoder.encode(mName, "UTF-8"));
				}
				if (!Strings.isNullOrEmpty(mKeyword)) {
					s.append("&keyword=").append(URLEncoder.encode(mKeyword, "UTF-8"));
				}
				if (!Strings.isNullOrEmpty(mQuery)) {
					s.append(alt ? "&input=" : "&query=")
							.append(URLEncoder.encode(mQuery, "UTF-8"));
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("UTF-8 encoding isn't supported?!", e);
			}
			if (mOffset > 0) {
				s.append("&offset=").append(mOffset);
			}
			if (mTypes != null) {
				if (sPipes == null) {
					sPipes = Joiner.on("%7C").skipNulls(); // URL encoded
				}
				sPipes.appendTo(s.append("&types="), mTypes);
			}
			if (mMinPrice >= 0) {
				s.append("&minprice=").append(mMinPrice);
			}
			if (mMaxPrice >= 0) {
				s.append("&maxprice=").append(mMaxPrice);
			}
			if (mOpen) {
				s.append("&opennow");
			}
			if (mCountries != null && mCountries.length > 0) {
				s.append("&components=country:").append(mCountries[0]);
			}
			if (type.mHasLang) {
				s.append("&language=").append(
						!Strings.isNullOrEmpty(mLanguage) ? mLanguage : Locale.getDefault());
			}
			if (mRankBy != null) {
				s.append("&rankby=").append(mRankBy.name().toLowerCase(ENGLISH));
			}
			if (mMaxWidth > 0) {
				s.append("&maxwidth=").append(mMaxWidth);
			}
			if (mMaxHeight > 0) {
				s.append("&maxheight=").append(mMaxHeight);
			}
			return s.toString();
		}

		/**
		 * Clear any set parameters so that this instance can be re-used for a new request.
		 * 
		 * @since 1.0.0
		 */
		public Params clear() {
			mPlaceId = null;
			mReference = null;
			mLat = Double.NEGATIVE_INFINITY;
			mLong = Double.NEGATIVE_INFINITY;
			mRadius = 0;
			mName = null;
			mKeyword = null;
			mQuery = null;
			mOffset = 0;
			mTypes = null;
			mMinPrice = -1;
			mMaxPrice = -1;
			mOpen = false;
			mCountries = null;
			mLanguage = null;
			mRankBy = null;
			mPageToken = null;
			mFilter = null;
			mMaxResults = 0;
			mMaxWidth = 0;
			mMaxHeight = 0;
			mEtag = null;
			return this;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(mPlaceId, mReference, mLat, mLong, mRadius, mName, mKeyword,
					mQuery, mOffset, Arrays.hashCode(mTypes), mMinPrice, mMaxPrice, mOpen,
					Arrays.hashCode(mCountries), mLanguage, mRankBy, mPageToken, mFilter,
					mMaxResults, mMaxWidth, mMaxHeight, mEtag);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null) {
				if (this == obj) {
					return true;
				} else if (obj instanceof Params) {
					Params o = (Params) obj;
					return Objects.equal(mPlaceId, o.mPlaceId)
							&& Objects.equal(mReference, o.mReference) && mLat == o.mLat
							&& mLong == o.mLong && mRadius == o.mRadius
							&& Objects.equal(mName, o.mName) && Objects.equal(mKeyword, o.mKeyword)
							&& Objects.equal(mQuery, o.mQuery) && mOffset == o.mOffset
							&& Objects.equal(mTypes, o.mTypes) && mMinPrice == o.mMinPrice
							&& mMaxPrice == o.mMaxPrice && mOpen == o.mOpen
							&& Objects.equal(mCountries, o.mCountries)
							&& Objects.equal(mLanguage, o.mLanguage) && mRankBy == o.mRankBy
							&& Objects.equal(mPageToken, o.mPageToken)
							&& Objects.equal(mFilter, o.mFilter) && mMaxResults == o.mMaxResults
							&& mMaxWidth == o.mMaxWidth && mMaxHeight == o.mMaxHeight
							&& Objects.equal(mEtag, o.mEtag);
				}
			}
			return false;
		}

		@Override
		public String toString() {
			boolean loc = mLat != Double.NEGATIVE_INFINITY && mLong != Double.NEGATIVE_INFINITY;
			return MoreObjects.toStringHelper(this).add("placeId", mPlaceId)
					.add("reference", mReference).add("location", loc ? mLat + "," + mLong : null)
					.add("radius", mRadius != 0 ? mRadius : null).add("name", mName)
					.add("keyword", mKeyword).add("query", mQuery)
					.add("offset", mOffset != 0 ? mOffset : null)
					.add("types", mTypes != null ? Arrays.toString(mTypes) : null)
					.add("minPrice", mMinPrice != -1 ? mMinPrice : null)
					.add("maxPrice", mMaxPrice != -1 ? mMaxPrice : null)
					.add("openNow", mOpen ? mOpen : null)
					.add("countries", mCountries != null ? Arrays.toString(mCountries) : null)
					.add("language", mLanguage).add("rankBy", mRankBy).add("pageToken", mPageToken)
					.add("filter", mFilter != null ? true : null)
					.add("maxResults", mMaxResults != 0 ? mMaxResults : null)
					.add("maxWidth", mMaxWidth != 0 ? mMaxWidth : null)
					.add("maxHeight", mMaxHeight != 0 ? mMaxHeight : null).add("etag", mEtag)
					.omitNullValues().toString();
		}
	}

	/**
	 * Fields included in the results of {@link Response Response}s.
	 */
	public enum Field {
		/** URL for an icon representing the type of place. */
		ICON,

		/** Google Place page. */
		URL,

		/** Latitude and longitude. */
		GEOMETRY,

		/** Name of the place, for example a business or landmark name. */
		NAME,

		/** List of sections and their offset within the place's name. */
		TERMS,

		/**
		 * List of substrings in the place's name that match the search text, often used for
		 * highlighting.
		 */
		MATCHED_SUBSTRINGS,

		/** All {@link Place.Address Address} components in separate properties. */
		ADDRESS,

		/** String containing all address components. */
		FORMATTED_ADDRESS,

		/** Simplified address string that stops after the city level. */
		VICINITY,

		/** Includes prefixed country code. */
		INTL_PHONE_NUMBER,

		/** In local format. */
		FORMATTED_PHONE_NUMBER,

		/** URL of the website for the place. */
		WEBSITE,

		/**
		 * Features describing the place.
		 * 
		 * @see <a href="https://developers.google.com/places/supported_types"
		 *      target="_blank">Supported Place Types</a>
		 */
		TYPES,

		/** Relative level of average expenses at the place. */
		PRICE_LEVEL,

		/** Based on user reviews. */
		RATING,

		/**
		 * Number of ratings that have been submitted.
		 * 
		 * @since 1.3.0
		 */
		USER_RATINGS_TOTAL,

		/** Comments and ratings from Google users. */
		REVIEWS,

		/** Opening and closing times for each day that the place is open. */
		OPENING_HOURS,

		/**
		 * Opening hours for each day of the week. e.g. ["Monday: 10:00 am – 6:00 pm", ...,
		 * "Sunday: Closed"]
		 * 
		 * @since 2.2.0
		 */
		FORMATTED_OPENING_HOURS,

		/** True if the place is currently open. */
		OPEN_NOW,

		/**
		 * True if the place has permanently shut down.
		 * 
		 * @since 2.2.0
		 */
		PERMANENTLY_CLOSED,

		/**
		 * Current events happening at the place.
		 * 
		 * @deprecated the Places API no longer returns events
		 */
		@Deprecated
		EVENTS,

		/** Number of minutes the place's time zone is offset from UTC. */
		UTC_OFFSET,

		/** Photos for the place that can be downloaded. */
		PHOTOS,

		/**
		 * Only populate the {@link Place#getPlaceId() placeId} and {@link Place#getAltIds() altIds}
		 * properties.
		 * 
		 * @since 2.2.0
		 */
		NONE;

		/** Unique flag bit to denote this Field. */
		private final int mMask;

		Field() {
			mMask = 1 << ordinal(); // will need to upgrade to a long when there are 33 fields
		}

		/**
		 * Get a bit field that represents the fields.
		 */
		private static int bits(Field[] fields) {
			int bits = 0;
			for (Field field : fields) {
				bits |= field.mMask;
			}
			return bits;
		}

		/**
		 * Check if this Field is in the bit field.
		 */
		boolean in(int fields) {
			return (fields & mMask) == mMask;
		}
	}

	/**
	 * Result from one of the {@link Places} methods.
	 * 
	 * @param <T>
	 *            type of result returned in the response
	 */
	public static class Response<T> {
		/** Maximum number of HTML attributions that are expected to be returned. */
		private static final int MAX_ATTRIBS = 3; // usually 1 or 2

		/**
		 * Indications of the success or failure of the request.
		 */
		public enum Status {
			OK, ZERO_RESULTS, OVER_QUERY_LIMIT, REQUEST_DENIED, INVALID_REQUEST, NOT_FOUND,
			UNKNOWN_ERROR, NOT_MODIFIED,

			/** New status that hasn't been added here yet. */
			UNKNOWN;

			/**
			 * Get the matching Status or {@link #UNKNOWN} if one can't be found.
			 */
			private static Status get(String status) {
				try {
					return Status.valueOf(status);
				} catch (IllegalArgumentException e) {
					String msg = "Unknown status code: {0}.  "
							+ "If this hasn''t already been reported, please create a new issue at "
							+ "https://github.com/pushbit/sprockets/issues";
					sLog.log(INFO, msg, status);
					return UNKNOWN;
				}
			}
		}

		Status mStatus;
		String mMessage;
		T mResult;
		List<String> mAttribs;
		String mToken;
		String mEtag;
		private int mHash;

		private Response() {
		}

		/**
		 * Set the {@link Status} from the string value.
		 */
		void status(String status) {
			mStatus = Status.get(status);
		}

		/**
		 * Indication of the success or failure of the request.
		 */
		public Status getStatus() {
			return mStatus;
		}

		/**
		 * Detailed information about why the {@link #getStatus() status} is not {@link Status#OK
		 * OK}.
		 * 
		 * @return null if an error message was not provided
		 * @since 1.4.0
		 */
		public String getErrorMessage() {
			return mMessage;
		}

		/**
		 * Check the {@link Places} method signature for the specific type of result it returns. Can
		 * be null if there was a problem with the request or an {@link Params#etag(String) ETag}
		 * was sent and the content has not changed on the server.
		 */
		@SuppressWarnings("unchecked")
		public T getResult() {
			if (mResult instanceof List && !(mResult instanceof ImmutableList)) {
				mResult = (T) ImmutableList.copyOf((List<?>) mResult);
			}
			return mResult;
		}

		/**
		 * Read the HTML attributions.
		 */
		void attrib(JsonReader in) throws IOException {
			in.beginArray();
			while (in.hasNext()) {
				if (mAttribs == null) {
					mAttribs = new ArrayList<String>(MAX_ATTRIBS);
				}
				mAttribs.add(in.nextString());
			}
			in.endArray();
		}

		/**
		 * Attributions for this result that must be displayed to the user if non-null.
		 */
		public List<String> getHtmlAttributions() {
			if (mAttribs != null && !(mAttribs instanceof ImmutableList)) {
				mAttribs = ImmutableList.copyOf(mAttribs);
			}
			return mAttribs;
		}

		/**
		 * If non-null, can be provided to {@link Params#pageToken(String) Params.pageToken(String)}
		 * in another request to get the next 20 results.
		 */
		public String getNextPageToken() {
			return mToken;
		}

		/**
		 * Identifier for this version of the content. If non-null, can be provided to
		 * {@link Params#etag(String) Params.etag(String)} in future requests to avoid downloading
		 * the same content again if it hasn't changed on the server. Currently only populated for
		 * {@link Places#photo(Params)} requests.
		 */
		public String getEtag() {
			return mEtag;
		}

		@Override
		public int hashCode() {
			if (mHash == 0) {
				mHash = Objects.hashCode(mStatus, mMessage, mResult, mAttribs, mToken, mEtag);
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
					return mStatus == o.mStatus && Objects.equal(mMessage, o.mMessage)
							&& Objects.equal(mResult, o.mResult)
							&& Objects.equal(mAttribs, o.mAttribs)
							&& Objects.equal(mToken, o.mToken) && Objects.equal(mEtag, o.mEtag);
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("status", mStatus)
					.add("errorMessage", mMessage).add("result", mResult)
					.add("htmlAttributions", mAttribs != null ? mAttribs.size() : null)
					.add("nextPageToken", mToken).add("etag", mEtag).omitNullValues().toString();
		}

		/**
		 * All known response field keys and {@link #UNKNOWN} for new keys not included here yet.
		 */
		enum Key {
			status, error_message, results, html_attributions, next_page_token, predictions,
			result, place_id, scope, alt_ids, id, reference, icon(ICON), url(URL),
			geometry(GEOMETRY), location, lat, lng, viewport, name(NAME), description(NAME),
			terms(TERMS), offset, value, matched_substrings(MATCHED_SUBSTRINGS), length,
			address_components(ADDRESS), long_name, short_name, adr_address,
			formatted_address(FORMATTED_ADDRESS), vicinity(VICINITY),
			international_phone_number(INTL_PHONE_NUMBER),
			formatted_phone_number(FORMATTED_PHONE_NUMBER), website(WEBSITE), types(TYPES),
			price_level(PRICE_LEVEL), rating(RATING), user_ratings_total(USER_RATINGS_TOTAL),
			reviews(REVIEWS), author_name, author_url, time, aspects, type, language, text,
			opening_hours(OPENING_HOURS), open_now(OPEN_NOW), periods, open, close, day,
			weekday_text(FORMATTED_OPENING_HOURS), permanently_closed(PERMANENTLY_CLOSED),
			events(EVENTS), event_id, start_time, summary, utc_offset(UTC_OFFSET), photos(PHOTOS),
			photo_reference, width, height, debug_info,

			/** New key that hasn't been added here yet. */
			UNKNOWN;

			/** Related Field. */
			final Field mField;

			/**
			 * Key without a related Field.
			 */
			Key() {
				mField = null;
			}

			/**
			 * Key with a related Field.
			 */
			Key(Field field) {
				mField = field;
			}

			/**
			 * Get the matching Key or {@link #UNKNOWN} if one can't be found.
			 */
			static Key get(String key) {
				try {
					return Key.valueOf(key);
				} catch (IllegalArgumentException e) {
					String msg = "Unknown response key: {0}.  "
							+ "If this hasn''t already been reported, please create a new issue at "
							+ "https://github.com/pushbit/sprockets/issues";
					sLog.log(INFO, msg, key);
					return UNKNOWN;
				}
			}
		}
	}

	/**
	 * Place search results.
	 */
	private static class PlacesResponse extends Response<List<Place>> {
		/** Maximum number of establishment results that will be returned. */
		private static final int MAX_RESULTS = 20;

		/** Maximum number of reviews, events, and photos that will be returned. */
		private static final int MAX_OBJECTS = 3;

		/**
		 * Read fields from a search response.
		 * 
		 * @param fields
		 *            to read or 0 if all fields should be read
		 */
		private PlacesResponse(JsonReader in, int fields, Params params) throws IOException {
			in.beginObject();
			while (in.hasNext()) {
				switch (Key.get(in.nextName())) {
				case status:
					status(in.nextString());
					break;
				case error_message:
					mMessage = in.nextString();
					break;
				case results:
					in.beginArray();
					while (in.hasNext()) {
						if (mResult == null) {
							int cap = Maths.clamp(params.mMaxResults, 0, MAX_RESULTS);
							mResult = new ArrayList<Place>(cap > 0 ? cap : MAX_RESULTS);
						}
						if (params.mMaxResults <= 0 || mResult.size() < params.mMaxResults) {
							Place place = new Place(in, fields, MAX_OBJECTS);
							if (params.mFilter == null || params.mFilter.apply(place)) {
								mResult.add(place);
							}
						} else {
							in.skipValue();
						}
					}
					in.endArray();
					break;
				case html_attributions:
					attrib(in);
					break;
				case next_page_token:
					mToken = in.nextString();
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
		}
	}

	/**
	 * Autocomplete search results.
	 */
	private static class PredictionsResponse extends Response<List<Prediction>> {
		/** Maximum number of results that will be returned. */
		private static final int MAX_RESULTS = 5;

		/**
		 * Read fields from an autocomplete response.
		 * 
		 * @param fields
		 *            to read or 0 if all fields should be read
		 */
		private PredictionsResponse(JsonReader in, int fields, Params params) throws IOException {
			in.beginObject();
			while (in.hasNext()) {
				switch (Key.get(in.nextName())) {
				case status:
					status(in.nextString());
					break;
				case error_message:
					mMessage = in.nextString();
					break;
				case predictions:
					in.beginArray();
					while (in.hasNext()) {
						if (mResult == null) {
							int cap = Maths.clamp(params.mMaxResults, 0, MAX_RESULTS);
							mResult = new ArrayList<Prediction>(cap > 0 ? cap : MAX_RESULTS);
						}
						if (params.mMaxResults <= 0 || mResult.size() < params.mMaxResults) {
							Prediction pred = new Prediction(in, fields);
							if (params.mFilter == null || params.mFilter.apply(pred)) {
								mResult.add(pred);
							}
						} else {
							in.skipValue();
						}
					}
					in.endArray();
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
		}
	}

	/**
	 * Place with full details.
	 */
	private static class PlaceResponse extends Response<Place> {
		/**
		 * Read fields from a details response.
		 * 
		 * @param fields
		 *            to read or 0 if all fields should be read
		 */
		private PlaceResponse(JsonReader in, int fields, Params params) throws IOException {
			in.beginObject();
			while (in.hasNext()) {
				switch (Key.get(in.nextName())) {
				case status:
					status(in.nextString());
					break;
				case error_message:
					mMessage = in.nextString();
					break;
				case result:
					mResult = new Place(in, fields, params.mMaxResults);
					break;
				case html_attributions:
					attrib(in);
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
		}
	}

	/**
	 * Photo bitstream for reading. Always {@link InputStream#close() close} the stream when
	 * finished.
	 */
	private static class PhotoResponse extends Response<InputStream> {
		/**
		 * Get the ETag and InputStream from the connection response.
		 */
		private PhotoResponse(HttpURLConnection con) throws IOException {
			switch (con.getResponseCode()) {
			case HTTP_OK:
				mStatus = OK;
				mEtag = con.getHeaderField("ETag");
				mResult = con.getInputStream();
				break;
			case HTTP_NOT_MODIFIED:
				mStatus = NOT_MODIFIED;
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
