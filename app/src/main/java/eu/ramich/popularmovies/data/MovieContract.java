package eu.ramich.popularmovies.data;

import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import static eu.ramich.popularmovies.data.MovieContract.MovieExtraEntry.COLUMN_SORT_ORDER;

public class MovieContract {

    public static final String CONTENT_AUHORITY = "eu.ramich.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_MOVIE_EXTRA = "movie-extra";
    public static final String PATH_MOVIE_FAV = "movie-fav";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_FAVORITE = "favorite";


        public static Uri buildMovieUriWithId(int id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }

        public static String movieSelection() {
            return COLUMN_SORT_ORDER + " = ?";
        }

        public static String[] movieSelectionArgs(Context context) {
            return new String[]{PopularMoviesPreferences.getSortOrderKey(context)};
        }
    }

    public static final class MovieExtraEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE_EXTRA)
                .build();

        public static final String TABLE_NAME = "movies_extra";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_SORT_ORDER = "sort_order";
        public static final String COLUMN_CREATED = "created";
    }

    public static final class MovieFavEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE_FAV)
                .build();

        public static final String TABLE_NAME = "movies_fav";

        public static final String COLUMN_MOVIE_ID = "movie_id";
    }

    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILER)
                .build();

        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_TRAILER_ID = "trailer_id";
        public static final String COLUMN_ISO_639_1 = "iso_639_1";
        public static final String COLUMN_ISO_3166_1 = "iso_3166_1";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_MOVIE_ID = "movie_id";


        public static String trailerSelection() {
            return COLUMN_MOVIE_ID + " = ? AND " + COLUMN_TYPE + " = ? AND " + COLUMN_SITE + " = ?";
        }

        public static String[] trailerSelectionArgs(String movieId) {
            return new String[]{movieId, "Trailer", "YouTube"};
        }
    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEW)
                .build();

        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_MOVIE_ID = "movie_id";
    }

}
