package com.udacity.shenka.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.shenka.popularmovies.data.model.Movie;
import com.udacity.shenka.popularmovies.utilities.NetworkUtils;
import com.udacity.shenka.popularmovies.utilities.PopularMoviesUtils;

import java.util.Locale;

import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView movieBackdrop = ButterKnife.findById(this, R.id.iv_movie_backdrop);
        final CollapsingToolbarLayout cToolbarLayout = ButterKnife.findById(this,
                R.id.toolbar_layout);

        TextView movieTitle = ButterKnife.findById(this, R.id.tv_movie_title_detail);
        TextView movieRelease = ButterKnife.findById(this, R.id.tv_movie_release_date);
        TextView movieOverview = ButterKnife.findById(this, R.id.tv_movie_overview);
        ImageView movieThumbnail = ButterKnife.findById(this, R.id.iv_movie_thumbnail);
        TextView movieAverageRating = ButterKnife.findById(this, R.id.tv_movie_average_rating);

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra("movie")) {
                Movie movie = intent.getParcelableExtra("movie");

                setTitle(movie.getTitle());

                movieTitle.setText(movie.getOriginalTitle());
                movieOverview.setText(movie.getOverview());
                movieRelease.setText(PopularMoviesUtils.normalizeDate(movie.getReleaseDate()));
                movieAverageRating.setText(String.format(Locale.getDefault(),
                        "%.1f/10", movie.getVoteAverage()));

                Picasso.with(this)
                        .load(NetworkUtils.buildPosterURL(movie.getPosterPath(),
                                PopularMoviesUtils.getPosterWidth(true)).toString())
                        .into(movieThumbnail);

                Picasso.with(this)
                        .load(NetworkUtils.buildPosterURL(movie.getBackdropPath(),
                                PopularMoviesUtils.getPosterWidth(false)).toString())
                        .into(movieBackdrop, new Callback() {

                            @Override
                            public void onSuccess() {
                                Bitmap bitmap = ((BitmapDrawable) movieBackdrop.getDrawable())
                                        .getBitmap();

                                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        Palette.Swatch swatch = palette.getDominantSwatch();
                                        if (swatch == null) {
                                            return;
                                        }
                                        int color = swatch.getRgb();
                                        cToolbarLayout.setBackgroundColor(color);
                                        cToolbarLayout.setStatusBarScrimColor(
                                                palette.getDarkVibrantColor(color));
                                        cToolbarLayout.setContentScrimColor(
                                                palette.getVibrantColor(color));
                                    }
                                });
                            }

                            @Override
                            public void onError() {
                                Log.e("something", "error");
                            }
                        });
            }
        }
    }
}
