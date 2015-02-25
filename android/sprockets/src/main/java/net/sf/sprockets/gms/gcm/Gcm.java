/*
 * Copyright 2015 pushbit <pushbit@gmail.com>
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

package net.sf.sprockets.gms.gcm;

import com.google.android.gms.auth.GoogleAuthUtil;

import retrofit.RestAdapter;
import retrofit.RestAdapter.Builder;
import retrofit.RetrofitError;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Utility methods for working with Google Cloud Messaging.
 */
public class Gcm {
    private static final Api API = new Builder().setEndpoint("https://android.googleapis.com/gcm")
            .setLogLevel(RestAdapter.LogLevel.FULL).build().create(Api.class); // todo del log level

    private Gcm() {
    }

    /**
     * Get a notification key for the user identified by the token.
     *
     * @param projectId      from Google Developers Console
     * @param idToken        from {@link GoogleAuthUtil#getToken}
     * @param keyName        unique per app and user, should be stored with notification key
     * @param registrationId to add to the notification key
     * @deprecated googlenotification service isn't working yet, always returns 401 Unauthorized
     */
    @Deprecated
    public static String getNotificationKey(String projectId, String idToken, String keyName,
                                            String registrationId) throws RetrofitError {
        return API.notificationKey(projectId, new Request(idToken, keyName, registrationId))
                .notification_key;
    }

    private interface Api {
        @POST("/googlenotification")
        Response notificationKey(@Header("project_id") String projectId, @Body Request request);
    }

    private static class Request {
        private final String operation = "add";
        private final String id_token;
        private final String notification_key_name;
        private final String[] registration_ids;

        private Request(String idToken, String keyName, String registrationId) {
            id_token = idToken;
            notification_key_name = keyName;
            registration_ids = new String[]{registrationId};
        }
    }

    private static class Response {
        private String notification_key;
    }
}
