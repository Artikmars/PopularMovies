package com.artamonov.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.artamonov.popularmovies.MainActivity.TAG;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static int viewHolderCount;
    private ItemClickListener listener;
    private String[] mData = new String[0];
    // private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int mNumberItems;
    private List<PopularMovies> popularMoviesList;
    private Context mContext;

    // data is passed into the constructor
    public RecyclerViewAdapter(List<PopularMovies> popularMovies) {
        popularMoviesList = popularMovies;
    }

    public RecyclerViewAdapter(Context context, List<PopularMovies> popularMovies, ItemClickListener listener) {
        this.mContext = context;
        this.listener = listener;
        popularMoviesList = popularMovies;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_items_movies, viewGroup, false);
        Log.i(TAG, " onCreateViewHolder ");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Log.i(TAG, " onBindViewHolder ");
        PopularMovies popularMovies = popularMoviesList.get(position);

        if (!TextUtils.isEmpty(popularMovies.getPosterPath())) {
            Picasso.with(mContext)
                    .load(popularMovies.getPosterPath())
                    // .load("http://image.tmdb.org/t/p/w185//jjPJ4s3DWZZvI4vw8Xfi4Vqa1Q8.jpg")
                    .into(viewHolder.imageView);
            Log.i(TAG, "in PICASSO:getPosterPath: " + popularMovies.getPosterPath());
        } else {
            Log.i(TAG, "popularMovies.getPosterPath() is Empty");
        }

        ViewGroup.LayoutParams lp = viewHolder.imageView.getLayoutParams();
        if (lp instanceof FlexboxLayoutManager.LayoutParams) {
            FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams)
                    viewHolder.imageView.getLayoutParams();
            flexboxLp.setFlexGrow(1.0f);
        }

    }

    // total number of cells
    @Override
    public int getItemCount() {

        return popularMoviesList.size();
    }



    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int position);
    }

    // stores and recycles views as they are scrolled off screen
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
