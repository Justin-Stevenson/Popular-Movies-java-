package com.nanodegree.android.stevenson.popularmovies.ui.detail;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nanodegree.android.stevenson.popularmovies.R;
import com.nanodegree.android.stevenson.popularmovies.common.UrlUtility;
import com.nanodegree.android.stevenson.popularmovies.common.Utility;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.nanodegree.android.stevenson.popularmovies.model.Review;
import com.nanodegree.android.stevenson.popularmovies.model.Status;
import com.nanodegree.android.stevenson.popularmovies.model.Trailer;
import com.nanodegree.android.stevenson.popularmovies.viewmodel.MovieDetailsViewModel;
import com.nanodegree.android.stevenson.popularmovies.viewmodel.MovieDetailsViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailsActivity extends AppCompatActivity
        implements TrailersAdapter.TrailerClickListener {

    public static final String EXTRA_MOVIE_KEY =
            "com.nanodegree.android.stevenson.popularmovies.EXTRA_MOVIE_KEY";

    private static final String TAG = "MovieDetailsActivity";

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
    @BindView(R.id.trailers_loading_pb) ProgressBar mTrailersLoading;
    @BindView(R.id.reviews_loading_pb) ProgressBar mReviewsLoading;
    @BindView(R.id.trailers_message_tv) TextView mTrailersMessage;
    @BindView(R.id.reviews_message_tv) TextView mReviewsMessage;
    @BindView(R.id.favorite_fab) FloatingActionButton mFavoriteFab;

    private Trailer mFirstTrailer;
    private Movie mMovie;
    private boolean mIsFavorite;
    private MovieDetailsViewModel mViewModel;

    public static Intent getStartIntent(Trailer clickedTrailer) {
        return new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(UrlUtility.getYouTubeVideoUrl(clickedTrailer)));
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

        setupTrailers();
        setupReviews();


        Intent intent = getIntent();
        mMovie = intent.getParcelableExtra(EXTRA_MOVIE_KEY);

        MovieDetailsViewModelFactory factory =
                new MovieDetailsViewModelFactory(getApplication(), mMovie.getId());

        mViewModel = ViewModelProviders.of(this, factory).get(MovieDetailsViewModel.class);

        displayMovieDetails(mMovie);
        initializeFavoriteButton();

        mViewModel.getTrailers().observe(this, result -> {
            Status status = result.getStatus();

            switch (status) {
                case ERROR:
                    Log.e(TAG, "onResponse: error retrieving trailers", result.getError());
                    showTrailersError();
                    break;

                case LOADING:
                    showTrailersLoading();
                    break;

                case SUCCESS:
                    handleTrailers(result.getData());
                    break;
            }
        });

        mViewModel.getReviews().observe(this, result -> {
            Status status = result.getStatus();

            switch (status) {
                case ERROR:
                    Log.e(TAG, "onResponse: error retrieving reviews", result.getError());
                    showReviewsError();
                    break;

                case LOADING:
                    showReviewsLoading();
                    break;

                case SUCCESS:
                    handleReviews(result.getData());
                    break;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_details_options, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedId = item.getItemId();

        switch (selectedId) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_share_movie:
                shareYoutubeLink();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTrailerClick(Trailer clickedTrailer) {
        Intent intent = getStartIntent(clickedTrailer);

        startActivity(intent);
    }

    @OnClick(R.id.favorite_fab)
    public void onFavoriteClick() {
        mIsFavorite = !mIsFavorite;

        if (mIsFavorite) {
            mViewModel.addFavoriteMovie(mMovie);
        } else {
            mViewModel.removeFavoriteMovie(mMovie);
        }
    }

    private void displayMovieDetails(Movie movie) {
        final String posterUrl = UrlUtility.getPosterUrl(movie);
        final String backdropUrl = UrlUtility.getBackdropUrl(movie);

        Picasso.get()
                .load(posterUrl)
                .fit()
                .placeholder(R.drawable.movie_frame_placeholder)
                .error(R.drawable.data_retrieval_error)
                .into(mMoviePosterImg);

        Picasso.get()
                .load(backdropUrl)
                .placeholder(R.drawable.movie_frame_placeholder)
                .error(R.drawable.data_retrieval_error)
                .into(mMovieBackdropImg);

        mMoviePosterImg.setContentDescription(getString(R.string.movie_poster_content_description, movie.getTitle()));
        mMovieBackdropImg.setContentDescription(getString(R.string.movie_backdrop_content_description, movie.getTitle()));
        mMovieTitle.setText(movie.getTitle());
        mReleaseDate.setText(Utility.formatReleaseDate(movie.getReleaseDate()));
        mUserRating.setText(getString(R.string.user_rating_text, movie.getUserRating()));
        mPlotSynopsis.setText(movie.getSynopsis());
        mCollapsingToolbar.setTitle(movie.getTitle());
    }

    private void updateFavoriteButton(boolean isFavorite) {
        int iconId = (isFavorite) ? R.drawable.ic_favorite : R.drawable.ic_favorite_border;

        mFavoriteFab.setImageDrawable(ContextCompat.getDrawable(this, iconId));
    }

    private void initializeFavoriteButton() {
        mViewModel.getMovie().observe(this, movie -> {
            mIsFavorite = movie != null;

            updateFavoriteButton(mIsFavorite);
        });
    }

    private void shareYoutubeLink() {
        if (mFirstTrailer == null) {
            Toast.makeText(
                    MovieDetailsActivity.this,
                    R.string.toast_no_trailers,
                    Toast.LENGTH_LONG
            ).show();
        } else {

            String trailerUrl = UrlUtility.getYouTubeVideoUrl(mFirstTrailer);
            String message = getString(R.string.share_youtube_message, mMovie.getTitle(), trailerUrl);
            ShareCompat.IntentBuilder intentBuilder =
                    ShareCompat.IntentBuilder.from(MovieDetailsActivity.this)
                            .setText(message)
                            .setType("text/plain");

            try {
                intentBuilder.startChooser();
            } catch (ActivityNotFoundException e) {
                Toast.makeText(
                        MovieDetailsActivity.this,
                        R.string.toast_no_share_app,
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    private void setupTrailers() {
        LinearLayoutManager trailersLayoutManager = getLinearLayoutManager(RecyclerView.HORIZONTAL);
        mTrailersRecyclerView.setLayoutManager(trailersLayoutManager);
    }

    private void setupReviews() {
        LinearLayoutManager reviewsLayoutManager = getLinearLayoutManager(RecyclerView.VERTICAL);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        mReviewsRecyclerView.setNestedScrollingEnabled(false);
    }

    private LinearLayoutManager getLinearLayoutManager(int orientation) {
        return new LinearLayoutManager(this, orientation, false);
    }

    private void showTrailersError() {
        mTrailersMessage.setText(getString(R.string.trailers_error_message));
        mTrailersMessage.setVisibility(View.VISIBLE);
        mTrailersRecyclerView.setVisibility(View.GONE);
        mTrailersLoading.setVisibility(View.GONE);
    }

    private void showTrailersLoading() {
        mTrailersLoading.setVisibility(View.VISIBLE);
        mTrailersMessage.setVisibility(View.GONE);
        mTrailersRecyclerView.setVisibility(View.GONE);
    }

    private void handleTrailers(List<Trailer> trailers) {
        if (trailers != null && !trailers.isEmpty()) {
            showTrailers(trailers);
            mFirstTrailer = trailers.get(0);
        } else {
            showTrailersEmpty();
        }
    }

    private void showTrailersEmpty() {
        mTrailersMessage.setText(getString(R.string.trailers_empty_message));
        mTrailersMessage.setVisibility(View.VISIBLE);
        mTrailersRecyclerView.setVisibility(View.GONE);
        mTrailersLoading.setVisibility(View.GONE);
    }

    private void showTrailers(List<Trailer> trailers) {
        TrailersAdapter trailersAdapter =
                new TrailersAdapter(trailers, MovieDetailsActivity.this);
        mTrailersRecyclerView.setAdapter(trailersAdapter);

        mTrailersRecyclerView.setVisibility(View.VISIBLE);
        mTrailersLoading.setVisibility(View.GONE);
        mTrailersMessage.setVisibility(View.GONE);
    }

    private void showReviewsError() {
        mReviewsMessage.setText(getString(R.string.reviews_error_message));
        mReviewsMessage.setVisibility(View.VISIBLE);
        mReviewsRecyclerView.setVisibility(View.GONE);
        mReviewsLoading.setVisibility(View.GONE);
    }

    private void showReviewsLoading() {
        mReviewsLoading.setVisibility(View.VISIBLE);
        mReviewsMessage.setVisibility(View.GONE);
        mReviewsRecyclerView.setVisibility(View.GONE);
    }

    private void handleReviews(List<Review> reviews) {
        if (reviews != null && !reviews.isEmpty()) {
            showReviews(reviews);
        } else {
            showReviewsEmpty();
        }
    }

    private void showReviewsEmpty() {
        mReviewsMessage.setText(getString(R.string.reviews_empty_message));
        mReviewsMessage.setVisibility(View.VISIBLE);
        mReviewsRecyclerView.setVisibility(View.GONE);
        mReviewsLoading.setVisibility(View.GONE);
    }

    private void showReviews(List<Review> reviews) {
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviews);
        mReviewsRecyclerView.setAdapter(reviewsAdapter);

        mReviewsRecyclerView.setVisibility(View.VISIBLE);
        mReviewsLoading.setVisibility(View.GONE);
        mReviewsMessage.setVisibility(View.GONE);
    }
}
