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
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Sprockets.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sprockets.util;

import java.util.List;

/**
 * Utility methods for working with array and list elements.
 * 
 * @since 1.1.0
 */
public class Elements {
	private Elements() {
	}

	/**
	 * Get the element at the index in the array.
	 * 
	 * @return null if the index is out of bounds
	 */
	public static <T> T get(T[] array, int index) {
		return index >= 0 && index < array.length ? array[index] : null;
	}

	/**
	 * Get the element at the index in the list.
	 * 
	 * @return null if the index is out of bounds
	 */
	public static <T> T get(List<T> list, int index) {
		return index >= 0 && index < list.size() ? list.get(index) : null;
	}
}
