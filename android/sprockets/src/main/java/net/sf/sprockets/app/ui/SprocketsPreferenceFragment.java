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

package net.sf.sprockets.app.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.TypedArray;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import net.sf.sprockets.R;
import net.sf.sprockets.app.Fragments;
import net.sf.sprockets.util.Elements;

import org.apache.commons.collections.primitives.ArrayIntList;
import org.apache.commons.collections.primitives.IntList;

import java.util.Arrays;
import java.util.Set;

/**
 * Sets preference values as their summary for {@link RingtonePreference},
 * {@link MultiSelectListPreference}, {@link EditTextPreference}, and {@link Preference}. This can
 * already be accomplished for {@link ListPreference} by including {@code %s} in its
 * {@link ListPreference#setSummary(CharSequence) summary}.
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/res/layout/settings.xml" target="_blank">Sample Usage</a>
 * </p>
 * <p>
 * <a href="https://github.com/pushbit/sprockets/blob/master/android/samples/src/main/res/xml/preferences.xml" target="_blank">Sample Preferences XML</a>
 * </p>
 */
public class SprocketsPreferenceFragment extends PreferenceFragment
        implements OnSharedPreferenceChangeListener {
    /**
     * Arguments key for a resource reference.
     */
    private static final String PREFS = SprocketsPreferenceFragment.class.getName() + ".prefs";

    /**
     * Shortcut to {@link #getActivity()}.
     */
    protected Activity a;

    /**
     * Display the preferences.
     */
    public static SprocketsPreferenceFragment newInstance(int prefsResId) {
        SprocketsPreferenceFragment frag = new SprocketsPreferenceFragment();
        Fragments.arguments(frag).putInt(PREFS, prefsResId);
        return frag;
    }

    @Override
    public void onInflate(Activity a, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(a, attrs, savedInstanceState);
        TypedArray array = a.obtainStyledAttributes(attrs, R.styleable.SprocketsPreferenceFragment);
        Fragments.arguments(this).putInt(PREFS,
                array.getResourceId(R.styleable.SprocketsPreferenceFragment_preferences, 0));
        array.recycle();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        a = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SprocketsFragment.onCreate(this, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            int prefs = args.getInt(PREFS);
            if (prefs > 0) {
                addPreferencesFromResource(prefs);
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PreferenceScreen screen = getPreferenceScreen();
        for (int i = 0; i < screen.getPreferenceCount(); i++) {
            setSummary(screen.getPreference(i));
        }
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Shortcut to {@link #getActivity()}, casting to your assignment type.
     */
    public <T extends Activity> T a() {
        return (T) a;
    }

    /**
     * Set the preference's value(s) as its summary.
     */
    protected void setSummary(Preference pref) {
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        if (pref instanceof RingtonePreference) {
            String val = prefs.getString(pref.getKey(), null);
            if (!TextUtils.isEmpty(val)) {
                Ringtone tone = RingtoneManager.getRingtone(a, Uri.parse(val));
                if (tone != null) {
                    pref.setSummary(tone.getTitle(a));
                }
            } else {
                pref.setSummary(R.string.none);
            }
        } else if (pref instanceof MultiSelectListPreference) {
            Set<String> vals = prefs.getStringSet(pref.getKey(), null);
            if (vals != null) {
                MultiSelectListPreference multi = (MultiSelectListPreference) pref;
                IntList indexList = new ArrayIntList(vals.size());
                for (String val : vals) { // find selected entry indexes
                    int i = multi.findIndexOfValue(val);
                    if (i >= 0) {
                        indexList.add(i);
                    }
                }
                int[] indexes = indexList.toArray();
                Arrays.sort(indexes); // to display in same order as dialog
                pref.setSummary(TextUtils.join(getString(R.string.delimiter),
                        Elements.slice(multi.getEntries(), indexes)));
            }
        } else if (pref instanceof EditTextPreference) {
            pref.setSummary(prefs.getString(pref.getKey(), null));
        } else if (pref.getClass() == Preference.class) {
            String val = prefs.getString(pref.getKey(), null);
            if (!TextUtils.isEmpty(val)) { // don't clear existing summary
                pref.setSummary(val);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref != null) {
            setSummary(pref);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SprocketsFragment.onSaveInstanceState(this, outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        a = null;
    }
}
