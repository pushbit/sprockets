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

package net.sf.sprockets.util;

import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

import org.apache.commons.collections.primitives.ArrayIntList;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for working with SparseArrays.
 */
public class SparseArrays {
    private SparseArrays() {
    }

    /**
     * Get the keys of the SparseArray.
     */
    public static int[] keys(SparseArray<?> array) {
        int[] keys = new int[array.size()];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = array.keyAt(i);
        }
        return keys;
    }

    /**
     * Get the values of the SparseArray.
     */
    public static <E> List<E> values(SparseArray<E> array) {
        int size = array.size();
        List<E> vals = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            vals.add(array.valueAt(i));
        }
        return vals;
    }

    /**
     * Get the keys of the SparseBooleanArray.
     */
    public static int[] keys(SparseBooleanArray array) {
        int[] keys = new int[array.size()];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = array.keyAt(i);
        }
        return keys;
    }

    /**
     * Get the values of the SparseBooleanArray.
     */
    public static boolean[] values(SparseBooleanArray array) {
        boolean[] vals = new boolean[array.size()];
        for (int i = 0; i < vals.length; i++) {
            vals[i] = array.valueAt(i);
        }
        return vals;
    }

    /**
     * Get the keys of the SparseBooleanArray whose value is true.
     */
    public static int[] trueKeys(SparseBooleanArray array) {
        return keys(array, true);
    }

    /**
     * Get the keys of the SparseBooleanArray whose value is false.
     */
    public static int[] falseKeys(SparseBooleanArray array) {
        return keys(array, false);
    }

    /**
     * Get the keys of the SparseBooleanArray which have the value.
     */
    private static int[] keys(SparseBooleanArray array, boolean value) {
        int size = array.size();
        ArrayIntList keys = new ArrayIntList(size);
        for (int i = 0; i < size; i++) {
            if (array.valueAt(i) == value) {
                keys.add(array.keyAt(i));
            }
        }
        return keys.toArray();
    }

    /**
     * Get the first key of the SparseBooleanArray whose value is true.
     *
     * @return {@link Integer#MIN_VALUE} if no values are true
     */
    public static int firstTrueKey(SparseBooleanArray array) {
        return firstKey(array, true);
    }

    /**
     * Get the first key of the SparseBooleanArray whose value is false.
     *
     * @return {@link Integer#MIN_VALUE} if no values are false
     */
    public static int firstFalseKey(SparseBooleanArray array) {
        return firstKey(array, false);
    }

    /**
     * Get the first key of the SparseBooleanArray which has the value.
     *
     * @return {@link Integer#MIN_VALUE} if the value is not found
     */
    private static int firstKey(SparseBooleanArray array, boolean value) {
        for (int i = 0; i < array.size(); i++) {
            if (array.valueAt(i) == value) {
                return array.keyAt(i);
            }
        }
        return Integer.MIN_VALUE;
    }

    /**
     * Get the keys of the SparseIntArray.
     */
    public static int[] keys(SparseIntArray array) {
        int[] keys = new int[array.size()];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = array.keyAt(i);
        }
        return keys;
    }

    /**
     * Get the values of the SparseIntArray.
     */
    public static int[] values(SparseIntArray array) {
        int[] vals = new int[array.size()];
        for (int i = 0; i < vals.length; i++) {
            vals[i] = array.valueAt(i);
        }
        return vals;
    }

    /**
     * Get the keys of the LongSparseArray.
     */
    public static long[] keys(LongSparseArray<?> array) {
        long[] keys = new long[array.size()];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = array.keyAt(i);
        }
        return keys;
    }

    /**
     * Get the values of the LongSparseArray.
     */
    public static <E> List<E> values(LongSparseArray<E> array) {
        int size = array.size();
        List<E> vals = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            vals.add(array.valueAt(i));
        }
        return vals;
    }
}
