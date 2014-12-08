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

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

/**
 * ForegroundColorSpan with an alpha property that can be changed after it's created. This span only
 * affects the foreground's alpha value, the underlying color is not changed. Note that after
 * updating this value, you will likely need to re-set the Spannable as the text for the View in
 * order to see the change reflected.
 * <p>
 * Inspired by Flavien Laurent's
 * <a href="http://flavienlaurent.com/blog/2014/01/31/spans/" target="_blank">Spans, a Powerful Concept.</a>
 * </p>
 */
public class MutableForegroundAlphaSpan extends ForegroundColorSpan {
    private int mAlpha;

    /**
     * Start with this alpha.
     *
     * @param alpha 0 (transparent) to 255 (opaque)
     */
    public MutableForegroundAlphaSpan(int alpha) {
        super(0);
        mAlpha = alpha;
    }

    public MutableForegroundAlphaSpan(Parcel src) {
        super(src);
        mAlpha = src.readInt();
    }

    /**
     * @param alpha 0 (transparent) to 255 (opaque)
     */
    public MutableForegroundAlphaSpan setAlpha(int alpha) {
        mAlpha = alpha;
        return this;
    }

    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setAlpha(mAlpha);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mAlpha);
    }
}
