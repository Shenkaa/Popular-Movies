package eu.ramich.popularmovies.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class PopularMoviesFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchMovieTask;


    @Override
    public boolean onStartJob(final JobParameters job) {

        mFetchMovieTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Context context = getApplicationContext();
                PopularMoviesSyncTasks
                        .executeTask(context, PopularMoviesSyncTasks.ACTION_INITIAL_SYNC, null);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };

        mFetchMovieTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchMovieTask != null) {
            mFetchMovieTask.cancel(true);
        }
        return true;
    }
}
