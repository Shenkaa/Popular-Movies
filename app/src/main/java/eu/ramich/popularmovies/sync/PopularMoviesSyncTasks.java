package eu.ramich.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import java.net.URL;
import java.util.List;

import eu.ramich.popularmovies.data.MovieContract.MovieEntry;
import eu.ramich.popularmovies.data.MovieContract.MovieExtraEntry;
import eu.ramich.popularmovies.data.MovieContract.MovieFavEntry;
import eu.ramich.popularmovies.data.MovieContract.ReviewEntry;
import eu.ramich.popularmovies.data.MovieContract.TrailerEntry;
import eu.ramich.popularmovies.data.PopularMoviesPreferences;
import eu.ramich.popularmovies.utilities.NetworkUtils;
import eu.ramich.popularmovies.utilities.TMDbJsonUtils;

public class PopularMoviesSyncTasks {

    public static final String ACTION_INITIAL_SYNC = "initial-sync";
    public static final String ACTION_SYNC_MOVIE_LIST = "sync-movie-list";
    public static final String ACTION_SYNC_MOVIE_DATA = "sync-movie-data";
    public static final String ACTION_SYNC_VIDEO_LIST = "sync-video-list";
    public static final String ACTION_SYNC_REVIEW_LIST = "sync-review-list";

    public static final String ACTION_ADD_TO_FAV_MOVIES = "add-to-fav-movies";
    public static final String ACTION_REMOVE_FROM_FAV_MOVIES = "remove-from-fav-movies";


    public static void executeTask(Context context, String action, String id) {
        switch (action) {
            case ACTION_INITIAL_SYNC:
                initialMoviesSync(context);
                break;

            case ACTION_SYNC_MOVIE_LIST:
                syncMovies(context, null);
                break;

            case ACTION_SYNC_VIDEO_LIST:
                syncVideoList(context, id);
                break;

            case ACTION_SYNC_REVIEW_LIST:
                syncReviewList(context, id);
                break;

            case ACTION_ADD_TO_FAV_MOVIES:
                addToFavMovies(context, id);
                break;

            case ACTION_REMOVE_FROM_FAV_MOVIES:
                removeFromFavMovies(context, id);
                break;
        }
    }


    synchronized private static void initialMoviesSync(Context context) {

        String[] sortOrderList = PopularMoviesPreferences.getAllSortOrders(context);
        for (String sortOrder: sortOrderList) {
            if (sortOrder.equals("favorite")) return;

            syncMovies(context, sortOrder);
        }
    }

    synchronized private static void syncMovies(Context context, String sortOrder) {
        if (sortOrder == null)
            sortOrder = PopularMoviesPreferences.getSortOrderKey(context);

        if (sortOrder.equals("favorite")) return;

        try {
            URL movieRequestUrl = NetworkUtils
                    .builMoviesdUrl(sortOrder);

            String jsonMovieResponse = NetworkUtils.getResponseFromHttpsUrl(movieRequestUrl);

            List<ContentValues[]> moviesList = TMDbJsonUtils
                    .getMovieContentValuesFromJson(context, jsonMovieResponse, sortOrder);

            if (moviesList != null && moviesList.size() == 2) {
                ContentValues[] movieValues = moviesList.get(0);
                ContentValues[] movieExtraValues = moviesList.get(1);

                if ((movieValues != null && movieValues.length != 0) &&
                        (movieExtraValues != null && movieExtraValues.length != 0)) {
                    ContentResolver contentResolver = context.getContentResolver();

                    String selection = MovieEntry.COLUMN_MOVIE_ID + " IN " +
                            "(SELECT " + MovieExtraEntry.COLUMN_MOVIE_ID + " " +
                            "FROM " + MovieExtraEntry.TABLE_NAME + " " +
                            "WHERE " + MovieExtraEntry.COLUMN_SORT_ORDER + " = '" + sortOrder + "') " +
                            "AND " + MovieEntry.COLUMN_MOVIE_ID + " NOT IN " +
                            "(SELECT " + MovieFavEntry.COLUMN_MOVIE_ID + " " +
                            "FROM " + MovieFavEntry.TABLE_NAME + ")";
                    String[] selectionArgs = {};

                    // Trailer
                    contentResolver.delete(
                            TrailerEntry.CONTENT_URI,
                            selection,
                            selectionArgs
                    );

                    // Reviews
                    contentResolver.delete(
                            ReviewEntry.CONTENT_URI,
                            selection,
                            selectionArgs
                    );

                    String selection2 = MovieEntry.COLUMN_MOVIE_ID + " IN " +
                            "(SELECT " + MovieExtraEntry.COLUMN_MOVIE_ID + " " +
                            "FROM " + MovieExtraEntry.TABLE_NAME + " " +
                            "WHERE " + MovieExtraEntry.COLUMN_SORT_ORDER + " = '" + sortOrder + "') " +
                            "AND " + MovieEntry.COLUMN_FAVORITE + " = ?";
                    String[] selectionArgs2 = {"0"};

                    // Movies
                    contentResolver.delete(
                            MovieEntry.CONTENT_URI,
                            selection2,
                            selectionArgs2
                    );

                    String selection3 = MovieExtraEntry.COLUMN_SORT_ORDER + " = ?";
                    String[] selectionArgs3 = {sortOrder};

                    // MovieExtra
                    contentResolver.delete(
                            MovieExtraEntry.CONTENT_URI,
                            selection3,
                            selectionArgs3
                    );

                    contentResolver.bulkInsert(
                            MovieEntry.CONTENT_URI,
                            movieValues
                    );

                    contentResolver.bulkInsert(
                            MovieExtraEntry.CONTENT_URI,
                            movieExtraValues
                    );

                    ContentValues movieFavValues = new ContentValues();
                    movieFavValues.put(MovieEntry.COLUMN_FAVORITE, 1);

                    String selectionUpdate = MovieEntry.COLUMN_MOVIE_ID + " IN " +
                            "(SELECT " + MovieFavEntry.COLUMN_MOVIE_ID + " " +
                            "FROM " + MovieFavEntry.TABLE_NAME + ")";

                    contentResolver.update(
                            MovieEntry.CONTENT_URI,
                            movieFavValues,
                            selectionUpdate,
                            null
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized private static void syncVideoList(Context context, String id) {

        try {
            URL videoRequestUrl = NetworkUtils
                    .buildVideoUrl(id);

            String jsonVideoResponse = NetworkUtils.getResponseFromHttpsUrl(videoRequestUrl);

            ContentValues[] videoValues = TMDbJsonUtils
                    .getVideoContentValuesFromJson(jsonVideoResponse);

            if (videoValues != null && videoValues.length != 0) {
                ContentResolver contentResolver = context.getContentResolver();

                String selection = TrailerEntry.COLUMN_MOVIE_ID + " = ?";
                String[] selectionArgs = {id};

                contentResolver.delete(
                        TrailerEntry.CONTENT_URI,
                        selection,
                        selectionArgs
                );

                contentResolver.bulkInsert(
                        TrailerEntry.CONTENT_URI,
                        videoValues
                );

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized private static void syncReviewList(Context context, String id) {

        try {
            URL reviewRequestUrl = NetworkUtils
                    .buildReviewUrl(id);

            String jsonReviewResponse = NetworkUtils.getResponseFromHttpsUrl(reviewRequestUrl);

            ContentValues[] reviewValues = TMDbJsonUtils
                    .getReviewContentValuesFromJson(jsonReviewResponse);

            if (reviewValues != null && reviewValues.length != 0) {
                ContentResolver contentResolver = context.getContentResolver();

                String selection = ReviewEntry.COLUMN_MOVIE_ID + " = ?";
                String[] selectionArgs = {id};

                contentResolver.delete(
                        ReviewEntry.CONTENT_URI,
                        selection,
                        selectionArgs
                );

                contentResolver.bulkInsert(
                        ReviewEntry.CONTENT_URI,
                        reviewValues
                );

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized private static void addToFavMovies(Context context, String id) {

        try {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry.COLUMN_FAVORITE, 1);
            ContentValues movieFavValues = new ContentValues();
            movieFavValues.put(MovieFavEntry.COLUMN_MOVIE_ID, id);

            String selection = MovieEntry.COLUMN_MOVIE_ID + " = ?";
            String[] selectionArgs = {id};

            ContentResolver contentResolver = context.getContentResolver();

            contentResolver.update(
                    MovieEntry.buildMovieUriWithId(Integer.valueOf(id)),
                    movieValues,
                    selection,
                    selectionArgs
            );

            contentResolver.insert(
                    MovieFavEntry.CONTENT_URI,
                    movieFavValues
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized private static void removeFromFavMovies(Context context, String id) {

        try {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry.COLUMN_FAVORITE, 0);

            String selection = MovieEntry.COLUMN_MOVIE_ID + " = ?";
            String[] selectionArgs = {id};

            ContentResolver contentResolver = context.getContentResolver();

            contentResolver.update(
                    MovieEntry.buildMovieUriWithId(Integer.valueOf(id)),
                    movieValues,
                    selection,
                    selectionArgs
            );

            contentResolver.delete(
                    MovieFavEntry.CONTENT_URI,
                    selection,
                    selectionArgs
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
