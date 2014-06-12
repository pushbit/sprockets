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

package net.sf.sprockets.util.concurrent;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Uninterruptibles;

/**
 * Opposite of Guava's {@link Uninterruptibles}. Interruptible operations are immediately
 * interrupted.
 * 
 * @since 1.4.0
 */
public class Interruptibles {
	private Interruptibles() {
	}

	/**
	 * Sleep for the length of time.
	 */
	public static void sleep(long length, TimeUnit unit) {
		try {
			unit.sleep(length);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
