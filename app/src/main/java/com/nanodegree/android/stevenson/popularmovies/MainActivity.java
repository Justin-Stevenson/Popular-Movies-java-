package com.nanodegree.android.stevenson.popularmovies;

import android.content.Context;
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
import com.nanodegree.android.stevenson.popularmovies.common.SortOrderType;
import com.nanodegree.android.stevenson.popularmovies.common.SortOrderType.SortOrder;
import com.nanodegree.android.stevenson.popularmovies.data.MoviesRepository;
import com.nanodegree.android.stevenson.popularmovies.data.network.helpers.NetworkConnectionException;
import com.nanodegree.android.stevenson.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements MoviesGridAdapter.MovieClickListener {

    private static final String TAG = "MainActivity";
    private static final String BUNDLE_MOVIES_KEY = "BUNDLE_MOVIES_KEY";
    private static final String BUNDLE_CURRENT_SORT_ORDER_KEY = "BUNDLE_CURRENT_SORT_ORDER_KEY";

    @BindView(R.id.movies_pb) ProgressBar mProgressBar;
    @BindView(R.id.error_iv) ImageView mErrorImg;
    @BindView(R.id.error_heading_tv) TextView mErrorHeading;
    @BindView(R.id.error_message_tv) TextView mErrorMessage;
    @BindView(R.id.error_btn) Button mErrorButton;
    @BindView(R.id.movies_rv) RecyclerView mMoviesGrid;

    private @SortOrder int mCurrentSortOrder;
    private List<Movie> mMovies;
    private MoviesRepository mMoviesRepository;

    public static Intent getStartIntent(Context context, Movie clickedMovie) {
        Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE_KEY, clickedMovie);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mMoviesGrid.setLayoutManager(gridLayoutManager);

        mMoviesRepository = new MoviesRepository();

        if (hasMoviesSaved(savedInstanceState)) {
            mCurrentSortOrder = getCurrentSortOrderFromSavedInstanceState(savedInstanceState);
            mMovies = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES_KEY);
            loadMovies(mMovies);
        } else {
            mCurrentSortOrder = SortOrderType.POPULAR;
            loadMovies();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> movies = new ArrayList<>(mMovies);
        outState.putParcelableArrayList(BUNDLE_MOVIES_KEY,movies);
        outState.putInt(BUNDLE_CURRENT_SORT_ORDER_KEY, mCurrentSortOrder);
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
                if (SortOrderType.POPULAR != mCurrentSortOrder) {
                    mCurrentSortOrder = SortOrderType.POPULAR;
                    loadMovies();
                }
                return true;

            case R.id.action_top_rated:
                if (SortOrderType.TOP_RATED != mCurrentSortOrder) {
                    mCurrentSortOrder = SortOrderType.TOP_RATED;
                    loadMovies();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieClick(Movie clickedMovie) {
        Intent intent = getStartIntent(this, clickedMovie);

        startActivity(intent);
    }

    @OnClick(R.id.error_btn)
    void loadMovies() {
        showProgressBar();

        mMoviesRepository.getMovies(mCurrentSortOrder, getMoviesCallback());
    }


    private void loadMovies(List<Movie> movies) {
        MoviesGridAdapter moviesGridAdapter = new MoviesGridAdapter(movies, MainActivity.this);
        mMoviesGrid.setAdapter(moviesGridAdapter);
        showMovies();
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
        return savedInstanceState != null && savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES_KEY) != null;
    }

    private @SortOrder int getCurrentSortOrderFromSavedInstanceState(Bundle savedInstanceState) {
        int sortOrderValue = savedInstanceState.getInt(BUNDLE_CURRENT_SORT_ORDER_KEY);
        return SortOrderType.convert(sortOrderValue);
    }
}
