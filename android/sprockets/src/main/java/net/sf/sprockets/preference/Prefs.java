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

package net.sf.sprockets.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import net.sf.sprockets.app.ui.BaseNavigationDrawerFragment;

import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Utility methods for working with SharedPreferences and keys used within the library.
 */
public class Prefs {
    /**
     * Library preferences name.
     */
    public static final String SPROCKETS = "net.sf.sprockets_preferences";
    public static final String APP_VERSION_CODE = "app_version_code";
    public static final String APP_VERSION_NAME = "app_version_name";
    /**
     * True if the {@link BaseNavigationDrawerFragment navigation drawer} 'Rate' option has been
     * clicked.
     */
    public static final String RATED = "rated";

    private Prefs() {
    }

    /**
     * Get the default SharedPreferences.
     */
    public static SharedPreferences get(Context context) {
        return get(context, null);
    }

    /**
     * Get the SharedPreferences.
     */
    public static SharedPreferences get(Context context, String name) {
        return name == null ? PreferenceManager.getDefaultSharedPreferences(context)
                : context.getSharedPreferences(name, MODE_PRIVATE);
    }

    /**
     * Get an Editor for the default SharedPreferences.
     */
    public static Editor edit(Context context) {
        return edit(context, null);
    }

    /**
     * Get an Editor for the SharedPreferences.
     */
    public static Editor edit(Context context, String name) {
        return get(context, name).edit();
    }

    /**
     * True if the default SharedPreferences contains the key.
     */
    public static boolean contains(Context context, String key) {
        return contains(context, null, key);
    }

    /**
     * True if the SharedPreferences contains the key.
     */
    public static boolean contains(Context context, String name, String key) {
        return get(context, name).contains(key);
    }

    /**
     * Put a value in the default SharedPreferences.
     */
    public static void putBoolean(Context context, String key, boolean value) {
        putBoolean(context, null, key, value);
    }

    /**
     * Put a value in the SharedPreferences.
     */
    public static void putBoolean(Context context, String name, String key, boolean value) {
        edit(context, name).putBoolean(key, value).apply();
    }

    /**
     * Get a value from the default SharedPreferences.
     *
     * @return false if the key does not exist
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /**
     * Get a value from the default SharedPreferences.
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getBoolean(context, null, key, defValue);
    }

    /**
     * Get a value from the SharedPreferences.
     *
     * @return false if the key does not exist
     */
    public static boolean getBoolean(Context context, String name, String key) {
        return getBoolean(context, name, key, false);
    }

    /**
     * Get a value from the SharedPreferences.
     */
    public static boolean getBoolean(Context context, String name, String key, boolean defValue) {
        return get(context, name).getBoolean(key, defValue);
    }

    /**
     * Put a value in the default SharedPreferences.
     */
    public static void putFloat(Context context, String key, float value) {
        putFloat(context, null, key, value);
    }

    /**
     * Put a value in the SharedPreferences.
     */
    public static void putFloat(Context context, String name, String key, float value) {
        edit(context, name).putFloat(key, value).apply();
    }

    /**
     * Get a value from the default SharedPreferences.
     *
     * @return 0 if the key does not exist
     */
    public static float getFloat(Context context, String key) {
        return getFloat(context, key, 0.0f);
    }

    /**
     * Get a value from the default SharedPreferences.
     */
    public static float getFloat(Context context, String key, float defValue) {
        return getFloat(context, null, key, defValue);
    }

    /**
     * Get a value from the SharedPreferences.
     *
     * @return 0 if the key does not exist
     */
    public static float getFloat(Context context, String name, String key) {
        return getFloat(context, name, key, 0.0f);
    }

    /**
     * Get a value from the SharedPreferences.
     */
    public static float getFloat(Context context, String name, String key, float defValue) {
        return get(context, name).getFloat(key, defValue);
    }

    /**
     * Put a value in the default SharedPreferences.
     */
    public static void putInt(Context context, String key, int value) {
        putInt(context, null, key, value);
    }

    /**
     * Put a value in the SharedPreferences.
     */
    public static void putInt(Context context, String name, String key, int value) {
        edit(context, name).putInt(key, value).apply();
    }

    /**
     * Get a value from the default SharedPreferences.
     *
     * @return 0 if the key does not exist
     */
    public static int getInt(Context context, String key) {
        return getInt(context, key, 0);
    }

    /**
     * Get a value from the default SharedPreferences.
     */
    public static int getInt(Context context, String key, int defValue) {
        return getInt(context, null, key, defValue);
    }

    /**
     * Get a value from the SharedPreferences.
     *
     * @return 0 if the key does not exist
     */
    public static int getInt(Context context, String name, String key) {
        return getInt(context, name, key, 0);
    }

    /**
     * Get a value from the SharedPreferences.
     */
    public static int getInt(Context context, String name, String key, int defValue) {
        return get(context, name).getInt(key, defValue);
    }

    /**
     * Put a value in the default SharedPreferences.
     */
    public static void putLong(Context context, String key, long value) {
        putLong(context, null, key, value);
    }

    /**
     * Put a value in the SharedPreferences.
     */
    public static void putLong(Context context, String name, String key, long value) {
        edit(context, name).putLong(key, value).apply();
    }

    /**
     * Get a value from the default SharedPreferences.
     *
     * @return 0 if the key does not exist
     */
    public static long getLong(Context context, String key) {
        return getLong(context, key, 0L);
    }

    /**
     * Get a value from the default SharedPreferences.
     */
    public static long getLong(Context context, String key, long defValue) {
        return getLong(context, null, key, defValue);
    }

    /**
     * Get a value from the SharedPreferences.
     *
     * @return 0 if the key does not exist
     */
    public static long getLong(Context context, String name, String key) {
        return getLong(context, name, key, 0L);
    }

    /**
     * Get a value from the SharedPreferences.
     */
    public static long getLong(Context context, String name, String key, long defValue) {
        return get(context, name).getLong(key, defValue);
    }

    /**
     * Put a value in the default SharedPreferences.
     */
    public static void putString(Context context, String key, String value) {
        putString(context, null, key, value);
    }

    /**
     * Put a value in the SharedPreferences.
     */
    public static void putString(Context context, String name, String key, String value) {
        edit(context, name).putString(key, value).apply();
    }

    /**
     * Get a value from the default SharedPreferences.
     *
     * @return null if the key does not exist
     */
    public static String getString(Context context, String key) {
        return getStringOrDef(context, key, null);
    }

    /**
     * Get a value from the default SharedPreferences.
     */
    public static String getStringOrDef(Context context, String key, String defValue) {
        return getStringOrDef(context, null, key, defValue);
    }

    /**
     * Get a value from the SharedPreferences.
     *
     * @return null if the key does not exist
     */
    public static String getString(Context context, String name, String key) {
        return getStringOrDef(context, name, key, null);
    }

    /**
     * Get a value from the SharedPreferences.
     */
    public static String getStringOrDef(Context context, String name, String key, String defValue) {
        return get(context, name).getString(key, defValue);
    }

    /**
     * Put a set of values in the default SharedPreferences.
     */
    public static void putStringSet(Context context, String key, Set<String> vals) {
        putStringSet(context, null, key, vals);
    }

    /**
     * Put a set of values in the SharedPreferences.
     */
    public static void putStringSet(Context context, String name, String key, Set<String> vals) {
        edit(context, name).putStringSet(key, vals).apply();
    }

    /**
     * Get a set of values from the default SharedPreferences.
     *
     * @return null if the key does not exist
     */
    public static Set<String> getStringSet(Context context, String key) {
        return getStringSet(context, key, (Set<String>) null);
    }

    /**
     * Get a set of values from the default SharedPreferences.
     */
    public static Set<String> getStringSet(Context context, String key, Set<String> defValue) {
        return getStringSet(context, null, key, defValue);
    }

    /**
     * Get a set of values from the SharedPreferences.
     *
     * @return null if the key does not exist
     */
    public static Set<String> getStringSet(Context context, String name, String key) {
        return getStringSet(context, name, key, null);
    }

    /**
     * Get a set of values from the SharedPreferences.
     */
    public static Set<String> getStringSet(Context context, String name, String key,
                                           Set<String> defValue) {
        return get(context, name).getStringSet(key, defValue);
    }

    /**
     * Remove a value from the default SharedPreferences.
     */
    public static void remove(Context context, String key) {
        edit(context).remove(key).apply();
    }

    /**
     * Remove a value from the SharedPreferences.
     */
    public static void remove(Context context, String name, String key) {
        edit(context, name).remove(key).apply();
    }
}
