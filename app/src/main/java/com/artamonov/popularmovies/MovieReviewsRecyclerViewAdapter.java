package com.artamonov.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieReviewsRecyclerViewAdapter extends RecyclerView.Adapter<MovieReviewsRecyclerViewAdapter.ViewHolder> {

    private final List<PopularMovies> movieReviewsList;
    private final Context context;


    public MovieReviewsRecyclerViewAdapter(Context context, List<PopularMovies> reviewsList) {
        this.context = context;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.reviewAuthor)
        TextView reviewAuthor;
        @BindView(R.id.reviewContent)
        TextView reviewContent;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
