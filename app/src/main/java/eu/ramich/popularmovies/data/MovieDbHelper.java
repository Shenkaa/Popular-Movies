package eu.ramich.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import eu.ramich.popularmovies.data.MovieContract.MovieEntry;
import eu.ramich.popularmovies.data.MovieContract.MovieExtraEntry;
import eu.ramich.popularmovies.data.MovieContract.MovieFavEntry;
import eu.ramich.popularmovies.data.MovieContract.ReviewEntry;
import eu.ramich.popularmovies.data.MovieContract.TrailerEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIES_TABLE =

                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        MovieEntry.COLUMN_MOVIE_ID          + " INTEGER PRIMARY KEY, " +
                        MovieEntry.COLUMN_TITLE             + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_ORIGINAL_TITLE    + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_RELEASE_DATE      + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_VOTE_AVERAGE      + " REAL NOT NULL, " +
                        MovieEntry.COLUMN_VOTE_COUNT        + " INTEGER NOT NULL, " +
                        MovieEntry.COLUMN_POPULARITY        + " REAL NOT NULL, " +
                        MovieEntry.COLUMN_OVERVIEW          + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_POSTER_PATH       + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_BACKDROP_PATH     + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_FAVORITE          + " INTEGER DEFAULT 0, " +
                        " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE" +
                ");";


        final String SQL_CREATE_MOVIES_EXTRA_TABLE =

                "CREATE TABLE " + MovieExtraEntry.TABLE_NAME + " (" +
                        MovieExtraEntry._ID                 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieExtraEntry.COLUMN_MOVIE_ID     + " INTEGER NOT NULL, " +
                        MovieExtraEntry.COLUMN_SORT_ORDER   + " TEXT NOT NULL, " +
                        MovieExtraEntry.COLUMN_CREATED      + " TEXT NOT NULL" +
                ");";


        final String SQL_CREATE_MOVIES_FAV_TABLE =

                "CREATE TABLE " + MovieFavEntry.TABLE_NAME + " (" +
                        MovieFavEntry.COLUMN_MOVIE_ID       + " INTEGER PRIMARY KEY" +
                ");";


        final String SQL_CREATE_TRAILER_TABLE =

                "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                        TrailerEntry._ID                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TrailerEntry.COLUMN_TRAILER_ID      + " TEXT NOT NULL, " +
                        TrailerEntry.COLUMN_ISO_639_1       + " TEXT NOT NULL, " +
                        TrailerEntry.COLUMN_ISO_3166_1      + " TEXT NOT NULL, " +
                        TrailerEntry.COLUMN_KEY             + " TEXT NOT NULL, " +
                        TrailerEntry.COLUMN_NAME            + " TEXT NOT NULL, " +
                        TrailerEntry.COLUMN_SITE            + " TEXT NOT NULL, " +
                        TrailerEntry.COLUMN_SIZE            + " INTEGER NOT NULL, " +
                        TrailerEntry.COLUMN_TYPE            + " TEXT NOT NULL, " +
                        TrailerEntry.COLUMN_MOVIE_ID        + " INTEGER NOT NULL, " +
                        " UNIQUE (" + TrailerEntry.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE, " +
                        " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_MOVIE_ID + ")" +
                ");";


        final String SQL_CREATE_REVIEW_TABLE =

                "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                        ReviewEntry._ID                     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ReviewEntry.COLUMN_REVIEW_ID        + " TEXT NOT NULL, " +
                        ReviewEntry.COLUMN_AUTHOR           + " TEXT NOT NULL, " +
                        ReviewEntry.COLUMN_CONTENT          + " TEXT NOT NULL, " +
                        ReviewEntry.COLUMN_URL              + " TEXT NOT NULL, " +
                        ReviewEntry.COLUMN_MOVIE_ID         + " INTEGER NOT NULL, " +
                        " UNIQUE (" + ReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE, " +
                        " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_MOVIE_ID + ")" +
                ");";


        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_MOVIES_EXTRA_TABLE);
        db.execSQL(SQL_CREATE_MOVIES_FAV_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieExtraEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieFavEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);

        onCreate(db);
    }
}
