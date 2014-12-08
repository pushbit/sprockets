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

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.widget.AbsListView;
import android.widget.ListView;

import net.sf.sprockets.content.res.Themes;
import net.sf.sprockets.text.style.MutableForegroundAlphaSpan;
import net.sf.sprockets.widget.ListScrollListeners.ObservingScrollListener;
import net.sf.sprockets.widget.ListScrollListeners.OnScrollApprover;

import java.util.Map;
import java.util.WeakHashMap;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 * Fades the ActionBar title and background from transparent to opaque while scrolling down the
 * list. The ActionBar will be opaque when the View for the specified adapter position reaches the
 * top of the list. When scrolling back up to the top of the list, the ActionBar will similarly fade
 * to transparent as the View for the adapter position returns to its starting location. The View
 * for the adapter position may start off screen, but the ActionBar will only fade while it is on
 * screen.
 * <p>
 * <strong>Note:</strong> Currently only ListView is supported. Support for GridView may be added in
 * the future.
 * </p>
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/java/net/sf/sprockets/sample/app/ui/FadingActionBarActivity.java" target="_blank">Sample Usage</a>
 * </p>
 */
public class FadingActionBarScrollListener extends ObservingScrollListener {
    /**
     * Multiple listeners in the same Activity can use the same background Drawable instance. APIs
     * below 18 require this in {@link ActionBar#setBackgroundDrawable(Drawable)}. Otherwise the
     * background becomes transparent when set to a Drawable other than the original instance.
     */
    private static final Map<Activity, Drawable> sBackgrounds = new WeakHashMap<>();

    private final Activity mActivity;
    private final SpannableStringBuilder mTitle;
    private final MutableForegroundAlphaSpan mTitleSpan;
    private final Drawable mBackground;
    /**
     * When this adapter position is at the top of the list, the ActionBar will be opaque.
     */
    private final int mPos;
    private int mMinTitleAlpha;
    private int mMinBgAlpha;
    private int mOpaqueOffset;
    private OnScrollApprover mApprover;
    /**
     * Starting location of the View at {@link #mPos}, used to calculate fade while scrolling.
     */
    private int mPosTop;

    /**
     * Fade the Activity's ActionBar title and/or background while the list is scrolling. They are
     * transparent when the list is scrolled to the top and opaque when the View at the adapter
     * position reaches the top of the list.
     */
    public FadingActionBarScrollListener(Activity a, boolean fadeTitle, boolean fadeBackground,
                                         int opaquePosition) {
        this(a, fadeTitle, fadeBackground, null, opaquePosition);
    }

    /**
     * Set the Activity's ActionBar background and fade it, and optionally the title, while the list
     * is scrolling. They are transparent when the list is scrolled to the top and opaque when the
     * View at the adapter position reaches the top of the list.
     */
    public FadingActionBarScrollListener(Activity a, boolean fadeTitle, Drawable background,
                                         int opaquePosition) {
        this(a, fadeTitle, background != null, background, opaquePosition);
    }

    private FadingActionBarScrollListener(Activity a, boolean fadeTitle, boolean fadeBackground,
                                          Drawable background, int opaquePosition) {
        mActivity = a;
        if (fadeTitle) {
            mTitle = new SpannableStringBuilder(a.getTitle().toString());
            mTitleSpan = new MutableForegroundAlphaSpan(0);
            mTitle.setSpan(mTitleSpan, 0, mTitle.length(), SPAN_INCLUSIVE_INCLUSIVE);
            a.setTitle(mTitle);
        } else {
            mTitle = null;
            mTitleSpan = null;
        }
        if (fadeBackground && background == null) { // get theme background
            background = sBackgrounds.get(a); // try cache in case multiple instances in Activity
            if (background == null) {
                background = Themes.getActionBarBackground(a);
                sBackgrounds.put(a, background);
            }
        }
        if (background != null) {
            background.setAlpha(0);
            a.getActionBar().setBackgroundDrawable(background);
        }
        mBackground = background;
        mPos = opaquePosition;
    }

    /**
     * Start fading in the title and background from the opacity instead of transparent.
     *
     * @param alpha 0 (transparent) to 255 (opaque)
     */
    public FadingActionBarScrollListener setMinOpacity(int alpha) {
        return setMinTitleOpacity(alpha).setMinBackgroundOpacity(alpha);
    }

    /**
     * Start fading in the title from the opacity instead of transparent.
     *
     * @param alpha 0 (transparent) to 255 (opaque)
     */
    public FadingActionBarScrollListener setMinTitleOpacity(int alpha) {
        mMinTitleAlpha = alpha;
        return this;
    }

    /**
     * Start fading in the background from the opacity instead of transparent.
     *
     * @param alpha 0 (transparent) to 255 (opaque)
     */
    public FadingActionBarScrollListener setMinBackgroundOpacity(int alpha) {
        mMinBgAlpha = alpha;
        return this;
    }

    /**
     * Offset the point at which the ActionBar title and background are opaque this many pixels
     * below the top of the list.
     */
    public FadingActionBarScrollListener setOpaqueOffset(int px) {
        mOpaqueOffset = px;
        return this;
    }

    /**
     * Request approval from the approver before fading the ActionBar.
     */
    public FadingActionBarScrollListener setOnScrollApprover(OnScrollApprover approver) {
        mApprover = approver;
        return this;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int first, int visible, int total) {
        if (mApprover != null && !mApprover.onScroll(this, view, first, visible, total)) {
            return;
        }
        super.onScroll(view, first, visible, total);
        int titleAlpha = 255; // when scrolled past opaque position
        int bgAlpha = 255;
        if (mPos > first) {
            if (mPos < first + visible) {
                int top = view.getChildAt(mPos - first).getTop(); // where it is now
                if (mPosTop == 0) { // find where it started from
                    if (first == 0 && view.getChildAt(0).getTop() == 0) { // list not scrolled
                        mPosTop = top;
                    } else { // list scrolled, sum the heights of the previous Views
                        if (view.getAdapter() == null) {
                            return; // wait until it's set
                        }
                        mPosTop = ListViews.getHeight((ListView) view, 0, mPos);
                    }
                }
                if (top > mOpaqueOffset) { // fade based on the distance it's travelled
                    int posTop = Math.min(mPosTop, view.getHeight()); // start when visible
                    float moved = (float) (posTop - top) / (posTop - mOpaqueOffset);
                    titleAlpha = Math.round(moved * (255 - mMinTitleAlpha) + mMinTitleAlpha);
                    bgAlpha = mMinTitleAlpha == mMinBgAlpha ? titleAlpha
                            : Math.round(moved * (255 - mMinBgAlpha) + mMinBgAlpha);
                }
            } else { // not scrolled on screen yet
                titleAlpha = mMinTitleAlpha;
                bgAlpha = mMinBgAlpha;
            }
        }
        if (mTitle != null) { // use current Activity title in case it's changed externally
            mTitleSpan.setAlpha(titleAlpha);
            mActivity.setTitle(mTitle.replace(0, mTitle.length(), mActivity.getTitle().toString()));
        }
        if (mBackground != null) {
            mBackground.setAlpha(bgAlpha);
            mActivity.getActionBar().setBackgroundDrawable(mBackground);
        }
    }

    @Override
    public void onChanged() {
        super.onChanged();
        mPosTop = 0;
    }
}
