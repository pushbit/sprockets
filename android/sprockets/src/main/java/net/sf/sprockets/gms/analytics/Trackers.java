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

package net.sf.sprockets.gms.analytics;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.HitBuilders.ExceptionBuilder;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * Utility methods for working with {@link Tracker}s. When you instantiate your Tracker (e.g. in
 * your {@link Application} class), provide it to {@link #use(Context, Tracker)} and that Tracker
 * will be used to send hits.
 */
public class Trackers {
    private static Context sContext;
    private static Tracker sTracker;
    private static StandardExceptionParser sParser;

    private Trackers() {
    }

    /**
     * Use the Tracker to send hits.
     */
    public static void use(Context context, Tracker tracker) {
        sContext = context.getApplicationContext();
        sTracker = tracker;
    }

    /**
     * Send an event hit.
     */
    public static void event(String category, String action) {
        event(category, action, null);
    }

    /**
     * Send an event hit with a label.
     */
    public static void event(String category, String action, String label) {
        event(category, action, label, Long.MIN_VALUE);
    }

    /**
     * Send an event hit with a value.
     */
    public static void event(String category, String action, long value) {
        event(category, action, null, value);
    }

    /**
     * Send an event hit with a label and a value.
     */
    public static void event(String category, String action, String label, long value) {
        EventBuilder builder = new EventBuilder(category, action);
        if (label != null) {
            builder.setLabel(label);
        }
        if (value != Long.MIN_VALUE) {
            builder.setValue(value);
        }
        send(builder.build());
    }

    /**
     * Send a non-fatal exception.
     */
    public static void exception(Throwable t) {
        exception(t, false);
    }

    /**
     * Send an exception.
     */
    public static void exception(Throwable t, boolean fatal) {
        if (sParser == null) {
            sParser = new StandardExceptionParser(sContext, null);
        }
        String desc = sParser.getDescription(Thread.currentThread().getName(), t);
        String msg = t.getMessage();
        ExceptionBuilder builder = new ExceptionBuilder();
        builder.setDescription(msg != null ? desc + ' ' + msg : desc);
        builder.setFatal(fatal);
        send(builder.build());
    }

    private static void send(Map<String, String> params) {
        checkState(sTracker != null, "you must call use(Context, Tracker) before sending hits");
        sTracker.send(params);
    }
}
