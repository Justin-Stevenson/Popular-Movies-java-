package com.nanodegree.android.stevenson.popularmovies.ui.list;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nanodegree.android.stevenson.popularmovies.R;
import com.nanodegree.android.stevenson.popularmovies.common.Error;
import com.nanodegree.android.stevenson.popularmovies.common.NoFavoriteMoviesExistException;
import com.nanodegree.android.stevenson.popularmovies.common.SortOrder;
import com.nanodegree.android.stevenson.popularmovies.data.network.helpers.NetworkConnectionException;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.nanodegree.android.stevenson.popularmovies.model.Status;
import com.nanodegree.android.stevenson.popularmovies.ui.detail.MovieDetailsActivity;
import com.nanodegree.android.stevenson.popularmovies.viewmodel.MainViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements MoviesGridAdapter.MovieClickListener {

    private static final String TAG = "MainActivity";
    private static final String BUNDLE_CURRENT_SORT_ORDER_KEY = "BUNDLE_CURRENT_SORT_ORDER_KEY";
    private static final String BUNDLE_RECYCLER_STATE = "BUNDLE_RECYCLER_STATE";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.movies_pb) ProgressBar mProgressBar;
    @BindView(R.id.error_iv) ImageView mErrorImg;
    @BindView(R.id.error_heading_tv) TextView mErrorHeading;
    @BindView(R.id.error_message_tv) TextView mErrorMessage;
    @BindView(R.id.error_btn) Button mErrorButton;
    @BindView(R.id.movies_rv) RecyclerView mMoviesGrid;

    private SortOrder mCurrentSortOrder;
    private MainViewModel mViewModel;
    private Parcelable mRecyclerState;
    private GridLayoutManager mGridLayoutManager;

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

        setSupportActionBar(mToolbar);

        setupMoviesRecyclerView();

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        if (savedInstanceState != null) {
            mCurrentSortOrder =
                    (SortOrder) savedInstanceState.getSerializable(BUNDLE_CURRENT_SORT_ORDER_KEY);
            mRecyclerState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_STATE);

        } else {
            mCurrentSortOrder = restoreSortOrderFromSharedPreferences();
        }

        loadMovies();

        mViewModel.getMovies().observe(this, result -> {
            Status status = result.getStatus();
            switch (status) {
                case ERROR:
                    handleError(result.getError());
                    break;

                case LOADING:
                    showProgressBar();
                    break;

                case SUCCESS:
                    handleSuccessfulResponse(result.getData());
                    break;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mRecyclerState != null) {
            mGridLayoutManager.onRestoreInstanceState(mRecyclerState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_order), mCurrentSortOrder.name());
        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_CURRENT_SORT_ORDER_KEY, mCurrentSortOrder);
        outState.putParcelable(BUNDLE_RECYCLER_STATE,
                mMoviesGrid.getLayoutManager().onSaveInstanceState());
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

            case R.id.action_favorites:
                if (SortOrder.FAVORITES != mCurrentSortOrder) {
                    mCurrentSortOrder = SortOrder.FAVORITES;
                    loadMovies();
                }
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
        mViewModel.retrieveMovies(mCurrentSortOrder);
    }

    private void loadMovies(List<Movie> movies) {
        MoviesGridAdapter moviesGridAdapter = new MoviesGridAdapter(movies, MainActivity.this);
        mMoviesGrid.setAdapter(moviesGridAdapter);
        showMovies();
    }

    private void handleError(Throwable throwable) {

        if (throwable instanceof NetworkConnectionException) {
            Log.e(TAG, "onFailure: error retrieving movie data due to no network connection", throwable);
            showError(Error.NETWORK_CONNECTION);
        } else if(throwable instanceof NoFavoriteMoviesExistException) {
            Log.e(TAG, "onFailure: no favorite movies exist in database", throwable);
            showError(Error.NO_FAVORITES);
        } else {
            Log.e(TAG, "onFailure: error retrieving movie data", throwable);
            showError(Error.DATA_RETRIEVAL);
        }
    }

    private void handleSuccessfulResponse(List<Movie> movies) {
        Log.d(TAG, "onResponse: retrieved movies successfully for " + mCurrentSortOrder);
        loadMovies(movies);
    }

    private void setupMoviesRecyclerView() {
        mGridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mMoviesGrid.setLayoutManager(mGridLayoutManager);
        mMoviesGrid.setHasFixedSize(true);
    }

    private SortOrder restoreSortOrderFromSharedPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = sharedPref.getString(getString(R.string.pref_sort_order), SortOrder.POPULAR.name());

        return SortOrder.valueOf(sortOrder);
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

        if (Error.NO_FAVORITES != errorType) {
            mErrorButton.setVisibility(View.VISIBLE);
        }

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
}
