package com.artamonov.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    ImageView ivDetailPoster;
    TextView tvOverview;
    TextView tvVoteAverage;
    TextView tvReleaseDate;
    TextView tvTitle;
    Context context;


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

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
