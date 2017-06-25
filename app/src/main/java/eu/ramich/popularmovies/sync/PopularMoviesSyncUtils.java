package eu.ramich.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import eu.ramich.popularmovies.data.MovieContract;

public class PopularMoviesSyncUtils {

    private static final int SYNC_INTERVAL_HOURS = 18;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;
    private static final String MOVIE_SYNC_TAG = "movie-sync";


    private static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job syncMovieJob = dispatcher.newJobBuilder()
                .setService(PopularMoviesFirebaseJobService.class)
                .setTag(MOVIE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS
                ))
                .setReplaceCurrent(true)
                .build();

        dispatcher.mustSchedule(syncMovieJob);
    }

    synchronized public static void initialize(@NonNull final Context context) {

        if (sInitialized) return;

        sInitialized = true;

        scheduleFirebaseJobDispatcherSync(context);

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;
                String[] projectionColumns = {MovieContract.MovieEntry.COLUMN_MOVIE_ID};

                Cursor cursor = context.getContentResolver().query(
                        movieQueryUri,
                        projectionColumns,
                        null,
                        null,
                        null
                );

                if (cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context);
                } else {
                    cursor.close();
                }
            }
        });

        checkForEmpty.start();
    }

    private static void startImmediateSync(@NonNull Context context) {
        Intent intentToSyncImmediatly = new Intent(context, PopularMoviesIntentService.class);
        intentToSyncImmediatly.setAction(PopularMoviesSyncTasks.ACTION_INITIAL_SYNC);
        context.startService(intentToSyncImmediatly);
    }

}
