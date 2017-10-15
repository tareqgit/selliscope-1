package com.humaclab.selliscope.Utils;

import android.util.Base64;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Leon on 4/5/17.
 */

public class HttpAuthInterceptor implements Interceptor {
    private final String userAgent;
    private String httpUsername;
    private String httpPassword;

    public HttpAuthInterceptor(String httpUsername, String httpPassword) {
        this.httpUsername = httpUsername;
        this.httpPassword = httpPassword;
        this.userAgent = Constants.userAgent;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newRequest = chain.request().newBuilder()
                .addHeader("Authorization", getAuthorizationValue())
                .addHeader("User-Agent", userAgent)
                .build();
        return chain.proceed(newRequest);
    }

    private String getAuthorizationValue() {
        final String userAndPassword = httpUsername + ":" + httpPassword;
        return "Basic " + Base64.encodeToString(userAndPassword.getBytes(), Base64.NO_WRAP);
    }
}

