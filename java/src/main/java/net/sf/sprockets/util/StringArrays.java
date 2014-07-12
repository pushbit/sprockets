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

/**
 * Utility methods for working with String arrays.
 * 
 * @since 1.5.0
 */
public class StringArrays {
	private StringArrays() {
	}

	/**
	 * Convert the ints to an array of Strings.
	 */
	public static String[] from(int... values) {
		String[] s = new String[values.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = String.valueOf(values[i]);
		}
		return s;
	}

	/**
	 * Convert the longs to an array of Strings.
	 */
	public static String[] from(long... values) {
		String[] s = new String[values.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = String.valueOf(values[i]);
		}
		return s;
	}
}
