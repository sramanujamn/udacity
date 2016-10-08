package com.udacity.ramanujam.popularmovies;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

//public class MovieDetailActivity extends AppCompatActivity implements MovieDetailFragment.OnFragmentInteractionListener {
public class MovieDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar)findViewById(R.id.movieToolbar);
        setSupportActionBar(toolbar);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_movie_detail, new MovieDetailFragment())
                    .commit();
        }

        //if(getSupportActionBar() != null) {
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //}
    }

    /*
    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i(LOG_TAG, "Reached inside onFragmentInteraction");


    }*/
}
