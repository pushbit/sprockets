/*
 * Copyright 2017 pushbit <pushbit@gmail.com>
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.primitives.ArrayLongList;
import org.junit.Test;

public class ElementsTest {
	@Test
	public void testAddAll() {
		ArrayLongList before = new ArrayLongList();
		before.add(3L);
		before.add(5L);
		ArrayLongList after = new ArrayLongList(before);
		after.add(7L);
		after.add(9L);
		assertTrue(Elements.addAll(before, new long[] { 7L, 9L }));
		assertEquals(after, before);
	}

	@Test
	public void testGetFromArray() {
		Integer[] array = { 3, 5, 7 };
		assertEquals((Integer) 5, Elements.get(array, 1));
		assertNull(Elements.get((Integer[]) null, 1));
		assertNull(Elements.get(array, 3));
	}

	@Test
	public void testGetFromList() {
		List<Integer> list = Arrays.asList(3, 5, 7);
		assertEquals((Integer) 5, Elements.get(list, 1));
		assertNull(Elements.get((List<Integer>) null, 1));
		assertNull(Elements.get(list, 3));
	}

	@Test
	public void testSliceArray() {
		Integer[] before = { 3, 5, 7, 9 };
		Integer[] after = { 5, 9 };
		assertArrayEquals(after, Elements.slice(before, 1, 3));
	}

	@Test
	public void testSliceList() {
		List<Integer> before = Arrays.asList(3, 5, 7, 9);
		List<Integer> after = Arrays.asList(5, 9);
		assertEquals(after, Elements.slice(before, 1, 3));
	}

	@Test
	public void testIntSum() {
		int[] array = { 3, 5, 7 };
		assertEquals(15, Elements.sum(array));
	}

	@Test
	public void testLongSum() {
		long[] array = { 3L, 5L, 7L };
		assertEquals(15L, Elements.sum(array));
	}

	@Test
	public void testToInts() {
		String[] before = { "3", "5" };
		int[] after = { 3, 5 };
		assertArrayEquals(after, Elements.toInts(before));
	}

	@Test
	public void testToLongs() {
		String[] before = { "3", "5" };
		long[] after = { 3L, 5L };
		assertArrayEquals(after, Elements.toLongs(before));
	}

	@Test
	public void testIntsToStrings() {
		int[] before = { 3, 5 };
		String[] after = { "3", "5" };
		assertArrayEquals(after, Elements.toStrings(before));
	}

	@Test
	public void testLongsToStrings() {
		long[] before = { 3L, 5L };
		String[] after = { "3", "5" };
		assertArrayEquals(after, Elements.toStrings(before));
	}
}
