package com.artamonov.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.artamonov.popularmovies.MainActivity.TAG;

class PopularMoviesParsing {
    private static Bitmap posterImage = null;
    private static byte[] poster;

    private static Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.i(TAG, "In onBitmapLoaded ");
            // posterImage = bitmap;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                poster = byteArrayOutputStream.toByteArray();
            } else {
                Log.i(TAG, "In PARSING:  bitmap is NULL: " + bitmap);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            Log.i(TAG, "In onBitmapFailed");
        }


        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.i(TAG, "onPrepareLoad");
        }
    };


    public static List<PopularMovies> parseMoviesJSON(Context context, String json) {

        List<PopularMovies> results = new ArrayList<>();


        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray resultsJsonArray = jsonObject.getJSONArray("results");

            if (resultsJsonArray.length() != 0) {
                for (int i = 0; i < resultsJsonArray.length(); i++) {
                    JSONObject jsonMovieObject = resultsJsonArray.getJSONObject(i);
                    String baseURL = jsonMovieObject.optString("poster_path");
                    String posterPath = "http://image.tmdb.org/t/p/w185/" + baseURL;

                    String title = jsonMovieObject.optString("title");
                    String voteAverage = jsonMovieObject.optString("vote_average");
                    String release_date = jsonMovieObject.optString("release_date");
                    String overview = jsonMovieObject.optString("overview");
                    Integer id = jsonMovieObject.optInt("id");

                    Picasso.get()
                            .load(posterPath)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder_error)
                            .into(target);

                   /* Log.i(TAG, "In PARSING:  posterImage: " + posterImage);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    if (posterImage != null) {
                        posterImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        poster = byteArrayOutputStream.toByteArray();
                    }*/

                    PopularMovies popularMovies = new PopularMovies();
                    popularMovies.setPosterPath(posterPath);
                    popularMovies.setPosterByte(poster);

                    popularMovies.setId(id);
                    popularMovies.setTitle(title);
                    popularMovies.setReleaseDate(release_date);
                    popularMovies.setVoteAverage(voteAverage);
                    popularMovies.setOverview(overview);

                    results.add(popularMovies);
                    Log.i(TAG, "In PARSING: poster: " + poster);
                    // Log.i(TAG, "In PARSING:  popularMovies.setPosterByte(poster): " + poster.toString());
                }
            } else {
                Log.i(TAG, "resultsJsonArray is empty");
            }
        } catch (JSONException e) {
            Log.i(TAG, "JSONException: " + e);
        }
        return results;

    }


}
