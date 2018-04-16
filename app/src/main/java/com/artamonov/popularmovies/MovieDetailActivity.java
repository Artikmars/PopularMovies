package com.artamonov.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.artamonov.popularmovies.MainActivity.API_KEY;
import static com.artamonov.popularmovies.MainActivity.responseJSON;

public class MovieDetailActivity extends AppCompatActivity implements MovieTrailersRecyclerViewAdapter.ItemClickListener {

    private static String movieTrailersURL;
    ImageView ivDetailPoster;
    TextView tvOverview;
    TextView tvVoteAverage;
    TextView tvReleaseDate;
    TextView tvTitle;
    Context context;
    RecyclerView rvTrailers;
    int movieID;
    List<PopularMovies> movieTrailers = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ivDetailPoster = findViewById(R.id.ivDetailPoster);
        tvOverview = findViewById(R.id.tvOverview);
        tvReleaseDate = findViewById(R.id.tvReleaseDate);
        tvVoteAverage = findViewById(R.id.tvVoteAverage);
        tvTitle = findViewById(R.id.tvTitle);
        rvTrailers = findViewById(R.id.rvTrailers);

        Intent intent = getIntent();
        Picasso.with(context)
                .load(intent.getStringExtra("posterPath"))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(ivDetailPoster);

        String releaseDate = intent.getStringExtra("releaseDate");
        String[] releaseDateArray = releaseDate.split("-");

        String releaseDateFullFormat = releaseDateArray[2] + "." + releaseDateArray[1] + "." +
                releaseDateArray[0];

        String voteAverage = intent.getStringExtra("voteAverage");
        String voteAverageOverTen = voteAverage + "/10";

        tvTitle.setText(intent.getStringExtra("title"));
        tvReleaseDate.setText(releaseDateFullFormat);
        tvVoteAverage.setText(voteAverageOverTen);
        tvOverview.setText(intent.getStringExtra("overview"));

        movieID = intent.getIntExtra("id", 1);
        Log.i(MainActivity.TAG, "id " + movieID);
        movieTrailersURL = "https://api.themoviedb.org/3/movie/" + movieID + "/videos?api_key=" + API_KEY + "&language=en-US";
        Log.i(MainActivity.TAG, "movieTrailersURL" + movieTrailersURL);

        rvTrailers.setLayoutManager(new LinearLayoutManager(this));
        getJSONMovieTrailers(movieTrailersURL);
        Log.i(MainActivity.TAG, "movieTrailers" + movieTrailers.toString());

    }

    public void getJSONMovieTrailers(String url) {

        //MainActivity ma = new MainActivity();
        if (isNetworkAvailable()) {
            OkHttpClient okHttpClient = new OkHttpClient();
            MainActivity.request = new Request.Builder()
                    .url(url)
                    .build();

            okHttpClient.newCall(MainActivity.request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i(MainActivity.TAG, "onFailure: " + e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Check your Internet connection or try later",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.i(MainActivity.TAG, "response: " + response.message());
                    if (response.isSuccessful()) {
                        responseJSON = response.body().string();
                        Log.i(MainActivity.TAG, "responseJSON: " + responseJSON);

                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              parseJSONMovieTrailers(responseJSON);
                                          }
                                      }
                        );

                    } else {
                        Log.i(MainActivity.TAG, "Response is not successful ");
                    }
                }
            });

        } else {
            Log.i(MainActivity.TAG, "No Internet Connection");
        }

    }


    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    private void parseJSONMovieTrailers(String responseJSON) {
        try {
            JSONObject jsonObject = new JSONObject(responseJSON);
            JSONArray resultsJsonArray = jsonObject.getJSONArray("results");
            if (resultsJsonArray.length() != 0) {

                for (int i = 0; i < resultsJsonArray.length(); i++) {
                    JSONObject movieTrailerObject = resultsJsonArray.getJSONObject(i);
                    String trailerKey = movieTrailerObject.optString("key");
                    Log.i(MainActivity.TAG, "key: " + trailerKey);
                    String trailerName = movieTrailerObject.optString("name");
                    String trailerQuality = movieTrailerObject.optString("size");

                    PopularMovies popularMovies = new PopularMovies();
                    popularMovies.setTrailerKey(trailerKey);
                    popularMovies.setTrailerName(trailerName);
                    popularMovies.setTrailerName(trailerQuality);
                    movieTrailers.add(popularMovies);
                }

            }

            MovieTrailersRecyclerViewAdapter movieTrailersRecyclerViewAdapter = new MovieTrailersRecyclerViewAdapter(MovieDetailActivity.this, movieTrailers, this);
            rvTrailers.setAdapter(movieTrailersRecyclerViewAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemTrailerClick(int position) {
        PopularMovies popularMovies = movieTrailers.get(position);
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + popularMovies.getTrailerKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + popularMovies.getTrailerKey()));
        try {

            startActivity(appIntent);

        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);

        }

    }
}
