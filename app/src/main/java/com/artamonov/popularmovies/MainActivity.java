package com.artamonov.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements MovieRecyclerViewAdapter.ItemClickListener {

    public static final String TAG = "myLogs";
    //Enter your API key here only once
    public static final String API_KEY = "";
    public static Request request;
    public static String responseJSON;
    private static String mostPopularUrl = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY + "&language=en-US";
    private static String topRatedURL = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY + "&language=en-US";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvMovies) RecyclerView rvMovies;

    CharSequence[] values = {"Most Popular", "Top Rated"};
    private List<PopularMovies> popularMoviesList;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        getJSONData(mostPopularUrl);
    }

    public void getJSONData(String url) {

        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            OkHttpClient okHttpClient = new OkHttpClient();
            request = new Request.Builder()
                    .url(url)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i(TAG, "onFailure: " + e);
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
                    Log.i(TAG, "response: " + response.message());
                    if (response.isSuccessful()) {
                        responseJSON = response.body().string();
                        Log.i(TAG, "responseJSON - MainActivity: " + responseJSON);

                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              parseJSONData(responseJSON);
                                          }
                                      }
                        );

                    } else {
                        Log.i(TAG, "Response is not successful ");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Response is not successful",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        } else {
            Log.i(TAG, "No Internet Connection");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Please check your Internet connection",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void parseJSONData(String responseJSON) {
        popularMoviesList = PopularMoviesParsing.parseMoviesJSON(responseJSON);
        MovieRecyclerViewAdapter movieRecyclerViewAdapter =
                new MovieRecyclerViewAdapter(MainActivity.this, popularMoviesList, this);
        rvMovies.setAdapter(movieRecyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort) {

            showAlertDialog();

        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sort thumbnails by: ");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        getJSONData(mostPopularUrl);
                        break;
                    case 1:
                        getJSONData(topRatedURL);
                        break;
                }
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onItemClick(int position) {

        Integer id = popularMoviesList.get(position).getId();
        Log.i(TAG, "In MainActivity: id" + id);
        String title = popularMoviesList.get(position).getTitle();
        String releaseDate = popularMoviesList.get(position).getReleaseDate();
        String voteAverage = popularMoviesList.get(position).getVoteAverage();
        Log.i(TAG, "In MainActivity: voteAverage" + voteAverage);
        String overview = popularMoviesList.get(position).getOverview();
        String posterPath = popularMoviesList.get(position).getPosterPath();

        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("releaseDate", releaseDate);
        intent.putExtra("voteAverage", voteAverage);
        intent.putExtra("overview", overview);
        intent.putExtra("posterPath", posterPath);
        intent.putExtra("title", title);

        startActivity(intent);
    }
}
