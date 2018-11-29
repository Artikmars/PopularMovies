package com.artamonov.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpinnerMovieTrailersAdapter extends ArrayAdapter<String> {

    private final LayoutInflater layoutInflater;
    private final Context context;
    private final List<PopularMovies> movieTrailersList;
    private final int resource;

    SpinnerMovieTrailersAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);

        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.resource = resource;
        movieTrailersList = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = layoutInflater.inflate(resource, parent, false);
        TextView trailerName = view.findViewById(R.id.trailerName);
        //TextView trailerNumber = view.findViewById(R.id.trailerNumber);
        TextView trailerQuality = view.findViewById(R.id.trailerQuality);
        TextView trailerLabel = view.findViewById(R.id.tvTrailer);
        Integer currentPosition = position + 1;
        trailerLabel.setText(context.getResources().getString(R.string.trailer, currentPosition));
        if (movieTrailersList.size() != 1 || (!movieTrailersList.get(position).getTrailerName().equals("No trailers")) &&
                movieTrailersList.size() == 1) {

            PopularMovies popularMovies = movieTrailersList.get(position);
            trailerName.setText(popularMovies.getTrailerName());

            // trailerNumber.setText(String.valueOf(currentPosition));

            String qualityString = popularMovies.getTrailerQuality();
            trailerQuality.setText(qualityString);
            switch (qualityString) {
                case "1080":
                    trailerQuality.setBackgroundColor(context.getResources().getColor(R.color.green));
                    break;
                case "720":
                    trailerQuality.setBackgroundColor(context.getResources().getColor(R.color.yellow));
                    break;
                default:
                    trailerQuality.setBackgroundColor(context.getResources().getColor(R.color.red));
                    break;
            }
        } else {
            MovieDetailActivity movieDetailActivity = new MovieDetailActivity();
            movieDetailActivity.noTrailersSpinnerOnClick();
        }
        return view;
    }


}
