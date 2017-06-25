package eu.ramich.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;

public class PopularMoviesIntentService extends IntentService {

    public PopularMoviesIntentService() {
        super("PopularMoviesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (intent.getExtras() != null) {
            String id = intent.getStringExtra("id");
            PopularMoviesSyncTasks.executeTask(this, action, id);
        } else {
            PopularMoviesSyncTasks.executeTask(this, action, null);
        }
    }
}
