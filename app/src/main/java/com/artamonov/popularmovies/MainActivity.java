package com.artamonov.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

import static com.artamonov.popularmovies.DBContract.DBEntry.TABLE_NAME;


public class MainActivity extends AppCompatActivity implements MovieRecyclerViewAdapter.ItemClickListener {
    public static final String TAG = "myLogs";
    //Enter your API key here only once
    public static final String API_KEY = "";
    public static Request request;
    public static String responseJSON;
    public static DBHelper dbHelper;
    public static SQLiteDatabase sqLiteDatabase;
    private final static String mostPopularUrl = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY + "&language=en-US";
    private final static String topRatedURL = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY + "&language=en-US";
    private static boolean isChoseFavorites = false;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvMovies)
    RecyclerView rvMovies;
    private final CharSequence[] values = {"Sort by Most Popular", "Sort by Top Rated", "Favorites"};
    //  ArrayList<PopularMovies> dbPopularMoviesList = new ArrayList<>();
    private Bitmap posterImage;
    private List<PopularMovies> popularMoviesList;
    private AlertDialog alertDialog;
    private MovieRecyclerViewAdapter movieRecyclerViewAdapter;

    public static boolean isChoseFavorites() {
        return isChoseFavorites;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dbHelper = new DBHelper(this);
        Log.i(TAG, "dbVersion: " + DBHelper.DB_VERSION);
        setSupportActionBar(toolbar);
        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        getJSONData(mostPopularUrl);
    }
    private void getJSONData(String url) {

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
        movieRecyclerViewAdapter = new MovieRecyclerViewAdapter(MainActivity.this, popularMoviesList, this);
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
        if (id == R.id.cleanDB)
            cleanDB();

        return super.onOptionsItemSelected(item);
    }

    private void cleanDB() {
        Log.i(MainActivity.TAG, "in cleanDb");
        if (sqLiteDatabase != null) {
            sqLiteDatabase.delete(TABLE_NAME, null, null);
            Log.i(MainActivity.TAG, "in cleanDb");
            Toast.makeText(getApplicationContext(), "Favorites are removed", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Choose your thumbnails: ");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        isChoseFavorites = false;
                        getJSONData(mostPopularUrl);
                        break;
                    case 1:
                        isChoseFavorites = false;
                        getJSONData(topRatedURL);
                        break;

                    case 2:
                        isChoseFavorites = true;
                        getAllFavoritesFromSQL();

                }
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void getAllFavoritesFromSQL() {

        Log.i(MainActivity.TAG, "IN getAllFavoritesFromSQL");
        sqLiteDatabase = dbHelper.getWritableDatabase();
        popularMoviesList = new ArrayList<>();
        String checkQuery = "SELECT * FROM " + TABLE_NAME;
        Log.i(MainActivity.TAG, "checkQuery: " + checkQuery);

        Cursor cursor = getContentResolver().query(
                FavoritesProvider.CONTENT_URI, null, null, null, DBContract.DBEntry.COLUMN_NAME);
        //  Cursor cursor = sqLiteDatabase.rawQuery(checkQuery, null);
        Log.i(MainActivity.TAG, "cursor.count(): " + cursor.getCount());

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            PopularMovies popularMovies = new PopularMovies();
            popularMovies.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(DBContract.DBEntry.COLUMN_MOVIE_ID))));
            popularMovies.setTitle(cursor.getString(cursor.getColumnIndex(DBContract.DBEntry.COLUMN_NAME)));
            popularMovies.setReleaseDate(cursor.getString(cursor.getColumnIndex(DBContract.DBEntry.COLUMN_RELEASE_DATE)));
            popularMovies.setVoteAverage(cursor.getString(cursor.getColumnIndex(DBContract.DBEntry.COLUMN_RATING)));
            popularMovies.setOverview(cursor.getString(cursor.getColumnIndex(DBContract.DBEntry.COLUMN_OVERVIEW)));
            // popularMovies.setPosterPath(cursor.getString(cursor.getColumnIndex(DBContract.DBEntry.COLUMN_POSTER_PATH)));
            popularMovies.setPoster(cursor.getBlob(cursor.getColumnIndex(DBContract.DBEntry.COLUMN_POSTER)));
            popularMoviesList.add(popularMovies);
            Log.i(MainActivity.TAG, "popularMovies: " + popularMoviesList.get(cursor.getPosition()).getTitle() + ", "
                    + popularMoviesList.get(cursor.getPosition()).getId() +
                    ", " + popularMoviesList.get(cursor.getPosition()).getPosterPath());
        }
        Log.i(MainActivity.TAG, " popularMoviesList.size:  " + popularMoviesList.size());
        // movieRecyclerViewAdapter.notifyDataSetChanged();

        for (int i = 0; i < popularMoviesList.size(); i++) {
            PopularMovies popularMovies2 = popularMoviesList.get(i);
            Log.i(MainActivity.TAG, "popularMovies: " + popularMovies2.getTitle() + ", " + popularMovies2.getId() +
                    ", " + popularMovies2.getPosterPath());

        }

        movieRecyclerViewAdapter =
                new MovieRecyclerViewAdapter(MainActivity.this, popularMoviesList, this);
        rvMovies.setAdapter(movieRecyclerViewAdapter);
        movieRecyclerViewAdapter.notifyDataSetChanged();
        cursor.close();
        // sqLiteDatabase.close();
    }


    @Override
    public void onItemClick(int position) {
        Integer id = popularMoviesList.get(position).getId();
        Log.i(TAG, "In MainActivity: id" + id);
        String title = popularMoviesList.get(position).getTitle();
        String releaseDate = popularMoviesList.get(position).getReleaseDate();
        String voteAverage = popularMoviesList.get(position).getVoteAverage();
        Log.i(TAG, "In MainActivity: voteAverage " + voteAverage);
        String overview = popularMoviesList.get(position).getOverview();
        String posterPath = popularMoviesList.get(position).getPosterPath();
        Log.i(TAG, "In MainActivity: posterPath " + posterPath);

        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("releaseDate", releaseDate);
        intent.putExtra("voteAverage", voteAverage);
        intent.putExtra("overview", overview);
        intent.putExtra("posterPath", posterPath);
        intent.putExtra("title", title);

        Picasso.with(getApplicationContext())
                .load(posterPath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        posterImage = bitmap;
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
        Log.i(TAG, "In onItemClick: posterImage - " + posterImage.toString());
        intent.putExtra("posterImage", posterImage);
        startActivity(intent);
    }
}
