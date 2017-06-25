package eu.ramich.popularmovies.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            MovieDetailsFragment details = new MovieDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("uri", getIntent().getData());
            details.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, details).commit();
        }
    }
}
