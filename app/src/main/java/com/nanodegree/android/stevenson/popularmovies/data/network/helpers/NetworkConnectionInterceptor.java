package com.nanodegree.android.stevenson.popularmovies.data.network.helpers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkConnectionInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!hasNetworkConnection()) {
            throw new NetworkConnectionException();
        }

        Request request = chain.request();

        Request.Builder requestBuilder = request.newBuilder();

        Request newRequest = requestBuilder.build();

        return chain.proceed(newRequest);
    }

    /**
     * Checks if device is connected to internet
     * Citation: http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts&sa=D&ust=1558068670318000
     *
     * @return false if not connected to internet
     */
    private boolean hasNetworkConnection() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
