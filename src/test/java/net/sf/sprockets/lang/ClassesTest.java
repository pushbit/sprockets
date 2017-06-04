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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ClassesTest {
	@Test
	public void testClass() {
		assertEquals(String.class, Classes.getClass("test"));
		assertEquals(String.class, Classes.getClass("test".getClass()));
	}

	@Test
	public void testNamedClass() {
		assertEquals(ClassesTest.class, Classes.getNamedClass(this));
		assertEquals(ClassesTest.class, Classes.getNamedClass(new Object() {
		}));
	}

	@Test
	public void testTypeArgument() {
		List<String> list = new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
		};
		assertEquals(String.class, Classes.getTypeArgument(list.getClass(), "E"));
	}

	@Test
	public void testTypeArguments() {
		Map<Integer, String> map = new HashMap<Integer, String>() {
			private static final long serialVersionUID = 1L;
		};
		assertArrayEquals(new Class<?>[] { Integer.class, String.class },
				Classes.getTypeArguments(map.getClass(), "K", "V"));
	}
}
