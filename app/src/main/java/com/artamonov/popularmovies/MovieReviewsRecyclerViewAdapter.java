package com.artamonov.popularmovies;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieReviewsRecyclerViewAdapter extends RecyclerView.Adapter<MovieReviewsRecyclerViewAdapter.ViewHolder> {

    private final List<PopularMovies> movieReviewsList;

    public MovieReviewsRecyclerViewAdapter(List<PopularMovies> reviewsList) {
        this.movieReviewsList = reviewsList;
    }

    @NonNull
    @Override
    public MovieReviewsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_reviews, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewsRecyclerViewAdapter.ViewHolder holder, int position) {
        PopularMovies popularMovies = movieReviewsList.get(position);
        Log.i(MainActivity.TAG, "movieReviewsList.size = " + movieReviewsList.size());

        // For distinguishing false positives and false negatives
        if (holder.reviewAuthor.getText() != "No reviews yet") {
            holder.reviewAuthor.setText(popularMovies.getReviewAuthor());
            holder.reviewContent.setText(popularMovies.getReviewContent());
        } else {
            holder.reviewAuthor.setText(popularMovies.getReviewAuthor());
            holder.reviewContent.setText(null);
        }
    }

    @Override
    public int getItemCount() {
        return movieReviewsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView reviewAuthor;
        TextView reviewContent;

        ViewHolder(View itemView) {
            super(itemView);
            reviewAuthor = itemView.findViewById(R.id.reviewAuthor);
            reviewContent = itemView.findViewById(R.id.reviewContent);

        }
    }
}
