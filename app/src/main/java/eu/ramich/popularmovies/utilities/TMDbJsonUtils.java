package eu.ramich.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.ramich.popularmovies.data.MovieContract;
import eu.ramich.popularmovies.data.PopularMoviesPreferences;

public class TMDbJsonUtils {

    private static final String TAG = TMDbJsonUtils.class.getSimpleName();

    private static final String TMDB_RESULT = "results";
    private static final String TMDB_STATUS_CODE = "status_code";
    private static final String TMDB_STATUS_MESSAGE = "status_message";


    public static List<ContentValues[]> getMovieContentValuesFromJson(Context context,
                                                                     String movieJsonStr,
                                                                     String sortOrder)
            throws JSONException {

        final String TMDB_ID = "id";
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
            String title = movieData.getString(TMDB_TITLE);
            String originalTitle = movieData.getString(TMDB_ORIGINAL_TITLE);
            String originalLanguage = movieData.getString(TMDB_ORIGINAL_LANGUAGE);
            String releaseDate = movieData.getString(TMDB_RELEASE_DATE);
            Double voteAvarage = movieData.getDouble(TMDB_VOTE_AVERAGE);
            int voteCount = movieData.getInt(TMDB_VOTE_COUNT);
            Double popularity = movieData.getDouble(TMDB_POPULARITY);
            String overview = movieData.getString(TMDB_OVERVIEW);
            String posterPath = movieData.getString(TMDB_POSTER_PATH);
            String backdropPath = movieData.getString(TMDB_BACKDROP_PATH);

            if (sortOrder == null) sortOrder = PopularMoviesPreferences.getSortOrderKey(context);
            String curTimestamp = PopularMoviesUtils.getCurrentTimestamp();

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, originalLanguage);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAvarage);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, voteCount);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);

            movieContentValues[i] = movieValues;


            ContentValues movieExtraValues = new ContentValues();
            movieExtraValues.put(MovieContract.MovieExtraEntry.COLUMN_MOVIE_ID, movieId);
            movieExtraValues.put(MovieContract.MovieExtraEntry.COLUMN_SORT_ORDER, sortOrder);
            movieExtraValues.put(MovieContract.MovieExtraEntry.COLUMN_CREATED, curTimestamp);

            movieExtraContentValues[i] = movieExtraValues;
        }

        moviesList.add(movieContentValues);
        moviesList.add(movieExtraContentValues);

        return moviesList;
    }

    public static ContentValues[] getVideoContentValuesFromJson(String videoJsonStr)
        throws JSONException {

        final String TMDB_ID = "id";
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

            String videoId = videoData.getString(TMDB_ID);
            String iso_639_1 = videoData.getString(TMDB_ISO_639_1);
            String iso_3166_1 = videoData.getString(TMDB_ISO_3166_1);
            String key = videoData.getString(TMDB_KEY);
            String name = videoData.getString(TMDB_NAME);
            String site = videoData.getString(TMDB_SITE);
            int size = videoData.getInt(TMDB_SIZE);
            String type = videoData.getString(TMDB_TYPE);
            int movieId = videoJson.getInt(TMDB_ID);

            ContentValues videoValues = new ContentValues();
            videoValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, videoId);
            videoValues.put(MovieContract.TrailerEntry.COLUMN_ISO_639_1, iso_639_1);
            videoValues.put(MovieContract.TrailerEntry.COLUMN_ISO_3166_1, iso_3166_1);
            videoValues.put(MovieContract.TrailerEntry.COLUMN_KEY, key);
            videoValues.put(MovieContract.TrailerEntry.COLUMN_NAME, name);
            videoValues.put(MovieContract.TrailerEntry.COLUMN_SITE, site);
            videoValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, size);
            videoValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, type);
            videoValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);

            videoContentValues[i] = videoValues;
        }

        return videoContentValues;
    }

    public static ContentValues[] getReviewContentValuesFromJson(String reviewJsonStr)
        throws JSONException {

        final String TMDB_ID = "id";
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

            String reviewId = reviewData.getString(TMDB_ID);
            String author = reviewData.getString(TMDB_AUTHOR);
            String content = reviewData.getString(TMDB_CONTENT);
            String url = reviewData.getString(TMDB_URL);
            int movieId = reviewJson.getInt(TMDB_ID);

            ContentValues reviewValues = new ContentValues();
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, reviewId);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, author);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, content);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_URL, url);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);

            reviewContentValues[i] = reviewValues;
        }

        return reviewContentValues;
    }
}
