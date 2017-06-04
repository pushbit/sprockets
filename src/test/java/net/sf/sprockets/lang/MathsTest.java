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

package net.sf.sprockets.lang;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathsTest {
	@Test
	public void testIntClamp() {
		assertEquals(7, Maths.clamp(7, 5, 9));
		assertEquals(5, Maths.clamp(3, 5, 9));
		assertEquals(9, Maths.clamp(11, 5, 9));
	}

	@Test
	public void testLongClamp() {
		assertEquals(7L, Maths.clamp(7L, 5L, 9L));
		assertEquals(5L, Maths.clamp(3L, 5L, 9L));
		assertEquals(9L, Maths.clamp(11L, 5L, 9L));
	}

	@Test
	public void testFloatClamp() {
		assertEquals(7.0f, Maths.clamp(7.0f, 5.0f, 9.0f), 0.0f);
		assertEquals(5.0f, Maths.clamp(3.0f, 5.0f, 9.0f), 0.0f);
		assertEquals(9.0f, Maths.clamp(11.0f, 5.0f, 9.0f), 0.0f);
	}

	@Test
	public void testDoubleClamp() {
		assertEquals(7.0, Maths.clamp(7.0, 5.0, 9.0), 0.0);
		assertEquals(5.0, Maths.clamp(3.0, 5.0, 9.0), 0.0);
		assertEquals(9.0, Maths.clamp(11.0, 5.0, 9.0), 0.0);
	}

	@Test
	public void testRollover() {
		assertEquals(7, Maths.rollover(7, 5, 9));
		assertEquals(8, Maths.rollover(3, 5, 9));
		assertEquals(6, Maths.rollover(11, 5, 9));
	}
}
