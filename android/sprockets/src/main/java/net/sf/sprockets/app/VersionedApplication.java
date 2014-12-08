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

package net.sf.sprockets.app;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.common.base.Objects;

import net.sf.sprockets.preference.Prefs;

import static net.sf.sprockets.preference.Prefs.APP_VERSION_CODE;
import static net.sf.sprockets.preference.Prefs.APP_VERSION_NAME;
import static net.sf.sprockets.preference.Prefs.SPROCKETS;

/**
 * Tracks the version of the app each time it runs. Implement
 * {@link #onVersionChanged(int, String, int, String) onVersionChanged} to be notified when the app
 * runs with a new version for the first time.
 */
public abstract class VersionedApplication extends SprocketsApplication {
    private static int sOldCode;
    private static String sOldName;
    private static int sNewCode;
    private static String sNewName;
    private static boolean sHasNew;

    @Override
    public void onCreate() {
        super.onCreate();
        /* get previous and current versions */
        SharedPreferences prefs = Prefs.get(this, SPROCKETS);
        sOldCode = prefs.getInt(APP_VERSION_CODE, 0);
        sOldName = prefs.getString(APP_VERSION_NAME, null);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            sNewCode = info.versionCode;
            sNewName = info.versionName;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("application's package could not be found?!", e);
        }
        /* notify if new version and save the new values */
        sHasNew = sOldCode != sNewCode || !Objects.equal(sOldName, sNewName);
        if (sHasNew) {
            onVersionChanged(sOldCode, sOldName, sNewCode, sNewName);
            prefs.edit().putInt(APP_VERSION_CODE, sNewCode).putString(APP_VERSION_NAME, sNewName)
                    .apply();
        }
    }

    /**
     * The current version of the app is different from the last version that ran or the application
     * is being run for the first time.
     *
     * @param oldCode 0 when the application is run for the first time
     * @param oldName null when the application is run for the first time
     */
    public abstract void onVersionChanged(int oldCode, String oldName, int newCode, String newName);

    /**
     * Get the version of the app when it last ran or 0 if the app is being run for the first time.
     *
     * @return 0 if this class (or a subclass) was not specified in the application's manifest
     */
    public static int getPreviousVersionCode() {
        return sOldCode;
    }

    /**
     * Get the version of the app when it last ran or null if the app is being run for the first
     * time.
     *
     * @return null if this class (or a subclass) was not specified in the application's manifest
     */
    public static String getPreviousVersionName() {
        return sOldName;
    }

    /**
     * Get the current version of the app.
     *
     * @return 0 if this class (or a subclass) was not specified in the application's manifest
     */
    public static int getVersionCode() {
        return sNewCode;
    }

    /**
     * Get the current version of the app.
     *
     * @return null if this class (or a subclass) was not specified in the application's manifest
     */
    public static String getVersionName() {
        return sNewName;
    }

    /**
     * True if the current version of the app is different from the last version that ran or the app
     * is being run for the first time.
     *
     * @return false if this class (or a subclass) was not specified in the application's manifest
     */
    public static boolean hasNewVersion() {
        return sHasNew;
    }

    /**
     * Reset the previous version to be the same as the current version, so it will no longer appear
     * that a new version of the app is being run.
     */
    public static void resetPreviousVersion() {
        sOldCode = sNewCode;
        sOldName = sNewName;
        sHasNew = false;
    }
}
