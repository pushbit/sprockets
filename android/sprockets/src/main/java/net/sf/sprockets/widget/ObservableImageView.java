/*
 * Copyright 2014 pushbit <pushbit@gmail.com>
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

import android.content.Context;
import android.database.Observable;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * <p>
 * Notifies observers when it has changed. Usage in XML is identical to {@link ImageView}, just with
 * a different name:
 * </p>
 * <pre>{@code
 * <net.sf.sprockets.widget.ObservableImageView
 *     ... />
 * }</pre>
 *
 * @since 2.1.0
 */
public class ObservableImageView extends ImageView {
    private final ImageViewObservable mObservable = new ImageViewObservable();

    public ObservableImageView(Context context) {
        super(context);
    }

    public ObservableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // todo Added in API level 21
    // public ObservableImageView(Context context, AttributeSet attrs, int defStyleAttr,
    //                            int defStyleRes) {
    //     super(context, attrs, defStyleAttr, defStyleRes);
    // }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        onDrawableChanged();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        onDrawableChanged();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        onDrawableChanged();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        // as of API 21, super calls setImageDrawable, which will call onDrawableChanged
    }

    /**
     * Notify observers that the Drawable source has changed.
     */
    private void onDrawableChanged() {
        if (mObservable != null) { // is during super(), which can be setting an image from XML
            mObservable.onDrawableChanged();
        }
    }

    /**
     * Add an observer to the list.
     *
     * @param observer which is not already registered
     */
    public void registerObserver(ImageViewObserver observer) {
        mObservable.registerObserver(observer);
    }

    /**
     * Remove an observer from the list.
     *
     * @param observer which was already registered
     */
    public void unregisterObserver(ImageViewObserver observer) {
        mObservable.unregisterObserver(observer);
    }

    /**
     * Remove all observers from the list.
     */
    public void unregisterAll() {
        mObservable.unregisterAll();
    }

    /**
     * Notified when an ImageView has changed.
     */
    public interface ImageViewObserver {
        /**
         * The image source has changed.
         */
        void onDrawableChanged();
    }

    /**
     * Notifies observers when an ImageView has changed.
     */
    private class ImageViewObservable extends Observable<ImageViewObserver> {
        private void onDrawableChanged() {
            for (ImageViewObserver observer : mObservers) {
                observer.onDrawableChanged();
            }
        }
    }
}
