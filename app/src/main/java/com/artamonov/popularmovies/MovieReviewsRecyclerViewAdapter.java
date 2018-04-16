package com.artamonov.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MovieReviewsRecyclerViewAdapter extends RecyclerView.Adapter<MovieReviewsRecyclerViewAdapter.ViewHolder> {

    private List<PopularMovies> movieReviewsList;
    private Context context;


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
        holder.reviewAuthor.setText(popularMovies.getReviewAuthor());
        holder.reviewContent.setText(popularMovies.getReviewContent());
    }

    @Override
    public int getItemCount() {
        return movieReviewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView reviewAuthor;
        TextView reviewContent;

        public ViewHolder(View itemView) {
            super(itemView);

            reviewAuthor = itemView.findViewById(R.id.reviewAuthor);
            reviewContent = itemView.findViewById(R.id.reviewContent);

        }
    }
}
