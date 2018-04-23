package com.artamonov.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.artamonov.popularmovies.databinding.MovieDetailActivityBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.artamonov.popularmovies.MainActivity.API_KEY;
import static com.artamonov.popularmovies.MainActivity.dbHelper;
import static com.artamonov.popularmovies.MainActivity.isChoseFavorites;
import static com.artamonov.popularmovies.MainActivity.responseJSON;
import static com.artamonov.popularmovies.MainActivity.sqLiteDatabase;

public class MovieDetailActivity extends AppCompatActivity {

    private static List<PopularMovies> movieReviews = new ArrayList<>();
    private final List<PopularMovies> movieTrailers = new ArrayList<>();
    Context context;
    private MovieDetailActivityBinding binding;
    private int detailMovieID;
    private Bitmap detailMoviePosterImage;
    private TextView trailerName, trailerQuality, tvTrailer;
    private String detailMovieTitle, detailMovieVoteAverage, detailMovieReleaseDate, detailMovieOverview;
    private String shareTrailerLink;
    private String shareTrailerTitle;
    private int spinnerCurrentPressedPosition;

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

    private static boolean isFavorite(Integer detailMovieID) {
        Log.i(MainActivity.TAG, "IN isFavorite");
        String checkQuery = "SELECT * FROM " + DBContract.DBEntry.TABLE_NAME + " WHERE " +
                DBContract.DBEntry.COLUMN_MOVIE_ID + " = " + detailMovieID;
        Log.i(MainActivity.TAG, "checkQuery: " + checkQuery);

        sqLiteDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(checkQuery, null);
        Log.i(MainActivity.TAG, "cursor.getCount()" + cursor.getCount());
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        //  sqLiteDatabase.close();
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.movie_detail_activity);
        binding = DataBindingUtil.setContentView(this, R.layout.movie_detail_activity);
        ButterKnife.bind(MovieDetailActivity.this);
        setSupportActionBar((Toolbar) binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();


        String releaseDateFromIntent = intent.getStringExtra("releaseDate");
        String[] releaseDateArray = releaseDateFromIntent.split("-");
        if (releaseDateArray.length > 1) {
            detailMovieReleaseDate = releaseDateArray[2] + "." + releaseDateArray[1] + "." +
                    releaseDateArray[0];
            binding.tvReleaseDate.setText(detailMovieReleaseDate);
        } else {
            detailMovieReleaseDate = releaseDateFromIntent;
        }

       /* for (int i = 0; i < releaseDateArray.length; i++) {
            Log.i(MainActivity.TAG, "releaseDateArray [" + i + "]: " + releaseDateArray[i]);
            detailMovieReleaseDate = releaseDateArray[i] + ".";
        }*/

        String voteAverage = intent.getStringExtra("voteAverage");
        String voteAverageOutOfTen = voteAverage + "/10";
        detailMovieVoteAverage = intent.getStringExtra("voteAverage");
        detailMovieTitle = intent.getStringExtra("title");
        Log.i(MainActivity.TAG, "MovieDetail = onCreate - title: " + detailMovieTitle);
        binding.tvTitle.setText(detailMovieTitle);

        binding.tvVoteAverage.setText(voteAverageOutOfTen);
        detailMovieOverview = (intent.getStringExtra("overview"));
        binding.tvOverview.setText(detailMovieOverview);
        detailMovieID = intent.getIntExtra("id", 1);
        Log.i(MainActivity.TAG, "movieID" + detailMovieID);
        Log.i(MainActivity.TAG, "id " + detailMovieID);


        if (isFavorite(detailMovieID))  {
            binding.ivFavorites.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
            binding.tvFavorites.setText(R.string.remove_favorites);
        }

        detailMoviePosterImage = intent.getParcelableExtra("posterImage");
        if (!isChoseFavorites()) {
            String detailMoviePosterPath = intent.getStringExtra("posterPath");
            Picasso.with(context)
                    .load(detailMoviePosterPath)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder_error)
                    .into(binding.ivDetailPoster);

            //spinner = findViewById(R.id.sMovieTrailers);
            binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
            String movieTrailersURL = "https://api.themoviedb.org/3/movie/" + detailMovieID + "/videos?api_key=" + API_KEY + "&language=en-US";
            // Log.i(MainActivity.TAG, "movieTrailersURL" + movieTrailersURL);
            getJSONMovieTrailers(movieTrailersURL);
            //Log.i(MainActivity.TAG, "movieTrailers" + movieTrailers.toString());
            String movieReviewsURL = "https://api.themoviedb.org/3/movie/" + detailMovieID + "/reviews?api_key=" + API_KEY + "&language=en-US";
            getJSONMovieReviews(movieReviewsURL);
        } else {
            binding.ivDetailPoster.setImageBitmap(detailMoviePosterImage);

        }
    }

    private void getJSONMovieTrailers(String url) {

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

    private void getJSONMovieReviews(String url) {

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
                                              binding.rvReviews.setAdapter(movieReviewsRecyclerViewAdapter);
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
                    R.layout.movie_trailers, binding.sMovieTrailers, movieTrailers);

            binding.sMovieTrailers.setAdapter(adapter);
            binding.sMovieTrailers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                    ConstraintLayout constraintLayout = view.findViewById(R.id.movieTrailersLayout);
                    tvTrailer = findViewById(R.id.tvTrailer);
                    // trailerNumber = findViewById(R.id.trailerNumber);
                    trailerName = findViewById(R.id.trailerName);
                    trailerQuality = findViewById(R.id.trailerQuality);
                    trailerName.setText(null);
                    trailerQuality.setText(null);

                    spinnerCurrentPressedPosition = pos + 1;
                    tvTrailer.setText(getResources().getString(R.string.trailer, spinnerCurrentPressedPosition));

                    PopularMovies popularMovies = movieTrailers.get(pos);

                    shareTrailerLink = popularMovies.getTrailerKey();
                    shareTrailerTitle = popularMovies.getTrailerName();
                    //  trailerName.setText(popularMovies.getTrailerName());

                    //  trailerNumber.setText(String.valueOf(currentPosition));
                    if (popularMovies.getTrailerQuality() != null) {
                        String qualityString = popularMovies.getTrailerQuality();
                        Log.i(MainActivity.TAG, "onItemSelected: qualityString : " + qualityString);
                        if (qualityString.equals("1080")) {
                            constraintLayout.setBackgroundColor(getResources().getColor(R.color.green));
                        } else if (qualityString.equals("720")) {
                            constraintLayout.setBackgroundColor(getResources().getColor(R.color.yellow));
                        } else {
                            constraintLayout.setBackgroundColor(getResources().getColor(R.color.red));
                        }
                    } else {
                        noTrailersSpinnerOnClick();

                    }

                }

                public void onNothingSelected(AdapterView<?> parent) {
                    trailerName = findViewById(R.id.trailerName);
                    trailerQuality = findViewById(R.id.trailerQuality);
                    trailerName.setText(null);
                    trailerQuality.setText(null);
                    tvTrailer.setText(getResources().getString(R.string.pick_trailer));

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void noTrailersSpinnerOnClick() {
        binding.sMovieTrailers.setClickable(false);
        tvTrailer.setText(getResources().getString(R.string.no_trailers));

        // Spinner spinner = view.findViewById(R.id.sMovieTrailers);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void playTrailer(View view) {

        if (movieTrailers.size() != 1) {
            // Integer position = Integer.parseInt(trailerNumber.getText().toString()) - 1;
            PopularMovies popularMovies = movieTrailers.get(spinnerCurrentPressedPosition - 1);
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
            binding.ibPlay.setEnabled(false);
            Toast.makeText(getApplicationContext(), "No trailers to play", Toast.LENGTH_SHORT).show();
        }

    }

    public void onFavoritesClick(View view) {
        Log.i(MainActivity.TAG, "onFavoritesClick");
        sqLiteDatabase = dbHelper.getWritableDatabase();
        if (isFavorite(detailMovieID)) {
            Log.i(MainActivity.TAG, "isFavorite - true");
            onFavoritesDelete(view);
            binding.ivFavorites.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
            binding.tvFavorites.setText(R.string.add_favorites);
            Toast.makeText(getApplicationContext(), "Deleted from Favorites", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(MainActivity.TAG, "isFavorite - false");
            onFavoritesAdd(view);
            binding.tvFavorites.setText(R.string.remove_favorites);
            binding.ivFavorites.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
            Toast.makeText(getApplicationContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
        }
    }

    public void onFavoritesAdd(View view) {

        ContentValues values = new ContentValues();
        values.put(DBContract.DBEntry.COLUMN_MOVIE_ID,
                detailMovieID);
        values.put(DBContract.DBEntry.COLUMN_NAME,
                detailMovieTitle);
        values.put(DBContract.DBEntry.COLUMN_RELEASE_DATE,
                detailMovieReleaseDate);
        values.put(DBContract.DBEntry.COLUMN_RATING,
                detailMovieVoteAverage);
        values.put(DBContract.DBEntry.COLUMN_OVERVIEW,
                detailMovieOverview);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (detailMoviePosterImage != null) {
            detailMoviePosterImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] poster = byteArrayOutputStream.toByteArray();
            values.put(DBContract.DBEntry.COLUMN_POSTER,
                    poster);
        } else {
            Log.i(MainActivity.TAG, "detailMoviePosterImage is null: ");
        }

        Uri uri = getContentResolver().insert(
                FavoritesProvider.CONTENT_URI, values);
        if (uri != null) {
            Log.i(MainActivity.TAG, "onFavoritesAdd - Uri: " + uri.toString());
        }
    }

    private void onFavoritesDelete(View view) {
        Uri uri = Uri.parse(FavoritesProvider.CONTENT_URI + "/" + detailMovieID);
        Log.i(MainActivity.TAG, "onFavoritesDelete - Uri: " + uri.toString());
        int cnt = getContentResolver().delete(uri, null, null);
        Log.i(MainActivity.TAG, "onFavoritesDelete - cnt: " + cnt);
    }


    public void onShareTrailerClick(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = "https://www.youtube.com/watch?v=" + shareTrailerLink;
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareTrailerTitle);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, "Choose Sharing Method"));
    }
}
