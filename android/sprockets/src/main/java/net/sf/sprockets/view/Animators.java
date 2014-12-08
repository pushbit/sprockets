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

package net.sf.sprockets.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewPropertyAnimator;

/**
 * Utility methods for working with ViewPropertyAnimators.
 */
public class Animators {
    private Animators() {
    }

    /**
     * Scale the Activity down to the rectangle provided by its
     * {@link Intent#getSourceBounds() getIntent().getSourceBounds()}. To see the Activity behind
     * this one, you must include the below attributes[1] in your Activity theme. You may further
     * modify the animator, for example to fade out the Activity by decreasing its
     * {@link ViewPropertyAnimator#alpha(float) alpha} and/or set an
     * {@link ViewPropertyAnimator#withEndAction(Runnable) end action} that finishes the Activity.
     * When finishing the Activity in this way, you may wish to also call
     * {@link Activity#overridePendingTransition(int, int) Activity.overridePendingTransition(0, 0)}
     * in order to prevent the system Activity close animation from running.
     * <p>
     * 1. Activity theme attributes
     * <pre>{@code
     * <item name="android:windowBackground">@android:color/transparent</item>
     * <item name="android:windowIsTranslucent">true</item>
     * }</pre>
     * </p>
     *
     * @return null when the Activity's Intent does not have
     * {@link Intent#getSourceBounds() source bounds}
     * @see ActivityOptions#makeScaleUpAnimation(View, int, int, int, int)
     */
    public static ViewPropertyAnimator makeScaleDownAnimation(Activity activity) {
        Rect to = activity.getIntent().getSourceBounds();
        return to != null ? makeScaleDownAnimation(activity, to) : null;
    }

    /**
     * Scale the Activity down to the rectangle. To see the Activity behind this one, you must
     * include the below attributes[1] in your Activity theme. You may further modify the animator,
     * for example to fade out the Activity by decreasing its
     * {@link ViewPropertyAnimator#alpha(float) alpha} and/or set an
     * {@link ViewPropertyAnimator#withEndAction(Runnable) end action} that finishes the Activity.
     * When finishing the Activity in this way, you may wish to also call
     * {@link Activity#overridePendingTransition(int, int) Activity.overridePendingTransition(0, 0)}
     * in order to prevent the system Activity close animation from running.
     * <p>
     * 1. Activity theme attributes
     * <pre>{@code
     * <item name="android:windowBackground">@android:color/transparent</item>
     * <item name="android:windowIsTranslucent">true</item>
     * }</pre>
     * </p>
     *
     * @see ActivityOptions#makeScaleUpAnimation(View, int, int, int, int)
     */
    public static ViewPropertyAnimator makeScaleDownAnimation(Activity activity, Rect to) {
        View view = activity.getWindow().getDecorView();
        Rect frame = Windows.getFrame(activity);
        float sx = (float) to.width() / view.getWidth();
        float sy = (float) to.height() / (view.getHeight() - frame.top); // ignore status bar
        view.setPivotX(0.0f);
        view.setPivotY(0.0f);
        return view.animate().translationX(to.left).translationY(to.top - frame.top * sy)
                .scaleX(sx).scaleY(sy).withLayer();
    }
}
