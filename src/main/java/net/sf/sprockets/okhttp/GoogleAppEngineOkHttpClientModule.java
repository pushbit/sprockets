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

package net.sf.sprockets.okhttp;

import javax.inject.Singleton;

import com.swizel.okhttp3.GoogleAppEngineOkHttpClient;

import dagger.Module;
import dagger.Provides;
import okhttp3.Call;
import okhttp3.Call.Factory;

/**
 * Provides a Singleton {@link GoogleAppEngineOkHttpClient} instance for {@link Factory}
 * dependencies.
 *
 * @since 4.0.0
 */
@Module
public class GoogleAppEngineOkHttpClientModule {
	@Provides
	@Singleton
	static Call.Factory callFactory() {
		return new GoogleAppEngineOkHttpClient();
	}
}
