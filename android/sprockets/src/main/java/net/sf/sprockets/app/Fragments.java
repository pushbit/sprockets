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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import static android.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE;
import static android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;

/**
 * Utility methods for working with Fragments.
 */
public class Fragments {
    private Fragments() {
    }

    /**
     * Get the arguments for the Fragment, instantiating them if necessary. As with
     * {@link Fragment#setArguments(Bundle) setArguments}, this can only be called before the
     * Fragment has been attached to its Activity.
     */
    public static Bundle arguments(Fragment frag) {
        Bundle args = frag.getArguments();
        if (args == null) {
            args = new Bundle();
            frag.setArguments(args);
        }
        return args;
    }

    /**
     * Begin a transaction that uses the
     * {@link FragmentTransaction#TRANSIT_FRAGMENT_OPEN TRANSIT_FRAGMENT_OPEN} transition.
     *
     * @return null if the FragmentManager is not available
     */
    public static FragmentTransaction open(Activity activity) {
        return open(activity.getFragmentManager());
    }

    /**
     * Begin a transaction that uses the
     * {@link FragmentTransaction#TRANSIT_FRAGMENT_OPEN TRANSIT_FRAGMENT_OPEN} transition.
     *
     * @return null if the FragmentManager is not available
     */
    public static FragmentTransaction open(Fragment frag) {
        return open(frag.getFragmentManager());
    }

    /**
     * Begin a transaction that uses the
     * {@link FragmentTransaction#TRANSIT_FRAGMENT_OPEN TRANSIT_FRAGMENT_OPEN} transition.
     *
     * @return null if the FragmentManager is null
     */
    public static FragmentTransaction open(FragmentManager fm) {
        return transit(fm, TRANSIT_FRAGMENT_OPEN);
    }

    /**
     * Begin a transaction that uses the
     * {@link FragmentTransaction#TRANSIT_FRAGMENT_CLOSE TRANSIT_FRAGMENT_CLOSE} transition.
     *
     * @return null if the FragmentManager is not available
     */
    public static FragmentTransaction close(Activity activity) {
        return close(activity.getFragmentManager());
    }

    /**
     * Begin a transaction that uses the
     * {@link FragmentTransaction#TRANSIT_FRAGMENT_CLOSE TRANSIT_FRAGMENT_CLOSE} transition.
     *
     * @return null if the FragmentManager is not available
     */
    public static FragmentTransaction close(Fragment frag) {
        return close(frag.getFragmentManager());
    }

    /**
     * Begin a transaction that uses the
     * {@link FragmentTransaction#TRANSIT_FRAGMENT_CLOSE TRANSIT_FRAGMENT_CLOSE} transition.
     *
     * @return null if the FragmentManager is null
     */
    public static FragmentTransaction close(FragmentManager fm) {
        return transit(fm, TRANSIT_FRAGMENT_CLOSE);
    }

    /**
     * Begin a transaction that uses the transition.
     *
     * @return null if the FragmentManager is null
     */
    private static FragmentTransaction transit(FragmentManager fm, int transit) {
        return fm != null ? fm.beginTransaction().setTransition(transit) : null;
    }
}
