package com.artamonov.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {

    public static final String TAG = "myLogs";

    //Enter your API key here only once
    private static final String API_KEY = "";

    private static String mostPopularUrl = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY + "&language=en-US&page=1";
    private static String topRatedURL = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY + "&language=en-US&page=1";
    Request request;
    String responseJSON;
    CharSequence[] values = {"Most Popular", "Top Rated"};

    private RecyclerView recyclerView;
    private List<PopularMovies> popularMoviesList;
    private RecyclerViewAdapter recyclerViewAdapter;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate ");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rvMovies);
        recyclerViewAdapter = new RecyclerViewAdapter(popularMoviesList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //recyclerView.addItemDecoration(new GridItemDecoration());
        getJSONData(mostPopularUrl);


    }

    private void getJSONData(String url) {

        if (isNetworkAvailable()) {
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
                        Log.i(TAG, "responseJSON: " + responseJSON);

                        // Log.i(TAG, "popularMoviesList: " + popularMoviesList.toString());

                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                              /*  if (!popularMoviesList.isEmpty()) {
                                    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this,
                                            popularMoviesList);
                                    recyclerView.setAdapter(recyclerViewAdapter);
                                } else {
                                    Log.i(TAG, "popularMoviesList is empty");
                                }
                          */
                                              parseJSONData(responseJSON);

                                          }
                                      }
                        );


                    } else {
                        Log.i(TAG, "response is not successful ");
                    }
                }
            });

        } else {
            Log.i(TAG, "NO Internet Connection");
        }

       /* if (popularMovies != null) {
            populateUI(popularMovies);
        } else {
            Log.i(TAG, "popularMovies: NULL - " + popularMovies);
        }*/


    }

    private void parseJSONData(String responseJSON) {

        popularMoviesList = PopularMoviesParsing.parseMoviesJSON(responseJSON);
        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, popularMoviesList, (RecyclerViewAdapter.ItemClickListener) this);
        recyclerViewAdapter.notifyDataSetChanged();
        Log.i(TAG, "before recyclerViewAdapter");
        Log.i(TAG, "popularMoviesList " + popularMoviesList);

        recyclerView.setLayoutManager(null);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //recyclerView.addItemDecoration(new GridItemDecoration(this, 0, 0, 0, 0));
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

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


    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null;
    }


    @Override
    public void onItemClick(int position) {

        String title = popularMoviesList.get(position).getTitle();
        String releaseDate = popularMoviesList.get(position).getReleaseDate();
        String voteAverage = popularMoviesList.get(position).getVoteAverage();
        String overview = popularMoviesList.get(position).getOverview();
        String posterPath = popularMoviesList.get(position).getPosterPath();

        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("releaseDate", releaseDate);
        intent.putExtra("voteAverage", voteAverage);
        intent.putExtra("overview", overview);
        intent.putExtra("posterPath", posterPath);
        intent.putExtra("title", title);

        startActivity(intent);
    }
}
