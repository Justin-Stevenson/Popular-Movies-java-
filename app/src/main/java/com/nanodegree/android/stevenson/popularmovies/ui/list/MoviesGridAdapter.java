package com.nanodegree.android.stevenson.popularmovies.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nanodegree.android.stevenson.popularmovies.R;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoviesGridAdapter extends RecyclerView.Adapter<MoviesGridAdapter.MovieViewHolder> {

    private final List<Movie> mMovies;
    private final MovieClickListener mMovieClickListener;

    public MoviesGridAdapter(List<Movie> movies, MovieClickListener listener) {
        this.mMovies = movies;
        this.mMovieClickListener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.movie_grid_item, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return (mMovies != null) ? mMovies.size() : 0;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.movie_poster_iv) ImageView mMoviePosterImg;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int positionIndex) {
            Movie movie = mMovies.get(positionIndex);
            Picasso.get()
                    .load(movie.getPoster())
                    .fit()
                    .placeholder(R.drawable.movie_frame_placeholder)
                    .error(R.drawable.data_retrieval_error)
                    .into(mMoviePosterImg);

            mMoviePosterImg.setContentDescription(movie.getTitle());
        }

        @OnClick
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Movie movie = mMovies.get(clickedPosition);
            mMovieClickListener.onMovieClick(movie);
        }
    }

    public interface MovieClickListener {
        void onMovieClick(Movie clickedMovie);
    }
}
