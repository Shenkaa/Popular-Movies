package eu.ramich.popularmovies.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.ramich.popularmovies.R;
import eu.ramich.popularmovies.adapter.ReviewAdapter;
import eu.ramich.popularmovies.adapter.TrailerAdapter;
import eu.ramich.popularmovies.data.MovieContract;
import eu.ramich.popularmovies.sync.PopularMoviesIntentService;
import eu.ramich.popularmovies.sync.PopularMoviesSyncTasks;
import eu.ramich.popularmovies.utilities.NetworkUtils;
import eu.ramich.popularmovies.utilities.PopularMoviesUtils;

public class MovieDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        TrailerAdapter.TrailerAdapterOnClickHandler,
        ReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String TAG = MovieDetailsFragment.class.getSimpleName();

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    @BindView(R.id.rv_trailer) RecyclerView mTrailerRecyclerView;
    @BindView(R.id.rv_review) RecyclerView mReviewRecyclerView;

    @BindView(R.id.iv_movie_backdrop) ImageView movieBackdrop;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout cToolbarLayout;
    @BindView(R.id.app_bar) AppBarLayout appBarLayout;

    @BindView(R.id.tv_movie_title_detail) TextView movieTitle;
    @BindView(R.id.tv_movie_release_date) TextView movieRelease;
    @BindView(R.id.tv_movie_overview) TextView movieOverview;
    @BindView(R.id.iv_movie_thumbnail) ImageView movieThumbnail;
    @BindView(R.id.tv_movie_average_rating) TextView movieAverageRating;

    @BindView(R.id.fab_fav) FloatingActionButton fav;

    public static final String[] DETAIL_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_FAVORITE
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_ORIGINAL_TITLE = 1;
    public static final int INDEX_TITLE = 2;
    public static final int INDEX_BACKDROP_PATH = 3;
    public static final int INDEX_POSTER_PATH = 4;
    public static final int INDEX_VOTE_AVERAGE = 5;
    public static final int INDEX_RELEASE_DATE = 6;
    public static final int INDEX_OVERVIEW = 7;
    public static final int INDEX_FAVOURITE = 8;

    public static final String[] DETAIL_TRAILER_PROJECTION = {
            MovieContract.TrailerEntry.COLUMN_KEY,
            MovieContract.TrailerEntry.COLUMN_NAME
    };

    public static final int INDEX_TRAILER_KEY = 0;
    public static final int INDEX_TRAILER_NAME = 1;

    public static final String[] DETAIL_REVIEW_PROJECTION = {
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT
    };

    public static final int INDEX_REVIEW_AUTHOR = 0;
    public static final int INDEX_REVIEW_CONTENT = 1;

    private Uri mUri;
    private String mTitle;

    private static final int ID_MOVIE_LOADER = 7;
    private static final int ID_TRAILER_LOADER = 8;
    private static final int ID_REVIEWS_LOADER = 9;


    public static MovieDetailsFragment newInstance(Uri uriForMovie) {
        MovieDetailsFragment f = new MovieDetailsFragment();

        Bundle args = new Bundle();
        args.putParcelable("uri", uriForMovie);
        f.setArguments(args);

        return f;
    }

    public Uri getShownMovieUri() {
        return getArguments().getParcelable("uri");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mUri = savedInstanceState.getParcelable("uri");
        } else if (PopularMoviesUtils.isTablet()) {
            mUri = getArguments().getParcelable("uri");
        } else {
            mUri = getActivity().getIntent().getData();
        }

        if (mUri == null)
            throw new NullPointerException("URI for MovieDetailsActivity cannot be null");

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (appBarLayout.getTotalScrollRange() * -1 == verticalOffset) {
                    cToolbarLayout.setTitle(mTitle);
                } else {
                    cToolbarLayout.setTitle(" ");
                }
            }
        });

        if (!PopularMoviesUtils.isTablet()) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent favIntent = new Intent(getActivity(), PopularMoviesIntentService.class);
                String message;

                if (fav.getTag().equals(R.drawable.ic_favorite_border)) {
                    favIntent.setAction(PopularMoviesSyncTasks.ACTION_ADD_TO_FAV_MOVIES);
                    message = getString(R.string.favorite_add);
                } else if (fav.getTag().equals(R.drawable.ic_favorite)) {
                    favIntent.setAction(PopularMoviesSyncTasks.ACTION_REMOVE_FROM_FAV_MOVIES);
                    message = getString(R.string.favorite_remove);
                } else {
                    return;
                }

                favIntent.putExtra("id", mUri.getLastPathSegment());
                getActivity().startService(favIntent);

                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
            }
        });

        mTrailerAdapter = new TrailerAdapter(this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        mTrailerRecyclerView.setHasFixedSize(true);

        mReviewAdapter = new ReviewAdapter(this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mReviewRecyclerView.setHasFixedSize(true);

        Intent intent = new Intent(getActivity(), PopularMoviesIntentService.class);
        intent.setAction(PopularMoviesSyncTasks.ACTION_SYNC_VIDEO_LIST);
        intent.putExtra("id", mUri.getLastPathSegment());
        getActivity().startService(intent);

        Intent reviewIntent = new Intent(getActivity(), PopularMoviesIntentService.class);
        reviewIntent.setAction(PopularMoviesSyncTasks.ACTION_SYNC_REVIEW_LIST);
        reviewIntent.putExtra("id", mUri.getLastPathSegment());
        getActivity().startService(reviewIntent);

        getLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
        getLoaderManager().initLoader(ID_TRAILER_LOADER, null, this);
        getLoaderManager().initLoader(ID_REVIEWS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection;
        String[] selectionArgs;

        switch (id) {
            case ID_MOVIE_LOADER:
                return new CursorLoader(getActivity(),
                        mUri,
                        DETAIL_MOVIE_PROJECTION,
                        null,
                        null,
                        null);

            case ID_TRAILER_LOADER:
                selection = MovieContract.TrailerEntry.trailerSelection();
                selectionArgs = MovieContract.TrailerEntry
                        .trailerSelectionArgs(mUri.getLastPathSegment());

                return new CursorLoader(getActivity(),
                        MovieContract.TrailerEntry.CONTENT_URI,
                        DETAIL_TRAILER_PROJECTION,
                        selection,
                        selectionArgs,
                        null);

            case ID_REVIEWS_LOADER:
                selection = MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?";
                selectionArgs = new String[]{mUri.getLastPathSegment()};

                return new CursorLoader(getActivity(),
                        MovieContract.ReviewEntry.CONTENT_URI,
                        DETAIL_REVIEW_PROJECTION,
                        selection,
                        selectionArgs,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData)
            return;

        switch (loader.getId()) {
            case ID_MOVIE_LOADER:
                mTitle = data.getString(INDEX_TITLE);
                movieTitle.setText(data.getString(INDEX_TITLE));

                movieOverview.setText(data.getString(INDEX_OVERVIEW));
                movieRelease.setText(PopularMoviesUtils.normalizeDate(data.getString(INDEX_RELEASE_DATE)));
                movieAverageRating.setText(String.format(Locale.getDefault(),
                        "%.1f/10", data.getDouble(INDEX_VOTE_AVERAGE)));
                if (data.getInt(INDEX_FAVOURITE) == 1) {
                    fav.setImageResource(R.drawable.ic_favorite);
                    fav.setTag(R.drawable.ic_favorite);
                } else {
                    fav.setImageResource(R.drawable.ic_favorite_border);
                    fav.setTag(R.drawable.ic_favorite_border);
                }

                Picasso.with(getActivity())
                        .load(NetworkUtils.buildPosterUrl(data.getString(INDEX_POSTER_PATH),
                                PopularMoviesUtils.getPosterWidth(true)).toString())
                        .into(movieThumbnail);

                Picasso.with(getActivity())
                        .load(NetworkUtils.buildPosterUrl(data.getString(INDEX_BACKDROP_PATH),
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
                                Log.e(TAG, "Error occured");
                            }
                        });

                break;

            case ID_TRAILER_LOADER:
                mTrailerAdapter.swapCursor(data);
                break;

            case ID_REVIEWS_LOADER:
                mReviewAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case ID_TRAILER_LOADER:
                mTrailerAdapter.swapCursor(null);
                break;

            case ID_REVIEWS_LOADER:
                mReviewAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onClick(String trailerKey) {
        Uri trailerUri = NetworkUtils.buildTrailerUri(trailerKey);
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, trailerUri);

        if (trailerIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(trailerIntent);
        }
    }

    @Override
    public void onClick(ReviewAdapter.ReviewAdapterViewHolder rA) {
        if (!rA.isExpanded) {
            rA.reviewContent.setMaxLines(Integer.MAX_VALUE);
            rA.reviewContent.setEllipsize(null);
            rA.reviewArrow.setVisibility(View.VISIBLE);
        } else {
            rA.reviewContent.setMaxLines(3);
            rA.reviewContent.setEllipsize(TextUtils.TruncateAt.END);
            rA.reviewArrow.setVisibility(View.GONE);
        }
        rA.isExpanded = !rA.isExpanded;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("uri", mUri);
    }
}
