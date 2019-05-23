package com.nanodegree.android.stevenson.popularmovies.data.network.helpers;

import java.io.IOException;

public class NetworkConnectionException extends IOException {

    public NetworkConnectionException() {
        super("No network connection available");
    }
}
