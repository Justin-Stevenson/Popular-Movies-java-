package com.nanodegree.android.stevenson.popularmovies.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nanodegree.android.stevenson.popularmovies.data.MoviesRepository;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.nanodegree.android.stevenson.popularmovies.model.Resource;
import com.nanodegree.android.stevenson.popularmovies.model.Review;
import com.nanodegree.android.stevenson.popularmovies.model.Trailer;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MovieDetailsViewModel extends AndroidViewModel {

    private final MoviesRepository mRepository;
    private final LiveData<Movie> mMovie;
    private final MutableLiveData<Resource<List<Trailer>>> mTrailers = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Review>>> mReviews = new MutableLiveData<>();
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public MovieDetailsViewModel(@NonNull Application application, String movieId) {
        super(application);

        mRepository = MoviesRepository.getInstance(application);
        mMovie = mRepository.getFavoriteMovieById(movieId);
        requestMovieTrailers(movieId);
        requestMovieReviews(movieId);
    }

    @Override
    protected void onCleared() {
        mCompositeDisposable.clear();
    }

    public LiveData<Movie> getMovie() {
        return mMovie;
    }

    public LiveData<Resource<List<Trailer>>> getTrailers() {
        return mTrailers;
    }

    public LiveData<Resource<List<Review>>> getReviews() {
        return mReviews;
    }

    public void addFavoriteMovie(Movie movie) {
        mCompositeDisposable.add(
                Completable.fromAction(() -> mRepository.addFavoriteMovie(movie))
                .subscribeOn(Schedulers.io())
                .subscribe()
        );
    }

    public void removeFavoriteMovie(Movie movie) {
        mCompositeDisposable.add(
                Completable.fromAction(() -> mRepository.removeFavoriteMovie(movie))
                        .subscribeOn(Schedulers.io())
                        .subscribe()
        );
    }

    private void requestMovieTrailers(String movieId) {
        Single<List<Trailer>> single = mRepository.getMovieTrailers(movieId);

        mCompositeDisposable.add(
                single.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> mTrailers.setValue(Resource.loading()))
                        .subscribe(
                                response -> mTrailers.setValue(Resource.success(response)),
                                error -> mTrailers.setValue(Resource.error(error))
                        )
        );
    }

    private void requestMovieReviews(String movieId) {
        Single<List<Review>> single = mRepository.getMovieReviews(movieId);

        mCompositeDisposable.add(
                single.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> mReviews.setValue(Resource.loading()))
                        .subscribe(
                                response -> mReviews.setValue(Resource.success(response)),
                                error -> mReviews.setValue(Resource.error(error))
                        )
        );
    }
}
