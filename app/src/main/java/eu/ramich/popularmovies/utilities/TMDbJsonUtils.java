package eu.ramich.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.ramich.popularmovies.data.MovieContract.MovieEntry;
import eu.ramich.popularmovies.data.MovieContract.MovieExtraEntry;
import eu.ramich.popularmovies.data.MovieContract.ReviewEntry;
import eu.ramich.popularmovies.data.MovieContract.TrailerEntry;
import eu.ramich.popularmovies.data.PopularMoviesPreferences;

public class TMDbJsonUtils {

    private static final String TAG = TMDbJsonUtils.class.getSimpleName();

    private static final String TMDB_RESULT = "results";
    private static final String TMDB_STATUS_CODE = "status_code";
    private static final String TMDB_STATUS_MESSAGE = "status_message";

    private static final String TMDB_ID = "id";


    public static List<ContentValues[]> getMovieContentValuesFromJson(Context context,
                                                                     String movieJsonStr,
                                                                     String sortOrder)
            throws JSONException {

        final String TMDB_TITLE = "title";
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_ORIGINAL_LANGUAGE = "original_language";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_VOTE_COUNT = "vote_count";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_BACKDROP_PATH = "backdrop_path";


        JSONObject movieJson = new JSONObject(movieJsonStr);

        if (movieJson.has(TMDB_STATUS_CODE)) {
            Log.e(TAG, movieJson.getString(TMDB_STATUS_MESSAGE));

            return null;
        }

        JSONArray jsonMovieArray = movieJson.getJSONArray(TMDB_RESULT);
        List<ContentValues[]> moviesList = new ArrayList<>();
        ContentValues[] movieContentValues = new ContentValues[jsonMovieArray.length()];
        ContentValues[] movieExtraContentValues = new ContentValues[jsonMovieArray.length()];

        for (int i = 0; i < jsonMovieArray.length(); i++) {
            JSONObject movieData = jsonMovieArray.getJSONObject(i);

            int movieId = movieData.getInt(TMDB_ID);
            if (sortOrder == null) sortOrder = PopularMoviesPreferences.getSortOrderKey(context);

            ContentValues mV = new ContentValues();
            mV.put(MovieEntry.COLUMN_MOVIE_ID, movieId);
            mV.put(MovieEntry.COLUMN_TITLE, movieData.getString(TMDB_TITLE));
            mV.put(MovieEntry.COLUMN_ORIGINAL_TITLE, movieData.getString(TMDB_ORIGINAL_TITLE));
            mV.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movieData.getString(TMDB_ORIGINAL_LANGUAGE));
            mV.put(MovieEntry.COLUMN_RELEASE_DATE, movieData.getString(TMDB_RELEASE_DATE));
            mV.put(MovieEntry.COLUMN_VOTE_AVERAGE, movieData.getDouble(TMDB_VOTE_AVERAGE));
            mV.put(MovieEntry.COLUMN_VOTE_COUNT, movieData.getInt(TMDB_VOTE_COUNT));
            mV.put(MovieEntry.COLUMN_POPULARITY, movieData.getDouble(TMDB_POPULARITY));
            mV.put(MovieEntry.COLUMN_OVERVIEW, movieData.getString(TMDB_OVERVIEW));
            mV.put(MovieEntry.COLUMN_POSTER_PATH, movieData.getString(TMDB_POSTER_PATH));
            mV.put(MovieEntry.COLUMN_BACKDROP_PATH, movieData.getString(TMDB_BACKDROP_PATH));

            movieContentValues[i] = mV;


            ContentValues mEV = new ContentValues();
            mEV.put(MovieExtraEntry.COLUMN_MOVIE_ID, movieId);
            mEV.put(MovieExtraEntry.COLUMN_SORT_ORDER, sortOrder);
            mEV.put(MovieExtraEntry.COLUMN_CREATED, PopularMoviesUtils.getCurrentTimestamp());

            movieExtraContentValues[i] = mEV;
        }

        moviesList.add(movieContentValues);
        moviesList.add(movieExtraContentValues);

        return moviesList;
    }

    public static ContentValues[] getVideoContentValuesFromJson(String videoJsonStr)
        throws JSONException {

        final String TMDB_ISO_639_1 = "iso_639_1";
        final String TMDB_ISO_3166_1 = "iso_3166_1";
        final String TMDB_KEY = "key";
        final String TMDB_NAME = "name";
        final String TMDB_SITE = "site";
        final String TMDB_SIZE = "size";
        final String TMDB_TYPE = "type";


        JSONObject videoJson = new JSONObject(videoJsonStr);

        if (videoJson.has(TMDB_STATUS_CODE)) {
            Log.e(TAG, videoJson.getString(TMDB_STATUS_MESSAGE));

            return null;
        }

        JSONArray jsonVideoArray = videoJson.getJSONArray(TMDB_RESULT);
        ContentValues[] videoContentValues = new ContentValues[jsonVideoArray.length()];

        for (int i = 0; i < jsonVideoArray.length(); i++) {
            JSONObject videoData = jsonVideoArray.getJSONObject(i);

            ContentValues vV = new ContentValues();
            vV.put(TrailerEntry.COLUMN_TRAILER_ID, videoData.getString(TMDB_ID));
            vV.put(TrailerEntry.COLUMN_ISO_639_1, videoData.getString(TMDB_ISO_639_1));
            vV.put(TrailerEntry.COLUMN_ISO_3166_1, videoData.getString(TMDB_ISO_3166_1));
            vV.put(TrailerEntry.COLUMN_KEY, videoData.getString(TMDB_KEY));
            vV.put(TrailerEntry.COLUMN_NAME, videoData.getString(TMDB_NAME));
            vV.put(TrailerEntry.COLUMN_SITE, videoData.getString(TMDB_SITE));
            vV.put(TrailerEntry.COLUMN_SIZE, videoData.getInt(TMDB_SIZE));
            vV.put(TrailerEntry.COLUMN_TYPE, videoData.getString(TMDB_TYPE));
            vV.put(TrailerEntry.COLUMN_MOVIE_ID, videoJson.getInt(TMDB_ID));

            videoContentValues[i] = vV;
        }

        return videoContentValues;
    }

    public static ContentValues[] getReviewContentValuesFromJson(String reviewJsonStr)
        throws JSONException {

        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";
        final String TMDB_URL = "url";


        JSONObject reviewJson = new JSONObject(reviewJsonStr);

        if (reviewJson.has(TMDB_STATUS_CODE)) {
            Log.e(TAG, reviewJson.getString(TMDB_STATUS_MESSAGE));

            return null;
        }

        JSONArray jsonReviewArray = reviewJson.getJSONArray(TMDB_RESULT);
        ContentValues[] reviewContentValues = new ContentValues[jsonReviewArray.length()];

        for (int i = 0; i < jsonReviewArray.length(); i++) {
            JSONObject reviewData = jsonReviewArray.getJSONObject(i);

            ContentValues rV = new ContentValues();
            rV.put(ReviewEntry.COLUMN_REVIEW_ID, reviewData.getString(TMDB_ID));
            rV.put(ReviewEntry.COLUMN_AUTHOR, reviewData.getString(TMDB_AUTHOR));
            rV.put(ReviewEntry.COLUMN_CONTENT, reviewData.getString(TMDB_CONTENT));
            rV.put(ReviewEntry.COLUMN_URL, reviewData.getString(TMDB_URL));
            rV.put(ReviewEntry.COLUMN_MOVIE_ID, reviewJson.getInt(TMDB_ID));

            reviewContentValues[i] = rV;
        }

        return reviewContentValues;
    }
}
