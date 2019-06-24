package com.nanodegree.android.stevenson.popularmovies.ui.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nanodegree.android.stevenson.popularmovies.R;
import com.nanodegree.android.stevenson.popularmovies.common.UrlUtility;
import com.nanodegree.android.stevenson.popularmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private final List<Trailer> mTrailers;
    private final TrailerClickListener mTrailerClickListener;

    public TrailersAdapter(List<Trailer> trailers, TrailerClickListener listener) {
        this.mTrailers = trailers;
        this.mTrailerClickListener = listener;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trailer, parent, false);

        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return (mTrailers != null) ? mTrailers.size() : 0;
    }


    class TrailerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.trailer_iv) ImageView mTrailerImg;

        TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int positionIndex) {
            Trailer trailer = mTrailers.get(positionIndex);

            Picasso.get()
                    .load(UrlUtility.getYouTubeImageUrl(trailer))
                    .fit()
                    .placeholder(R.drawable.movie_frame_placeholder)
                    .error(R.drawable.data_retrieval_error)
                    .into(mTrailerImg);

            mTrailerImg.setContentDescription(trailer.getName());
        }

        @OnClick
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Trailer trailer = mTrailers.get(clickedPosition);
            mTrailerClickListener.onTrailerClick(trailer);
        }
    }

    public interface TrailerClickListener {
        void onTrailerClick(Trailer clickedTrailer);
    }
}
