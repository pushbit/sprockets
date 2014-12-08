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

package net.sf.sprockets.widget;

import android.Manifest.permission;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.common.base.Predicate;

import net.sf.sprockets.R;
import net.sf.sprockets.google.LocalPlacesParams;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Place.Prediction;
import net.sf.sprockets.google.Places;
import net.sf.sprockets.google.Places.Params;
import net.sf.sprockets.google.Places.Response;
import net.sf.sprockets.google.Places.Response.Status;
import net.sf.sprockets.lang.Substring;

import java.io.IOException;
import java.util.List;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static net.sf.sprockets.google.Places.Response.Status.OK;
import static net.sf.sprockets.google.Places.Response.Status.ZERO_RESULTS;

/**
 * AutoCompleteTextView that provides local suggestions from the
 * <a href="https://developers.google.com/places/" target="_blank">Google Places API</a>.
 * <p>
 * Requires {@link permission#ACCESS_COARSE_LOCATION ACCESS_COARSE_LOCATION} (or
 * {@link permission#ACCESS_FINE_LOCATION FINE}) and
 * {@link permission#INTERNET INTERNET} permissions.
 * </p>
 * <p>
 * XML Attributes: {@link #setRadius(int) radius}, {@link #setTypes(String) types},
 * {@link #setCountries(String) countries}, {@link #setLanguage(String) language},
 * {@link #setMaxResults(int) maxResults}, {@link #setSuggestionLayout(int) suggestionLayout},
 * {@link #setMatchedSubstringColor(int) matchedSubstringColor}
 * </p>
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/res/layout/google_place_auto_complete.xml" target="_blank">Sample Layout</a>
 * </p>
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/java/net/sf/sprockets/sample/app/ui/GooglePlaceAutoCompleteActivity.java" target="_blank">Sample Usage</a>
 * </p>
 *
 * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_radius
 * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_types
 * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_countries
 * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_language
 * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_maxResults
 * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_suggestionLayout
 * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_matchedSubstringColor
 */
public class GooglePlaceAutoComplete extends AutoCompleteTextView {
    private static final StyleSpan BOLD_SPAN = new StyleSpan(BOLD);

    /**
     * Used by the Filter to get results.
     */
    private final Params mParams;
    private int mRadius;
    private String mTypes;
    private String mCountries;
    private String mLanguage;
    private Predicate<Place> mFilter;
    private int mMaxResults;
    private int mLayout;
    private int mColor;
    private ForegroundColorSpan mColorSpan;
    private OnPlaceClickListener mListener;

    public GooglePlaceAutoComplete(Context context) {
        this(context, null);
    }

    public GooglePlaceAutoComplete(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.autoCompleteTextViewStyle);
    }

    public GooglePlaceAutoComplete(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        PlaceAdapter adapter = new PlaceAdapter();
        setAdapter(adapter);
        mParams = adapter.mFilter.mParams;
        /* theme attributes for defaults */
        TypedArray a = context.obtainStyledAttributes(new int[]{R.attr.matchedSubstringColor});
        int matchedSubstringColor = a.getColor(0, 0);
        a.recycle();
        /* element attributes */
        a = context.obtainStyledAttributes(attrs, R.styleable.GooglePlaceAutoComplete, defStyle, 0);
        setRadius(a.getInt(R.styleable.GooglePlaceAutoComplete_radius, 0));
        setTypes(a.getString(R.styleable.GooglePlaceAutoComplete_types));
        setCountries(a.getString(R.styleable.GooglePlaceAutoComplete_countries));
        setLanguage(a.getString(R.styleable.GooglePlaceAutoComplete_language));
        setMaxResults(a.getInt(R.styleable.GooglePlaceAutoComplete_maxResults, 0));
        setSuggestionLayout(
                a.getResourceId(R.styleable.GooglePlaceAutoComplete_suggestionLayout, 0));
        setMatchedSubstringColor(a.getColor(
                R.styleable.GooglePlaceAutoComplete_matchedSubstringColor, matchedSubstringColor));
        a.recycle();
    }

    /**
     * Prefer places within this many metres from the current location.
     *
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_radius
     */
    public GooglePlaceAutoComplete setRadius(int radius) {
        mRadius = radius;
        mParams.radius(radius);
        return this;
    }

    /**
     * Get the number of metres within which places will be preferred.
     *
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_radius
     */
    public int getRadius() {
        return mRadius;
    }

    /**
     * Autocomplete places of this type. Must be one of "geocode", "establishment", "(regions)", or
     * "(cities)".
     *
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_types
     */
    public GooglePlaceAutoComplete setTypes(String types) {
        mTypes = types;
        mParams.types((String[]) null); // reset list
        mParams.types(types);
        return this;
    }

    /**
     * Get the type of places that will be autocompleted.
     *
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_types
     */
    public String getTypes() {
        return mTypes;
    }

    /**
     * Autocomplete places in this country. Must be a two character ISO 3166-1 Alpha-2 compatible
     * country code, e.g. "GB". Currently only one country value is supported.
     *
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_countries
     */
    public GooglePlaceAutoComplete setCountries(String countries) {
        mCountries = countries;
        mParams.countries(countries);
        return this;
    }

    /**
     * Get the country code in which places will be autocompleted.
     *
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_countries
     */
    public String getCountries() {
        return mCountries;
    }

    /**
     * Return results in this language, if possible. Must be one of the supported language codes.
     *
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_language
     * @see <a href="https://spreadsheets.google.com/pub?key=p9pdwsai2hDMsLkXsoM05KQ&gid=1"
     * target="_blank">Supported Languages</a>
     */
    public GooglePlaceAutoComplete setLanguage(String language) {
        mLanguage = language;
        mParams.language(language);
        return this;
    }

    /**
     * Get the language code that results will be returned in.
     *
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_language
     */
    public String getLanguage() {
        return mLanguage;
    }

    /**
     * Only display places for which the filter returns true.
     */
    public GooglePlaceAutoComplete setPlaceFilter(Predicate<Place> filter) {
        mFilter = filter;
        mParams.filter(filter);
        return this;
    }

    /**
     * Get the filter that must return true for a place to be displayed.
     */
    public Predicate<Place> getPlaceFilter() {
        return mFilter;
    }

    /**
     * Return this many results, at most.
     *
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_maxResults
     */
    public GooglePlaceAutoComplete setMaxResults(int maxResults) {
        mMaxResults = maxResults;
        mParams.maxResults(maxResults);
        return this;
    }

    /**
     * Get the maximum number of results that will be returned.
     *
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_maxResults
     */
    public int getMaxResults() {
        return mMaxResults;
    }

    /**
     * Use the layout resource with a TextView for each suggestion.
     *
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_suggestionLayout
     */
    public GooglePlaceAutoComplete setSuggestionLayout(int layout) {
        mLayout = layout;
        return this;
    }

    /**
     * Get the layout resource that will be used for each suggestion.
     *
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_suggestionLayout
     */
    public int getSuggestionLayout() {
        return mLayout;
    }

    /**
     * Highlight matched substrings with the color.
     *
     * @param color 0 for no color highlight
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_matchedSubstringColor
     */
    public GooglePlaceAutoComplete setMatchedSubstringColor(int color) {
        mColor = color;
        mColorSpan = color != 0 ? new ForegroundColorSpan(color) : null;
        return this;
    }

    /**
     * Get the color that will be used to highlight matched substrings.
     *
     * @attr ref net.sf.sprockets.R.styleable#GooglePlaceAutoComplete_matchedSubstringColor
     */
    public int getMatchedSubstringColor() {
        return mColor;
    }

    /**
     * Register a callback to invoke when a place in autocomplete suggestions is clicked.
     */
    public void setOnPlaceClickListener(final OnPlaceClickListener listener) {
        mListener = listener;
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onPlaceClick(parent, (Prediction) parent.getItemAtPosition(position),
                        position);
            }
        });
    }

    /**
     * Get the callback that was registered.
     */
    public OnPlaceClickListener getOnPlaceClickListener() {
        return mListener;
    }

    /**
     * Callback to invoke when a place in autocomplete suggestions is clicked.
     */
    public static interface OnPlaceClickListener {
        /**
         * The place at the position in the autocomplete suggestions was clicked.
         *
         * @param place with {@link Place#getPlaceId() placeId}, {@link Place#getName() name}, and
         *              {@link Place#getTypes() types} properties populated
         */
        void onPlaceClick(AdapterView<?> parent, Prediction place, int position);
    }

    /**
     * Translates Predictions to Views.
     */
    private class PlaceAdapter extends BaseAdapter implements Filterable {
        private final PlaceFilter mFilter = new PlaceFilter();
        private List<Prediction> mPredictions;
        private int mCount;

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Prediction getItem(int position) {
            return mPredictions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = convertView != null ? (TextView) convertView
                    : (TextView) LayoutInflater.from(getContext())
                    .inflate(mLayout != 0 ? mLayout
                            : android.R.layout.simple_spinner_dropdown_item, parent, false);
            Prediction pred = getItem(position);
            SpannableString s = new SpannableString(pred.getName());
            for (Substring sub : pred.getMatchedSubstrings()) { // highlight matching substrings
                int start = sub.getOffset();
                int end = start + sub.getLength();
                s.setSpan(BOLD_SPAN, start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
                if (mColorSpan != null) {
                    s.setSpan(mColorSpan, start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            view.setText(s);
            return view;
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        /**
         * Sends autocomplete requests to the Google Places API and provides the results.
         */
        private class PlaceFilter extends Filter {
            private static final String TAG = "PlaceFilter";

            private final Params mParams = new LocalPlacesParams(getContext()).required(false);

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    try {
                        Response<List<Prediction>> resp =
                                Places.autocomplete(mParams.query(constraint.toString()));
                        Status status = resp.getStatus();
                        if (status == OK) {
                            List<Prediction> preds = resp.getResult();
                            results.values = preds;
                            if (preds != null) {
                                results.count = preds.size();
                            }
                        } else if (status != ZERO_RESULTS) {
                            Log.e(TAG, "autocomplete failed: " + status);
                        }
                    } catch (IOException e) {
                        Log.w(TAG, "sending autocomplete request", e);
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mPredictions = (List<Prediction>) results.values;
                mCount = results.count;
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((Place) resultValue).getName(); // displayed in TextView after item clicked
            }
        }
    }
}
