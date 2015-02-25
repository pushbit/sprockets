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

import android.database.DataSetObserver;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import net.sf.sprockets.widget.ObservableImageView;
import net.sf.sprockets.widget.ObservableImageView.ImageViewObserver;

import static android.widget.ImageView.ScaleType.MATRIX;

/**
 * Scales an image so that both dimensions fill its ImageView (similar to
 * {@link ScaleType#CENTER_CROP CENTER_CROP}) and then translates the image when a ViewPager is
 * scrolled to reveal the "cropped" portion. The start of the image is visible on the first page,
 * the middle of the image is progressively visible while scrolling through the middle page(s), and
 * the end of the image is visible on the last page.
 * <p>
 * If the Drawable source of the ImageView changes (e.g. by calling {@code setImage*} after
 * asynchronous loading), this class must be notified so that it can scale and translate the new
 * Drawable. This can be done automatically by using an {@link ObservableImageView} or manually by
 * calling {@link #onDrawableChanged()}.
 * </p>
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/java/net/sf/sprockets/sample/app/ui/TranslateImageActivity.java" target="_blank">Sample Usage</a>
 * </p>
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/res/layout/translate_image.xml" target="_blank">Sample Layout</a>
 * </p>
 *
 * @since 2.1.0
 */
public class TranslateImagePageChangeListener extends SimpleOnPageChangeListener
        implements ImageViewObserver {
    private final ViewPager mPager;
    private PagerAdapter mAdapter;
    private final AdapterObserver mObserver = new AdapterObserver();
    private final ImageView mView;
    private int mViewWidth;
    private int mViewHeight;
    private Drawable mDrawable;
    private int mDrawableWidth;
    private int mDrawableHeight;
    private final Matrix mMatrix = new Matrix();
    private float mScale;
    private int mPageCount;
    /**
     * Number of pixels to translate the image per page.
     */
    private float mPageX;
    private float mPageY;

    /**
     * Scale the ImageView's Drawable and translate it when the ViewPager is scrolled.
     *
     * @param view can be an {@link ObservableImageView} and it will be automatically updated when
     *             the Drawable source changes, otherwise you must call {@link #onDrawableChanged()}
     *             when the Drawable source changes
     */
    public TranslateImagePageChangeListener(ViewPager pager, ImageView view) {
        mPager = pager;
        mView = view;
        if (view instanceof ObservableImageView) {
            ((ObservableImageView) view).registerObserver(this);
        }
    }

    @Override
    public void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
        checkAdapter();
        checkDrawable();
        updateMatrix(position, offset);
    }

    /**
     * Check if the pager has a new adapter and switch to it if it does.
     */
    private void checkAdapter() {
        PagerAdapter adapter = mPager.getAdapter();
        if (mAdapter != adapter) {
            if (mAdapter != null) {
                mAdapter.unregisterDataSetObserver(mObserver);
            }
            mAdapter = adapter;
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(mObserver);
            }
            reset();
        }
    }

    /**
     * Check if the ImageView has a new Drawable and calculate the new scaling if it does.
     */
    private void checkDrawable() {
        Drawable drawable = mView.getDrawable();
        if (mDrawable != drawable) {
            /* get the latest View size and ensure that it's been measured */
            mViewWidth = mView.getWidth();
            mViewHeight = mView.getHeight();
            if (mViewWidth > 0 && mViewHeight > 0) {
                mDrawable = drawable; // don't save until now so above is repeated until measured
                if (mDrawable != null) {
                    mDrawableWidth = mDrawable.getIntrinsicWidth();
                    mDrawableHeight = mDrawable.getIntrinsicHeight();
                    if (mDrawableWidth > 0 && mDrawableHeight > 0) { // e.g. colors don't have size
                        float widthRatio = (float) mViewWidth / mDrawableWidth;
                        float heightRatio = (float) mViewHeight / mDrawableHeight;
                        mScale = widthRatio > heightRatio ? widthRatio : heightRatio;
                    } else { // nothing to scale, ensure matrix is skipped
                        mScale = 0.0f;
                    }
                }
            } else { // don't update matrix until View is measured
                mScale = 0.0f;
            }
            reset();
        }
    }

    /**
     * Scale the image and translate it according to the pager position and offset.
     */
    private void updateMatrix(int position, float offset) {
        if (mDrawable != null && mScale > 0.0f) {
            if (mPageCount == -1 && mAdapter != null) { // cache page count and translation values
                mPageCount = mAdapter.getCount();
                if (mPageCount > 1) {
                    mPageX = (mDrawableWidth * mScale - mViewWidth) / (mPageCount - 1);
                    mPageY = (mDrawableHeight * mScale - mViewHeight) / (mPageCount - 1);
                }
            }
            mMatrix.setTranslate(-mPageX * position - mPageX * offset,
                    -mPageY * position - mPageY * offset);
            mMatrix.preScale(mScale, mScale);
            mView.setScaleType(MATRIX);
            mView.setImageMatrix(mMatrix);
        }
    }

    @Override
    public void onDrawableChanged() {
        checkDrawable();
        updateMatrix(mPager.getCurrentItem(), 0.0f);
    }

    /**
     * Reset adapter and drawable dependent values.
     */
    private void reset() {
        mPageCount = -1;
        mPageX = 0.0f;
        mPageY = 0.0f;
    }

    /**
     * Resets dependent values when the adapter data changes.
     */
    private class AdapterObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            reset();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            reset();
        }
    }
}
