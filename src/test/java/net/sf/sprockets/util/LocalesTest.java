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

import static java.util.Locale.GERMANY;
import static java.util.Locale.US;
import static net.sf.sprockets.util.MeasureUnit.KILOMETER;
import static net.sf.sprockets.util.MeasureUnit.MILE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LocalesTest {
	@Test
	public void testDistanceUnit() {
		assertEquals(KILOMETER, Locales.getDistanceUnit(GERMANY));
		assertEquals(MILE, Locales.getDistanceUnit(US));
	}
}
