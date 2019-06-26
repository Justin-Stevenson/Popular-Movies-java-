/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nanodegree.android.stevenson.popularmovies.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Adapted from:
 * https://github.com/googlesamples/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/vo/Resource.kt
 */
public class Resource<T> {

    @NonNull
    private final Status mStatus;

    @Nullable
    private final Throwable mError;

    @Nullable
    private final T mData;

    public Resource(@NonNull Status status, @Nullable T data, @Nullable Throwable error) {
        this.mStatus = status;
        this.mData = data;
        this.mError = error;
    }

    public static <T> Resource<T> error(Throwable error) {
        return new Resource<>(Status.ERROR, null, error);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    @NonNull
    public Status getStatus() {
        return mStatus;
    }

    @Nullable
    public Throwable getError() {
        return mError;
    }

    @Nullable
    public T getData() {
        return mData;
    }
}
