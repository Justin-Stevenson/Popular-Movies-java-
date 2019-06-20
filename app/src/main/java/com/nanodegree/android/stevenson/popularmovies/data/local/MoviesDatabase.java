package com.nanodegree.android.stevenson.popularmovies.data.local;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nanodegree.android.stevenson.popularmovies.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase {

    private static final String TAG = "MoviesDatabase";
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "FavoriteMovies.db";
    private static MoviesDatabase sInstance;

    public static MoviesDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "getInstance: creating new database instance");
                sInstance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        MoviesDatabase.class,
                        MoviesDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG, "getInstance: getting the database instance");
        return sInstance;
    }

    public abstract MoviesDao moviesDao();
}
