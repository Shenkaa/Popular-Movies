package eu.ramich.popularmovies.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.ramich.popularmovies.R;
import eu.ramich.popularmovies.adapter.MovieAdapter;
import eu.ramich.popularmovies.data.MovieContract;
import eu.ramich.popularmovies.data.PopularMoviesPreferences;
import eu.ramich.popularmovies.sync.PopularMoviesIntentService;
import eu.ramich.popularmovies.sync.PopularMoviesSyncTasks;
import eu.ramich.popularmovies.sync.PopularMoviesSyncUtils;
import eu.ramich.popularmovies.utilities.NetworkUtils;
import eu.ramich.popularmovies.utilities.PopularMoviesUtils;

public class MoviesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private MovieAdapter mMovieAdapter;
    private Snackbar snackbar;

    @BindView(R.id.rv_movies) RecyclerView mRecyclerView;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.tv_sorted_by) TextView mSortOption;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;

    public static final String[] MAIN_MOVIES_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_POSTER_PATH = 1;

    public static boolean IS_MOVIE_LOADED = false;
    private int mPosition = RecyclerView.NO_POSITION;
    private static final int ID_MOVIES_LOADER = 12;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt("position");
            IS_MOVIE_LOADED = savedInstanceState.getBoolean("is_movie_loaded");
        }

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.setLayoutManager(
                new GridLayoutManager(getActivity(),
                        PopularMoviesUtils.getOptimalColumnCount()
                )
        );
        mRecyclerView.setHasFixedSize(true);

        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (PopularMoviesPreferences.getSortOrderKey(getActivity())
                                .equals(getString(R.string.sort_order__favorite_key))) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            return;
                        }

                        loadData();
                    }
                }
        );

        mSortOption.setText(PopularMoviesPreferences.getSortOrderLabel(getActivity()));
        snackbar = Snackbar.make(getActivity().findViewById(R.id.rv_movies),
                getString(R.string.sb_offline), Snackbar.LENGTH_INDEFINITE);

        getActivity().getSupportLoaderManager().initLoader(ID_MOVIES_LOADER, null, this);

        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);

        if (NetworkUtils.isNetworkStatusAvialable(getActivity())) {
            showLoading();
            PopularMoviesSyncUtils.initialize(getActivity());
        }
    }

    public void loadData() {
        Intent intent = new Intent(getActivity(), PopularMoviesIntentService.class);
        intent.setAction(PopularMoviesSyncTasks.ACTION_SYNC_MOVIE_LIST);
        getActivity().startService(intent);

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case ID_MOVIES_LOADER:
                Uri moviesQueryUri = MovieContract.MovieEntry.CONTENT_URI;

                String sortOrder = PopularMoviesPreferences.getSortOrderKey(getActivity());
                String selection;
                String[] selectionArgs;
                if (sortOrder.equals(getString(R.string.sort_order__favorite_key))) {
                    selection = MovieContract.MovieEntry.COLUMN_FAVORITE + " = ?";
                    selectionArgs = new String[]{"1"};
                } else {
                    selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " IN " +
                            "(SELECT " + MovieContract.MovieExtraEntry.COLUMN_MOVIE_ID + " " +
                            "FROM " + MovieContract.MovieExtraEntry.TABLE_NAME + " " +
                            "WHERE " + MovieContract.MovieExtraEntry.COLUMN_SORT_ORDER +
                            " = '" + sortOrder + "'" +
                            ")";
                    selectionArgs = new String[]{};
                }

                return new CursorLoader(getActivity(),
                        moviesQueryUri,
                        MAIN_MOVIES_PROJECTION,
                        selection,
                        selectionArgs,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
            mRecyclerView.smoothScrollToPosition(mPosition);
        }

        if (!NetworkUtils.isNetworkStatusAvialable(getActivity())) {
            snackbar.setAction(getString(R.string.sb_dismiss), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        } else {
            if (snackbar.isShown()) snackbar.dismiss();
        }

        String sortOrder = PopularMoviesPreferences.getSortOrderKey(getActivity());
        if (data.getCount() != 0 || sortOrder.equals(getString(R.string.sort_order__favorite_key)))
            showMovieDataView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    @Override
    public void onClick(int movieId) {
        Uri uriForMovieClicked = MovieContract.MovieEntry.buildMovieUriWithId(movieId);

        if (PopularMoviesUtils.isTablet()) {
            MovieDetailsFragment details = (MovieDetailsFragment)
                    getFragmentManager().findFragmentById(R.id.f_movie_detail);
            if (details == null || details.getShownMovieUri() != uriForMovieClicked) {
                details = MovieDetailsFragment.newInstance(uriForMovieClicked);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.f_movie_detail, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        } else {
            Intent movieDetailIntent = new Intent(getActivity(), MovieDetailsActivity.class);
            movieDetailIntent.setData(uriForMovieClicked);
            startActivity(movieDetailIntent);
        }
    }

    private void showMovieDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_order:
                SortOrderFragment dialog = new SortOrderFragment();
                dialog.show(getActivity().getFragmentManager(), "SortOrderFragment");
                break;

            case R.id.action_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                loadData();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", mPosition);
        outState.putBoolean("is_movie_loaded", IS_MOVIE_LOADED);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (isAdded() && key.equals(getString(R.string.sort_order_key))) {
            mSortOption.setText(PopularMoviesPreferences.getSortOrderLabel(getActivity()));
            mPosition = RecyclerView.NO_POSITION;
            getActivity().getSupportLoaderManager().restartLoader(ID_MOVIES_LOADER, null, this);
        }
    }

}
