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

package net.sf.sprockets.text.style;

import android.graphics.Color;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

/**
 * ForegroundColorSpan with alpha and color properties that can be changed after it's created. Note
 * that after updating these values, you will likely need to re-set the Spannable as the text for
 * the View in order to see the changes reflected.
 * <p>
 * Inspired by Flavien Laurent's
 * <a href="http://flavienlaurent.com/blog/2014/01/31/spans/" target="_blank">Spans, a Powerful Concept.</a>
 * </p>
 */
public class MutableForegroundColorSpan extends ForegroundColorSpan {
    private int mAlpha;
    private int mColor;

    /**
     * Start with this alpha and color.
     *
     * @param alpha 0 (transparent) to 255 (opaque)
     * @param color e.g. 0x00FF00 for green
     */
    public MutableForegroundColorSpan(int alpha, int color) {
        super(color);
        mAlpha = alpha;
        mColor = color;
    }

    public MutableForegroundColorSpan(Parcel src) {
        super(src);
        mAlpha = src.readInt();
        mColor = src.readInt();
    }

    /**
     * @param alpha 0 (transparent) to 255 (opaque)
     */
    public MutableForegroundColorSpan setAlpha(int alpha) {
        mAlpha = alpha;
        return this;
    }

    public int getAlpha() {
        return mAlpha;
    }

    /**
     * @param color e.g. 0x00FF00 for green
     */
    public MutableForegroundColorSpan setForegroundColor(int color) {
        mColor = color;
        return this;
    }

    /**
     * Get the result of applying the alpha to the color.
     */
    @Override
    public int getForegroundColor() {
        return Color.argb(mAlpha, Color.red(mColor), Color.green(mColor), Color.blue(mColor));
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(getForegroundColor());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mAlpha);
        dest.writeInt(mColor);
    }
}
