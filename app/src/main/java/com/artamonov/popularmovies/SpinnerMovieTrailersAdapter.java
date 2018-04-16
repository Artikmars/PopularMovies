package com.artamonov.popularmovies;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerMovieTrailersAdapter extends ArrayAdapter<String> {

    private final LayoutInflater layoutInflater;
    private final Context context;
    private final List<PopularMovies> movieTrailersList;
    private final int resource;

    public SpinnerMovieTrailersAdapter(@NonNull Context context, @LayoutRes int resource,
                                       @NonNull List objects) {
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
        TextView trailerNumber = view.findViewById(R.id.trailerNumber);
        TextView trailerQuality = view.findViewById(R.id.trailerQuality);


        PopularMovies popularMovies = movieTrailersList.get(position);

        trailerName.setText(popularMovies.getTrailerName());
        Integer currentPosition = position + 1;
        trailerNumber.setText(String.valueOf(currentPosition));

        String qualityString = popularMovies.getTrailerQuality();
        trailerQuality.setText(qualityString);
        if (qualityString.equals("1080"))
            trailerQuality.setBackgroundColor(context.getResources().getColor(R.color.green));
        else if (qualityString.equals("720"))
            trailerQuality.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        else
            trailerQuality.setBackgroundColor(context.getResources().getColor(R.color.red));
        return view;
    }


}
