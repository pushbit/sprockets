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

package net.sf.sprockets.naming;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Utility methods for working with an {@link InitialContext}.
 * 
 * @since 1.5.0
 */
public class Contexts {
	private Contexts() {
	}

	/**
	 * Get the named object in the starting context.
	 * 
	 * @throws IllegalStateException
	 *             wrapping any NamingException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T lookup(String name) {
		try {
			return (T) new InitialContext().lookup(name);
		} catch (NamingException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Get the named object in the {@code java:comp/env} namespace.
	 * 
	 * @throws IllegalStateException
	 *             wrapping any NamingException
	 */
	public static <T> T resource(String name) {
		return lookup("java:comp/env/" + name);
	}
}
