/*
 * Copyright 2015-2017 pushbit <pushbit@gmail.com>
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

import static net.sf.sprockets.util.MeasureUnit.KILOMETER;
import static net.sf.sprockets.util.MeasureUnit.MILE;

import java.util.Locale;

import com.google.common.collect.ImmutableSet;

/**
 * Utility methods for working with Locales.
 *
 * @since 2.2.0
 */
public class Locales {
	private Locales() {
	}

	/** Countries that measure distances in miles. */
	private static final ImmutableSet<String> USES_MILES =
			ImmutableSet.of("AS", "BS", "BZ", "DM", "FK", "GB", "GD", "GU", "KN", "KY", "LC", "LR",
					"MM", "MP", "SH", "TC", "US", "VC", "VG", "VI", "WS");

	/**
	 * Get the unit of distance that is used in the default locale, {@code KILOMETER} or
	 * {@code MILE}.
	 */
	public static MeasureUnit getDistanceUnit() {
		return getDistanceUnit(Locale.getDefault());
	}

	/**
	 * Get the unit of distance that is used in the locale, {@code KILOMETER} or {@code MILE}.
	 */
	public static MeasureUnit getDistanceUnit(Locale locale) {
		return USES_MILES.contains(locale.getCountry()) ? MILE : KILOMETER;
	}
}
