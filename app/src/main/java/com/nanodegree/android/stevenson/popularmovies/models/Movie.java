package com.nanodegree.android.stevenson.popularmovies.models;

import com.google.gson.annotations.SerializedName;

public class Movie {

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    private String title;

    @SerializedName("poster_path")
    private String poster;

    @SerializedName("overview")
    private String synopsis;

    @SerializedName("vote_average")
    private String userRating;

    @SerializedName("release_date")
    private String releaseDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return POSTER_BASE_URL + poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
