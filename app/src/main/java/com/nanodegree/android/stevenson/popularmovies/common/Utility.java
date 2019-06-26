package com.nanodegree.android.stevenson.popularmovies.common;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

    private static final String TAG = "Utility";
    private static final String INPUT_RELEASE_DATE_FORMAT = "yyyy-MM-dd";
    private static final String OUTPUT_RELEASE_DATE_FORMAT = "MMMM d, yyyy";

    public static String formatReleaseDate(String releaseDate) {
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
}
