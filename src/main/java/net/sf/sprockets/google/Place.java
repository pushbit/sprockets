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

import static net.sf.sprockets.google.Places.FIELD_ADDRESS;
import static net.sf.sprockets.google.Places.FIELD_DESCRIPTION;
import static net.sf.sprockets.google.Places.FIELD_FORMATTED_ADDRESS;
import static net.sf.sprockets.google.Places.FIELD_FORMATTED_OPENING_HOURS;
import static net.sf.sprockets.google.Places.FIELD_FORMATTED_PHONE_NUMBER;
import static net.sf.sprockets.google.Places.FIELD_ICON;
import static net.sf.sprockets.google.Places.FIELD_INTL_PHONE_NUMBER;
import static net.sf.sprockets.google.Places.FIELD_MATCHED_SUBSTRINGS;
import static net.sf.sprockets.google.Places.FIELD_NAME;
import static net.sf.sprockets.google.Places.FIELD_OPENING_HOURS;
import static net.sf.sprockets.google.Places.FIELD_PHOTOS;
import static net.sf.sprockets.google.Places.FIELD_REVIEWS;
import static net.sf.sprockets.google.Places.FIELD_TERMS;
import static net.sf.sprockets.google.Places.FIELD_TYPES;
import static net.sf.sprockets.google.Places.FIELD_URL;
import static net.sf.sprockets.google.Places.FIELD_VICINITY;
import static net.sf.sprockets.google.Places.FIELD_WEBSITE;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Nullable;

import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.lang.ImmutableSubstring;
import net.sf.sprockets.lang.Maths;
import net.sf.sprockets.lang.Substring;
import net.sf.sprockets.time.DayOfWeek;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Modifiable;
import org.immutables.value.Value.Style;

import com.google.common.base.Predicate;
import com.google.gson.stream.JsonReader;

/**
 * Google Place returned from a {@link Places} method. The properties which are populated will vary
 * according to the Places method called and the fields provided. {@link #getPlaceId() placeId} and
 * primitive properties (when available) will always be populated. Properties that have not been
 * populated will return null, an empty list, or the default primitive value specified in the method
 * documentation.
 */
@Immutable
public abstract class Place {
	Place() {
	}

	/**
	 * Build an immutable instance.
	 * 
	 * @since 3.0.0
	 */
	public static ImmutablePlace.Builder builder() {
		return ImmutablePlace.builder();
	}

	/**
	 * Unique identifier that can be used to retrieve {@link Places#details(Params) details} about
	 * this place.
	 * 
	 * @since 1.5.0
	 */
	@Nullable
	public abstract Id getPlaceId();

	/**
	 * Alternative identifiers that have been mapped to {@link #getPlaceId() the main one}.
	 * 
	 * @since 1.5.0
	 */
	public abstract List<Id> getAltIds();

	/**
	 * URL for an icon representing this type of place.
	 */
	@Nullable
	public abstract String getIcon();

	/**
	 * Google Place page.
	 */
	@Nullable
	public abstract String getUrl();

	/**
	 * Default value: {@link Double#NEGATIVE_INFINITY}.
	 */
	@Default
	public double getLatitude() {
		return Double.NEGATIVE_INFINITY;
	}

	/**
	 * Default value: {@link Double#NEGATIVE_INFINITY}.
	 */
	@Default
	public double getLongitude() {
		return Double.NEGATIVE_INFINITY;
	}

	/**
	 * Name of this place, for example a business or landmark name.
	 */
	@Nullable
	public abstract String getName();

	/**
	 * All address components in separate properties.
	 */
	@Nullable
	public abstract Address getAddress();

	/**
	 * All address components formatted together.
	 */
	@Nullable
	public abstract String getFormattedAddress();

	/**
	 * Simplified address that stops after the city level.
	 */
	@Nullable
	public abstract String getVicinity();

	/**
	 * Includes prefixed country code.
	 */
	@Nullable
	public abstract String getIntlPhoneNumber();

	/**
	 * In local format.
	 */
	@Nullable
	public abstract String getFormattedPhoneNumber();

	/**
	 * URL of the website for this place.
	 */
	@Nullable
	public abstract String getWebsite();

	/**
	 * Features describing this place.
	 * 
	 * @see <a href="https://developers.google.com/places/supported_types" target="_blank">Place
	 *      Types</a>
	 */
	public abstract List<String> getTypes();

	/**
	 * Relative level of average expenses at this place. From 0 (least expensive) to 4 (most
	 * expensive). Default value: -1.
	 */
	@Default
	public int getPriceLevel() {
		return -1;
	}

	/**
	 * From 0.0 to 5.0, based on user reviews. Default value: -1.0f.
	 */
	@Default
	public float getRating() {
		return -1.0f;
	}

	/**
	 * Number of ratings that have been submitted. Default value: -1.
	 * 
	 * @since 1.3.0
	 */
	@Default
	public int getRatingCount() {
		return -1;
	}

	/**
	 * Comments and ratings from Google users.
	 */
	public abstract List<Review> getReviews();

	/**
	 * True if this place is currently open.
	 * 
	 * @return null if unknown
	 */
	@Nullable
	public abstract Boolean getOpenNow();

	/**
	 * Opening and closing times for each day that this place is open.
	 */
	public abstract List<OpeningHours> getOpeningHours();

	/**
	 * Opening hours for each day of the week. e.g. ["Monday: 10:00 am – 6:00 pm", ...,
	 * "Sunday: Closed"]
	 * 
	 * @since 2.2.0
	 */
	public abstract List<String> getFormattedOpeningHours();

	/**
	 * True if this place has permanently shut down.
	 * 
	 * @since 2.2.0
	 */
	@Default
	public boolean isPermanentlyClosed() {
		return false;
	}

	/**
	 * Number of minutes this place's time zone is offset from UTC. Default value:
	 * {@link Integer#MIN_VALUE}.
	 */
	@Default
	public int getUtcOffset() {
		return Integer.MIN_VALUE;
	}

	/**
	 * Photos for this place that can be downloaded by supplying the
	 * {@link Place.Photo#getReference() reference} to {@link Places#photo(Params)}.
	 */
	public abstract List<Photo> getPhotos();

	/**
	 * Read fields from a result object.
	 * 
	 * @param fields
	 *            to read or 0 if all fields should be read
	 * @param maxResults
	 *            maximum number of reviews and photos to return or 0 to return all
	 */
	static Place from(JsonReader in, int fields, int maxResults, ImmutablePlace.Builder place,
			ImmutableId.Builder id, ImmutablePhoto.Builder photo) throws IOException {
		String placeId = null; // save for later so `id` can be used for alt_ids
		String scope = null;
		in.beginObject();
		while (in.hasNext()) {
			switch (in.nextName()) {
			case "place_id":
				placeId = in.nextString();
				break;
			case "scope":
				scope = in.nextString();
				break;
			case "alt_ids":
				in.beginArray();
				while (in.hasNext()) {
					place.addAltIds(Id.from(in, id.clear()));
				}
				in.endArray();
				break;
			case "icon":
				if (wants(FIELD_ICON, fields, in)) {
					place.icon(in.nextString());
				}
				break;
			case "url":
				if (wants(FIELD_URL, fields, in)) {
					place.url(in.nextString());
				}
				break;
			case "geometry":
				in.beginObject();
				while (in.hasNext()) {
					if (in.nextName().equals("location")) {
						in.beginObject();
						while (in.hasNext()) {
							switch (in.nextName()) {
							case "lat":
								place.latitude(in.nextDouble());
								break;
							case "lng":
								place.longitude(in.nextDouble());
								break;
							default:
								in.skipValue();
							}
						}
						in.endObject();
					} else {
						in.skipValue(); // "viewport"
					}
				}
				in.endObject();
				break;
			case "name":
				if (wants(FIELD_NAME, fields, in)) {
					place.name(in.nextString());
				}
				break;
			case "address_components":
				if (wants(FIELD_ADDRESS, fields, in)) {
					place.address(Address.from(in));
				}
				break;
			case "formatted_address":
				if (wants(FIELD_FORMATTED_ADDRESS, fields, in)) {
					place.formattedAddress(in.nextString());
				}
				break;
			case "vicinity":
				if (wants(FIELD_VICINITY, fields, in)) {
					place.vicinity(in.nextString());
				}
				break;
			case "international_phone_number":
				if (wants(FIELD_INTL_PHONE_NUMBER, fields, in)) {
					place.intlPhoneNumber(in.nextString());
				}
				break;
			case "formatted_phone_number":
				if (wants(FIELD_FORMATTED_PHONE_NUMBER, fields, in)) {
					place.formattedPhoneNumber(in.nextString());
				}
				break;
			case "website":
				if (wants(FIELD_WEBSITE, fields, in)) {
					place.website(in.nextString());
				}
				break;
			case "types":
				if (wants(FIELD_TYPES, fields, in)) {
					in.beginArray();
					while (in.hasNext()) {
						place.addTypes(in.nextString());
					}
					in.endArray();
				}
				break;
			case "price_level":
				place.priceLevel(in.nextInt());
				break;
			case "rating":
				place.rating((float) in.nextDouble());
				break;
			case "user_ratings_total":
				place.ratingCount(in.nextInt());
				break;
			case "reviews":
				if (wants(FIELD_REVIEWS, fields, in)) {
					int i = 0;
					ImmutableReview.Builder review = ImmutableReview.builder();
					ImmutableAspect.Builder aspect = ImmutableAspect.builder();
					in.beginArray();
					while (in.hasNext()) {
						if (maxResults <= 0 || i < maxResults) {
							place.addReviews(Review.from(in, review.clear(), aspect));
							i++;
						} else {
							in.skipValue();
						}
					}
					in.endArray();
				}
				break;
			case "opening_hours":
				in.beginObject();
				while (in.hasNext()) {
					switch (in.nextName()) {
					case "open_now":
						place.openNow(in.nextBoolean());
						break;
					case "periods":
						if (wants(FIELD_OPENING_HOURS, fields, in)) {
							ImmutableOpeningHours.Builder hours = ImmutableOpeningHours.builder();
							in.beginArray();
							while (in.hasNext()) {
								place.addOpeningHours(OpeningHours.from(in, hours.clear()));
							}
							in.endArray();
						}
						break;
					case "weekday_text":
						if (wants(FIELD_FORMATTED_OPENING_HOURS, fields, in)) {
							in.beginArray();
							while (in.hasNext()) {
								place.addFormattedOpeningHours(in.nextString());
							}
							in.endArray();
						}
						break;
					default:
						in.skipValue();
					}
				}
				in.endObject();
				break;
			case "permanently_closed":
				place.isPermanentlyClosed(in.nextBoolean());
				break;
			case "utc_offset":
				place.utcOffset(in.nextInt());
				break;
			case "photos":
				if (wants(FIELD_PHOTOS, fields, in)) {
					int i = 0;
					in.beginArray();
					while (in.hasNext()) {
						if (maxResults <= 0 || i < maxResults) {
							place.addPhotos(Photo.from(in, photo.clear()));
							i++;
						} else {
							in.skipValue();
						}
					}
					in.endArray();
				}
				break;
			default:
				in.skipValue();
			}
		}
		in.endObject();
		return place.placeId(id.clear().id(placeId).scope(scope).build()).build();
	}

	/**
	 * True if fields is zero or the field is in the fields bitmask. If false and a JsonReader was
	 * provided, the next JSON value will be skipped.
	 */
	private static boolean wants(int field, int fields, JsonReader in) throws IOException {
		boolean wants = fields == 0 || (fields & field) == field;
		if (!wants && in != null) {
			in.skipValue();
		}
		return wants;
	}

	/**
	 * Unique identifier that can be used to retrieve {@link Places#details(Params) details} about a
	 * place.
	 * 
	 * @since 1.5.0
	 */
	@Immutable
	public static abstract class Id {
		Id() {
		}

		/** Local to an application. */
		public static final String SCOPE_APP = "APP";

		/** Publicly available. */
		public static final String SCOPE_GOOGLE = "GOOGLE";

		/**
		 * Unique identifier that can be used to retrieve {@link Places#details(Params) details}
		 * about the place.
		 */
		@Nullable
		public abstract String getId();

		/**
		 * Availability of this place ID. If set, should be equal to {@link #SCOPE_GOOGLE} or
		 * {@link #SCOPE_APP}.
		 */
		@Nullable
		public abstract String getScope();

		/**
		 * Read fields from an alt_id object.
		 */
		static Id from(JsonReader in, ImmutableId.Builder b) throws IOException {
			in.beginObject();
			while (in.hasNext()) {
				switch (in.nextName()) {
				case "place_id":
					b.id(in.nextString());
					break;
				case "scope":
					b.scope(in.nextString());
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
			return b.build();
		}

		/**
		 * Base class for {@link Predicate}s that filter on place IDs.
		 * 
		 * @since 3.0.0
		 */
		public static abstract class Filter {
			Filter() {
			}

			/**
			 * IDs of places to filter out.
			 */
			public abstract List<String> ids();

			/**
			 * True if the place with the ID should be included.
			 */
			protected boolean apply(Id id) {
				return id == null || !ids().contains(id.getId());
			}
		}
	}

	/**
	 * Search {@link Params#placeFilter() filter} on place IDs.
	 * 
	 * @since 3.0.0
	 */
	@Modifiable
	@Style(typeModifiable = "Place*", create = "new", get = "*", set = "*")
	public static abstract class IdFilter extends Id.Filter implements Predicate<Place> {
		IdFilter() {
		}

		/**
		 * Mutable instance where values can be set.
		 */
		public static PlaceIdFilter create() {
			return new PlaceIdFilter();
		}

		@Override
		public boolean apply(Place place) {
			return apply(place.getPlaceId());
		}
	}

	/**
	 * All address components in separate properties. Each property has a full name, e.g.
	 * "New York", and when applicable an abbreviated name, e.g. "NY". Properties will be null when
	 * the value is not available.
	 */
	@Immutable
	public static abstract class Address {
		Address() {
		}

		@Nullable
		public abstract String getCountry();

		@Nullable
		public abstract String getCountryAbbr();

		/**
		 * State or province.
		 */
		@Nullable
		public abstract String getAdminAreaL1();

		@Nullable
		public abstract String getAdminAreaL1Abbr();

		/**
		 * County or region.
		 */
		@Nullable
		public abstract String getAdminAreaL2();

		@Nullable
		public abstract String getAdminAreaL2Abbr();

		@Nullable
		public abstract String getAdminAreaL3();

		@Nullable
		public abstract String getAdminAreaL3Abbr();

		@Nullable
		public abstract String getAdminAreaL4();

		@Nullable
		public abstract String getAdminAreaL4Abbr();

		@Nullable
		public abstract String getAdminAreaL5();

		@Nullable
		public abstract String getAdminAreaL5Abbr();

		/**
		 * City.
		 */
		@Nullable
		public abstract String getLocality();

		@Nullable
		public abstract String getLocalityAbbr();

		/**
		 * City district.
		 */
		@Nullable
		public abstract String getSublocality();

		@Nullable
		public abstract String getSublocalityAbbr();

		@Nullable
		public abstract String getSublocalityL1();

		@Nullable
		public abstract String getSublocalityL1Abbr();

		@Nullable
		public abstract String getSublocalityL2();

		@Nullable
		public abstract String getSublocalityL2Abbr();

		@Nullable
		public abstract String getSublocalityL3();

		@Nullable
		public abstract String getSublocalityL3Abbr();

		@Nullable
		public abstract String getSublocalityL4();

		@Nullable
		public abstract String getSublocalityL4Abbr();

		@Nullable
		public abstract String getSublocalityL5();

		@Nullable
		public abstract String getSublocalityL5Abbr();

		@Nullable
		public abstract String getNeighborhood();

		@Nullable
		public abstract String getNeighborhoodAbbr();

		@Nullable
		public abstract String getPostalCode();

		@Nullable
		public abstract String getPostalCodeAbbr();

		@Nullable
		public abstract String getPostalTown();

		@Nullable
		public abstract String getPostalTownAbbr();

		/**
		 * Street.
		 */
		@Nullable
		public abstract String getRoute();

		@Nullable
		public abstract String getRouteAbbr();

		@Nullable
		public abstract String getPremise();

		@Nullable
		public abstract String getPremiseAbbr();

		@Nullable
		public abstract String getStreetNumber();

		@Nullable
		public abstract String getStreetNumberAbbr();

		/**
		 * Read fields from an address components array.
		 */
		static Address from(JsonReader in) throws IOException {
			ImmutableAddress.Builder b = ImmutableAddress.builder();
			in.beginArray();
			while (in.hasNext()) {
				String type = null;
				String longName = null;
				String shortName = null;

				in.beginObject();
				while (in.hasNext()) {
					switch (in.nextName()) {
					case "types":
						in.beginArray();
						while (in.hasNext()) {
							if (type == null) { // only use the first match
								type = in.nextString();
							} else {
								in.skipValue();
							}
						}
						in.endArray();
						break;
					case "long_name":
						longName = in.nextString();
						break;
					case "short_name":
						shortName = in.nextString();
						break;
					default:
						in.skipValue();
					}
				}
				in.endObject();

				if (type != null) {
					switch (type) {
					case "country":
						b.country(longName).countryAbbr(shortName);
						break;
					case "administrative_area_level_1":
						b.adminAreaL1(longName).adminAreaL1Abbr(shortName);
						break;
					case "administrative_area_level_2":
						b.adminAreaL2(longName).adminAreaL2Abbr(shortName);
						break;
					case "administrative_area_level_3":
						b.adminAreaL3(longName).adminAreaL3Abbr(shortName);
						break;
					case "administrative_area_level_4":
						b.adminAreaL4(longName).adminAreaL4Abbr(shortName);
						break;
					case "administrative_area_level_5":
						b.adminAreaL5(longName).adminAreaL5Abbr(shortName);
						break;
					case "locality":
						b.locality(longName).localityAbbr(shortName);
						break;
					case "sublocality":
						b.sublocality(longName).sublocalityAbbr(shortName);
						break;
					case "sublocality_level_1":
						b.sublocalityL1(longName).sublocalityL1Abbr(shortName);
						break;
					case "sublocality_level_2":
						b.sublocalityL2(longName).sublocalityL2Abbr(shortName);
						break;
					case "sublocality_level_3":
						b.sublocalityL3(longName).sublocalityL3Abbr(shortName);
						break;
					case "sublocality_level_4":
						b.sublocalityL4(longName).sublocalityL4Abbr(shortName);
						break;
					case "sublocality_level_5":
						b.sublocalityL5(longName).sublocalityL5Abbr(shortName);
						break;
					case "neighborhood":
						b.neighborhood(longName).neighborhoodAbbr(shortName);
						break;
					case "postal_code":
						b.postalCode(longName).postalCodeAbbr(shortName);
						break;
					case "postal_town":
						b.postalTown(longName).postalTownAbbr(shortName);
						break;
					case "route":
						b.route(longName).routeAbbr(shortName);
						break;
					case "premise":
						b.premise(longName).premiseAbbr(shortName);
						break;
					case "street_number":
						b.streetNumber(longName).streetNumberAbbr(shortName);
						break;
					}
				}
			}
			in.endArray();
			return b.build();
		}
	}

	/**
	 * Comments and ratings from a Google user.
	 */
	@Immutable
	public static abstract class Review {
		Review() {
		}

		@Nullable
		public abstract String getAuthorName();

		/**
		 * Google+ profile.
		 */
		@Nullable
		public abstract String getAuthorUrl();

		/**
		 * When the review was submitted, in epoch seconds.
		 */
		@Default
		public long getTime() {
			return 0L;
		}

		/**
		 * Ratings for different attributes of the place. The first element is the primary aspect.
		 */
		public abstract List<Aspect> getAspects();

		/**
		 * From 1 to 5, the user's overall rating. Default value: 0.
		 * 
		 * @since 1.2.0
		 */
		@Default
		public int getRating() {
			return 0;
		}

		/**
		 * IETF language code (without country code) for the language of the {@link #getText() text}
		 * .
		 * 
		 * @since 1.2.0
		 */
		@Nullable
		public abstract String getLanguage();

		/**
		 * Review comments, which can contain HTML character and entity references.
		 */
		@Nullable
		public abstract String getText();

		/**
		 * Read fields from a review object.
		 */
		static Review from(JsonReader in, ImmutableReview.Builder review,
				ImmutableAspect.Builder aspect) throws IOException {
			in.beginObject();
			while (in.hasNext()) {
				switch (in.nextName()) {
				case "author_name":
					review.authorName(in.nextString());
					break;
				case "author_url":
					review.authorUrl(in.nextString());
					break;
				case "time":
					review.time(in.nextLong());
					break;
				case "aspects":
					in.beginArray();
					while (in.hasNext()) {
						review.addAspects(Aspect.from(in, aspect.clear()));
					}
					in.endArray();
					break;
				case "rating":
					review.rating(in.nextInt());
					break;
				case "language":
					review.language(in.nextString());
					break;
				case "text":
					review.text(in.nextString());
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
			return review.build();
		}

		/**
		 * Rating for one attribute of a place.
		 */
		@Immutable
		public static abstract class Aspect {
			Aspect() {
			}

			/**
			 * The aspect that was rated, e.g. atmosphere, service, food, overall, etc.
			 */
			@Nullable
			public abstract String getType();

			/**
			 * From 0 to 3.
			 */
			@Default
			public int getRating() {
				return 0;
			}

			/**
			 * Read fields from an aspect object.
			 */
			static Aspect from(JsonReader in, ImmutableAspect.Builder b) throws IOException {
				in.beginObject();
				while (in.hasNext()) {
					switch (in.nextName()) {
					case "type":
						b.type(in.nextString());
						break;
					case "rating":
						b.rating(in.nextInt());
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

	/**
	 * Opening and closing times for a day (or span of days) on which a place is open.
	 */
	@Immutable
	public static abstract class OpeningHours {
		OpeningHours() {
		}

		@Nullable
		public abstract DayOfWeek getOpenDay();

		/**
		 * 0-2359. Default value: -1.
		 * 
		 * @since 2.3.0
		 */
		@Default
		public int getOpenTime() {
			return -1;
		}

		/**
		 * 0-23. Default value: -1.
		 */
		public int getOpenHour() {
			int time = getOpenTime();
			return time >= 0 ? time / 100 : time;
		}

		/**
		 * 0-59. Default value: -1.
		 */
		public int getOpenMinute() {
			return getOpenTime() % 100;
		}

		/**
		 * Converted to epoch milliseconds. Default value: -1.
		 * 
		 * @since 2.3.0
		 */
		public int getOpenTimeMillis() {
			int time = getOpenTime();
			return time >= 0 ? millis(getOpenHour(), getOpenMinute()) : time;
		}

		@Nullable
		public abstract DayOfWeek getCloseDay();

		/**
		 * 0-2359. Default value: -1.
		 * 
		 * @since 2.3.0
		 */
		@Default
		public int getCloseTime() {
			return -1;
		}

		/**
		 * 0-23. Default value: -1.
		 */
		public int getCloseHour() {
			int time = getCloseTime();
			return time >= 0 ? time / 100 : time;
		}

		/**
		 * 0-59. Default value: -1.
		 */
		public int getCloseMinute() {
			return getCloseTime() % 100;
		}

		/**
		 * Converted to epoch milliseconds. Default value: -1.
		 * 
		 * @since 2.3.0
		 */
		public int getCloseTimeMillis() {
			int time = getCloseTime();
			return time >= 0 ? millis(getCloseHour(), getCloseMinute()) : time;
		}

		/**
		 * Convert the hours and minutes to epoch milliseconds.
		 */
		private int millis(int hours, int minutes) {
			return (hours * 60 + minutes) * 60 * 1000 - TimeZone.getDefault().getOffset(0L);
		}

		/**
		 * Read fields from a period object.
		 */
		static OpeningHours from(JsonReader in, ImmutableOpeningHours.Builder b)
				throws IOException {
			in.beginObject();
			while (in.hasNext()) {
				switch (in.nextName()) {
				case "open":
					in.beginObject();
					while (in.hasNext()) {
						switch (in.nextName()) {
						case "day":
							b.openDay(day(in.nextInt()));
							break;
						case "time":
							b.openTime(Integer.parseInt(in.nextString()));
							break;
						default:
							in.skipValue();
						}
					}
					in.endObject();
					break;
				case "close":
					in.beginObject();
					while (in.hasNext()) {
						switch (in.nextName()) {
						case "day":
							b.closeDay(day(in.nextInt()));
							break;
						case "time":
							b.closeTime(Integer.parseInt(in.nextString()));
							break;
						default:
							in.skipValue();
						}
					}
					in.endObject();
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
			return b.build();
		}

		private static final DayOfWeek[] sDaysOfWeek = DayOfWeek.values(); // clone only once

		/**
		 * Get the DayOfWeek for the day number, where 0 == Sunday.
		 */
		private static DayOfWeek day(int day) {
			return sDaysOfWeek[Maths.rollover(day - 1, 0, 6)]; // DayOfWeek starts on Monday
		}
	}

	/**
	 * Photo for a place that can be downloaded by supplying the {@link #getReference() reference}
	 * to {@link Places#photo(Params)}.
	 */
	@Immutable
	public static abstract class Photo {
		Photo() {
		}

		/**
		 * Token that can be used to download the photo by supplying it to
		 * {@link Places#photo(Params)}.
		 */
		@Nullable
		public abstract String getReference();

		/**
		 * Maximum available pixels.
		 */
		@Default
		public int getWidth() {
			return 0;
		}

		/**
		 * Maximum available pixels.
		 */
		@Default
		public int getHeight() {
			return 0;
		}

		/**
		 * Any attributions that must be displayed along with the photo.
		 */
		public abstract List<String> getHtmlAttributions();

		/**
		 * Read fields from a photo object.
		 */
		static Photo from(JsonReader in, ImmutablePhoto.Builder b) throws IOException {
			in.beginObject();
			while (in.hasNext()) {
				switch (in.nextName()) {
				case "photo_reference":
					b.reference(in.nextString());
					break;
				case "width":
					b.width(in.nextInt());
					break;
				case "height":
					b.height(in.nextInt());
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
	 * Place or query that was returned from a {@link Places} autocomplete method.
	 */
	@Immutable
	public static abstract class Prediction {
		Prediction() {
		}

		/**
		 * Unique identifier that can be used to retrieve {@link Places#details(Params) details}
		 * about this place. Not available if this is a query prediction.
		 */
		@Nullable
		public abstract Id getPlaceId();

		/**
		 * Name of this place or a query suggestion.
		 * 
		 * @since 3.0.0
		 */
		@Nullable
		public abstract String getDescription();

		/**
		 * Features describing this place.
		 * 
		 * @see <a href="https://developers.google.com/places/supported_types" target="_blank">Place
		 *      Types</a>
		 */
		public abstract List<String> getTypes();

		/**
		 * Sections in the {@link Place.Prediction#getDescription() description}.
		 */
		public abstract List<Substring> getTerms();

		/**
		 * Substrings in the {@link Place.Prediction#getDescription() description} that match the
		 * search text, often used for highlighting.
		 */
		public abstract List<Substring> getMatchedSubstrings();

		/**
		 * Read fields from a prediction object.
		 * 
		 * @param fields
		 *            to read or 0 if all fields should be read
		 */
		static Prediction from(JsonReader in, int fields, ImmutablePrediction.Builder pred,
				ImmutableId.Builder id, ImmutableSubstring.Builder s) throws IOException {
			String description = null; // save for use in Substrings
			in.beginObject();
			while (in.hasNext()) {
				switch (in.nextName()) {
				case "place_id":
					pred.placeId(id.id(in.nextString()).build());
					break;
				case "description":
					description = in.nextString();
					if (wants(FIELD_DESCRIPTION, fields, null)) {
						pred.description(description);
					}
					break;
				case "types":
					if (wants(FIELD_TYPES, fields, in)) {
						in.beginArray();
						while (in.hasNext()) {
							pred.addTypes(in.nextString());
						}
						in.endArray();
					}
					break;
				case "terms":
					if (wants(FIELD_TERMS, fields, in)) {
						in.beginArray();
						while (in.hasNext()) {
							s.clear().superstring(description);
							in.beginObject();
							while (in.hasNext()) {
								switch (in.nextName()) {
								case "offset":
									s.offset(in.nextInt());
									break;
								case "value":
									s.value(in.nextString());
									break;
								default:
									in.skipValue();
								}
							}
							in.endObject();
							pred.addTerms(s.build());
						}
						in.endArray();
					}
					break;
				case "matched_substrings":
					if (wants(FIELD_MATCHED_SUBSTRINGS, fields, in)) {
						in.beginArray();
						while (in.hasNext()) {
							s.clear().superstring(description);
							int offset = -1;
							int length = 0;
							in.beginObject();
							while (in.hasNext()) {
								switch (in.nextName()) {
								case "offset":
									offset = in.nextInt();
									s.offset(offset);
									break;
								case "length":
									length = in.nextInt();
									s.length(length);
									break;
								default:
									in.skipValue();
								}
							}
							in.endObject();

							if (offset >= 0 && length > 0) {
								int end = offset + length;
								if (description != null && description.length() >= end) {
									s.value(description.substring(offset, end));
								}
							}
							pred.addMatchedSubstrings(s.build());
						}
						in.endArray();
					}
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
			return pred.build();
		}

		/**
		 * Autocomplete {@link Params#predictionFilter() filter} on place IDs.
		 * 
		 * @since 3.0.0
		 */
		@Modifiable
		@Style(typeModifiable = "Prediction*", create = "new", get = "*", set = "*")
		public static abstract class IdFilter extends Id.Filter implements Predicate<Prediction> {
			IdFilter() {
			}

			/**
			 * Mutable instance where values can be set.
			 */
			public static PredictionIdFilter create() {
				return new PredictionIdFilter();
			}

			@Override
			public boolean apply(Prediction pred) {
				return apply(pred.getPlaceId());
			}
		}
	}
}
