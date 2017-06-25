package eu.ramich.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIE_DATA = 101;
    public static final int CODE_MOVIE_EXTRA = 110;
    public static final int CODE_MOVIE_FAV = 111;
    public static final int CODE_TRAILER = 200;
    public static final int CODE_REVIEWS = 300;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;


    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_DATA);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_EXTRA, CODE_MOVIE_EXTRA);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_FAV, CODE_MOVIE_FAV);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, CODE_TRAILER);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, CODE_REVIEWS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case CODE_MOVIE_DATA:
                String movieId = uri.getLastPathSegment();

                selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";
                selectionArgs = new String[]{movieId};

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case CODE_TRAILER:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case CODE_REVIEWS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowsInserted;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value: values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                break;

            case CODE_MOVIE_EXTRA:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value: values) {
                        long _id = db.insert(MovieContract.MovieExtraEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                break;

            case CODE_TRAILER:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value: values) {
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                break;

            case CODE_REVIEWS:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value: values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                break;

            default:
                return super.bulkInsert(uri, values);
        }

        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsInserted;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_FAV:
                mOpenHelper.getWritableDatabase().insert(
                        MovieContract.MovieFavEntry.TABLE_NAME,
                        null,
                        values
                );
                break;
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int rowsDeleted;

        if (selection == null)
            selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;

            case CODE_MOVIE_EXTRA:
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieExtraEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;

            case CODE_MOVIE_FAV:
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieFavEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;

            case CODE_TRAILER:
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;

            case CODE_REVIEWS:
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;

            case CODE_MOVIE_DATA:
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
