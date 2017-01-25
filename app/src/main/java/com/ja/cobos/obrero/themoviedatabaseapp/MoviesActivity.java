package com.ja.cobos.obrero.themoviedatabaseapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


/**
 * Created by Juan Antonio Cobos Obrero on 21/01/17.
 */

public class MoviesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        MoviesFragment moviesFragment = (MoviesFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_movies);

        MoviesPresenter moviesPresenter = new MoviesPresenter(moviesFragment);
        moviesFragment.setPresenter(moviesPresenter);

    }
}
