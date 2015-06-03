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

import static net.sf.sprockets.google.Places.Response.Key.UNKNOWN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response.Key;
import net.sf.sprockets.lang.Maths;
import net.sf.sprockets.lang.Substring;
import net.sf.sprockets.time.DayOfWeek;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.stream.JsonReader;

/**
 * Google Place returned from a {@link Places} method. The properties which are populated will vary
 * according to the Places method called and the {@link Places.Field Field}s provided. Check the
 * Places method documentation for the available fields. Properties that have not been populated
 * will return null, when possible, or the default value will be specified in the method
 * documentation.
 */
public class Place {
	/** Expected number of alt_ids that will be returned, if any. */
	private static final int MAX_ALT_IDS = 1;

	/** Maximum number of reviews that will be returned. */
	private static final int MAX_REVIEWS = 5;

	/** Maximum number of events that will be returned. */
	private static final int MAX_EVENTS = 10;

	/** Maximum number of photos that will be returned. */
	private static final int MAX_PHOTOS = 10;

	Id mPlaceId;
	List<Id> mAltIds;
	String mId;
	String mReference;
	String mIcon;
	String mUrl;
	double mLat = Double.NEGATIVE_INFINITY;
	double mLong = Double.NEGATIVE_INFINITY;
	String mName;
	Address mAddress;
	String mFmtAddress;
	String mVicinity;
	String mIntlPhone;
	String mFmtPhone;
	String mWebsite;
	List<String> mTypes;
	int mPrice = -1;
	float mRating = -1.0f;
	int mRatingCount = -1;
	List<Review> mReviews;
	Boolean mOpen;
	List<OpeningHours> mOpenHours;
	List<String> mFmtOpenHours;
	boolean mPermClosed;
	List<Event> mEvents;
	int mUtcOffset = Integer.MIN_VALUE;
	List<Photo> mPhotos;
	private int mHash;

	/**
	 * Empty Place for subclasses.
	 */
	private Place() {
	}

	/**
	 * Read fields from a result object.
	 * 
	 * @param fields
	 *            to read or 0 if all fields should be read
	 * @param maxResults
	 *            maximum number of reviews, events, and photos to return
	 */
	Place(JsonReader in, int fields, int maxResults) throws IOException {
		in.beginObject();
		while (in.hasNext()) {
			Key key = Key.get(in.nextName());
			if (key == UNKNOWN || fields != 0 && key.mField != null && !key.mField.in(fields)) {
				in.skipValue(); // unknown field or caller doesn't want it
				continue;
			}

			switch (key) {
			case place_id:
				mPlaceId = Id.id(mPlaceId, in.nextString());
				break;
			case scope:
				mPlaceId = Id.scope(mPlaceId, in.nextString());
				break;
			case alt_ids:
				in.beginArray();
				while (in.hasNext()) {
					if (mAltIds == null) {
						mAltIds = new ArrayList<Id>(MAX_ALT_IDS);
					}
					mAltIds.add(new Id(in));
				}
				in.endArray();
				break;
			case id:
				mId = in.nextString();
				break;
			case reference:
				mReference = in.nextString();
				break;
			case icon:
				mIcon = in.nextString();
				break;
			case url:
				mUrl = in.nextString();
				break;
			case geometry:
				in.beginObject();
				while (in.hasNext()) {
					if (in.nextName().equals("location")) {
						in.beginObject();
						while (in.hasNext()) {
							switch (Key.get(in.nextName())) {
							case lat:
								mLat = in.nextDouble();
								break;
							case lng:
								mLong = in.nextDouble();
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
			case name:
				mName = in.nextString();
				break;
			case address_components:
				mAddress = new Address(in);
				break;
			case formatted_address:
				mFmtAddress = in.nextString();
				break;
			case vicinity:
				mVicinity = in.nextString();
				break;
			case international_phone_number:
				mIntlPhone = in.nextString();
				break;
			case formatted_phone_number:
				mFmtPhone = in.nextString();
				break;
			case website:
				mWebsite = in.nextString();
				break;
			case types:
				types(in);
				break;
			case price_level:
				mPrice = in.nextInt();
				break;
			case rating:
				mRating = (float) in.nextDouble();
				break;
			case user_ratings_total:
				mRatingCount = in.nextInt();
				break;
			case reviews:
				in.beginArray();
				while (in.hasNext()) {
					if (mReviews == null) {
						int cap = Maths.clamp(maxResults, 0, MAX_REVIEWS);
						mReviews = new ArrayList<Review>(cap > 0 ? cap : MAX_REVIEWS);
					}
					if (maxResults <= 0 || mReviews.size() < maxResults) {
						mReviews.add(new Review(in));
					} else {
						in.skipValue();
					}
				}
				in.endArray();
				break;
			case opening_hours:
				in.beginObject();
				while (in.hasNext()) {
					switch (Key.get(in.nextName())) {
					case open_now:
						mOpen = in.nextBoolean();
						break;
					case periods:
						in.beginArray();
						while (in.hasNext()) {
							if (mOpenHours == null) {
								mOpenHours = new ArrayList<OpeningHours>();
							}
							mOpenHours.add(new OpeningHours(in));
						}
						in.endArray();
						break;
					case weekday_text:
						in.beginArray();
						while (in.hasNext()) {
							if (mFmtOpenHours == null) {
								mFmtOpenHours = new ArrayList<String>(7);
							}
							mFmtOpenHours.add(in.nextString());
						}
						in.endArray();
						break;
					default:
						in.skipValue();
					}
				}
				in.endObject();
				break;
			case permanently_closed:
				mPermClosed = in.nextBoolean();
				break;
			case events:
				in.beginArray();
				while (in.hasNext()) {
					if (mEvents == null) {
						int cap = Maths.clamp(maxResults, 0, MAX_EVENTS);
						mEvents = new ArrayList<Event>(cap > 0 ? cap : MAX_EVENTS);
					}
					if (maxResults <= 0 || mEvents.size() < maxResults) {
						mEvents.add(new Event(in));
					} else {
						in.skipValue();
					}
				}
				in.endArray();
				break;
			case utc_offset:
				mUtcOffset = in.nextInt();
				break;
			case photos:
				in.beginArray();
				while (in.hasNext()) {
					if (mPhotos == null) {
						int cap = Maths.clamp(maxResults, 0, MAX_PHOTOS);
						mPhotos = new ArrayList<Photo>(cap > 0 ? cap : MAX_PHOTOS);
					}
					if (maxResults <= 0 || mPhotos.size() < maxResults) {
						mPhotos.add(new Photo(in));
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

	/**
	 * Read field values from a types array.
	 */
	void types(JsonReader in) throws IOException {
		in.beginArray();
		while (in.hasNext()) {
			if (mTypes == null) {
				mTypes = new ArrayList<String>();
			}
			mTypes.add(in.nextString());
		}
		in.endArray();
	}

	/**
	 * Unique identifier that can be used to retrieve details about this place.
	 * 
	 * @since 1.5.0
	 */
	public Id getPlaceId() {
		return mPlaceId;
	}

	/**
	 * Alternative identifiers that have been mapped to {@link #getPlaceId() the main one}.
	 * 
	 * @since 1.5.0
	 */
	public List<Id> getAltIds() {
		if (mAltIds != null && !(mAltIds instanceof ImmutableList)) {
			mAltIds = ImmutableList.copyOf(mAltIds);
		}
		return mAltIds;
	}

	/**
	 * Unique identifier that can be used to consolidate information about this place.
	 * 
	 * @deprecated use {@link Place.Id#getId() getPlaceId().getId()} instead
	 */
	@Deprecated
	public String getId() {
		return mId;
	}

	/**
	 * Token that can be used to retrieve details about this place. This may be one of multiple
	 * references that can be used to access this place.
	 * 
	 * @deprecated use {@link Place.Id#getId() getPlaceId().getId()} instead
	 */
	@Deprecated
	public String getReference() {
		return mReference;
	}

	/**
	 * URL for an icon representing this type of place.
	 */
	public String getIcon() {
		return mIcon;
	}

	/**
	 * Google Place page.
	 */
	public String getUrl() {
		return mUrl;
	}

	/**
	 * Default value: {@link Double#NEGATIVE_INFINITY}.
	 */
	public double getLatitude() {
		return mLat;
	}

	/**
	 * Default value: {@link Double#NEGATIVE_INFINITY}.
	 */
	public double getLongitude() {
		return mLong;
	}

	/**
	 * Name of this place, for example a business or landmark name.
	 */
	public String getName() {
		return mName;
	}

	/**
	 * All address components in separate properties.
	 */
	public Address getAddress() {
		return mAddress;
	}

	/**
	 * All address components formatted together.
	 */
	public String getFormattedAddress() {
		return mFmtAddress;
	}

	/**
	 * Simplified address that stops after the city level.
	 */
	public String getVicinity() {
		return mVicinity;
	}

	/**
	 * Includes prefixed country code.
	 */
	public String getIntlPhoneNumber() {
		return mIntlPhone;
	}

	/**
	 * In local format.
	 */
	public String getFormattedPhoneNumber() {
		return mFmtPhone;
	}

	/**
	 * URL of the website for this place.
	 */
	public String getWebsite() {
		return mWebsite;
	}

	/**
	 * Features describing this place.
	 * 
	 * @see <a href="https://developers.google.com/places/supported_types" target="_blank">Supported
	 *      Place Types</a>
	 */
	public List<String> getTypes() {
		if (mTypes != null && !(mTypes instanceof ImmutableList)) {
			mTypes = ImmutableList.copyOf(mTypes);
		}
		return mTypes;
	}

	/**
	 * Relative level of average expenses at this place. From 0 (least expensive) to 4 (most
	 * expensive). Default value: -1.
	 */
	public int getPriceLevel() {
		return mPrice;
	}

	/**
	 * From 0.0 to 5.0, based on user reviews. Default value: -1.0.
	 */
	public float getRating() {
		return mRating;
	}

	/**
	 * Number of ratings that have been submitted. Default value: -1.
	 * 
	 * @since 1.3.0
	 */
	public int getRatingCount() {
		return mRatingCount;
	}

	/**
	 * Comments and ratings from Google users.
	 */
	public List<Review> getReviews() {
		if (mReviews != null && !(mReviews instanceof ImmutableList)) {
			mReviews = ImmutableList.copyOf(mReviews);
		}
		return mReviews;
	}

	/**
	 * True if this place is currently open.
	 */
	public Boolean getOpenNow() {
		return mOpen;
	}

	/**
	 * Opening and closing times for each day that this place is open.
	 */
	public List<OpeningHours> getOpeningHours() {
		if (mOpenHours != null && !(mOpenHours instanceof ImmutableList)) {
			mOpenHours = ImmutableList.copyOf(mOpenHours);
		}
		return mOpenHours;
	}

	/**
	 * Opening hours for each day of the week. e.g. ["Monday: 10:00 am – 6:00 pm", ...,
	 * "Sunday: Closed"]
	 * 
	 * @since 2.2.0
	 */
	public List<String> getFormattedOpeningHours() {
		if (mFmtOpenHours != null && !(mFmtOpenHours instanceof ImmutableList)) {
			mFmtOpenHours = ImmutableList.copyOf(mFmtOpenHours);
		}
		return mFmtOpenHours;
	}

	/**
	 * True if this place has permanently shut down.
	 * 
	 * @since 2.2.0
	 */
	public boolean isPermanentlyClosed() {
		return mPermClosed;
	}

	/**
	 * Current events happening at this place.
	 * 
	 * @deprecated the Places API no longer returns events
	 */
	@Deprecated
	public List<Event> getEvents() {
		if (mEvents != null && !(mEvents instanceof ImmutableList)) {
			mEvents = ImmutableList.copyOf(mEvents);
		}
		return mEvents;
	}

	/**
	 * Number of minutes this place's time zone is offset from UTC. Default value:
	 * {@link Integer#MIN_VALUE}.
	 */
	public int getUtcOffset() {
		return mUtcOffset;
	}

	/**
	 * Photos for this place that can be downloaded by supplying the {@link Photo#getReference()
	 * reference} to {@link Places#photo(Params)}.
	 */
	public List<Photo> getPhotos() {
		if (mPhotos != null && !(mPhotos instanceof ImmutableList)) {
			mPhotos = ImmutableList.copyOf(mPhotos);
		}
		return mPhotos;
	}

	@Override
	public int hashCode() {
		if (mHash == 0) {
			mHash = mPlaceId != null ? mPlaceId.hashCode() : super.hashCode();
		}
		return mHash;
	}

	/**
	 * True if they have the same {@link #getPlaceId() place ID}.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			if (this == obj) {
				return true;
			} else if (obj instanceof Place && mPlaceId != null) {
				return mPlaceId.equals(((Place) obj).mPlaceId);
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return helper().toString();
	}

	/**
	 * Shared ToStringHelper for subclasses.
	 */
	ToStringHelper helper() {
		return MoreObjects.toStringHelper(this).add("placeId", mPlaceId)
				.add("altIds", mAltIds != null ? mAltIds.size() : null).add("id", mId)
				.add("reference", mReference).add("icon", mIcon).add("url", mUrl)
				.add("latitude", mLat != Double.NEGATIVE_INFINITY ? mLat : null)
				.add("longitude", mLong != Double.NEGATIVE_INFINITY ? mLong : null)
				.add("name", mName).add("address", mAddress != null ? true : null)
				.add("formattedAddress", mFmtAddress).add("vicinity", mVicinity)
				.add("intlPhoneNumber", mIntlPhone).add("formattedPhoneNumber", mFmtPhone)
				.add("website", mWebsite).add("types", mTypes)
				.add("priceLevel", mPrice != -1 ? mPrice : null)
				.add("rating", mRating != -1.0f ? mRating : null)
				.add("ratingCount", mRatingCount != -1 ? mRatingCount : null)
				.add("reviews", mReviews != null ? mReviews.size() : null).add("openNow", mOpen)
				.add("openingHours", mOpenHours != null ? mOpenHours.size() : null)
				.add("formattedOpeningHours", mFmtOpenHours != null ? true : null)
				.add("permanentlyClosed", mPermClosed)
				.add("events", mEvents != null ? mEvents.size() : null)
				.add("utcOffset", mUtcOffset != Integer.MIN_VALUE ? mUtcOffset : null)
				.add("photos", mPhotos != null ? mPhotos.size() : null).omitNullValues();
	}

	/**
	 * Unique identifier that can be used to retrieve details about a place.
	 * 
	 * @since 1.5.0
	 */
	public static class Id {
		private String mId;
		private Scope mScope;
		private int mHash;

		private Id() {
		}

		/**
		 * Read fields from an alt_id object.
		 */
		private Id(JsonReader in) throws IOException {
			in.beginObject();
			while (in.hasNext()) {
				switch (Key.get(in.nextName())) {
				case place_id:
					mId = in.nextString();
					break;
				case scope:
					mScope = Scope.get(in.nextString());
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
		}

		/**
		 * Set the place ID of the Id, creating it if necessary.
		 */
		private static Id id(Id id, String placeId) {
			if (id == null) {
				id = new Id();
			}
			id.mId = placeId;
			return id;
		}

		/**
		 * Unique identifier that can be used to retrieve details about the place.
		 */
		public String getId() {
			return mId;
		}

		/**
		 * Set the scope of the Id, creating it if necessary.
		 */
		private static Id scope(Id id, String scope) {
			if (id == null) {
				id = new Id();
			}
			id.mScope = Scope.get(scope);
			return id;
		}

		/**
		 * Availability of this place ID.
		 */
		public Scope getScope() {
			return mScope;
		}

		@Override
		public int hashCode() {
			if (mHash == 0) {
				mHash = mId != null ? mId.hashCode() : super.hashCode();
			}
			return mHash;
		}

		/**
		 * True if they have the same {@link #getId() ID}.
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj != null) {
				if (this == obj) {
					return true;
				} else if (obj instanceof Id && mId != null) {
					return mId.equals(((Id) obj).mId);
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("id", mId).add("scope", mScope)
					.omitNullValues().toString();
		}

		/**
		 * Availability of a place ID.
		 */
		public enum Scope {
			/** Local to an application. */
			APP,
			/** Publicly available. */
			GOOGLE;

			/**
			 * Get the matching Scope or null if one can't be found.
			 */
			private static Scope get(String scope) {
				try {
					return Scope.valueOf(scope);
				} catch (IllegalArgumentException e) {
					return null;
				}
			}
		}

		/**
		 * Search or autocomplete {@link Params#filter(Predicate) filter} on place IDs. After
		 * creating an instance, call {@link #include(String...) include} or
		 * {@link #exclude(String...) exclude} to provide the IDs to filter.
		 * 
		 * @since 1.6.0
		 */
		public static class Filter implements Predicate<Place> {
			/** Include or exclude the IDs. */
			private boolean mInclude;
			private final HashSet<String> mIds = new HashSet<String>();

			/**
			 * Add the IDs to those that the place must match to be returned.
			 */
			public Filter include(String... ids) {
				if (!mInclude) {
					reset().mInclude = true;
				}
				Collections.addAll(mIds, ids);
				return this;
			}

			/**
			 * Add the IDs to those that the place must not match to be returned.
			 */
			public Filter exclude(String... ids) {
				if (mInclude) {
					reset().mInclude = false;
				}
				Collections.addAll(mIds, ids);
				return this;
			}

			/**
			 * Reset the list of IDs to match.
			 */
			public Filter reset() {
				mIds.clear();
				return this;
			}

			@Override
			public boolean apply(Place place) {
				boolean contains = mIds.contains(place.getPlaceId().getId());
				return mInclude ? contains : !contains;
			}

			@Override
			public int hashCode() {
				return Objects.hashCode(mInclude, mIds);
			}

			@Override
			public boolean equals(Object obj) {
				if (obj != null) {
					if (this == obj) {
						return true;
					} else if (obj instanceof Filter) {
						Filter o = (Filter) obj;
						return mInclude == o.mInclude && Objects.equal(mIds, o.mIds);
					}
				}
				return false;
			}

			@Override
			public String toString() {
				return MoreObjects.toStringHelper(this)
						.add("filter", mInclude ? "include" : "exclude").add("ids", mIds)
						.omitNullValues().toString();
			}
		}
	}

	/**
	 * All address components in separate properties. For each property the intention is to have a
	 * full name, e.g. "New York", and an abbreviated name, e.g. "NY". Though note that as of May
	 * 2013, Google Places data often provides the same value for both properties. Typically this is
	 * the full name, though for countries and states/provinces it is often the abbreviated name.
	 * Properties will be null when the value is not available.
	 */
	public static class Address {
		private String mCountry;
		private String mCountryAbbr;
		private String mAdminL1;
		private String mAdminL1Abbr;
		private String mAdminL2;
		private String mAdminL2Abbr;
		private String mLocality;
		private String mLocalityAbbr;
		private String mSublocality;
		private String mSublocalityAbbr;
		private String mPostalCode;
		private String mPostalCodeAbbr;
		private String mPostalTown;
		private String mPostalTownAbbr;
		private String mRoute;
		private String mRouteAbbr;
		private String mStreetNum;
		private String mStreetNumAbbr;
		private int mHash;

		/**
		 * Read fields from an address components array.
		 */
		private Address(JsonReader in) throws IOException {
			in.beginArray();
			while (in.hasNext()) {
				String longName = null;
				String shortName = null;
				Type type = null;

				in.beginObject();
				while (in.hasNext()) {
					switch (Key.get(in.nextName())) {
					case long_name:
						longName = in.nextString();
						break;
					case short_name:
						shortName = in.nextString();
						break;
					case types:
						in.beginArray();
						while (in.hasNext()) {
							if (type == null) { // only use the first match, ignore "political"
								type = Type.get(in.nextString());
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

				if (type != null) {
					switch (type) {
					case country:
						mCountry = longName;
						mCountryAbbr = shortName;
						break;
					case administrative_area_level_1:
						mAdminL1 = longName;
						mAdminL1Abbr = shortName;
						break;
					case administrative_area_level_2:
						mAdminL2 = longName;
						mAdminL2Abbr = shortName;
						break;
					case locality:
						mLocality = longName;
						mLocalityAbbr = shortName;
						break;
					case sublocality:
						mSublocality = longName;
						mSublocalityAbbr = shortName;
						break;
					case postal_code:
						mPostalCode = longName;
						mPostalCodeAbbr = shortName;
						break;
					case postal_town:
						mPostalTown = longName;
						mPostalTownAbbr = shortName;
						break;
					case route:
						mRoute = longName;
						mRouteAbbr = shortName;
						break;
					case street_number:
						mStreetNum = longName;
						mStreetNumAbbr = shortName;
						break;
					}
				}
			}
			in.endArray();
		}

		/**
		 * Types of address components that are currently supported.
		 */
		private enum Type {
			country, administrative_area_level_1, administrative_area_level_2, locality,
			sublocality, postal_code, postal_town, route, street_number;

			/**
			 * Get the matching Type or null if one can't be found.
			 */
			private static Type get(String type) {
				try {
					return Type.valueOf(type);
				} catch (IllegalArgumentException e) {
					return null;
				}
			}
		}

		public String getCountry() {
			return mCountry;
		}

		public String getCountryAbbr() {
			return mCountryAbbr;
		}

		/**
		 * State or province.
		 */
		public String getAdminAreaL1() {
			return mAdminL1;
		}

		public String getAdminAreaL1Abbr() {
			return mAdminL1Abbr;
		}

		/**
		 * County or region.
		 */
		public String getAdminAreaL2() {
			return mAdminL2;
		}

		public String getAdminAreaL2Abbr() {
			return mAdminL2Abbr;
		}

		/**
		 * City.
		 */
		public String getLocality() {
			return mLocality;
		}

		public String getLocalityAbbr() {
			return mLocalityAbbr;
		}

		/**
		 * City district.
		 */
		public String getSublocality() {
			return mSublocality;
		}

		public String getSublocalityAbbr() {
			return mSublocalityAbbr;
		}

		public String getPostalCode() {
			return mPostalCode;
		}

		public String getPostalCodeAbbr() {
			return mPostalCodeAbbr;
		}

		public String getPostalTown() {
			return mPostalTown;
		}

		public String getPostalTownAbbr() {
			return mPostalTownAbbr;
		}

		/**
		 * Street.
		 */
		public String getRoute() {
			return mRoute;
		}

		public String getRouteAbbr() {
			return mRouteAbbr;
		}

		public String getStreetNumber() {
			return mStreetNum;
		}

		public String getStreetNumberAbbr() {
			return mStreetNumAbbr;
		}

		@Override
		public int hashCode() {
			if (mHash == 0) {
				mHash = Objects.hashCode(mCountry, mCountryAbbr, mAdminL1, mAdminL1Abbr, mAdminL2,
						mAdminL2Abbr, mLocality, mLocalityAbbr, mSublocality, mSublocalityAbbr,
						mPostalCode, mPostalCodeAbbr, mPostalTown, mPostalTownAbbr, mRoute,
						mRouteAbbr, mStreetNum, mStreetNumAbbr);
			}
			return mHash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null) {
				if (this == obj) {
					return true;
				} else if (obj instanceof Address) {
					Address o = (Address) obj;
					return Objects.equal(mCountry, o.mCountry)
							&& Objects.equal(mCountryAbbr, o.mCountryAbbr)
							&& Objects.equal(mAdminL1, o.mAdminL1)
							&& Objects.equal(mAdminL1Abbr, o.mAdminL1Abbr)
							&& Objects.equal(mAdminL2, o.mAdminL2)
							&& Objects.equal(mAdminL2Abbr, o.mAdminL2Abbr)
							&& Objects.equal(mLocality, o.mLocality)
							&& Objects.equal(mLocalityAbbr, o.mLocalityAbbr)
							&& Objects.equal(mSublocality, o.mSublocality)
							&& Objects.equal(mSublocalityAbbr, o.mSublocalityAbbr)
							&& Objects.equal(mPostalCode, o.mPostalCode)
							&& Objects.equal(mPostalCodeAbbr, o.mPostalCodeAbbr)
							&& Objects.equal(mPostalTown, o.mPostalTown)
							&& Objects.equal(mPostalTownAbbr, o.mPostalTownAbbr)
							&& Objects.equal(mRoute, o.mRoute)
							&& Objects.equal(mRouteAbbr, o.mRouteAbbr)
							&& Objects.equal(mStreetNum, o.mStreetNum)
							&& Objects.equal(mStreetNumAbbr, o.mStreetNumAbbr);
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("country", mCountry)
					.add("countryAbbr", mCountryAbbr).add("adminL1", mAdminL1)
					.add("adminL1Abbr", mAdminL1Abbr).add("adminL2", mAdminL2)
					.add("adminL2Abbr", mAdminL2Abbr).add("locality", mLocality)
					.add("localityAbbr", mLocalityAbbr).add("sublocality", mSublocality)
					.add("sublocalityAbbr", mSublocalityAbbr).add("postalCode", mPostalCode)
					.add("postalCodeAbbr", mPostalCodeAbbr).add("postalTown", mPostalTown)
					.add("postalTownAbbr", mPostalTownAbbr).add("route", mRoute)
					.add("routeAbbr", mRouteAbbr).add("streetNum", mStreetNum)
					.add("streetNumAbbr", mStreetNumAbbr).omitNullValues().toString();
		}
	}

	/**
	 * Comments and ratings from a Google user.
	 */
	public static class Review {
		/** Maximum number of aspects that will be returned. */
		private static final int MAX_ASPECTS = 3;

		private String mAuthorName;
		private String mAuthorUrl;
		private long mTime;
		private List<Aspect> mAspects;
		private int mRating;
		private String mLanguage;
		private String mText;
		private int mHash;

		/**
		 * Read fields from a review object.
		 */
		private Review(JsonReader in) throws IOException {
			in.beginObject();
			while (in.hasNext()) {
				switch (Key.get(in.nextName())) {
				case author_name:
					mAuthorName = in.nextString();
					break;
				case author_url:
					mAuthorUrl = in.nextString();
					break;
				case time:
					mTime = in.nextLong();
					break;
				case aspects:
					in.beginArray();
					while (in.hasNext()) {
						if (mAspects == null) {
							mAspects = new ArrayList<Aspect>(MAX_ASPECTS);
						}
						mAspects.add(new Aspect(in));
					}
					in.endArray();
					break;
				case rating:
					mRating = in.nextInt();
					break;
				case language:
					mLanguage = in.nextString();
					break;
				case text:
					mText = in.nextString();
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
		}

		public String getAuthorName() {
			return mAuthorName;
		}

		/**
		 * Google+ profile.
		 */
		public String getAuthorUrl() {
			return mAuthorUrl;
		}

		/**
		 * When the review was submitted, in epoch seconds.
		 */
		public long getTime() {
			return mTime;
		}

		/**
		 * Ratings for different attributes of the place. The first element is the primary aspect.
		 */
		public List<Aspect> getAspects() {
			if (mAspects != null && !(mAspects instanceof ImmutableList)) {
				mAspects = ImmutableList.copyOf(mAspects);
			}
			return mAspects;
		}

		/**
		 * From 1 to 5, the user's overall rating. Default value: 0.
		 * 
		 * @since 1.2.0
		 */
		public int getRating() {
			return mRating;
		}

		/**
		 * IETF language code (without country code) for the language of the {@link #getText() text}
		 * .
		 * 
		 * @since 1.2.0
		 */
		public String getLanguage() {
			return mLanguage;
		}

		/**
		 * Review comments, which can contain HTML character and entity references.
		 */
		public String getText() {
			return mText;
		}

		@Override
		public int hashCode() {
			if (mHash == 0) {
				mHash = Objects.hashCode(mAuthorName, mAuthorUrl, mTime, mAspects, mRating,
						mLanguage, mText);
			}
			return mHash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null) {
				if (this == obj) {
					return true;
				} else if (obj instanceof Review) {
					Review o = (Review) obj;
					return Objects.equal(mAuthorName, o.mAuthorName)
							&& Objects.equal(mAuthorUrl, o.mAuthorUrl) && mTime == o.mTime
							&& Objects.equal(mAspects, o.mAspects) && mRating == o.mRating
							&& Objects.equal(mLanguage, o.mLanguage)
							&& Objects.equal(mText, o.mText);
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("authorName", mAuthorName)
					.add("authorUrl", mAuthorUrl).add("time", mTime)
					.add("aspects", mAspects != null ? mAspects.size() : null)
					.add("rating", mRating != 0 ? mRating : null).add("language", mLanguage)
					.add("text", mText).omitNullValues().toString();
		}

		/**
		 * Rating for one attribute of a place.
		 */
		public static class Aspect {
			private String mType;
			private int mRating;
			private int mHash;

			/**
			 * Read fields from an aspect object.
			 */
			private Aspect(JsonReader in) throws IOException {
				in.beginObject();
				while (in.hasNext()) {
					switch (Key.get(in.nextName())) {
					case type:
						mType = in.nextString();
						break;
					case rating:
						mRating = in.nextInt();
						break;
					default:
						in.skipValue();
					}
				}
				in.endObject();
			}

			/**
			 * The aspect that was rated, e.g. atmosphere, service, food, overall, etc.
			 */
			public String getType() {
				return mType;
			}

			/**
			 * From 0 to 3.
			 */
			public int getRating() {
				return mRating;
			}

			@Override
			public int hashCode() {
				if (mHash == 0) {
					mHash = Objects.hashCode(mType, mRating);
				}
				return mHash;
			}

			@Override
			public boolean equals(Object obj) {
				if (obj != null) {
					if (this == obj) {
						return true;
					} else if (obj instanceof Aspect) {
						Aspect o = (Aspect) obj;
						return Objects.equal(mType, o.mType) && mRating == o.mRating;
					}
				}
				return false;
			}

			@Override
			public String toString() {
				return MoreObjects.toStringHelper(this).add("type", mType).add("rating", mRating)
						.omitNullValues().toString();
			}
		}
	}

	/**
	 * Opening and closing times for a day (or span of days) on which a place is open.
	 */
	public static class OpeningHours {
		private DayOfWeek mOpenDay;
		private int mOpenTime = -1;
		private DayOfWeek mCloseDay;
		private int mCloseTime = -1;
		private int mHash;

		/**
		 * Read fields from a period object.
		 */
		private OpeningHours(JsonReader in) throws IOException {
			in.beginObject();
			while (in.hasNext()) {
				switch (Key.get(in.nextName())) {
				case open:
					in.beginObject();
					while (in.hasNext()) {
						switch (Key.get(in.nextName())) {
						case day:
							mOpenDay = day(in.nextInt());
							break;
						case time:
							mOpenTime = Integer.parseInt(in.nextString());
							break;
						default:
							in.skipValue();
						}
					}
					in.endObject();
					break;
				case close:
					in.beginObject();
					while (in.hasNext()) {
						switch (Key.get(in.nextName())) {
						case day:
							mCloseDay = day(in.nextInt());
							break;
						case time:
							mCloseTime = Integer.parseInt(in.nextString());
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
		}

		/**
		 * Get the DayOfWeek for the day number, where 0 == Sunday.
		 */
		private DayOfWeek day(int day) {
			return DayOfWeek.values()[Maths.rollover(day - 1, 0, 6)]; // DayOfWeek starts on Monday
		}

		public DayOfWeek getOpenDay() {
			return mOpenDay;
		}

		/**
		 * 0-2359. Default value: -1.
		 * 
		 * @since 2.3.0
		 */
		public int getOpenTime() {
			return mOpenTime;
		}

		/**
		 * 0-23. Default value: -1.
		 */
		public int getOpenHour() {
			return mOpenTime >= 0 ? mOpenTime / 100 : mOpenTime;
		}

		/**
		 * 0-59. Default value: -1.
		 */
		public int getOpenMinute() {
			return mOpenTime % 100;
		}

		/**
		 * Converted to epoch milliseconds. Default value: -1.
		 * 
		 * @since 2.3.0
		 */
		public int getOpenTimeMillis() {
			return mOpenTime >= 0 ? millis(getOpenHour(), getOpenMinute()) : mOpenTime;
		}

		public DayOfWeek getCloseDay() {
			return mCloseDay;
		}

		/**
		 * 0-2359. Default value: -1.
		 * 
		 * @since 2.3.0
		 */
		public int getCloseTime() {
			return mCloseTime;
		}

		/**
		 * 0-23. Default value: -1.
		 */
		public int getCloseHour() {
			return mCloseTime >= 0 ? mCloseTime / 100 : mCloseTime;
		}

		/**
		 * 0-59. Default value: -1.
		 */
		public int getCloseMinute() {
			return mCloseTime % 100;
		}

		/**
		 * Converted to epoch milliseconds. Default value: -1.
		 * 
		 * @since 2.3.0
		 */
		public int getCloseTimeMillis() {
			return mCloseTime >= 0 ? millis(getCloseHour(), getCloseMinute()) : mCloseTime;
		}

		/**
		 * Convert the hours and minutes to epoch milliseconds.
		 */
		private int millis(int hours, int minutes) {
			return (hours * 60 + minutes) * 60 * 1000 - TimeZone.getDefault().getOffset(0L);
		}

		@Override
		public int hashCode() {
			if (mHash == 0) {
				mHash = Objects.hashCode(mOpenDay, mOpenTime, mCloseDay, mCloseTime);
			}
			return mHash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null) {
				if (this == obj) {
					return true;
				} else if (obj instanceof OpeningHours) {
					OpeningHours o = (OpeningHours) obj;
					return mOpenDay == o.mOpenDay && mOpenTime == o.mOpenTime
							&& mCloseDay == o.mCloseDay && mCloseTime == o.mCloseTime;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("openDay", mOpenDay)
					.add("openTime", mOpenTime).add("closeDay", mCloseDay)
					.add("closeTime", mCloseTime).omitNullValues().toString();
		}
	}

	/**
	 * Current event happening at a place.
	 * 
	 * @deprecated the Places API no longer returns events
	 */
	@Deprecated
	public static class Event {
		private String mId;
		private long mTime;
		private String mSummary;
		private String mUrl;
		private int mHash;

		/**
		 * Read fields from an event object.
		 */
		private Event(JsonReader in) throws IOException {
			in.beginObject();
			while (in.hasNext()) {
				switch (Key.get(in.nextName())) {
				case event_id:
					mId = in.nextString();
					break;
				case start_time:
					mTime = in.nextLong();
					break;
				case summary:
					mSummary = in.nextString();
					break;
				case url:
					mUrl = in.nextString();
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
		}

		/**
		 * Unique identifier for this event.
		 */
		public String getId() {
			return mId;
		}

		/**
		 * When the event starts, in epoch seconds.
		 */
		public long getStartTime() {
			return mTime;
		}

		/**
		 * Description of the event, which can contain HTML.
		 */
		public String getSummary() {
			return mSummary;
		}

		/**
		 * Web page with details about the event.
		 */
		public String getUrl() {
			return mUrl;
		}

		@Override
		public int hashCode() {
			if (mHash == 0) {
				mHash = Objects.hashCode(mId, mTime, mSummary, mUrl);
			}
			return mHash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null) {
				if (this == obj) {
					return true;
				} else if (obj instanceof Event) {
					Event o = (Event) obj;
					return Objects.equal(mId, o.mId) && mTime == o.mTime
							&& Objects.equal(mSummary, o.mSummary) && Objects.equal(mUrl, o.mUrl);
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("id", mId).add("startTime", mTime)
					.add("summary", mSummary).add("url", mUrl).omitNullValues().toString();
		}
	}

	/**
	 * Photo for a place that can be downloaded by supplying the {@link #getReference() reference}
	 * to {@link Places#photo(Params)}.
	 */
	public static class Photo {
		/** Maximum number of HTML attributions that are expected to be returned. */
		private static final int MAX_ATTRIBS = 2; // usually 1

		private String mReference;
		private int mWidth;
		private int mHeight;
		private List<String> mAttribs;
		private int mHash;

		/**
		 * Read fields from a photo object.
		 */
		private Photo(JsonReader in) throws IOException {
			in.beginObject();
			while (in.hasNext()) {
				switch (Key.get(in.nextName())) {
				case photo_reference:
					mReference = in.nextString();
					break;
				case width:
					mWidth = in.nextInt();
					break;
				case height:
					mHeight = in.nextInt();
					break;
				case html_attributions:
					in.beginArray();
					while (in.hasNext()) {
						if (mAttribs == null) {
							mAttribs = new ArrayList<String>(MAX_ATTRIBS);
						}
						mAttribs.add(in.nextString());
					}
					in.endArray();
					break;
				default:
					in.skipValue();
				}
			}
			in.endObject();
		}

		/**
		 * Token that can be used to download the photo by supplying it to
		 * {@link Places#photo(Params)}.
		 */
		public String getReference() {
			return mReference;
		}

		/**
		 * Maximum available pixels.
		 */
		public int getWidth() {
			return mWidth;
		}

		/**
		 * Maximum available pixels.
		 */
		public int getHeight() {
			return mHeight;
		}

		/**
		 * Attributions that must be displayed along with the photo if non-null.
		 */
		public List<String> getHtmlAttributions() {
			if (mAttribs != null && !(mAttribs instanceof ImmutableList)) {
				mAttribs = ImmutableList.copyOf(mAttribs);
			}
			return mAttribs;
		}

		@Override
		public int hashCode() {
			if (mHash == 0) {
				mHash = Objects.hashCode(mReference, mWidth, mHeight, mAttribs);
			}
			return mHash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null) {
				if (this == obj) {
					return true;
				} else if (obj instanceof Photo) {
					Photo o = (Photo) obj;
					return Objects.equal(mReference, o.mReference) && mWidth == o.mWidth
							&& mHeight == o.mHeight && Objects.equal(mAttribs, o.mAttribs);
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("reference", mReference)
					.add("width", mWidth).add("height", mHeight)
					.add("htmlAttributions", mAttribs != null ? mAttribs.size() : null)
					.omitNullValues().toString();
		}
	}

	/**
	 * Place or query that was returned from a {@link Places} autocomplete method.
	 */
	public static class Prediction extends Place {
		/**
		 * Technically, there could be more, though it appears only one is ever returned. Add an
		 * extra slot, just in case.
		 */
		private static final int MAX_MATCHES = 2;

		private List<Substring> mTerms;
		private List<Substring> mMatches;

		/**
		 * Read fields from a prediction object.
		 * 
		 * @param fields
		 *            to read or 0 if all fields should be read
		 */
		Prediction(JsonReader in, int fields) throws IOException {
			in.beginObject();
			while (in.hasNext()) {
				Key key = Key.get(in.nextName());
				if (key == UNKNOWN || fields != 0 && key.mField != null && !key.mField.in(fields)) {
					in.skipValue(); // unknown field or caller doesn't want it
					continue;
				}

				switch (key) {
				case place_id:
					mPlaceId = Id.id(mPlaceId, in.nextString());
					break;
				case id:
					mId = in.nextString();
					break;
				case reference:
					mReference = in.nextString();
					break;
				case description:
					mName = in.nextString();
					break;
				case types:
					types(in);
					break;
				case terms:
					in.beginArray();
					while (in.hasNext()) {
						int offset = -1;
						String value = null;
						in.beginObject();
						while (in.hasNext()) {
							switch (Key.get(in.nextName())) {
							case offset:
								offset = in.nextInt();
								break;
							case value:
								value = in.nextString();
								break;
							default:
								in.skipValue();
							}
						}
						in.endObject();

						if (offset >= 0 && !Strings.isNullOrEmpty(value)) {
							if (mTerms == null) {
								mTerms = new ArrayList<Substring>();
							}
							mTerms.add(new Substring(offset, value.length(), value, mName));
						}
					}
					in.endArray();
					break;
				case matched_substrings:
					in.beginArray();
					while (in.hasNext()) {
						int offset = -1;
						int length = 0;
						in.beginObject();
						while (in.hasNext()) {
							switch (Key.get(in.nextName())) {
							case offset:
								offset = in.nextInt();
								break;
							case length:
								length = in.nextInt();
								break;
							default:
								in.skipValue();
							}
						}
						in.endObject();

						if (offset >= 0 && length > 0) {
							int end = offset + length;
							String value = mName != null && mName.length() >= end ? mName
									.substring(offset, end) : null;
							if (mMatches == null) {
								mMatches = new ArrayList<Substring>(MAX_MATCHES);
							}
							mMatches.add(new Substring(offset, length, value, mName));
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

		/**
		 * Sections in the {@link Place#getName() name}.
		 */
		public List<Substring> getTerms() {
			if (mTerms != null && !(mTerms instanceof ImmutableList)) {
				mTerms = ImmutableList.copyOf(mTerms);
			}
			return mTerms;
		}

		/**
		 * Substrings in the {@link Place#getName() name} that match the search text, often used for
		 * highlighting.
		 */
		public List<Substring> getMatchedSubstrings() {
			if (mMatches != null && !(mMatches instanceof ImmutableList)) {
				mMatches = ImmutableList.copyOf(mMatches);
			}
			return mMatches;
		}

		/**
		 * True if they have the same {@link #getPlaceId() place ID} and
		 * {@link #getMatchedSubstrings() matched substrings}.
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj != null) {
				if (this == obj) {
					return true;
				} else if (obj.getClass().equals(Place.class)) {
					return super.equals(obj);
				} else if (obj instanceof Prediction) {
					Prediction o = (Prediction) obj;
					return super.equals(o) && Objects.equal(mMatches, o.mMatches);
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return helper().add("terms", mTerms != null ? mTerms.size() : null)
					.add("matchedSubstrings", mMatches != null ? mMatches.size() : null).toString();
		}
	}

	/**
	 * Search or autocomplete {@link Params#filter(Predicate) filter} on place IDs. After creating
	 * an instance, call {@link #include(String...) include} or {@link #exclude(String...) exclude}
	 * to provide the IDs to filter.
	 * 
	 * @since 1.4.0
	 * @deprecated use {@link Place.Id.Filter} instead
	 */
	@Deprecated
	public static class IdPredicate implements Predicate<Place> {
		/** Include or exclude the IDs. */
		private boolean mInclude;
		private final HashSet<String> mIds = new HashSet<String>();
		private int mHash;

		/**
		 * Add the IDs to those that the place must match to be returned.
		 */
		public IdPredicate include(String... ids) {
			if (!mInclude) {
				reset().mInclude = true;
			}
			Collections.addAll(mIds, ids);
			return this;
		}

		/**
		 * Add the IDs to those that the place must not match to be returned.
		 */
		public IdPredicate exclude(String... ids) {
			if (mInclude) {
				reset().mInclude = false;
			}
			Collections.addAll(mIds, ids);
			return this;
		}

		/**
		 * Reset the list of IDs to match.
		 */
		public IdPredicate reset() {
			mIds.clear();
			return this;
		}

		@Override
		public boolean apply(Place place) {
			return mInclude ? mIds.contains(place.getId()) : !mIds.contains(place.getId());
		}

		@Override
		public int hashCode() {
			if (mHash == 0) {
				mHash = Objects.hashCode(mInclude, mIds);
			}
			return mHash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj != null) {
				if (this == obj) {
					return true;
				} else if (obj instanceof IdPredicate) {
					IdPredicate o = (IdPredicate) obj;
					return mInclude == o.mInclude && Objects.equal(mIds, o.mIds);
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this).add("filter", mInclude ? "include" : "exclude")
					.add("ids", mIds).omitNullValues().toString();
		}
	}
}
