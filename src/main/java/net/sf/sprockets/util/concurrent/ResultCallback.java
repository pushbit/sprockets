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

package net.sf.sprockets.util.concurrent;

import com.google.common.util.concurrent.FutureCallback;

/**
 * Modelled after Guava's {@link FutureCallback}, but also includes a result in
 * {@link #onFailure(Object, Throwable) onFailure} that provides the context of the failure.
 * 
 * @since 1.0.0
 */
public interface ResultCallback<V> {
	/**
	 * The operation has completed successfully and provides a result.
	 */
	void onSuccess(V result);

	/**
	 * The operation has failed and provides some form of result along with the cause of the
	 * failure.
	 */
	void onFailure(V result, Throwable t);
}
