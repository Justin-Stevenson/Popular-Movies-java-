package com.nanodegree.android.stevenson.popularmovies.data.network;

import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.nanodegree.android.stevenson.popularmovies.model.Review;
import com.nanodegree.android.stevenson.popularmovies.model.Trailer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MoviesService {

    @GET("movie/popular")
    Call<List<Movie>> getPopularMovies();

    @GET("movie/top_rated")
    Call<List<Movie>> getTopRatedMovies();

    @GET("movie/{id}/videos")
    Call<List<Trailer>> getMovieTrailers(@Path("id") String id);

    @GET("movie/{id}/reviews")
    Call<List<Review>> getMovieReviews(@Path("id") String id);
}
