/*
 * Copyright 2015 pushbit <pushbit@gmail.com>
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

package net.sf.sprockets.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.Log;

import com.google.common.base.MoreObjects;
import com.squareup.picasso.Transformation;

import net.sf.sprockets.R;
import net.sf.sprockets.graphics.Bitmaps;

import java.util.Arrays;

import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Shader.TileMode.CLAMP;

/**
 * Overlays a gradient on the bitmap to be transformed.
 *
 * @since 2.1.0
 */
public class GradientTransformation implements Transformation {
    private static final String TAG = GradientTransformation.class.getSimpleName();
    private static final int[] sDefColors = {TRANSPARENT, TRANSPARENT}; // [0] set in constructor
    private static final float[] sDefPositions = {0.5f, 1.0f};

    private final Orientation mOrient;
    private final int[] mColors;
    private final float[] mPositions;

    /**
     * Use the default colors and positions. The first half of the overlay will be
     * {@code R.color.overlay}. The second half will fade from {@code overlay} to transparent.
     */
    public GradientTransformation(Context context, Orientation orientation) {
        this(orientation, sDefColors, sDefPositions);
        if (sDefColors[0] == TRANSPARENT) {
            sDefColors[0] = context.getResources().getColor(R.color.overlay);
        }
    }

    /**
     * Apply a gradient of the colors at the positions in the direction of the orientation.
     *
     * @see LinearGradient#LinearGradient(float, float, float, float, int[], float[],
     * Shader.TileMode)
     */
    public GradientTransformation(Orientation orientation, int[] colors, float[] positions) {
        mOrient = orientation;
        mColors = colors;
        mPositions = positions;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap bm = Bitmaps.mutable(source);
        if (bm == null) {
            Log.e(TAG, "bitmap could not be copied, returning untransformed");
            return source;
        }
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        int width = bm.getWidth();
        int height = bm.getHeight();
        float x0 = 0.0f, y0 = 0.0f, x1 = 0.0f, y1 = 0.0f;
        switch (mOrient) {
            case BOTTOM_TOP:
                y0 = height;
                break;
            case BL_TR:
                y0 = height;
                x1 = width;
                break;
            case LEFT_RIGHT:
                x1 = width;
                break;
            case TL_BR:
                x1 = width;
                y1 = height;
                break;
            case TOP_BOTTOM:
                y1 = height;
                break;
            case TR_BL:
                x0 = width;
                y1 = height;
                break;
            case RIGHT_LEFT:
                x0 = width;
                break;
            case BR_TL:
                x0 = width;
                y0 = height;
                break;
        }
        paint.setShader(new LinearGradient(x0, y0, x1, y1, mColors, mPositions, CLAMP));
        canvas.drawRect(0.0f, 0.0f, width, height, paint);
        return bm;
    }

    @Override
    public String key() {
        return toString();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("orientation", mOrient)
                .add("colors", Arrays.toString(mColors))
                .add("positions", Arrays.toString(mPositions)).omitNullValues().toString();
    }
}
