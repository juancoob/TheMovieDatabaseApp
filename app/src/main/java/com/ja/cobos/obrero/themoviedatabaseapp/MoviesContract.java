package com.ja.cobos.obrero.themoviedatabaseapp;

/**
 * Created by Juan Antonio Cobos Obrero on 21/01/17.
 */

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

/**
 * This specifies the contract between the view and the presenter
 */
public interface MoviesContract {

    interface View {
        void updateList(ArrayList<Movie> movies);
        void manageProgressBar(boolean state);
        void showMessage(int textId);
        void setClient(OkHttpClient client);
    }

    interface Presenter {
        void fetchingMovies(int searchType, @Nullable String query) throws IOException;
        RecyclerView scrollListener(RecyclerView rv, LinearLayoutManager llm);
        void initKeywordMovieValues();
        void cancelCall(OkHttpClient client, String tag);
    }

}
