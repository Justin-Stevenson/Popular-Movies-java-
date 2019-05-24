package com.nanodegree.android.stevenson.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nanodegree.android.stevenson.popularmovies.common.Error;
import com.nanodegree.android.stevenson.popularmovies.common.SortOrder;
import com.nanodegree.android.stevenson.popularmovies.data.MoviesRepository;
import com.nanodegree.android.stevenson.popularmovies.data.network.helpers.NetworkConnectionException;
import com.nanodegree.android.stevenson.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements MoviesGridAdapter.MovieClickListener {

    private static final String TAG = "MainActivity";
    private static final String MOVIES_KEY = "movies";
    private static final String CURRENT_SORT_ORDER_KEY = "sort_order";

    private ProgressBar mProgressBar;
    private ImageView mErrorImg;
    private TextView mErrorHeading;
    private TextView mErrorMessage;
    private Button mErrorButton;
    private RecyclerView mMoviesGrid;
    private SortOrder mCurrentSortOrder;
    private List<Movie> mMovies;
    private MoviesRepository mMoviesRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.movies_pb);
        mErrorImg = findViewById(R.id.error_iv);
        mErrorHeading = findViewById(R.id.error_heading_tv);
        mErrorMessage = findViewById(R.id.error_message_tv);
        mErrorButton = findViewById(R.id.error_btn);
        mMoviesGrid = findViewById(R.id.movies_rv);

        mErrorButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadMovies();
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mMoviesGrid.setLayoutManager(gridLayoutManager);

        mMoviesRepository = new MoviesRepository();

        if (hasMoviesSaved(savedInstanceState)) {
            mCurrentSortOrder = (SortOrder) savedInstanceState.getSerializable(CURRENT_SORT_ORDER_KEY);
            mMovies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
            loadMovies(mMovies);
        } else {
            mCurrentSortOrder = SortOrder.POPULAR;
            loadMovies();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> movies = new ArrayList<>(mMovies);
        outState.putParcelableArrayList(MOVIES_KEY,movies);
        outState.putSerializable(CURRENT_SORT_ORDER_KEY, mCurrentSortOrder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies_options, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();

        switch (selectedItemId) {
            case R.id.action_popular:
                if (SortOrder.POPULAR != mCurrentSortOrder) {
                    mCurrentSortOrder = SortOrder.POPULAR;
                    loadMovies();
                }
                return true;

            case R.id.action_top_rated:
                if (SortOrder.TOP_RATED != mCurrentSortOrder) {
                    mCurrentSortOrder = SortOrder.TOP_RATED;
                    loadMovies();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieClick(Movie clickedMovie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.MOVIE_KEY, clickedMovie);

        startActivity(intent);
    }

    private void loadMovies(List<Movie> movies) {
        MoviesGridAdapter moviesGridAdapter = new MoviesGridAdapter(movies, MainActivity.this);
        mMoviesGrid.setAdapter(moviesGridAdapter);
        showMovies();
    }

    private void loadMovies() {
        showProgressBar();

        mMoviesRepository.getMovies(mCurrentSortOrder, getMoviesCallback());
    }

    private Callback<List<Movie>> getMoviesCallback() {
        return new Callback<List<Movie>>() {
            @Override
            public void onResponse(@NonNull Call<List<Movie>> call, @NonNull Response<List<Movie>> response) {
                if (response.isSuccessful()) {
                    mMovies = response.body();
                    loadMovies(mMovies);
                } else {
                    Log.e(TAG, "onResponse: " + response.code() + " " + response.message());
                    showError(Error.DATA_RETRIEVAL);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Movie>> call, @NonNull Throwable t) {
                if (t instanceof NetworkConnectionException) {
                    Log.e(TAG, "onFailure: error retrieving movie data due to no network connection", t);
                    showError(Error.NETWORK_CONNECTION);
                } else {
                    Log.e(TAG, "onFailure: error retrieving movie data", t);
                    showError(Error.DATA_RETRIEVAL);
                }
            }
        };
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
