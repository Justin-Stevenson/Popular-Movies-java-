package com.nanodegree.android.stevenson.popularmovies.data;

import com.nanodegree.android.stevenson.popularmovies.common.SortOrderType;
import com.nanodegree.android.stevenson.popularmovies.common.SortOrderType.SortOrder;
import com.nanodegree.android.stevenson.popularmovies.data.network.MoviesService;
import com.nanodegree.android.stevenson.popularmovies.data.network.ServiceFactory;
import com.nanodegree.android.stevenson.popularmovies.models.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MoviesRepository {

    public void getMovies(@SortOrder int sortOrder, Callback<List<Movie>> callback) {
        final Call<List<Movie>> request;
        MoviesService moviesService = ServiceFactory.getService(MoviesService.class);

        if (SortOrderType.POPULAR == sortOrder) {
            request = moviesService.getPopularMovies();
        } else {
            request = moviesService.getTopRatedMovies();
        }


        request.enqueue(callback);
    }
}
