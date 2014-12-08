/*
 * Copyright 2013 pushbit <pushbit@gmail.com>
 *
 * This file is part of Sprockets.
 *
 * Sprockets is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Sprockets is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Sprockets.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sprockets.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;

import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;

import java.util.List;

/**
 * Adapter for a list of places, likely received from a {@link Places} method. Implement
 * {@link #getView(int, Place, View, ViewGroup)} to define how each place will be displayed.
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/java/net/sf/sprockets/sample/app/ui/GooglePlacesLoaderActivity.java" target="_blank">Sample Usage</a>
 * </p>
 */
public abstract class GooglePlacesAdapter extends BaseAdapter {
    private List<Place> mPlaces;
    private int mCount;

    /**
     * Start with an empty list and swap places in later.
     */
    public GooglePlacesAdapter() {
    }

    /**
     * Use these places to back the list.
     */
    public GooglePlacesAdapter(List<Place> places) {
        swapPlaces(places);
    }

    /**
     * Use these new places instead of the old places.
     *
     * @param places can be null to empty the list
     * @return the old places or null if there weren't any or the object references are equal
     */
    public List<Place> swapPlaces(List<Place> places) {
        if (mPlaces == places) {
            return null;
        }
        List<Place> old = mPlaces;
        mPlaces = places;
        mCount = places != null ? places.size() : 0;
        notifyDataSetChanged();
        return old;
    }

    /**
     * Get the places that are currently backing the list.
     *
     * @return null if the list is currently empty
     */
    public List<Place> getPlaces() {
        return mPlaces;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Place getItem(int position) {
        return mPlaces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(position, getItem(position), convertView, parent);
    }

    /**
     * Get a View that displays the place.
     *
     * @see Adapter#getView(int, View, ViewGroup)
     */
    public abstract View getView(int position, Place place, View convertView, ViewGroup parent);
}
