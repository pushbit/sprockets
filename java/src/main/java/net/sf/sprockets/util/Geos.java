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
 * Utility methods for working with geographical functions.
 * 
 * @since 1.3.0
 */
public class Geos {
	private Geos() {
	}

	/**
	 * Get the cosine of the latitude after converting it to radians. The result can be used to
	 * improve the accuracy of geo point distance calculations. See <a
	 * href="http://www.meridianworlddata.com/Distance-calculation.asp" target="_blank">Distance
	 * Calculation</a> for more information.
	 * 
	 * @param latitude
	 *            in degrees
	 */
	public static double cos(double latitude) {
		return Math.cos(latitude / 57.295779579);
	}
}
