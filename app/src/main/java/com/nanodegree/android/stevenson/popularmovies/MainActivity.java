package com.nanodegree.android.stevenson.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nanodegree.android.stevenson.popularmovies.models.Movie;
import com.nanodegree.android.stevenson.popularmovies.rest.MoviesService;
import com.nanodegree.android.stevenson.popularmovies.rest.ServiceFactory;
import com.nanodegree.android.stevenson.popularmovies.rest.helpers.NetworkConnectionException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements MoviesGridAdapter.MovieClickListener {

    private static final String TAG = "MainActivity";
    private static final String POPULAR_MOVIES = "popular";
    private static final String TOP_RATED_MOVIES = "top rated";
    private static final String MOVIES_KEY = "movies";
    private static final String CURRENT_QUERY_KEY = "current_query";

    private ProgressBar mProgressBar;
    private ImageView mErrorImg;
    private TextView mErrorHeading;
    private TextView mErrorMessage;
    private Button mErrorButton;
    private RecyclerView mMoviesGrid;
    private MoviesGridAdapter mMoviesGridAdapter;
    private String mCurrentMovieQuery = POPULAR_MOVIES;  // default to POPULAR on initial load
    private List<Movie> mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.movies_pb);
        mErrorImg = (ImageView) findViewById(R.id.error_iv);
        mErrorHeading = (TextView) findViewById(R.id.error_heading_tv);
        mErrorMessage = (TextView) findViewById(R.id.error_message_tv);
        mErrorButton = (Button) findViewById(R.id.error_btn);
        mMoviesGrid = (RecyclerView) findViewById(R.id.movies_rv);

        mErrorButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadMovies();
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mMoviesGrid.setLayoutManager(gridLayoutManager);

        if (hasMoviesSaved(savedInstanceState)) {
            mCurrentMovieQuery = savedInstanceState.getString(CURRENT_QUERY_KEY);
            List<Movie> movies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
            loadMovies(movies);
        } else {
            loadMovies();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> movies = new ArrayList<>(mMovies);
        outState.putParcelableArrayList(MOVIES_KEY,movies);
        outState.putString(CURRENT_QUERY_KEY, mCurrentMovieQuery);
    }

    @Override
    public void onMovieClick(Movie clickedMovie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.MOVIE_KEY, clickedMovie);

        startActivity(intent);
    }

    private void loadMovies(List<Movie> movies) {
        mMovies = movies;
        mMoviesGridAdapter = new MoviesGridAdapter(mMovies, MainActivity.this);
        mMoviesGrid.setAdapter(mMoviesGridAdapter);
        showMovies();
    }

    private void loadMovies() {
        showProgressBar();

        if (mCurrentMovieQuery == POPULAR_MOVIES) {
            getPopularMovies();
        } else {
            getTopRatedMovies();
        }
    }

    private void getTopRatedMovies() {
        mCurrentMovieQuery = TOP_RATED_MOVIES;
        MoviesService moviesService = ServiceFactory.getService(MoviesService.class);

        final Call<List<Movie>> request = moviesService.getTopRatedMovies();

        makeHttpRequest(request);
    }

    private void getPopularMovies() {
        mCurrentMovieQuery = POPULAR_MOVIES;
        MoviesService moviesService = ServiceFactory.getService(MoviesService.class);

        final Call<List<Movie>> request = moviesService.getPopularMovies();

        makeHttpRequest(request);
    }

    private void makeHttpRequest(Call<List<Movie>> request) {
        request.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful()) {
                    int size = response.body().size();
                    Log.d(TAG, "onResponse: retrieved " + size + " movies");

                    loadMovies(response.body());
                } else {
                    Log.e(TAG, "onResponse: " + response.code() + " " + response.message());
                    showError(Error.DATA_RETRIEVAL);
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                if (t instanceof NetworkConnectionException) {
                    Log.e(TAG, "onFailure: error retrieving movie data due to no network connection", t);
                    showError(Error.NETWORK_CONNECTION);
                } else {
                    Log.e(TAG, "onFailure: error retrieving movie data", t);
                    showError(Error.DATA_RETRIEVAL);
                }
            }
        });
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);

        mErrorImg.setVisibility(View.INVISIBLE);
        mErrorHeading.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mErrorButton.setVisibility(View.INVISIBLE);
        mMoviesGrid.setVisibility(View.INVISIBLE);
    }

    private void showError(Error errorType) {
        setErrorData(errorType);
        mErrorImg.setVisibility(View.VISIBLE);
        mErrorHeading.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
        mErrorButton.setVisibility(View.VISIBLE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mMoviesGrid.setVisibility(View.INVISIBLE);
    }

    private void showMovies() {
        mMoviesGrid.setVisibility(View.VISIBLE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorImg.setVisibility(View.INVISIBLE);
        mErrorHeading.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mErrorButton.setVisibility(View.INVISIBLE);
    }

    private void setErrorData(Error errorType) {
        mErrorImg.setImageResource(errorType.getImage());
        mErrorImg.setContentDescription(getString(errorType.getImageDescription()));
        mErrorHeading.setText(errorType.getHeader());
        mErrorMessage.setText(errorType.getMessage());
    }

    private boolean hasMoviesSaved(Bundle savedInstanceState) {
        return savedInstanceState != null && savedInstanceState.getParcelableArrayList(MOVIES_KEY) != null;
    }
}
