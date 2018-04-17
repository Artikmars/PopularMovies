package com.artamonov.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.artamonov.popularmovies.MainActivity.API_KEY;
import static com.artamonov.popularmovies.MainActivity.responseJSON;

public class MovieDetailActivity extends AppCompatActivity {

    private static List<PopularMovies> movieReviews = new ArrayList<>();
    List<PopularMovies> movieTrailers = new ArrayList<>();

    TextView trailerName;
    TextView trailerNumber;
    TextView trailerQuality;
    TextView tvTrailer;

    ImageView ibPlay;
    Spinner sMovieTrailers;

    @BindView(R.id.ivDetailPoster)
    ImageView ivDetailPoster;
    @BindView(R.id.rvReviews)
    RecyclerView rvReviews;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvOverview)
    TextView tvOverview;
    @BindView(R.id.tvVoteAverage)
    TextView tvVoteAverage;
    @BindView(R.id.tvReleaseDate)
    TextView tvReleaseDate;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    Context context;
    int movieID;

    private static List<PopularMovies> parseJSONMovieReviews(String responseJSON) {

        try {
            JSONObject jsonObject = new JSONObject(responseJSON);
            JSONArray resultsJsonArray = jsonObject.getJSONArray("results");
            movieReviews = new ArrayList<>();
            if (resultsJsonArray.length() != 0) {
                for (int i = 0; i < resultsJsonArray.length(); i++) {
                    JSONObject movieReviewObject = resultsJsonArray.getJSONObject(i);
                    String reviewAuthor = movieReviewObject.optString("author");
                    String reviewContent = movieReviewObject.optString("content");

                    PopularMovies popularMovies = new PopularMovies();
                    popularMovies.setReviewAuthor(reviewAuthor);
                    popularMovies.setReviewContent(reviewContent);
                    movieReviews.add(popularMovies);
                }

            } else {
                Log.i(MainActivity.TAG, "No reviews");
                PopularMovies popularMovies = new PopularMovies();
                popularMovies.setReviewAuthor("No reviews yet");
                movieReviews.add(popularMovies);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieReviews;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ivDetailPoster = findViewById(R.id.ivDetailPoster);
        sMovieTrailers = findViewById(R.id.sMovieTrailers);
        ibPlay = findViewById(R.id.ibPlay);

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
        Log.i(MainActivity.TAG, "movieID" + movieID);
        Log.i(MainActivity.TAG, "id " + movieID);
        String movieTrailersURL = "https://api.themoviedb.org/3/movie/" + movieID + "/videos?api_key=" + API_KEY + "&language=en-US";
        // Log.i(MainActivity.TAG, "movieTrailersURL" + movieTrailersURL);

        getJSONMovieTrailers(movieTrailersURL);
        //Log.i(MainActivity.TAG, "movieTrailers" + movieTrailers.toString());

        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        String movieReviewsURL = "https://api.themoviedb.org/3/movie/" + movieID + "/reviews?api_key=" + API_KEY + "&language=en-US";
        getJSONMovieReviews(movieReviewsURL);
    }

    public void getJSONMovieTrailers(String url) {

        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
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
                        Log.i(MainActivity.TAG, "responseJSON Trailers : " + responseJSON);

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

    public void getJSONMovieReviews(String url) {

        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
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
                        Log.i(MainActivity.TAG, "responseJSON Reviews: " + responseJSON);

                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              movieReviews = parseJSONMovieReviews(responseJSON);
                                              MovieReviewsRecyclerViewAdapter movieReviewsRecyclerViewAdapter =
                                                      new MovieReviewsRecyclerViewAdapter(MovieDetailActivity.this,
                                                              movieReviews);
                                              rvReviews.setAdapter(movieReviewsRecyclerViewAdapter);
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
                    popularMovies.setTrailerQuality(trailerQuality);
                    movieTrailers.add(popularMovies);
                }

            } else {
                Log.i(MainActivity.TAG, "No trailers");
                PopularMovies popularMovies = new PopularMovies();
                popularMovies.setTrailerName("No trailers");
                movieTrailers.add(popularMovies);
            }

            SpinnerMovieTrailersAdapter adapter = new SpinnerMovieTrailersAdapter(this,
                    R.layout.movie_trailers, movieTrailers);

            sMovieTrailers.setAdapter(adapter);
            sMovieTrailers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    ConstraintLayout constraintLayout = view.findViewById(R.id.movieTrailersLayout);


                    trailerName = findViewById(R.id.trailerName);
                    trailerNumber = findViewById(R.id.trailerNumber);
                    trailerQuality = findViewById(R.id.trailerQuality);

                    PopularMovies popularMovies = movieTrailers.get(pos);
                    trailerName.setText(popularMovies.getTrailerName());
                    Integer currentPosition = pos + 1;
                    trailerNumber.setText(String.valueOf(currentPosition));

                    String qualityString = popularMovies.getTrailerQuality();
                    Integer quality = Integer.parseInt(qualityString);
                    if (quality == 1080)
                        constraintLayout.setBackgroundColor(getResources().getColor(R.color.green));
                    else if (quality == 720)
                        constraintLayout.setBackgroundColor(getResources().getColor(R.color.yellow));
                    else
                        constraintLayout.setBackgroundColor(getResources().getColor(R.color.red));
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    trailerName.setText(null);
                    trailerNumber.setText(null);
                    trailerQuality.setText(null);
                    tvTrailer.setText(getResources().getString(R.string.pick_trailer));

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void playTrailer(View view) {

        if (movieTrailers.size() != 1) {
            Integer position = Integer.parseInt(trailerNumber.getText().toString()) - 1;
            PopularMovies popularMovies = movieTrailers.get(position);
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + popularMovies.getTrailerKey()));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + popularMovies.getTrailerKey()));
            try {

                startActivity(appIntent);

            } catch (ActivityNotFoundException ex) {
                startActivity(webIntent);

            }
        } else {
            Log.i(MainActivity.TAG, "playTrailer - No trailers");
            ibPlay.setEnabled(false);
            Toast.makeText(getApplicationContext(), "No trailers to play", Toast.LENGTH_SHORT).show();
        }

    }


}
