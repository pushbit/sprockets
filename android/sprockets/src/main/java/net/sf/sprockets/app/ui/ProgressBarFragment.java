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
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Sprockets. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sprockets.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import icepick.Icicle;

import static android.view.Gravity.CENTER;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.RelativeLayout.CENTER_IN_PARENT;

/**
 * Displays a {@link ProgressBar} that is centred in its {@link FrameLayout} or
 * {@link RelativeLayout} container.
 */
public class ProgressBarFragment extends SprocketsFragment {
    @Icicle
    int mStyle = android.R.attr.progressBarStyleLarge;

    /**
     * Create a ProgressBar with the {@code android.R.attr.progressBarStyleLarge} style.
     */
    public ProgressBarFragment() {
    }

    /**
     * Create a ProgressBar with the style of the theme attribute.
     */
    public static ProgressBarFragment newInstance(int styleAttr) {
        ProgressBarFragment frag = new ProgressBarFragment();
        frag.mStyle = styleAttr;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        ProgressBar bar = new ProgressBar(a, null, mStyle);
        LayoutParams params = null;
        if (container instanceof FrameLayout) {
            params = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, CENTER);
        } else if (container instanceof RelativeLayout) {
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(WRAP_CONTENT,
                    WRAP_CONTENT);
            p.addRule(CENTER_IN_PARENT);
            params = p;
        }
        if (params != null) {
            bar.setLayoutParams(params);
        }
        return bar;
    }
}
