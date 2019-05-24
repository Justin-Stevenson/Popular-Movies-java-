package com.nanodegree.android.stevenson.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    private final String title;

    @SerializedName("poster_path")
    private final String poster;

    @SerializedName("overview")
    private final String synopsis;

    @SerializedName("vote_average")
    private final String userRating;

    @SerializedName("release_date")
    private final String releaseDate;

    private Movie(Parcel in) {
        title = in.readString();
        poster = in.readString();
        synopsis = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return POSTER_BASE_URL + poster;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(synopsis);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
    }
}
