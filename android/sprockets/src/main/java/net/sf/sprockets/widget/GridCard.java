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

import android.content.res.Resources;
import android.widget.GridView;

import net.sf.sprockets.R;

/**
 * Measurements for the content area (full area minus padding) of an {@code R.drawable.image_card}
 * in a {@link GridView}.
 */
public class GridCard {
    private final GridView mView;
    private final int mPadding;
    private int mWidth;
    private final int mHeight;
    private double mAspectRatio;

    /**
     * Calculate measurements for image cards that would appear in the GridView. The card height is
     * assumed to be {@code R.dimen.grid_card_height}.
     */
    public GridCard(GridView view) {
        this(view, R.dimen.grid_card_height);
    }

    /**
     * Calculate measurements for image cards that would appear in the GridView.
     *
     * @param heightResId dimension resource for the card's height
     */
    public GridCard(GridView view, int heightResId) {
        mView = view;
        Resources res = view.getResources();
        mPadding = res.getDimensionPixelSize(R.dimen.image_card_padding);
        mHeight = res.getDimensionPixelSize(heightResId) - mPadding
                - res.getDimensionPixelSize(R.dimen.image_card_padding_bottom);
    }

    /**
     * Image card content width (card width minus padding).
     *
     * @return 0 if the GridView hasn't been laid out yet
     */
    public int getWidth() {
        if (mWidth == 0) {
            mWidth = mView.getColumnWidth();
            if (mWidth > 0) {
                mWidth -= mPadding * 2;
            }
        }
        return mWidth;
    }

    /**
     * Image card content height (card height minus padding).
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * Aspect ratio of the image card content (width / height).
     *
     * @return 0.0 if the GridView hasn't been laid out yet
     */
    public double getAspectRatio() {
        if (mAspectRatio == 0.0) {
            mAspectRatio = (double) getWidth() / mHeight;
        }
        return mAspectRatio;
    }
}
