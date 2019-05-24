package com.nanodegree.android.stevenson.popularmovies.data;

import com.nanodegree.android.stevenson.popularmovies.common.SortOrder;
import com.nanodegree.android.stevenson.popularmovies.data.network.MoviesService;
import com.nanodegree.android.stevenson.popularmovies.data.network.ServiceFactory;
import com.nanodegree.android.stevenson.popularmovies.models.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MoviesRepository {

    public void getMovies(SortOrder sortOrder, Callback<List<Movie>> callback) {
        final Call<List<Movie>> request;
        MoviesService moviesService = ServiceFactory.getService(MoviesService.class);

        if (SortOrder.POPULAR == sortOrder) {
            request = moviesService.getPopularMovies();
        } else {
            request = moviesService.getTopRatedMovies();
        }


        request.enqueue(callback);
    }
}
