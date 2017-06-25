package eu.ramich.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import eu.ramich.popularmovies.BuildConfig;

public class NetworkUtils {

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p";
    private static final String API_KEY = BuildConfig.TMDb_KEY;

    private static String VIDEOS_PATH = "videos";
    private static String REVIEWS_PATH = "reviews";
    private static String API_PARAM = "api_key";
    private static String LANGUAGE_PARAM = "language";
    private static String REGION_PARAM = "region";

    private static final String TRAILER_BASE_URL = "https://www.youtube.com/watch";
    private static final String TRAILER_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi";

    private static String VIDEO_ID_PARAM = "v";
    private static String VIDEO_THUMBNAIL_SIZE = "/0.jpg";


    public static URL builMoviesdUrl(String searchQuery) {
        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(searchQuery)
                .appendQueryParameter(API_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, Locale.getDefault().getLanguage())
                .appendQueryParameter(REGION_PARAM, Locale.getDefault().getCountry())
                .build();

        return buildUrl(buildUri);
    }

    public static URL buildPosterUrl(String posterPath, String posterWidth) {
        Uri uri = Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendEncodedPath(posterWidth + posterPath)
                .build();

        return buildUrl(uri);
    }

    public static URL buildVideoUrl(String movieID) {
        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(movieID)
                .appendPath(VIDEOS_PATH)
                .appendQueryParameter(API_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, Locale.getDefault().getLanguage())
                .build();

        return buildUrl(buildUri);
    }

    public static Uri buildTrailerUri(String videoID) {
        Uri buildUri = Uri.parse(TRAILER_BASE_URL).buildUpon()
                .appendQueryParameter(VIDEO_ID_PARAM, videoID)
                .build();

        return buildUri;
    }

    public static URL buildVideoThumbnailUrl(String videoID) {
        Uri buildUri = Uri.parse(TRAILER_THUMBNAIL_BASE_URL).buildUpon()
                .appendEncodedPath(videoID + VIDEO_THUMBNAIL_SIZE)
                .build();

        return buildUrl(buildUri);
    }

    public static URL buildReviewUrl(String movieID) {
        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(movieID)
                .appendPath(REVIEWS_PATH)
                .appendQueryParameter(API_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, Locale.getDefault().getLanguage())
                .build();

        return buildUrl(buildUri);
    }

    private static URL buildUrl(Uri uri) {
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.isConnected()) return true;
            }
        }

        return false;
    }

    public static String getResponseFromHttpsUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
