/*
 * Copyright 2013-2015 pushbit <pushbit@gmail.com>
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.primitives.LongCollection;

import com.google.common.collect.ObjectArrays;

/**
 * Utility methods for working with array and collection elements.
 * 
 * @since 1.1.0
 */
public class Elements {
	private Elements() {
	}

	/**
	 * Add all elements in the array to the collection.
	 * 
	 * @return true if the collection was changed
	 * @since 2.6.0
	 */
	public static boolean addAll(LongCollection collection, long[] elements) {
		boolean changed = false;
		for (long element : elements) {
			changed |= collection.add(element);
		}
		return changed;
	}

	/**
	 * Get the element at the index in the array.
	 * 
	 * @return null if the array is null or the index is out of bounds
	 */
	public static <T> T get(T[] array, int index) {
		return array != null && index >= 0 && index < array.length ? array[index] : null;
	}

	/**
	 * Get the element at the index in the list.
	 * 
	 * @return null if the list is null or the index is out of bounds
	 */
	public static <T> T get(List<T> list, int index) {
		return list != null && index >= 0 && index < list.size() ? list.get(index) : null;
	}

	/**
	 * Get the elements in the array that are at the indexes.
	 * 
	 * @since 1.4.0
	 */
	public static <T> T[] slice(T[] array, int... indexes) {
		final int length = indexes.length;
		T[] slice = ObjectArrays.newArray(array, length);
		for (int i = 0; i < length; i++) {
			slice[i] = array[indexes[i]];
		}
		return slice;
	}

	/**
	 * Get the elements in the list that are at the indexes.
	 * 
	 * @since 1.4.0
	 */
	public static <T> List<T> slice(List<T> list, int... indexes) {
		List<T> slice = new ArrayList<T>(indexes.length);
		for (int i : indexes) {
			slice.add(list.get(i));
		}
		return slice;
	}

	/**
	 * Convert the Strings to an array of longs.
	 * 
	 * @since 2.6.0
	 */
	public static long[] toLongs(String... values) {
		final int length = values.length;
		long[] l = new long[length];
		for (int i = 0; i < length; i++) {
			l[i] = Long.parseLong(values[i]);
		}
		return l;
	}

	/**
	 * Convert the ints to an array of Strings.
	 * 
	 * @since 2.6.0
	 */
	public static String[] toStrings(int... values) {
		return toStrings(values, null);
	}

	/**
	 * Convert the longs to an array of Strings.
	 * 
	 * @since 2.6.0
	 */
	public static String[] toStrings(long... values) {
		return toStrings(null, values);
	}

	private static String[] toStrings(int[] intValues, long[] longValues) {
		final boolean ints = intValues != null;
		final int length = ints ? intValues.length : longValues.length;
		String[] s = new String[length];
		for (int i = 0; i < length; i++) {
			if (ints) {
				s[i] = String.valueOf(intValues[i]);
			} else {
				s[i] = String.valueOf(longValues[i]);
			}
		}
		return s;
	}
}
