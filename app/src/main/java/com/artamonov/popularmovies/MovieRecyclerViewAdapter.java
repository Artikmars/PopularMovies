package com.artamonov.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.artamonov.popularmovies.MainActivity.TAG;
import static com.artamonov.popularmovies.MainActivity.isChoseFavorites;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {
    private static ItemClickListener listener;
    private final List<PopularMovies> popularMoviesList;
    private Context mContext;

    public MovieRecyclerViewAdapter(Context context, List<PopularMovies> popularMoviesList, ItemClickListener itemClickListener) {
        this.mContext = context;
        listener = itemClickListener;
        this.popularMoviesList = popularMoviesList;
        Log.i(TAG, " Adapter is constructed ");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_items_movies, viewGroup, false);
        Log.i(TAG, " onCreateViewHolder invoked ");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        Log.i(TAG, " onBindViewHolder " + position);
        PopularMovies popularMovies = popularMoviesList.get(position);
        Log.i(MainActivity.TAG, "popularMovies: " + popularMovies.getTitle() + ", " + popularMovies.getId() +
                ", " + popularMovies.getPosterPath());
        Log.i(TAG, " onBindViewHolder: get(position).getTitle() " + popularMoviesList.get(position).getTitle());
        if (!isChoseFavorites()) {
            Log.i(TAG, " onBindViewHolder: IS NOT Favorites: getPosterPath()) " + popularMovies.getPosterPath());
            if (!TextUtils.isEmpty(popularMovies.getPosterPath())) {
                Picasso.with(mContext)
                        .load(popularMovies.getPosterPath())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder_error)
                        .into(viewHolder.ivPoster);
            } else {
                Log.i(TAG, " onBindViewHolder: popularMovies.getPoster() is Empty) " + popularMovies.getPosterPath());
            }
        } else {
            Log.i(TAG, " onBindViewHolder: IS Favorites");
            if (popularMovies.getPosterByte() != null) {
                byte[] poster = popularMovies.getPosterByte();
                Bitmap bMap = BitmapFactory.decodeByteArray(poster, 0, poster.length);
                viewHolder.ivPoster.setImageBitmap(bMap);
            } else {
                viewHolder.ivPoster.setImageResource(R.drawable.placeholder);
                Log.i(TAG, " onBindViewHolder: popularMovies.getPosterByte() is Empty");
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
