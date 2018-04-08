package com.artamonov.popularmovies;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class PopularMoviesParsing {



    public static List<PopularMovies> parseMoviesJSON(String json) {

        List<PopularMovies> results = new ArrayList<>();


        try {
            JSONObject jsonObject = new JSONObject(json);
            Log.i(MainActivity.TAG, "jsonObject: " + jsonObject);
            JSONArray resultsJsonArray = jsonObject.getJSONArray("results");
            Log.i(MainActivity.TAG, "resultsJsonArray: " + resultsJsonArray);

            if (resultsJsonArray.length() != 0 ) {
                for (int i = 0; i < resultsJsonArray.length(); i++) {
                    JSONObject jsonMovieObject = resultsJsonArray.getJSONObject(i);
                    String posterPath = jsonMovieObject.optString("poster_path");
                    String baseURL = "http://image.tmdb.org/t/p/w185/" + posterPath;

                    String title = jsonMovieObject.optString("title");
                    String voteAverage = jsonMovieObject.optString("vote_average");
                    String release_date = jsonMovieObject.optString("release_date");
                    String overview = jsonMovieObject.optString("overview");

                    PopularMovies popularMovies = new PopularMovies();
                    popularMovies.setBaseUrl(baseURL);
                    popularMovies.setTitle(title);
                    popularMovies.setReleaseDate(release_date);
                    popularMovies.setVoteAverage(voteAverage);
                    popularMovies.setOverview(overview);

                    results.add(popularMovies);

                    Log.i(MainActivity.TAG, "popularMovies: " + results.get(i).getTitle() + ", " +
                    results.get(i).getVoteAverage() + " " + results.get(i).getReleaseDate() +
                    " " + results.get(i).getOverview());
                }
            } else {
                Log.i(MainActivity.TAG, "resultsJsonArray is empty");
            }
        } catch (JSONException e) {
            Log.i(MainActivity.TAG, "JSONException: " + e);
        }
        return results;

     /*


        Uri webpage = Uri.parse(url);*/


    }


}
