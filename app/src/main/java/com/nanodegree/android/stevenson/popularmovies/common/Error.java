package com.nanodegree.android.stevenson.popularmovies.common;

import com.nanodegree.android.stevenson.popularmovies.R;

public enum Error {

    NETWORK_CONNECTION(R.drawable.network_connection_error, R.string.no_network_connection_img_description,
            R.string.no_network_connection_header, R.string.no_network_connection_message),

    DATA_RETRIEVAL(R.drawable.data_retrieval_error, R.string.data_retrieval_img_description,
            R.string.data_retrieval_header, R.string.data_retrieval_message);

    private final int image;
    private final int imageDescription;
    private final int header;
    private final int message;

    Error(int image, int imageDescription, int header, int message) {
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
