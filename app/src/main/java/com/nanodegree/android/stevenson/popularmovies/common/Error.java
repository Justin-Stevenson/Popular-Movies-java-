package com.nanodegree.android.stevenson.popularmovies.common;

import com.nanodegree.android.stevenson.popularmovies.R;

public class Error {

    public final static Error NETWORK_CONNECTION = new Error(
            R.drawable.network_connection_error,
            R.string.no_network_connection_img_description,
            R.string.no_network_connection_header,
            R.string.no_network_connection_message);

    public final static Error DATA_RETRIEVAL = new Error(
            R.drawable.data_retrieval_error,
            R.string.data_retrieval_img_description,
            R.string.data_retrieval_header,
            R.string.data_retrieval_message);

    public final static Error NO_FAVORITES = new Error(
            R.drawable.ic_favorite_border,
            R.string.no_favorites_img_description,
            R.string.no_favorites_header,
            R.string.no_favorites_message);

    private final int image;
    private final int imageDescription;
    private final int header;
    private final int message;

    private Error(int image, int imageDescription, int header, int message) {
        this.image = image;
        this.imageDescription = imageDescription;
        this.header = header;
        this.message = message;
    }

    public int getImage() {
        return image;
    }

    public int getImageDescription() {
        return imageDescription;
    }

    public int getHeader() {
        return header;
    }

    public int getMessage() {
        return message;
    }
}
