package com.udacity.shenka.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.udacity.shenka.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String API_KEY = BuildConfig.TMDb_KEY;

    private static String API_PARAM = "api_key";
    private static String LANGUAGE_PARAM = "language";
    private static String REGION_PARAM = "region";


    public static URL buildUrl(String searchQuery) {
        Uri buildUri = Uri.parse(MOVIE_BASE_URL + searchQuery).buildUpon()
                .appendQueryParameter(API_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, Locale.getDefault().getLanguage())
                .appendQueryParameter(REGION_PARAM, Locale.getDefault().getCountry())
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url.toString().replace(BuildConfig.TMDb_KEY, "[YOUR_API_KEY]"));

        return url;
    }

    public static URL buildPosterURL(String posterPath, String posterWidth) {
        Uri uri = Uri.parse(POSTER_BASE_URL + posterWidth + posterPath);

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//        Log.v(TAG, "Build poster URI " + url);

        return url;
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
