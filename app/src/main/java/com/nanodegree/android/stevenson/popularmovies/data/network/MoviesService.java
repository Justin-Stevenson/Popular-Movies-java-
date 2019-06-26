package com.nanodegree.android.stevenson.popularmovies.data.network;

import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.nanodegree.android.stevenson.popularmovies.model.Review;
import com.nanodegree.android.stevenson.popularmovies.model.Trailer;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MoviesService {

    @GET("movie/popular")
    Single<List<Movie>> getPopularMovies();

    @GET("movie/top_rated")
    Single<List<Movie>> getTopRatedMovies();

    @GET("movie/{id}/videos")
    Single<List<Trailer>> getMovieTrailers(@Path("id") String id);

    @GET("movie/{id}/reviews")
    Single<List<Review>> getMovieReviews(@Path("id") String id);
}
