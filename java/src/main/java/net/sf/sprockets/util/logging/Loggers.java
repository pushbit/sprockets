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

package net.sf.sprockets.util.logging;

import java.util.logging.Logger;

/**
 * Utility methods for working with Loggers.
 * 
 * @since 1.1.0
 */
public class Loggers {
	private Loggers() {
	}

	/**
	 * Get a logger for the class's package.
	 */
	public static Logger get(Class<?> cls) {
		return Logger.getLogger(cls.getPackage().getName());
	}

	/**
	 * Get a logger for the class's package that uses the resource bundle for localisation.
	 */
	public static Logger get(Class<?> cls, String resourceBundleName) {
		return Logger.getLogger(cls.getPackage().getName(), resourceBundleName);
	}
}
