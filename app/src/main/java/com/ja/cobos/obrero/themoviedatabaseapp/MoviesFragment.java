package com.ja.cobos.obrero.themoviedatabaseapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;

/**
 * Created by Juan Antonio Cobos Obrero on 21/01/17.
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {link MoviesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoviesFragment extends Fragment implements MoviesContract.View {

    public static final int POPULAR_MOVIES = 1;
    @BindView(R.id.pb_movies)
    ProgressBar pb_movies;
    @BindView(R.id.search_box)
    TextInputEditText search_box;
    private MoviesContract.Presenter mPresenter;
    private MovieAdapter mAdapter;
    private ArrayList<Movie> mMovies;
    private OkHttpClient mClient;

    public MoviesFragment() {
        // Required empty public constructor
    }

    public void setPresenter(MoviesContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public MoviesFragment newInstance() {
        return new MoviesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movies, container, false);

        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestMovies(POPULAR_MOVIES, null); // There is no query to add
        mMovies = new ArrayList<>();
        initRecyclerView();

        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPresenter.initKeywordMovieValues();
                mPresenter.cancelCall(mClient, "TAG");
                requestMovies(2, search_box.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void initRecyclerView() {

        RecyclerView rv = (RecyclerView) getActivity().findViewById(R.id.rv_movies);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(llm.VERTICAL);
        rv.setLayoutManager(llm);
        rv = mPresenter.scrollListener(rv, llm);
        mAdapter = new MovieAdapter(getContext(), mMovies);
        rv.setAdapter(mAdapter);
    }


    public void requestMovies(int searchType, @Nullable String query) {
        try {
            mPresenter.fetchingMovies(searchType, query);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the movie list.
     *
     * @param movies Movies to show
     */
    @Override
    public void updateList(ArrayList<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Show the progress bar to display the delay on charge or hide it for the other case.
     *
     * @param state The state stores the value to show or hide the ProgressBar
     */
    @Override
    public void manageProgressBar(boolean state) {
        if (state) {
            pb_movies.setVisibility(View.VISIBLE);
        } else {
            pb_movies.setVisibility(View.GONE);
        }
    }

    /**
     * Show a message to inform the user.
     *
     * @param textId The message to show
     */
    @Override
    public void showMessage(int textId) {
        Toast.makeText(getActivity(), getString(textId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setClient(OkHttpClient client) {
        mClient = client;
    }
}
