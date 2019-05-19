package com.nanodegree.android.stevenson.popularmovies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nanodegree.android.stevenson.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesGridAdapter extends RecyclerView.Adapter<MoviesGridAdapter.MovieViewHolder> {

    private List<Movie> movies;

    public MoviesGridAdapter(List<Movie> movies) {
        this.movies = movies;
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
        return (movies != null) ? movies.size() : 0;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView moviePosterImg;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePosterImg = (ImageView) itemView.findViewById(R.id.movie_poster_iv);
        }

        void bind(int positionIndex) {
            Movie movie = movies.get(positionIndex);
            Picasso.get()
                    .load(movie.getPoster())
                    .fit()
                    .placeholder(R.drawable.movie_frame_placeholder)
                    .error(R.drawable.data_retrieval_error)
                    .into(moviePosterImg);
        }
    }
}
