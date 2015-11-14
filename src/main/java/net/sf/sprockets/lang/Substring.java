/*
 * Copyright 2013-2015 pushbit <pushbit@gmail.com>
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

import javax.annotation.Nullable;

import net.sf.sprockets.lang.ImmutableSubstring.Builder;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

/**
 * Contextual information about a string found within another string.
 */
@Immutable
public abstract class Substring {
	Substring() {
	}

	/**
	 * Build an immutable instance.
	 * 
	 * @since 3.0.0
	 */
	public static Builder builder() {
		return ImmutableSubstring.builder();
	}

	/**
	 * Zero-based position of the first character of the substring within the superstring.
	 */
	@Default
	public int getOffset() {
		String value = getValue();
		String superstring = getSuperstring();
		return value != null && superstring != null ? superstring.indexOf(value) : -1;
	}

	/**
	 * Number of characters in the substring.
	 */
	@Default
	public int getLength() {
		String value = getValue();
		return value != null ? value.length() : 0;
	}

	/**
	 * Content of the substring.
	 * 
	 * @return null if not available
	 */
	@Nullable
	public abstract String getValue();

	/**
	 * Content of the superstring that contains the substring.
	 * 
	 * @return null if not available
	 */
	@Nullable
	public abstract String getSuperstring();
}
