package com.nanodegree.android.stevenson.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Movie implements Parcelable {

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private static final String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w342/";

    @PrimaryKey
    @NonNull
    private String id;

    private String title;

    @SerializedName("poster_path")
    private String poster;

    @SerializedName("backdrop_path")
    private String backdrop;

    @SerializedName("overview")
    private String synopsis;

    @SerializedName("vote_average")
    private String userRating;

    @SerializedName("release_date")
    private String releaseDate;

    public Movie() {}

    private Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        poster = in.readString();
        backdrop = in.readString();
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

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return IMAGE_BASE_URL + poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getBackdrop() {
        return BACKDROP_BASE_URL + backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(backdrop);
        dest.writeString(synopsis);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
    }
}
