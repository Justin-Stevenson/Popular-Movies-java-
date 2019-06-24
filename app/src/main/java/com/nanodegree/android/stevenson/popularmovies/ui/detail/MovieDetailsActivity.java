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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nanodegree.android.stevenson.popularmovies.R;
import com.nanodegree.android.stevenson.popularmovies.common.AppExecutors;
import com.nanodegree.android.stevenson.popularmovies.common.UrlUtility;
import com.nanodegree.android.stevenson.popularmovies.data.MoviesRepository;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.nanodegree.android.stevenson.popularmovies.model.Review;
import com.nanodegree.android.stevenson.popularmovies.model.Trailer;
import com.nanodegree.android.stevenson.popularmovies.viewmodel.MovieDetailsViewModel;
import com.nanodegree.android.stevenson.popularmovies.viewmodel.MovieDetailsViewModelFactory;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.favorite_fab) FloatingActionButton mFavoriteFab;

    private MoviesRepository mMoviesRepository;
    private List<Trailer> mTrailers;
    private List<Review> mReviews;
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

        mMoviesRepository = MoviesRepository.getInstance(getApplication());

        setupTrailers();
        setupReviews();


        Intent intent = getIntent();
        mMovie = intent.getParcelableExtra(EXTRA_MOVIE_KEY);

        MovieDetailsViewModelFactory factory =
                new MovieDetailsViewModelFactory(mMoviesRepository, mMovie.getId());

        mViewModel = ViewModelProviders.of(this, factory).get(MovieDetailsViewModel.class);

        displayMovieDetails(mMovie);
        displayTrailers(mMovie);
        displayReviews(mMovie);
        initializeFavoriteButton();
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

        updateFavoriteButton(mIsFavorite);

        if (mIsFavorite) {
            AppExecutors.getInstance()
                    .diskIO()
                    .execute(() -> mMoviesRepository.addFavoriteMovie(mMovie));
        } else {
            AppExecutors.getInstance()
                    .diskIO()
                    .execute(() -> mMoviesRepository.removeFavoriteMovie(mMovie));
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
                    TrailersAdapter trailersAdapter =
                            new TrailersAdapter(mTrailers, MovieDetailsActivity.this);
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
            SimpleDateFormat inputFormat =
                    new SimpleDateFormat(INPUT_RELEASE_DATE_FORMAT, Locale.US);
            Date date = inputFormat.parse(releaseDate);

            SimpleDateFormat outputFormatter =
                    new SimpleDateFormat(OUTPUT_RELEASE_DATE_FORMAT, Locale.US);
            return outputFormatter.format(date);
        } catch (ParseException pe) {
            Log.e(TAG, "formatReleaseDate: error formatting release date", pe);
            return releaseDate;
        }
    }

    private void updateFavoriteButton(boolean isFavorite) {
        int iconId = (isFavorite) ? R.drawable.ic_favorite : R.drawable.ic_favorite_border;

        mFavoriteFab.setImageDrawable(ContextCompat.getDrawable(this, iconId));
    }

    private void initializeFavoriteButton() {
        mViewModel.getMovie().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(Movie movie) {
                mIsFavorite = movie != null;

                updateFavoriteButton(mIsFavorite);

                mViewModel.getMovie().removeObserver(this);
            }
        });
    }

    private void shareYoutubeLink() {
        if (mTrailers == null || mTrailers.isEmpty()) {
            Toast.makeText(
                    MovieDetailsActivity.this,
                    R.string.toast_no_trailers,
                    Toast.LENGTH_LONG
            ).show();
        } else {

            Trailer trailer = mTrailers.get(0);
            String trailerUrl = UrlUtility.getYouTubeVideoUrl(trailer);
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
}
