package com.nanodegree.android.stevenson.popularmovies.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.nanodegree.android.stevenson.popularmovies.R;
import com.nanodegree.android.stevenson.popularmovies.data.MoviesRepository;
import com.nanodegree.android.stevenson.popularmovies.data.local.MoviesDao;
import com.nanodegree.android.stevenson.popularmovies.data.local.MoviesDatabase;
import com.nanodegree.android.stevenson.popularmovies.data.network.MoviesService;
import com.nanodegree.android.stevenson.popularmovies.data.network.ServiceFactory;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.nanodegree.android.stevenson.popularmovies.model.Review;
import com.nanodegree.android.stevenson.popularmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity
        implements TrailersAdapter.TrailerClickListener {

    public static final String EXTRA_MOVIE_KEY =
            "com.nanodegree.android.stevenson.popularmovies.EXTRA_MOVIE_KEY";

    private static final String TAG = "MovieDetailsActivity";
    private static final String INPUT_RELEASE_DATE_FORMAT = "yyyy-MM-dd";
    private static final String OUTPUT_RELEASE_DATE_FORMAT = "MMMM d, yyyy";

    @BindView(R.id.movie_poster_iv) ImageView mMoviePosterImg;
    @BindView(R.id.movie_backdrop_iv) ImageView mMovieBackdropImg;
    @BindView(R.id.movie_title_tv) TextView mMovieTitle;
    @BindView(R.id.release_date_tv) TextView mReleaseDate;
    @BindView(R.id.user_rating_tv) TextView mUserRating;
    @BindView(R.id.plot_synopsis_tv) TextView mPlotSynopsis;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.trailers_rv) RecyclerView mTrailersRecyclerView;
    @BindView(R.id.reviews_rv) RecyclerView mReviewsRecyclerView;

    private MoviesRepository mMoviesRepository;
    private List<Trailer> mTrailers;
    private List<Review> mReviews;

    public static Intent getStartIntent(Context context, Trailer clickedTrailer) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + clickedTrailer.getKey()));

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        MoviesService moviesService = ServiceFactory.getService(MoviesService.class);
        MoviesDatabase database = MoviesDatabase.getInstance(getApplication());
        MoviesDao moviesDao = database.moviesDao();
        mMoviesRepository = MoviesRepository.getInstance(moviesService, moviesDao);

        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(MovieDetailsActivity.this, RecyclerView.HORIZONTAL, false);
        mTrailersRecyclerView.setLayoutManager(trailersLayoutManager);

        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(MovieDetailsActivity.this, RecyclerView.VERTICAL, false);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        mReviewsRecyclerView.setNestedScrollingEnabled(false);


        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(EXTRA_MOVIE_KEY);

        displayMovieDetails(movie);
        displayTrailers(movie);
        displayReviews(movie);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedId = item.getItemId();

        if (selectedId == android.R.id.home) {
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTrailerClick(Trailer clickedTrailer) {
        Intent intent = getStartIntent(this, clickedTrailer);

        startActivity(intent);
    }

    private void displayMovieDetails(Movie movie) {
        Picasso.get()
                .load(movie.getPoster())
                .fit()
                .placeholder(R.drawable.movie_frame_placeholder)
                .error(R.drawable.data_retrieval_error)
                .into(mMoviePosterImg);

        Picasso.get()
                .load(movie.getBackdrop())
                .placeholder(R.drawable.movie_frame_placeholder)
                .error(R.drawable.data_retrieval_error)
                .into(mMovieBackdropImg);

        mMoviePosterImg.setContentDescription(getString(R.string.movie_poster_content_description, movie.getTitle()));
        mMovieBackdropImg.setContentDescription(getString(R.string.movie_backdrop_content_description, movie.getTitle()));
        mMovieTitle.setText(movie.getTitle());
        mReleaseDate.setText(formatReleaseDate(movie.getReleaseDate()));
        mUserRating.setText(movie.getUserRating());
        mPlotSynopsis.setText(movie.getSynopsis());
        mCollapsingToolbar.setTitle(movie.getTitle());
    }

    private void displayTrailers(Movie movie) {
        String id = movie.getId();
        mMoviesRepository.getMovieTrailers(id, getTrailersCallback());
    }

    private void displayReviews(Movie movie) {
        String id = movie.getId();
        mMoviesRepository.getMovieReviews(id, getReviewsCallback());
    }

    private Callback<List<Trailer>> getTrailersCallback() {
        return new Callback<List<Trailer>>() {
            @Override
            public void onResponse(Call<List<Trailer>> call, Response<List<Trailer>> response) {
                if (response.isSuccessful()) {
                    mTrailers = response.body();
                    TrailersAdapter trailersAdapter = new TrailersAdapter(mTrailers, MovieDetailsActivity.this);
                    mTrailersRecyclerView.setAdapter(trailersAdapter);
                } else {
                    Log.e(TAG, "onResponse: error retrieving movie trailers");
                    mTrailersRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Trailer>> call, Throwable t) {
                Log.e(TAG, "onResponse: error retrieving movie trailers", t);
                mTrailersRecyclerView.setVisibility(View.GONE);
            }
        };
    }

    private Callback<List<Review>> getReviewsCallback() {
        return new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful()) {
                    mReviews = response.body();
                    ReviewsAdapter reviewsAdapter = new ReviewsAdapter(mReviews);
                    mReviewsRecyclerView.setAdapter(reviewsAdapter);
                } else {
                    Log.e(TAG, "onResponse: error retrieving movie reviews");
                    mReviewsRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Log.e(TAG, "onResponse: error retrieving movie reviews", t);
                mReviewsRecyclerView.setVisibility(View.GONE);
            }
        };
    }

    private String formatReleaseDate(String releaseDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(INPUT_RELEASE_DATE_FORMAT, Locale.US);
            Date date = inputFormat.parse(releaseDate);

            SimpleDateFormat outputFormatter = new SimpleDateFormat(OUTPUT_RELEASE_DATE_FORMAT, Locale.US);
            return outputFormatter.format(date);
        } catch (ParseException pe) {
            Log.e(TAG, "formatReleaseDate: error formatting release date", pe);
            return releaseDate;
        }
    }
}
