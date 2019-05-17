package com.nanodegree.android.stevenson.popularmovies.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nanodegree.android.stevenson.popularmovies.models.Movie;
import com.nanodegree.android.stevenson.popularmovies.rest.helpers.ApiKeyInterceptor;
import com.nanodegree.android.stevenson.popularmovies.rest.helpers.MoviesDeserializer;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    private static ApiKeyInterceptor apiKeyInterceptor = new ApiKeyInterceptor();

    private static HttpLoggingInterceptor httpLogger =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder okHttp =
            new OkHttpClient.Builder()
                    .addInterceptor(httpLogger)
                    .addInterceptor(apiKeyInterceptor);

    private static Gson gson =
            new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Movie>>() {}.getType(), new MoviesDeserializer())
                .create();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttp.build());

    private static Retrofit retrofit = builder.build();

    public static <T> T getService(Class<T> serviceType) {
        return retrofit.create(serviceType);
    }
}
