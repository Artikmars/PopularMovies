package com.artamonov.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.artamonov.popularmovies.MainActivity.TAG;

public class MovieTrailersRecyclerViewAdapter extends RecyclerView.Adapter<MovieTrailersRecyclerViewAdapter.ViewHolder> {

    private static ItemClickListener listener;
    private Context context;
    private List<PopularMovies> movieTrailersList;


    public MovieTrailersRecyclerViewAdapter(Context context, List<PopularMovies> movieTrailersList, ItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.movieTrailersList = movieTrailersList;
        Log.i(TAG, " MovieTrailersRecyclerViewAdapter is constructed");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, " MovieTrailersRecyclerViewAdapter: onCreateViewHolder before invoking ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_trailers, parent, false);
        Log.i(TAG, " MovieTrailersRecyclerViewAdapter: onCreateViewHolder invoked ");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.i(TAG, " MovieTrailersRecyclerViewAdapter: onBindViewHolder " + position);

        PopularMovies popularMovies = movieTrailersList.get(position);
        Integer currentPosition = position + 1;
        Log.i(TAG, " MovieTrailersRecyclerViewAdapter: onBindViewHolder - currentPosition " + currentPosition);

        holder.trailerName.setText(popularMovies.getTrailerName());
        Log.i(TAG, " MovieTrailersRecyclerViewAdapter: onBindViewHolder - trailerName " + holder.trailerName);
        holder.trailerNumber.setText(String.valueOf(currentPosition));
        Log.i(TAG, " MovieTrailersRecyclerViewAdapter: onBindViewHolder - trailerNumber " + holder.trailerNumber);
        holder.trailerQuality.setText(popularMovies.getTrailerQuality());
        Log.i(TAG, " MovieTrailersRecyclerViewAdapter: onBindViewHolder - trailerQuality " + holder.trailerQuality);
    }

    @Override
    public int getItemCount() {
        return movieTrailersList.size();
    }

    public interface ItemClickListener {
        void onItemTrailerClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ibPlay;
        private TextView trailerName;
        private TextView tvTrailer;
        private TextView trailerQuality;
        private TextView trailerNumber;

        public ViewHolder(View view) {
            super(view);
            ibPlay = view.findViewById(R.id.ibPlay);
            trailerName = view.findViewById(R.id.trailerName);
            tvTrailer = view.findViewById(R.id.tvTrailer);
            trailerQuality = view.findViewById(R.id.trailerQuality);
            trailerNumber = view.findViewById(R.id.trailerNumber);

            ibPlay.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            listener.onItemTrailerClick(position);
        }
    }
}
