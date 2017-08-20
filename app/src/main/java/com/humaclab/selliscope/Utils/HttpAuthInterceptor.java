package com.humaclab.selliscope.Utils;

import android.util.Base64;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Miaki on 3/12/17.
 */

public class HttpAuthInterceptor implements Interceptor {
    private String httpUsername;
    private String httpPassword;

    public HttpAuthInterceptor(String httpUsername, String httpPassword) {
        this.httpUsername = httpUsername;
        this.httpPassword = httpPassword;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newRequest = chain.request().newBuilder()
                .addHeader("Authorization", getAuthorizationValue())
                .build();
        return chain.proceed(newRequest);
    }

    private String getAuthorizationValue() {
        final String userAndPassword = httpUsername + ":" + httpPassword;
        return "Basic " + Base64.encodeToString(userAndPassword.getBytes(), Base64.NO_WRAP);
    }
}

