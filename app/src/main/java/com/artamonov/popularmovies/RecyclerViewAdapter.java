package com.artamonov.popularmovies;

import android.content.Context;
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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ItemClickListener listener;
    private List<PopularMovies> popularMoviesList;
    private Context mContext;

    public RecyclerViewAdapter(Context context, List<PopularMovies> popularMovies, ItemClickListener listener) {
        this.mContext = context;
        this.listener = listener;
        popularMoviesList = popularMovies;
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

        PopularMovies popularMovies = popularMoviesList.get(position);

        if (!TextUtils.isEmpty(popularMovies.getPosterPath())) {

            Picasso.with(mContext)
                    .load(popularMovies.getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder_error)
                    .into(viewHolder.imageView);
        } else {
            Log.i(TAG, "popularMovies.getPosterPath() is Empty");
        }
    }

    @Override
    public int getItemCount() {

        return popularMoviesList.size();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivPoster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            listener.onItemClick(position);
        }
    }
}
