/*
 * Copyright 2014-2015 pushbit <pushbit@gmail.com>
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

package net.sf.sprockets.view;

import android.view.View;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import butterknife.ButterKnife;

/**
 * Base class for the <a href=
 * "https://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder"
 * target="_blank">View Holder</a> pattern. Implementations must be public.
 * <p>
 * Sample implementation:
 * </p>
 * <pre><code> public class SomeHolder extends ViewHolder {
 *     {@literal @}InjectView(R.id.someView)
 *     View someView;
 *
 *     {@literal @}Override
 *     protected SomeHolder newInstance() {
 *         return new SomeHolder();
 *     }
 * }</code></pre>
 * <p>
 * Sample usage:
 * </p>
 * <pre>{@code
 * SomeHolder holder = ViewHolder.get(view, SomeHolder.class);
 * }</pre>
 */
public abstract class ViewHolder {
    private static final ClassToInstanceMap<ViewHolder> sHolders =
            MutableClassToInstanceMap.create();

    /**
     * Get a ViewHolder for the View that is {@link ButterKnife#inject(Object, View) injected} and
     * {@link View#setTag(Object) tagged} on the View.
     *
     * @param type must be public
     */
    public static <T extends ViewHolder> T get(View view, Class<T> type) {
        T holder = (T) view.getTag();
        if (holder == null) { // get a new instance for the view
            holder = sHolders.getInstance(type);
            if (holder == null) { // get a new instance (factory) for creating new instances
                try {
                    holder = type.newInstance();
                    sHolders.putInstance(type, holder);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new IllegalArgumentException(
                            "couldn't instantiate implementation: " + type.getName(), e);
                }
            }
            holder = holder.newInstance();
            ButterKnife.inject(holder, view);
            view.setTag(holder);
        }
        return holder;
    }

    /**
     * Get the ViewHolder that is already {@link View#setTag(Object) tagged} on the View.
     *
     * @throws NullPointerException if the View has not already been tagged in
     *                              {@link #get(View, Class)}
     */
    public static <T extends ViewHolder> T get(View view) {
        return get(view, null);
    }

    /**
     * Get a new instance of the implementation class.
     */
    protected abstract <T extends ViewHolder> T newInstance();
}
