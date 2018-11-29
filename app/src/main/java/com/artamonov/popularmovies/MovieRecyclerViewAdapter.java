package com.artamonov.popularmovies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.artamonov.popularmovies.MainActivity.TAG;
import static com.artamonov.popularmovies.MainActivity.isChoseFavorites;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {
    private static ItemClickListener listener;
    private final List<PopularMovies> popularMoviesList;

    MovieRecyclerViewAdapter(List<PopularMovies> popularMoviesList, ItemClickListener itemClickListener) {
        listener = itemClickListener;
        this.popularMoviesList = popularMoviesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_items_movies, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        PopularMovies popularMovies = popularMoviesList.get(position);
        if (!isChoseFavorites()) {
            if (!TextUtils.isEmpty(popularMovies.getPosterPath())) {
                Picasso.get()
                        .load(popularMovies.getPosterPath())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder_error)
                        .into(viewHolder.ivPoster);
            } else {
                Log.i(TAG, " onBindViewHolder: popularMovies.getPoster() is Empty) " + popularMovies.getPosterPath());
            }
        } else {
            if (popularMovies.getPosterByte() != null) {
                byte[] poster = popularMovies.getPosterByte();
                Bitmap bMap = BitmapFactory.decodeByteArray(poster, 0, poster.length);
                viewHolder.ivPoster.setImageBitmap(bMap);
            } else {
                viewHolder.ivPoster.setImageResource(R.drawable.placeholder);
            }
        }
    }

    @Override
    public int getItemCount() {

        return popularMoviesList.size();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView ivPoster;

        ViewHolder(View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            listener.onItemClick(position);
        }
    }
}
