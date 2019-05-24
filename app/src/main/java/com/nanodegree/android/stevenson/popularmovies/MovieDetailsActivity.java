package com.nanodegree.android.stevenson.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.nanodegree.android.stevenson.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String MOVIE_KEY = "movie";

    private static final String TAG = "MovieDetailsActivity";
    private static final String INPUT_RELEASE_DATE_FORMAT = "yyyy-MM-dd";
    private static final String OUTPUT_RELEASE_DATE_FORMAT = "MMMM d, yyyy";

    private ImageView mMoviePosterImg;
    private TextView mMovieTitle;
    private TextView mReleaseDate;
    private TextView mUserRating;
    private TextView mPlotSynopsis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mMoviePosterImg = findViewById(R.id.movie_poster_iv);
        mMovieTitle = findViewById(R.id.movie_title_tv);
        mReleaseDate = findViewById(R.id.release_date_tv);
        mUserRating = findViewById(R.id.user_rating_tv);
        mPlotSynopsis = findViewById(R.id.plot_synopsis_tv);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(MOVIE_KEY);

        displayMovieDetails(movie);
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

    private void displayMovieDetails(Movie movie) {
        Picasso.get()
                .load(movie.getPoster())
                .fit()
                .placeholder(R.drawable.movie_frame_placeholder)
                .error(R.drawable.data_retrieval_error)
                .into(mMoviePosterImg);

        mMoviePosterImg.setContentDescription(getString(R.string.movie_poster_content_description, movie.getTitle()));
        mMovieTitle.setText(movie.getTitle());
        mReleaseDate.setText(formatReleaseDate(movie.getReleaseDate()));
        mUserRating.setText(getString(R.string.user_rating_text, movie.getUserRating()));
        mPlotSynopsis.setText(movie.getSynopsis());
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
