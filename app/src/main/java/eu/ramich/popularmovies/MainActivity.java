package eu.ramich.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import eu.ramich.popularmovies.data.model.Movie;
import eu.ramich.popularmovies.utilities.NetworkUtils;
import eu.ramich.popularmovies.utilities.PopularMoviesUtils;
import eu.ramich.popularmovies.utilities.TMDbJsonUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private MovieAdapter mMovieAdapter;

    @BindView(R.id.rv_movies) RecyclerView mRecyclerView;
    @BindView(R.id.tv_sorted_by) TextView mSortOption;
    @BindView(R.id.tv_error_message) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;

    @BindString(R.string.sort_order_popular) String sortOption;
    private String currentSortOption = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.setLayoutManager(
                new GridLayoutManager(this,
                PopularMoviesUtils.getOptimalColumnCount(
                        getResources().getDimension(R.dimen.poster_width))
                )
        );
        mRecyclerView.setHasFixedSize(true);

        mSortOption.setText(R.string.sort_order_f_popular);

        if (savedInstanceState != null) {
            mMovieAdapter.setMovieList((List) savedInstanceState.getParcelableArrayList("movieList"));
            sortOption = savedInstanceState.getString("sortOption");
            currentSortOption = savedInstanceState.getString("sortOption");
            mSortOption.setText(savedInstanceState.getString("sortOptionTitle"));
        }

        loadMovieData();
    }

    private void loadMovieData() {
        if (!sortOption.equals(currentSortOption)) {
            showErrorMessage(false);
            new FetchMovieTask().execute(sortOption);

            currentSortOption = sortOption;
        }
    }

    private void showErrorMessage(boolean error) {
        if (error) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        } else {
            mErrorMessageDisplay.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_order_top_rated:
                sortOption = getString(R.string.sort_order_top_rated);
                mSortOption.setText(R.string.sort_order_f_top_rated);
                loadMovieData();
                return true;
            case R.id.sort_order_popular:
                sortOption = getString(R.string.sort_order_popular);
                mSortOption.setText(R.string.sort_order_f_popular);
                loadMovieData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movieList", (ArrayList) mMovieAdapter.getMovieList());
        outState.putString("sortOption", sortOption);
        outState.putString("sortOptionTitle", mSortOption.getText().toString());
        super.onSaveInstanceState(outState);
    }


    public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);

            if (mMovieAdapter.getMovieList() != null) {
                mMovieAdapter.setMovieList(null);
            }
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String search = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(search);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpsUrl(movieRequestUrl);

                List<Movie> simpleJsonMovieData = TMDbJsonUtils
                        .getSimpleMovieListFromJson(MainActivity.this, jsonMovieResponse);

                return simpleJsonMovieData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                mMovieAdapter.setMovieList(movies);
            } else {
                showErrorMessage(true);
            }
        }
    }

}
