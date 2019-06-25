package com.nanodegree.android.stevenson.popularmovies.common;

import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.nanodegree.android.stevenson.popularmovies.model.Trailer;

public class UrlUtility {

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w342";
    private static final String YOUTUBE_VIDEO_URL = "http://www.youtube.com/watch?v=";
    private static final String YOUTUBE_IMAGE_URL = "http://img.youtube.com/vi/%s/0.jpg";

    public static String getPosterUrl(Movie movie) {
        return IMAGE_BASE_URL + movie.getPoster();
    }

    public static String getBackdropUrl(Movie movie) {
        return BACKDROP_BASE_URL + movie.getBackdrop();
    }

    public  static String getYouTubeImageUrl(Trailer trailer) {
        return String.format(YOUTUBE_IMAGE_URL, trailer.getKey());
    }

    public static String getYouTubeVideoUrl(Trailer trailer) {
        return YOUTUBE_VIDEO_URL + trailer.getKey();
    }
}
