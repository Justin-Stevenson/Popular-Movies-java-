package com.nanodegree.android.stevenson.popularmovies.common;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SortOrderType {

    public static final int POPULAR = 0;
    public static final int TOP_RATED = 1;

    @IntDef({POPULAR, TOP_RATED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SortOrder {}

    public static @SortOrder int convert(int value) {
        switch (value) {
            case 0:
                return POPULAR;

            case 1:
                return TOP_RATED;

            default:
                return POPULAR;
        }
    }
}
