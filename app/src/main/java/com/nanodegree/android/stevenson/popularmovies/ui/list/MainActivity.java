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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nanodegree.android.stevenson.popularmovies.R;
import com.nanodegree.android.stevenson.popularmovies.common.Error;
import com.nanodegree.android.stevenson.popularmovies.common.SortOrder;
import com.nanodegree.android.stevenson.popularmovies.data.MoviesRepository;
import com.nanodegree.android.stevenson.popularmovies.data.network.helpers.NetworkConnectionException;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.nanodegree.android.stevenson.popularmovies.ui.detail.MovieDetailsActivity;
import com.nanodegree.android.stevenson.popularmovies.viewmodel.MainViewModel;
import com.nanodegree.android.stevenson.popularmovies.viewmodel.MainViewModelFactory;

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
    private List<Movie> mMovies;
    private MoviesRepository mMoviesRepository;
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

        mMoviesRepository = MoviesRepository.getInstance(getApplication());

        setupViewModel();

        if (savedInstanceState != null) {
            mCurrentSortOrder =
                    (SortOrder) savedInstanceState.getSerializable(BUNDLE_CURRENT_SORT_ORDER_KEY);
            mRecyclerState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_STATE);

        } else {
            mCurrentSortOrder = restoreSortOrderFromSharedPreferences();
        }

        if (SortOrder.FAVORITES == mCurrentSortOrder) {
            setupFavoritesObserver();
        } else {
            loadMovies();
        }
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
        editor.commit();
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
                    removeFavoritesObserver();
                }
                return true;

            case R.id.action_top_rated:
                if (SortOrder.TOP_RATED != mCurrentSortOrder) {
                    mCurrentSortOrder = SortOrder.TOP_RATED;
                    loadMovies();
                    removeFavoritesObserver();
                }
                return true;

            case R.id.action_favorites:
                if (SortOrder.FAVORITES != mCurrentSortOrder) {
                    mCurrentSortOrder = SortOrder.FAVORITES;
                    setupFavoritesObserver();
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
        showProgressBar();

        mMoviesRepository.getMovies(mCurrentSortOrder, getMoviesCallback());
    }

    private void loadMovies(List<Movie> movies) {
        mMovies = movies;
        MoviesGridAdapter moviesGridAdapter = new MoviesGridAdapter(mMovies, MainActivity.this);
        mMoviesGrid.setAdapter(moviesGridAdapter);
        showMovies();
    }

    private Callback<List<Movie>> getMoviesCallback() {
        return new Callback<List<Movie>>() {
            @Override
            public void onResponse(@NonNull Call<List<Movie>> call, @NonNull Response<List<Movie>> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: retrieved movies successfully for " + mCurrentSortOrder);
                    loadMovies(response.body());
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

    private void setupMoviesRecyclerView() {
        mGridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mMoviesGrid.setLayoutManager(mGridLayoutManager);
        mMoviesGrid.setHasFixedSize(true);
    }

    private void setupViewModel() {
        MainViewModelFactory factory = new MainViewModelFactory(mMoviesRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
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

    private void setupFavoritesObserver() {
        mViewModel.getFavoriteMovies().observe(this, movies -> {
            if (mMovies != null && mMovies.isEmpty()) {
                mMovies.clear();
            }

            loadMovies(movies);

            if (movies == null || movies.isEmpty()) {
                Toast.makeText(
                        MainActivity.this,
                        R.string.toast_no_favorites,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void removeFavoritesObserver() {
        mViewModel.getFavoriteMovies().removeObservers(this);
    }
}
