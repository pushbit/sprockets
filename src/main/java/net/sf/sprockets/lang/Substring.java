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

package net.sf.sprockets.lang;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Objects;

/**
 * Contextual information about a string found within another string.
 */
public class Substring {
	private final int mOffset;
	private final int mLength;
	private final String mSub;
	private final String mSuper;
	private int mHash;

	/**
	 * @throws IllegalArgumentException
	 *             if offset < 0 or length <= 0
	 */
	public Substring(int offset, int length) {
		this(offset, length, null, null);
	}

	/**
	 * @throws IllegalArgumentException
	 *             if offset < 0 or length <= 0
	 */
	public Substring(int offset, int length, String value, String superstring) {
		checkArgument(offset >= 0, "offset must be >= 0");
		checkArgument(length > 0, "length must be > 0");
		mOffset = offset;
		mLength = length;
		mSub = value;
		mSuper = superstring;
	}

	/**
	 * Zero-based position of the first character of the substring within the superstring.
	 */
	public int getOffset() {
		return mOffset;
	}

	/**
	 * Number of characters in the substring.
	 */
	public int getLength() {
		return mLength;
	}

	/**
	 * Content of the substring. May be null if not available.
	 */
	public String getValue() {
		return mSub;
	}

	/**
	 * Content of the superstring that contains the substring. May be null if not available.
	 */
	public String getSuperstring() {
		return mSuper;
	}

	@Override
	public int hashCode() {
		if (mHash == 0) {
			mHash = Objects.hashCode(mOffset, mLength, mSub, mSuper);
		}
		return mHash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			if (this == obj) {
				return true;
			} else if (obj instanceof Substring) {
				Substring o = (Substring) obj;
				return mOffset == o.mOffset && mLength == o.mLength && Objects.equal(mSub, o.mSub)
						&& Objects.equal(mSuper, o.mSuper);
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("offset", mOffset).add("length", mLength)
				.add("value", mSub).add("superstring", mSuper).omitNullValues().toString();
	}
}
