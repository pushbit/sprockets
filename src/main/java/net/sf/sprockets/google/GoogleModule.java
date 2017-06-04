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

package net.sf.sprockets.google;

import dagger.Module;
import dagger.Provides;
import net.sf.sprockets.okhttp.GoogleAppEngineOkHttpClientModule;
import net.sf.sprockets.okhttp.OkHttpClientModule;
import okhttp3.Call.Factory;

/**
 * Provides Singleton {@link InstanceId} and {@link StreetView} dependencies. You must provide a
 * {@link Factory} implementation (or include one of the below Modules) and a {@link GoogleApiAuth}
 * instance.
 *
 * @see OkHttpClientModule
 * @see GoogleAppEngineOkHttpClientModule
 * @since 4.0.0
 */
@Module
public class GoogleModule {
	@Provides
	static InstanceId instanceId(GoogleInstanceId instanceId) {
		return instanceId;
	}

	@Provides
	static StreetView streetView(GoogleStreetView streetView) {
		return streetView;
	}
}
