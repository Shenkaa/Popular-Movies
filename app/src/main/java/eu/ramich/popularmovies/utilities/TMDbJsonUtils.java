package eu.ramich.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import eu.ramich.popularmovies.data.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TMDbJsonUtils {

    private static final String TAG = TMDbJsonUtils.class.getSimpleName();


    public static List<Movie> getSimpleMovieListFromJson(Context context, String movieJsonStr)
        throws JSONException {

        final String TMDB_RESULT = "results";

        final String TMDB_ID = "id";
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_TITLE = "title";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_BACKDROP_PATH = "backdrop_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_VOTE_AVERAGE = "vote_average";

        final String TMDB_STATUS_CODE = "status_code";
        final String TMDB_STATUS_MESSAGE = "status_message";


        JSONObject movieJson = new JSONObject(movieJsonStr);

        if (movieJson.has(TMDB_STATUS_CODE)) {
            Log.e(TAG, movieJson.getString(TMDB_STATUS_MESSAGE));

            return null;
        }

        JSONArray movieList = movieJson.getJSONArray(TMDB_RESULT);
        List<Movie> parsedMovieData = new ArrayList<>(movieList.length());

        for (int i = 0; i < movieList.length(); i++) {
            JSONObject movieData = movieList.getJSONObject(i);

            parsedMovieData.add(i, new Movie(
                    movieData.getInt(TMDB_ID),
                    movieData.getString(TMDB_TITLE),
                    movieData.getString(TMDB_ORIGINAL_TITLE),
                    movieData.getString(TMDB_POSTER_PATH),
                    movieData.getString(TMDB_BACKDROP_PATH),
                    movieData.getString(TMDB_OVERVIEW),
                    movieData.getDouble(TMDB_VOTE_AVERAGE),
                    movieData.getString(TMDB_RELEASE_DATE))
            );
        }

        return parsedMovieData;
    }
}
