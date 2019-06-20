package com.nanodegree.android.stevenson.popularmovies.ui.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nanodegree.android.stevenson.popularmovies.data.local.MoviesDao;
import com.nanodegree.android.stevenson.popularmovies.data.local.MoviesDatabase;
import com.nanodegree.android.stevenson.popularmovies.data.network.MoviesService;
import com.nanodegree.android.stevenson.popularmovies.data.network.ServiceFactory;
import com.nanodegree.android.stevenson.popularmovies.ui.detail.MovieDetailsActivity;
import com.nanodegree.android.stevenson.popularmovies.R;
import com.nanodegree.android.stevenson.popularmovies.common.Error;
import com.nanodegree.android.stevenson.popularmovies.common.SortOrderType;
import com.nanodegree.android.stevenson.popularmovies.common.SortOrderType.SortOrder;
import com.nanodegree.android.stevenson.popularmovies.data.MoviesRepository;
import com.nanodegree.android.stevenson.popularmovies.data.network.helpers.NetworkConnectionException;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.nanodegree.android.stevenson.popularmovies.viewmodel.MainViewModel;
import com.nanodegree.android.stevenson.popularmovies.viewmodel.MainViewModelFactory;

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
    private static final String BUNDLE_CURRENT_SORT_ORDER_KEY = "BUNDLE_CURRENT_SORT_ORDER_KEY";
    private static final String BUNDLE_RECYCLER_STATE = "BUNDLE_RECYCLER_STATE";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.movies_pb) ProgressBar mProgressBar;
    @BindView(R.id.error_iv) ImageView mErrorImg;
    @BindView(R.id.error_heading_tv) TextView mErrorHeading;
    @BindView(R.id.error_message_tv) TextView mErrorMessage;
    @BindView(R.id.error_btn) Button mErrorButton;
    @BindView(R.id.movies_rv) RecyclerView mMoviesGrid;

    private @SortOrder int mCurrentSortOrder;
    private MoviesRepository mMoviesRepository;
    private MainViewModel mViewModel;
    private Parcelable mRecyclerState;
    private GridLayoutManager mGridLayoutManager;
    private MoviesGridAdapter mMoviesGridAdapter;

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

        mGridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mMoviesGrid.setLayoutManager(mGridLayoutManager);
        mMoviesGrid.setHasFixedSize(true);
        mMoviesGridAdapter = new MoviesGridAdapter(MainActivity.this);
        mMoviesGrid.setAdapter(mMoviesGridAdapter);

        MoviesService moviesService = ServiceFactory.getService(MoviesService.class);
        MoviesDatabase database = MoviesDatabase.getInstance(getApplication());
        MoviesDao moviesDao = database.moviesDao();
        mMoviesRepository = MoviesRepository.getInstance(moviesService, moviesDao);

        MainViewModelFactory factory = new MainViewModelFactory(mMoviesRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);

        if (savedInstanceState != null) {
            mCurrentSortOrder = getCurrentSortOrderFromSavedInstanceState(savedInstanceState);
            mRecyclerState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_STATE);

        } else {
            mCurrentSortOrder = SortOrderType.POPULAR;
        }

        loadMovies();

        mViewModel.getMovies().observe(this, movies -> {
            if (movies == null) {
                Error error = getErrorType(mViewModel.isNetworkConnectionException());
                showError(error);
            } else {

                showMovies();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_CURRENT_SORT_ORDER_KEY, mCurrentSortOrder);
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

        mViewModel.loadMovies(mCurrentSortOrder);
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

    private @SortOrder int getCurrentSortOrderFromSavedInstanceState(Bundle savedInstanceState) {
        int sortOrderValue = savedInstanceState.getInt(BUNDLE_CURRENT_SORT_ORDER_KEY);
        return SortOrderType.convert(sortOrderValue);
    }

    private Error getErrorType(boolean isNetworkException) {
        return (isNetworkException) ? Error.NETWORK_CONNECTION : Error.DATA_RETRIEVAL;
    }
}
