package com.nanodegree.android.stevenson.popularmovies.rest.helpers;

import com.nanodegree.android.stevenson.popularmovies.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiKeyInterceptor implements Interceptor {

    private static final String API_KEY_PARAM_KEY = "api_key";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url();

        HttpUrl newUrl = url.newBuilder()
                .addEncodedQueryParameter(API_KEY_PARAM_KEY, BuildConfig.MovieDbApiKey)
                .build();

        Request.Builder requestBuilder = request.newBuilder().url(newUrl);

        Request newRequest = requestBuilder.build();

        return chain.proceed(newRequest);
    }
}
