/*
 * Copyright 2013-2016 pushbit <pushbit@gmail.com>
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
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.sf.sprockets.google.Places.Response.STATUS_INVALID_REQUEST;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import net.sf.sprockets.Sprockets;
import net.sf.sprockets.google.Place.Id;
import net.sf.sprockets.google.Place.Photo;
import net.sf.sprockets.google.Place.Prediction;
import net.sf.sprockets.lang.ImmutableSubstring;
import net.sf.sprockets.net.HttpClient;
import net.sf.sprockets.net.Urls;
import net.sf.sprockets.util.concurrent.Interruptibles;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Modifiable;
import org.immutables.value.Value.Style;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.io.Closeables;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Methods for calling <a href="https://developers.google.com/places/webservice/"
 * target="_blank">Google Places API</a> services. All methods accept {@link Params Params} which
 * define the places to search for, the photo to download, or the place to add or delete. Most
 * search methods also have a version that allows you to specify the fields that should be populated
 * in the results, which can reduce execution time and memory allocation when you are not using all
 * of the available fields. {@link Params#maxResults() Params.maxResults} can be used to similar
 * effect when you will only use a limited number of results.
 * <p>
 * Below is a simple example that prints the names and addresses of fish & chips shops that are
 * within 1 km of Big Ben in London and are currently open.
 * </p>
 * 
 * <pre>{@code
 * Response<List<Place>> resp = Places.nearbySearch(Params.create()
 *         .latitude(51.500702).longitude(-0.124576).radius(1000)
 *         .type("restaurant").keyword("fish & chips").openNow(true),
 *         FIELD_NAME | FIELD_VICINITY);
 * 
 * String status = resp.getStatus();
 * List<Place> places = resp.getResult();
 * 
 * if (STATUS_OK.equals(status)) {
 *     for (Place place : places) {
 *         System.out.println(place.getName() + " @ " + place.getVicinity());
 *     }
 * } else if (STATUS_ZERO_RESULTS.equals(status)) {
 *     System.out.println("no results");
 * } else {
 *     System.out.println("error: " + status);
 * }
 * }</pre>
 */
public class Places {
	public static final String URL_NEARBY_SEARCH =
			"https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	public static final String URL_TEXT_SEARCH =
			"https://maps.googleapis.com/maps/api/place/textsearch/json?";
	public static final String URL_RADAR_SEARCH =
			"https://maps.googleapis.com/maps/api/place/radarsearch/json?";
	public static final String URL_AUTOCOMPLETE =
			"https://maps.googleapis.com/maps/api/place/autocomplete/json?";
	public static final String URL_QUERY_AUTOCOMPLETE =
			"https://maps.googleapis.com/maps/api/place/queryautocomplete/json?";
	public static final String URL_DETAILS =
			"https://maps.googleapis.com/maps/api/place/details/json?";
	public static final String URL_PHOTO = "https://maps.googleapis.com/maps/api/place/photo?";
	public static final String URL_ADD = "https://maps.googleapis.com/maps/api/place/add/json?";
	public static final String URL_DELETE =
			"https://maps.googleapis.com/maps/api/place/delete/json?";

	/**
	 * Only populate the {@link Place#getPlaceId() placeId}, {@link Place#getAltIds() altIds}, and
	 * primitive properties.
	 */
	public static final int FIELD_NONE = 1 << 0;

	/** URL for an icon representing the type of place. */
	public static final int FIELD_ICON = 1 << 1;

	/** Google Place page. */
	public static final int FIELD_URL = 1 << 2;

	/** Name of the place, for example a business or landmark name. */
	public static final int FIELD_NAME = 1 << 3;

	/** All {@link Place.Address Address} components in separate properties. */
	public static final int FIELD_ADDRESS = 1 << 4;

	/** String containing all address components. */
	public static final int FIELD_FORMATTED_ADDRESS = 1 << 5;

	/** Simplified address string that stops after the city level. */
	public static final int FIELD_VICINITY = 1 << 6;

	/** Includes prefixed country code. */
	public static final int FIELD_INTL_PHONE_NUMBER = 1 << 7;

	/** In local format. */
	public static final int FIELD_FORMATTED_PHONE_NUMBER = 1 << 8;

	/** URL of the website for the place. */
	public static final int FIELD_WEBSITE = 1 << 9;

	/**
	 * Features describing the place.
	 * 
	 * @see <a href="https://developers.google.com/places/supported_types" target="_blank">Place
	 *      Types</a>
	 */
	public static final int FIELD_TYPES = 1 << 10;

	/** Comments and ratings from Google users. */
	public static final int FIELD_REVIEWS = 1 << 11;

	/** Opening and closing times for each day that the place is open. */
	public static final int FIELD_OPENING_HOURS = 1 << 12;

	/**
	 * Opening hours for each day of the week. e.g. ["Monday: 10:00 am – 6:00 pm", ...,
	 * "Sunday: Closed"]
	 */
	public static final int FIELD_FORMATTED_OPENING_HOURS = 1 << 13;

	/** Photos for the place that can be downloaded. */
	public static final int FIELD_PHOTOS = 1 << 14;

	/** Name of the place or a query suggestion. */
	public static final int FIELD_DESCRIPTION = 1 << 15;

	/** List of sections and their offset within the place's name. */
	public static final int FIELD_TERMS = 1 << 16;

	/**
	 * List of substrings in the place's name that match the search text, often used for
	 * highlighting.
	 */
	public static final int FIELD_MATCHED_SUBSTRINGS = 1 << 17;

	private Places() {
	}

	/**
	 * Get places that are near a location.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#latitude() latitude}</li>
	 * <li>{@link Params#longitude() longitude}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#radius() radius}</li>
	 * <li>{@link Params#name() name}</li>
	 * <li>{@link Params#keyword() keyword}</li>
	 * <li>{@link Params#type() type}</li>
	 * <li>{@link Params#minPrice() minPrice}</li>
	 * <li>{@link Params#maxPrice() maxPrice}</li>
	 * <li>{@link Params#openNow() openNow}</li>
	 * <li>{@link Params#language() language}</li>
	 * <li>{@link Params#rankBy() rankBy}</li>
	 * <li>{@link Params#pageToken() pageToken}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href="https://developers.google.com/places/web-service/search#PlaceSearchRequests"
	 *      target="_blank">Nearby Search Requests</a>
	 */
	public static Response<List<Place>> nearbySearch(Params params) throws IOException {
		return nearbySearch(params, 0);
	}

	/**
	 * Get places that are near a location.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#latitude() latitude}</li>
	 * <li>{@link Params#longitude() longitude}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#radius() radius}</li>
	 * <li>{@link Params#name() name}</li>
	 * <li>{@link Params#keyword() keyword}</li>
	 * <li>{@link Params#type() type}</li>
	 * <li>{@link Params#minPrice() minPrice}</li>
	 * <li>{@link Params#maxPrice() maxPrice}</li>
	 * <li>{@link Params#openNow() openNow}</li>
	 * <li>{@link Params#language() language}</li>
	 * <li>{@link Params#rankBy() rankBy}</li>
	 * <li>{@link Params#pageToken() pageToken}</li>
	 * </ul>
	 * <p>
	 * Available fields:
	 * </p>
	 * <ul>
	 * <li>{@link #FIELD_ICON}</li>
	 * <li>{@link #FIELD_NAME}</li>
	 * <li>{@link #FIELD_VICINITY}</li>
	 * <li>{@link #FIELD_TYPES}</li>
	 * <li>{@link #FIELD_PHOTOS}</li>
	 * </ul>
	 * 
	 * @param fields
	 *            FIELD_* bitmask of the fields to populate in the results
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href="https://developers.google.com/places/web-service/search#PlaceSearchRequests"
	 *      target="_blank">Nearby Search Requests</a>
	 * @since 3.0.0
	 */
	public static Response<List<Place>> nearbySearch(Params params, int fields) throws IOException {
		return places(URL_NEARBY_SEARCH, params, fields);
	}

	/**
	 * Get places based on a text query, for example "fish & chips in London".
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#query() query}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#latitude() latitude}</li>
	 * <li>{@link Params#longitude() longitude}</li>
	 * <li>{@link Params#radius() radius}</li>
	 * <li>{@link Params#type() type}</li>
	 * <li>{@link Params#minPrice() minPrice}</li>
	 * <li>{@link Params#maxPrice() maxPrice}</li>
	 * <li>{@link Params#openNow() openNow}</li>
	 * <li>{@link Params#language() language}</li>
	 * <li>{@link Params#pageToken() pageToken}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href="https://developers.google.com/places/web-service/search#TextSearchRequests"
	 *      target="_blank">Text Search Requests</a>
	 */
	public static Response<List<Place>> textSearch(Params params) throws IOException {
		return textSearch(params, 0);
	}

	/**
	 * Get places based on a text query, for example "fish & chips in London".
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#query() query}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#latitude() latitude}</li>
	 * <li>{@link Params#longitude() longitude}</li>
	 * <li>{@link Params#radius() radius}</li>
	 * <li>{@link Params#type() type}</li>
	 * <li>{@link Params#minPrice() minPrice}</li>
	 * <li>{@link Params#maxPrice() maxPrice}</li>
	 * <li>{@link Params#openNow() openNow}</li>
	 * <li>{@link Params#language() language}</li>
	 * <li>{@link Params#pageToken() pageToken}</li>
	 * </ul>
	 * <p>
	 * Available fields:
	 * </p>
	 * <ul>
	 * <li>{@link #FIELD_ICON}</li>
	 * <li>{@link #FIELD_NAME}</li>
	 * <li>{@link #FIELD_FORMATTED_ADDRESS}</li>
	 * <li>{@link #FIELD_TYPES}</li>
	 * <li>{@link #FIELD_PHOTOS}</li>
	 * </ul>
	 * 
	 * @param fields
	 *            FIELD_* bitmask of the fields to populate in the results
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href="https://developers.google.com/places/web-service/search#TextSearchRequests"
	 *      target="_blank">Text Search Requests</a>
	 * @since 3.0.0
	 */
	public static Response<List<Place>> textSearch(Params params, int fields) throws IOException {
		return places(URL_TEXT_SEARCH, params, fields);
	}

	/**
	 * Get a large number of place locations for an area.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#latitude() latitude}</li>
	 * <li>{@link Params#longitude() longitude}</li>
	 * <li>At least one of:
	 * <ul>
	 * <li>{@link Params#name() name}</li>
	 * <li>{@link Params#keyword() keyword}</li>
	 * <li>{@link Params#type() type}</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#radius() radius}</li>
	 * <li>{@link Params#minPrice() minPrice}</li>
	 * <li>{@link Params#maxPrice() maxPrice}</li>
	 * <li>{@link Params#openNow() openNow}</li>
	 * <li>{@link Params#language() language}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href="https://developers.google.com/places/web-service/search#RadarSearchRequests"
	 *      target="_blank">Radar Search Requests</a>
	 */
	public static Response<List<Place>> radarSearch(Params params) throws IOException {
		return places(URL_RADAR_SEARCH, params, 0);
	}

	/**
	 * Get places that match a partial search term.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#query() query}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#latitude() latitude}</li>
	 * <li>{@link Params#longitude() longitude}</li>
	 * <li>{@link Params#radius() radius}</li>
	 * <li>{@link Params#offset() offset}</li>
	 * <li>{@link Params#type() type}
	 * <ul>
	 * <li>"geocode"</li>
	 * <li>"address"</li>
	 * <li>"establishment"</li>
	 * <li>"(regions)"</li>
	 * <li>"(cities)"</li>
	 * </ul>
	 * </li>
	 * <li>{@link Params#countries() countries}</li>
	 * <li>{@link Params#language() language}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href=
	 *      "https://developers.google.com/places/web-service/autocomplete#place_autocomplete_requests"
	 *      target="_blank">Place Autocomplete Requests</a>
	 */
	public static Response<List<Prediction>> autocomplete(Params params) throws IOException {
		return autocomplete(params, 0);
	}

	/**
	 * Get places that match a partial search term.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#query() query}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#latitude() latitude}</li>
	 * <li>{@link Params#longitude() longitude}</li>
	 * <li>{@link Params#radius() radius}</li>
	 * <li>{@link Params#offset() offset}</li>
	 * <li>{@link Params#type() type}
	 * <ul>
	 * <li>"geocode"</li>
	 * <li>"address"</li>
	 * <li>"establishment"</li>
	 * <li>"(regions)"</li>
	 * <li>"(cities)"</li>
	 * </ul>
	 * </li>
	 * <li>{@link Params#countries() countries}</li>
	 * <li>{@link Params#language() language}</li>
	 * </ul>
	 * <p>
	 * Available fields:
	 * </p>
	 * <ul>
	 * <li>{@link #FIELD_DESCRIPTION}</li>
	 * <li>{@link #FIELD_TYPES}</li>
	 * <li>{@link #FIELD_TERMS}</li>
	 * <li>{@link #FIELD_MATCHED_SUBSTRINGS}</li>
	 * </ul>
	 * 
	 * @param fields
	 *            FIELD_* bitmask of the fields to populate in the results
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href=
	 *      "https://developers.google.com/places/web-service/autocomplete#place_autocomplete_requests"
	 *      target="_blank">Place Autocomplete Requests</a>
	 * @since 3.0.0
	 */
	public static Response<List<Prediction>> autocomplete(Params params, int fields)
			throws IOException {
		return predictions(URL_AUTOCOMPLETE, params, fields);
	}

	/**
	 * Get suggested queries for a partial search query.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#query() query}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#latitude() latitude}</li>
	 * <li>{@link Params#longitude() longitude}</li>
	 * <li>{@link Params#radius() radius}</li>
	 * <li>{@link Params#offset() offset}</li>
	 * <li>{@link Params#language() language}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href=
	 *      "https://developers.google.com/places/web-service/query#query_autocomplete_requests"
	 *      target="_blank">Query Autocomplete Requests</a>
	 */
	public static Response<List<Prediction>> queryAutocomplete(Params params) throws IOException {
		return queryAutocomplete(params, 0);
	}

	/**
	 * Get suggested queries for a partial search query.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#query() query}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#latitude() latitude}</li>
	 * <li>{@link Params#longitude() longitude}</li>
	 * <li>{@link Params#radius() radius}</li>
	 * <li>{@link Params#offset() offset}</li>
	 * <li>{@link Params#language() language}</li>
	 * </ul>
	 * <p>
	 * Available fields:
	 * </p>
	 * <ul>
	 * <li>{@link #FIELD_NAME}</li>
	 * <li>{@link #FIELD_TYPES}</li>
	 * <li>{@link #FIELD_TERMS}</li>
	 * <li>{@link #FIELD_MATCHED_SUBSTRINGS}</li>
	 * </ul>
	 * 
	 * @param fields
	 *            FIELD_* bitmask of the fields to populate in the results
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href=
	 *      "https://developers.google.com/places/web-service/query#query_autocomplete_requests"
	 *      target="_blank">Query Autocomplete Requests</a>
	 * @since 3.0.0
	 */
	public static Response<List<Prediction>> queryAutocomplete(Params params, int fields)
			throws IOException {
		return predictions(URL_QUERY_AUTOCOMPLETE, params, fields);
	}

	/**
	 * Get all data for a place. Normally this will be called after getting a
	 * {@link Place#getPlaceId() place ID} from the results of a search or autocomplete method. The
	 * {@link Params#maxResults() maxResults} parameter can be used to limit the number of reviews
	 * and photos. For example, if maxResults == 1, then at most 1 review and 1 photo will be
	 * returned.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#placeId() placeId}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#language() language}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href="https://developers.google.com/places/web-service/details#PlaceDetailsRequests"
	 *      target="_blank">Place Details Requests</a>
	 */
	public static Response<Place> details(Params params) throws IOException {
		return details(params, 0);
	}

	/**
	 * Get all data for a place. Normally this will be called after getting a
	 * {@link Place#getPlaceId() place ID} from the results of a search or autocomplete method. The
	 * {@link Params#maxResults() maxResults} parameter can be used to limit the number of reviews
	 * and photos. For example, if maxResults == 1, then at most 1 review and 1 photo will be
	 * returned.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#placeId() placeId}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#language() language}</li>
	 * </ul>
	 * <p>
	 * Available fields:
	 * </p>
	 * <ul>
	 * <li>All except:
	 * <ul>
	 * <li>{@link #FIELD_DESCRIPTION}</li>
	 * <li>{@link #FIELD_TERMS}</li>
	 * <li>{@link #FIELD_MATCHED_SUBSTRINGS}</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param fields
	 *            FIELD_* bitmask of the fields to populate in the result
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href="https://developers.google.com/places/web-service/details#PlaceDetailsRequests"
	 *      target="_blank">Place Details Requests</a>
	 * @since 3.0.0
	 */
	public static Response<Place> details(Params params, int fields) throws IOException {
		JsonReader in = reader(params.format(URL_DETAILS));
		try {
			return PlaceResponse.from(in, fields, params);
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
	 * <li>{@link Params#reference() reference}</li>
	 * <li>One or both of:
	 * <ul>
	 * <li>{@link Params#maxWidth() maxWidth}</li>
	 * <li>{@link Params#maxHeight() maxHeight}</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#etag() etag}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href="https://developers.google.com/places/web-service/photos#place_photo_requests"
	 *      target="_blank">Place Photo Requests</a>
	 */
	public static Response<InputStream> photo(Params params) throws IOException {
		HttpURLConnection con = HttpClient.openConnection(params.format(URL_PHOTO));
		String etag = params.etag();
		if (!Strings.isNullOrEmpty(etag)) {
			con.setRequestProperty("If-None-Match", etag);
		}
		return PhotoResponse.from(con);
	}

	/**
	 * Add the place to Google Maps.
	 * <p>
	 * Required properties:
	 * </p>
	 * <ul>
	 * <li>{@link Place#getLatitude() latitude}</li>
	 * <li>{@link Place#getLongitude() longitude}</li>
	 * <li>{@link Place#getName() name} (max. 255 characters)</li>
	 * <li>{@link Place#getTypes() types} (max. 1 value)</li>
	 * </ul>
	 * <p>
	 * Recommended properties:
	 * </p>
	 * <ul>
	 * <li>{@link Place#getFormattedAddress() formattedAddress}</li>
	 * <li>One of:
	 * <ul>
	 * <li>{@link Place#getFormattedPhoneNumber() formattedPhoneNumber}</li>
	 * <li>{@link Place#getIntlPhoneNumber() intlPhoneNumber}</li>
	 * </ul>
	 * </li>
	 * <li>{@link Place#getWebsite() website}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href="https://developers.google.com/places/web-service/add-place#add-place"
	 *      target="_blank">Add a place</a>
	 * @since 3.0.0
	 */
	public static Response<Id> add(Place place) throws IOException {
		return add(place, Params.create());
	}

	/**
	 * Add the place to Google Maps.
	 * <p>
	 * Required properties:
	 * </p>
	 * <ul>
	 * <li>{@link Place#getLatitude() latitude}</li>
	 * <li>{@link Place#getLongitude() longitude}</li>
	 * <li>{@link Place#getName() name} (max. 255 characters)</li>
	 * <li>{@link Place#getTypes() types} (exactly 1 value)</li>
	 * </ul>
	 * <p>
	 * Recommended properties:
	 * </p>
	 * <ul>
	 * <li>{@link Place#getFormattedAddress() formattedAddress}</li>
	 * <li>One of:
	 * <ul>
	 * <li>{@link Place#getFormattedPhoneNumber() formattedPhoneNumber}</li>
	 * <li>{@link Place#getIntlPhoneNumber() intlPhoneNumber}</li>
	 * </ul>
	 * </li>
	 * <li>{@link Place#getWebsite() website}</li>
	 * </ul>
	 * <p>
	 * Optional params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#language() language}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href="https://developers.google.com/places/web-service/add-place#add-place"
	 *      target="_blank">Add a place</a>
	 * @since 3.0.0
	 */
	public static Response<Id> add(Place place, Params params) throws IOException {
		URLConnection con = HttpClient.openConnection(params.format(URL_ADD));
		JsonWriter out = writer(con);
		JsonReader in = null;
		try {
			out.beginObject();
			out.name("location").beginObject().name("lat").value(place.getLatitude())
					.name("lng").value(place.getLongitude()).endObject();
			out.name("name").value(place.getName());
			out.name("types").beginArray().value(place.getTypes().get(0)).endArray();
			String address = place.getFormattedAddress();
			if (!Strings.isNullOrEmpty(address)) {
				out.name("address").value(address);
			}
			String phone = place.getFormattedPhoneNumber();
			if (Strings.isNullOrEmpty(phone)) {
				phone = place.getIntlPhoneNumber();
			}
			if (!Strings.isNullOrEmpty(phone)) {
				out.name("phone_number").value(phone);
			}
			String website = place.getWebsite();
			if (!Strings.isNullOrEmpty(website)) {
				out.name("website").value(Urls.addHttp(website));
			}
			String lang = params.language();
			if (!Strings.isNullOrEmpty(lang)) {
				out.name("language").value(lang);
			}
			out.endObject().close();
			out = null; // don't try to close it again
			in = reader(con);
			return PlaceIdResponse.from(in);
		} finally {
			Closeables.close(out, true);
			Closeables.close(in, true);
		}
	}

	/**
	 * Delete a place from Google Maps that you previously added. The place must not have already
	 * been moderated and officially added to Google Maps.
	 * <p>
	 * Required params:
	 * </p>
	 * <ul>
	 * <li>{@link Params#placeId() placeId}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             if there is a problem communicating with the Google Places API service
	 * @see <a href="https://developers.google.com/places/web-service/add-place#delete-place"
	 *      target="_blank">Delete a place</a>
	 * @since 3.0.0
	 */
	public static Response<Void> delete(Params params) throws IOException {
		URLConnection con = HttpClient.openConnection(params.format(URL_DELETE));
		JsonWriter out = writer(con);
		JsonReader in = null;
		try {
			out.beginObject().name("place_id").value(params.placeId()).endObject().close();
			out = null; // don't try to close it again
			in = reader(con);
			return VoidResponse.from(in);
		} finally {
			Closeables.close(out, true);
			Closeables.close(in, true);
		}
	}

	private static PlacesResponse places(String baseUrl, Params params, int fields)
			throws IOException {
		String url = params.format(baseUrl);
		JsonReader in = reader(url);
		try {
			PlacesResponse resp = PlacesResponse.from(in, fields, params);
			/* try request again if next page wasn't available yet */
			if (resp.getStatus().equals(STATUS_INVALID_REQUEST)
					&& (baseUrl.equals(URL_NEARBY_SEARCH) || baseUrl.equals(URL_TEXT_SEARCH))
					&& !Strings.isNullOrEmpty(params.pageToken())) {
				in.close();
				Interruptibles.sleep(2, SECONDS);
				in = reader(url);
				resp = PlacesResponse.from(in, fields, params);
			}
			return resp;
		} finally {
			Closeables.close(in, true);
		}
	}

	private static PredictionsResponse predictions(String baseUrl, Params params, int fields)
			throws IOException {
		JsonReader in = reader(params.format(baseUrl));
		try {
			return PredictionsResponse.from(in, fields, params);
		} finally {
			Closeables.close(in, true);
		}
	}

	private static JsonWriter writer(URLConnection con) throws IOException {
		con.setDoOutput(true);
		return new JsonWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
	}

	private static JsonReader reader(String url) throws IOException {
		return reader(HttpClient.openConnection(url));
	}

	private static JsonReader reader(URLConnection con) throws IOException {
		return new JsonReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
	}

	/**
	 * <p>
	 * Parameters for Google Places API services. Example usage:
	 * </p>
	 * 
	 * <pre>{@code
	 * Params.create().latitude(51.500702).longitude(-0.124576).radius(1000)
	 *         .type("restaurant").keyword("fish & chips").openNow(true);
	 * }</pre>
	 */
	@Modifiable
	@Style(typeModifiable = "Places*", create = "new", get = "*", set = "*")
	public static abstract class Params {
		Params() {
		}

		/**
		 * Mutable instance where values can be set.
		 * 
		 * @since 3.0.0
		 */
		public static PlacesParams create() {
			return new PlacesParams();
		}

		/**
		 * {@link Place.Id#getId() Place ID} of the place to get, as returned from a {@link Places}
		 * search, autocomplete, or details method.
		 * 
		 * @since 1.5.0
		 */
		@Nullable
		public abstract String placeId();

		/**
		 * Token for the photo to get, as returned from a {@link Places} search or details method.
		 */
		@Nullable
		public abstract String reference();

		/**
		 * Used with {@link #longitude() longitude} and {@link #radius() radius} to define the
		 * search area for places.
		 * 
		 * @since 3.0.0
		 */
		@Default
		public double latitude() {
			return Double.NEGATIVE_INFINITY;
		}

		/**
		 * Used with {@link #latitude() latitude} and {@link #radius() radius} to define the search
		 * area for places.
		 * 
		 * @since 3.0.0
		 */
		@Default
		public double longitude() {
			return Double.NEGATIVE_INFINITY;
		}

		/**
		 * Used with {@link #latitude() latitude} and {@link #longitude() longitude} to define the
		 * search area for places. Must be between 1 and 50000 metres. Default value: 50000.
		 */
		@Default
		public int radius() {
			return 50000;
		}

		/**
		 * Name of places to search for.
		 */
		@Nullable
		public abstract String name();

		/**
		 * Term to search for in places content.
		 */
		@Nullable
		public abstract String keyword();

		/**
		 * Text to search for.
		 */
		@Nullable
		public abstract String query();

		/**
		 * Zero-based position at which the previous characters in the search text should be used.
		 * For example, if the search text is 'Sprocke' and the offset is 4, then the search will be
		 * performed with 'Spro'. The offset value is typically the position of the text caret.
		 */
		@Default
		public int offset() {
			return 0;
		}

		/**
		 * Type of places to search for.
		 * 
		 * @see <a href="https://developers.google.com/places/supported_types" target="_blank">Place
		 *      Types</a>
		 * @since 3.1.0
		 */
		@Nullable
		public abstract String type();

		/**
		 * Types of places to search for.
		 * 
		 * @see <a href="https://developers.google.com/places/supported_types" target="_blank">Place
		 *      Types</a>
		 * @deprecated use {@link #type()} instead
		 */
		@Deprecated
		public abstract List<String> types();

		/**
		 * Lowest price level of places to search for. Valid values are from 0 (least expensive) to
		 * 4 (most expensive).
		 */
		@Default
		public int minPrice() {
			return -1;
		}

		/**
		 * Highest price level of places to search for. Valid values are from 0 (least expensive) to
		 * 4 (most expensive).
		 */
		@Default
		public int maxPrice() {
			return -1;
		}

		/**
		 * True if only places that have specified opening hours and are open right now should be
		 * returned.
		 */
		@Default
		public boolean openNow() {
			return false;
		}

		/**
		 * Country to search for places in. The value must be a two character ISO 3166-1 Alpha-2
		 * compatible country code, e.g. "GB". Currently only one country parameter is supported.
		 */
		@Nullable
		public abstract String countries();

		/**
		 * When searching for places, the language to return results in, if possible. If not
		 * specified, the default locale will be used.
		 * <p>
		 * When adding a place, the language of the place's name.
		 * </p>
		 * <p>
		 * The value must be one of the supported language codes.
		 * </p>
		 * 
		 * @see <a href="https://developers.google.com/maps/faq#languagesupport"
		 *      target="_blank">Supported Languages</a>
		 */
		@Nullable
		public abstract String language();

		/** Sort by importance. */
		public static final String RANK_BY_PROMINENCE = "prominence";

		/**
		 * Sort by distance from the specified location. When using this option,
		 * {@link Params#radius() radius} is ignored and one or more of {@link Params#name() name},
		 * {@link Params#keyword() keyword}, or {@link Params#type() type} is required.
		 */
		public static final String RANK_BY_DISTANCE = "distance";

		/**
		 * How the results should be sorted. Default value: {@link #RANK_BY_PROMINENCE}.
		 */
		@Nullable
		public abstract String rankBy();

		/**
		 * Token for the next batch of results from a previous search. When this value is set, all
		 * other parameters are ignored.
		 */
		@Nullable
		public abstract String pageToken();

		/**
		 * Applied to the results of search methods. Return true in {@link Predicate#apply(Object)
		 * apply} to include the Place in the results or false to filter it out.
		 * 
		 * @see Place.IdFilter
		 * @since 3.0.0
		 */
		@Nullable
		public abstract Predicate<Place> placeFilter();

		/**
		 * Applied to the results of autocomplete methods. Return true in
		 * {@link Predicate#apply(Object) apply} to include the Prediction in the results or false
		 * to filter it out.
		 * 
		 * @see Place.Prediction.IdFilter Prediction.IdFilter
		 * @since 3.0.0
		 */
		@Nullable
		public abstract Predicate<Prediction> predictionFilter();

		/**
		 * Maximum number of places, predictions, reviews, or photos to return.
		 */
		@Default
		public int maxResults() {
			return 0;
		}

		/**
		 * Maximum width of the photo to download. The original aspect ratio will be preserved. The
		 * value must be between 1 and 1600 pixels.
		 */
		@Default
		public int maxWidth() {
			return 0;
		}

		/**
		 * Maximum height of the photo to download. The original aspect ratio will be preserved. The
		 * value must be between 1 and 1600 pixels.
		 */
		@Default
		public int maxHeight() {
			return 0;
		}

		/**
		 * ETag returned when the photo was previously downloaded. If the photo on the server hasn't
		 * changed, then it will not be downloaded again.
		 */
		@Nullable
		public abstract String etag();

		/** Append the types parameter with pipe symbols between the values. */
		private static Joiner sPipes;

		/**
		 * Get the URL with appended parameters.
		 * 
		 * @param url
		 *            one of the {@link Places} URL_* constants
		 * @since 1.0.0
		 */
		public String format(String url) {
			String key = Sprockets.getConfig().getString("google.api-key");
			checkState(!Strings.isNullOrEmpty(key), "google.api-key not set");
			StringBuilder s = new StringBuilder(url.length() + 256);
			s.append(url).append("key=").append(key);
			if (url.equals(URL_ADD) || url.equals(URL_DELETE)) { // don't need any other params
				return s.toString();
			}
			String pageToken = pageToken();
			if (!Strings.isNullOrEmpty(pageToken)) { // don't need any other params
				return s.append("&pagetoken=").append(pageToken).toString();
			}
			String placeId = placeId();
			if (!Strings.isNullOrEmpty(placeId)) {
				s.append("&placeid=").append(placeId);
			}
			double lat = latitude();
			double lon = longitude();
			String rankBy = rankBy();
			if (lat > Double.NEGATIVE_INFINITY && lon > Double.NEGATIVE_INFINITY) {
				s.append("&location=").append(lat).append(',').append(lon);
				if (!RANK_BY_DISTANCE.equals(rankBy)) {
					s.append("&radius=").append(radius());
				}
			}
			try {
				String name = name();
				if (!Strings.isNullOrEmpty(name)) {
					s.append("&name=").append(URLEncoder.encode(name, "UTF-8"));
				}
				String keyword = keyword();
				if (!Strings.isNullOrEmpty(keyword)) {
					s.append("&keyword=").append(URLEncoder.encode(keyword, "UTF-8"));
				}
				String query = query();
				if (!Strings.isNullOrEmpty(query)) {
					s.append(url.equals(URL_AUTOCOMPLETE) || url.equals(URL_QUERY_AUTOCOMPLETE)
							? "&input=" : "&query=").append(URLEncoder.encode(query, "UTF-8"));
				}
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException("UTF-8 encoding isn't supported?!", e);
			}
			int offset = offset();
			if (offset > 0) {
				s.append("&offset=").append(offset);
			}
			String type = type();
			if (!Strings.isNullOrEmpty(type)) {
				s.append("&type=").append(type);
			}
			List<String> types = types();
			if (!types.isEmpty()) {
				if (sPipes == null) {
					sPipes = Joiner.on("%7C").skipNulls(); // URL encoded pipe
				}
				sPipes.appendTo(s.append("&types="), types);
			}
			int minPrice = minPrice();
			if (minPrice >= 0) {
				s.append("&minprice=").append(minPrice);
			}
			int maxPrice = maxPrice();
			if (maxPrice >= 0) {
				s.append("&maxprice=").append(maxPrice);
			}
			if (openNow()) {
				s.append("&opennow");
			}
			String countries = countries();
			if (!Strings.isNullOrEmpty(countries)) {
				s.append("&components=country:").append(countries);
			}
			if (!url.equals(URL_RADAR_SEARCH) && !url.equals(URL_PHOTO)) {
				String lang = language();
				s.append("&language=")
						.append(!Strings.isNullOrEmpty(lang) ? lang : Locale.getDefault());
			}
			if (!Strings.isNullOrEmpty(rankBy)) {
				s.append("&rankby=").append(rankBy);
			}
			String reference = reference();
			if (!Strings.isNullOrEmpty(reference)) {
				s.append("&photoreference=").append(reference);
			}
			int maxWidth = maxWidth();
			if (maxWidth > 0) {
				s.append("&maxwidth=").append(maxWidth);
			}
			int maxHeight = maxHeight();
			if (maxHeight > 0) {
				s.append("&maxheight=").append(maxHeight);
			}
			return s.toString();
		}
	}

	/**
	 * Result from one of the {@link Places} methods.
	 * 
	 * @param <T>
	 *            type of result returned in the response
	 */
	public static abstract class Response<T> {
		public static final String STATUS_OK = "OK";
		public static final String STATUS_ZERO_RESULTS = "ZERO_RESULTS";
		public static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
		public static final String STATUS_REQUEST_DENIED = "REQUEST_DENIED";
		public static final String STATUS_INVALID_REQUEST = "INVALID_REQUEST";
		public static final String STATUS_NOT_FOUND = "NOT_FOUND";
		public static final String STATUS_UNKNOWN_ERROR = "UNKNOWN_ERROR";
		public static final String STATUS_NOT_MODIFIED = "NOT_MODIFIED";

		Response() {
		}

		/**
		 * Indication of the success or failure of the request. Should be equal to one of the
		 * STATUS_* constants.
		 */
		@Nullable
		public abstract String getStatus();

		/**
		 * Detailed information about why the {@link #getStatus() status} is not {@link #STATUS_OK
		 * OK}.
		 * 
		 * @return null if an error message was not provided
		 * @since 1.4.0
		 */
		@Nullable
		public abstract String getErrorMessage();

		/**
		 * Check the {@link Places} method signature for the specific type of result it returns.
		 * 
		 * @return empty list or null if there was a problem with the request or an
		 *         {@link Params#etag() ETag} was sent and the photo on the server has not changed
		 */
		public abstract T getResult();

		/**
		 * Any attributions for this result that must be displayed to the user.
		 */
		public abstract List<String> getHtmlAttributions();

		/**
		 * If non-null, can be {@link Params#pageToken() used} in another request to get the next
		 * batch of results.
		 */
		@Nullable
		public abstract String getNextPageToken();

		/**
		 * Identifier for this version of the photo. If non-null, can be {@link Params#etag() used}
		 * in future requests to avoid downloading the photo if it hasn't changed on the server.
		 */
		@Nullable
		public abstract String getEtag();
	}

	/**
	 * Place search results.
	 */
	@Immutable
	static abstract class PlacesResponse extends Response<List<Place>> {
		/**
		 * Read fields from a search response.
		 * 
		 * @param fields
		 *            to read or 0 if all fields should be read
		 */
		static PlacesResponse from(JsonReader in, int fields, Params params) throws IOException {
			ImmutablePlacesResponse.Builder b = ImmutablePlacesResponse.builder();
			in.beginObject();
			while (in.hasNext()) {
				switch (in.nextName()) {
				case "status":
					b.status(in.nextString());
					break;
				case "error_message":
					b.errorMessage(in.nextString());
					break;
				case "results":
					int i = 0;
					int maxResults = params.maxResults();
					ImmutablePlace.Builder place = ImmutablePlace.builder();
					ImmutableId.Builder id = ImmutableId.builder();
					ImmutablePhoto.Builder photo = ImmutablePhoto.builder();
					Predicate<Place> filter = params.placeFilter();
					in.beginArray();
					while (in.hasNext()) {
						if (maxResults <= 0 || i < maxResults) {
							Place result = Place.from(in, fields, 0, place.clear(), id, photo);
							if (filter == null || filter.apply(result)) {
								b.addResult(result);
								i++;
							}
						} else {
							in.skipValue();
						}
					}
					in.endArray();
					break;
				case "html_attributions":
					in.beginArray();
					while (in.hasNext()) {
						b.addHtmlAttributions(in.nextString());
					}
					in.endArray();
					break;
				case "next_page_token":
					b.nextPageToken(in.nextString());
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
			return b.build();
		}
	}

	/**
	 * Autocomplete search results.
	 */
	@Immutable
	static abstract class PredictionsResponse extends Response<List<Prediction>> {
		/**
		 * Read fields from an autocomplete response.
		 * 
		 * @param fields
		 *            to read or 0 if all fields should be read
		 */
		static PredictionsResponse from(JsonReader in, int fields, Params params)
				throws IOException {
			ImmutablePredictionsResponse.Builder b = ImmutablePredictionsResponse.builder();
			in.beginObject();
			while (in.hasNext()) {
				switch (in.nextName()) {
				case "status":
					b.status(in.nextString());
					break;
				case "error_message":
					b.errorMessage(in.nextString());
					break;
				case "predictions":
					int i = 0;
					int maxResults = params.maxResults();
					ImmutablePrediction.Builder pred = ImmutablePrediction.builder();
					ImmutableId.Builder id = ImmutableId.builder();
					ImmutableSubstring.Builder s = ImmutableSubstring.builder();
					Predicate<Prediction> filter = params.predictionFilter();
					in.beginArray();
					while (in.hasNext()) {
						if (maxResults <= 0 || i < maxResults) {
							Prediction result = Prediction.from(in, fields,
									pred.clear(), id.clear(), s);
							if (filter == null || filter.apply(result)) {
								b.addResult(result);
								i++;
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
			return b.build();
		}
	}

	/**
	 * Place with full details.
	 */
	@Immutable
	static abstract class PlaceResponse extends Response<Place> {
		@Override
		@Nullable
		public abstract Place getResult();

		/**
		 * Read fields from a details response.
		 * 
		 * @param fields
		 *            to read or 0 if all fields should be read
		 */
		static PlaceResponse from(JsonReader in, int fields, Params params) throws IOException {
			ImmutablePlaceResponse.Builder b = ImmutablePlaceResponse.builder();
			in.beginObject();
			while (in.hasNext()) {
				switch (in.nextName()) {
				case "status":
					b.status(in.nextString());
					break;
				case "error_message":
					b.errorMessage(in.nextString());
					break;
				case "result":
					b.result(Place.from(in, fields, params.maxResults(), ImmutablePlace.builder(),
							ImmutableId.builder(), ImmutablePhoto.builder()));
					break;
				case "html_attributions":
					in.beginArray();
					while (in.hasNext()) {
						b.addHtmlAttributions(in.nextString());
					}
					in.endArray();
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
			return b.build();
		}
	}

	/**
	 * Photo bitstream for reading. Always {@link InputStream#close() close} the stream when
	 * finished.
	 */
	@Immutable
	static abstract class PhotoResponse extends Response<InputStream> {
		@Override
		@Nullable
		public abstract InputStream getResult();

		/**
		 * Get the ETag and InputStream from the connection response.
		 */
		static PhotoResponse from(HttpURLConnection con) throws IOException {
			ImmutablePhotoResponse.Builder b = ImmutablePhotoResponse.builder();
			switch (con.getResponseCode()) {
			case HTTP_OK:
				b.status(STATUS_OK).etag(con.getHeaderField("ETag")).result(con.getInputStream());
				break;
			case HTTP_NOT_MODIFIED:
				b.status(STATUS_NOT_MODIFIED);
				break;
			case HTTP_BAD_REQUEST:
				b.status(STATUS_INVALID_REQUEST);
				break;
			case HTTP_FORBIDDEN:
				b.status(STATUS_OVER_QUERY_LIMIT).result(con.getInputStream()); // "quota exceeded"
				break;
			default:
				b.status(STATUS_UNKNOWN_ERROR);
			}
			return b.build();
		}
	}

	/**
	 * Place ID of added place.
	 */
	@Immutable
	static abstract class PlaceIdResponse extends Response<Id> {
		@Override
		@Nullable
		public abstract Id getResult();

		/**
		 * Read fields from an add response.
		 */
		static PlaceIdResponse from(JsonReader in) throws IOException {
			ImmutablePlaceIdResponse.Builder b = ImmutablePlaceIdResponse.builder();
			String placeId = null;
			String scope = null;
			in.beginObject();
			while (in.hasNext()) {
				switch (in.nextName()) {
				case "status":
					b.status(in.nextString());
					break;
				case "error_message":
					b.errorMessage(in.nextString());
					break;
				case "place_id":
					placeId = in.nextString();
					break;
				case "scope":
					scope = in.nextString();
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
			if (!Strings.isNullOrEmpty(placeId)) {
				b.result(ImmutableId.builder().id(placeId).scope(scope).build());
			}
			return b.build();
		}
	}

	/**
	 * No result.
	 */
	@Immutable
	static abstract class VoidResponse extends Response<Void> {
		@Override
		@Nullable
		public abstract Void getResult();

		/**
		 * Read fields from a delete response.
		 */
		static VoidResponse from(JsonReader in) throws IOException {
			ImmutableVoidResponse.Builder b = ImmutableVoidResponse.builder();
			in.beginObject();
			while (in.hasNext()) {
				switch (in.nextName()) {
				case "status":
					b.status(in.nextString());
					break;
				case "error_message":
					b.errorMessage(in.nextString());
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
			return b.build();
		}
	}
}
