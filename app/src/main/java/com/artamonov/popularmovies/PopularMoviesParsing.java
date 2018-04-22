package com.artamonov.popularmovies;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PopularMoviesParsing {


    public static List<PopularMovies> parseMoviesJSON(String json) {

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

                    PopularMovies popularMovies = new PopularMovies();
                    popularMovies.setPosterPath(posterPath);
                    popularMovies.setId(id);
                    popularMovies.setTitle(title);
                    popularMovies.setReleaseDate(release_date);
                    popularMovies.setVoteAverage(voteAverage);
                    popularMovies.setOverview(overview);

                    results.add(popularMovies);

                }
            } else {
                Log.i(MainActivity.TAG, "resultsJsonArray is empty");
            }
        } catch (JSONException e) {
            Log.i(MainActivity.TAG, "JSONException: " + e);
        }
        return results;

    }


}
