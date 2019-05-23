package com.nanodegree.android.stevenson.popularmovies.data.network;

import com.nanodegree.android.stevenson.popularmovies.models.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MoviesService {

    @GET("movie/popular")
    Call<List<Movie>> getPopularMovies();

    @GET("movie/top_rated")
    Call<List<Movie>> getTopRatedMovies();
}
