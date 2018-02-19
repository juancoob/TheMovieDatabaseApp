package com.ja.cobos.obrero.themoviedatabaseapp;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Juan Antonio Cobos Obrero on 21/01/17.
 */

public class MoviesPresenter implements MoviesContract.Presenter {

    public static final String API_KEY = "YOUR_API_KEY";
    public static final String URL = "https://api.themoviedb.org/3";
    public static final String POPULAR_MOVIES = "/movie/popular";
    public static final String SEARCH_KEYWORD = "/search/keyword";
    public static final String MOVIES_BY_KEYWORD_PART1 = "/keyword";
    public static final String MOVIES_BY_KEYWORD_PART2 = "/movies";
    public static String sLanguage = "en-US";
    private ArrayList<Movie> mMovies;
    private MoviesContract.View mViewCommunication;
    private Handler mHandler;
    private boolean mLoading;
    private int mPreviouSearchType;
    private String mUrl;
    private String mQuery;

    private int mKeywordCurrentPage;
    private int mTotalKeywordPages;
    private int mCurrentKeywordPosition;
    private int mTotalKeywords;
    private String mKeywordSearched;
    private boolean mAreThereMoreKeywords;

    private int mMoviesCurrentPage;
    private int mTotalMoviePages;
    private int mTotalMovies;
    private int mLastTotalMovies;

    public MoviesPresenter() {
        // Empty constructor
    }

    public MoviesPresenter(MoviesContract.View moviesView) {
        mViewCommunication = moviesView;
        mMovies = new ArrayList<>();
        mHandler = new Handler();
        mLoading = false;
        mPreviouSearchType = 0;
        mUrl = null;
        mQuery = null;
        initKeywordMovieValues();
    }

    public static String getApiKey() {
        return API_KEY;
    }

    public static String getURL() {
        return URL;
    }

    public static String getPopularMovies() {
        return POPULAR_MOVIES;
    }

    public static String getSearchKeyword() {
        return SEARCH_KEYWORD;
    }

    public static String getMoviesByKeywordPart1() {
        return MOVIES_BY_KEYWORD_PART1;
    }

    public static String getMoviesByKeywordPart2() {
        return MOVIES_BY_KEYWORD_PART2;
    }

    public static String getLanguage() {
        return sLanguage;
    }

    public static void setLanguage(String language) {
        sLanguage = language;
    }

    @Override
    public void initKeywordMovieValues() {
        mKeywordCurrentPage = 1;
        mTotalKeywordPages = 0;
        mTotalKeywords = 0;
        mCurrentKeywordPosition = -1;
        mKeywordSearched = "";
        mAreThereMoreKeywords = false;

        mMoviesCurrentPage = 1;
        mTotalMoviePages = 1;
        mTotalMovies = 1;
        mLastTotalMovies = 0;
        mMovies.clear();
    }

    /**
     * Get the most popular movies.
     *
     * @param searchType Integer which shows the search type
     * @param query      String which stores the searched element or not, depending on the
     *                   searchType value
     * @return response JSON with the most popular movies
     * @throws IOException
     */
    public void fetchingMovies(int searchType, @Nullable String query) throws IOException {

        mQuery = query;

        if (mPreviouSearchType == 0) {
            mPreviouSearchType = searchType;
        } else if (mPreviouSearchType != searchType) { // Controls when the search type is
            mMoviesCurrentPage = 1;                    // different and reset the page number
            mPreviouSearchType = searchType;
        }

        OkHttpClient client = new OkHttpClient();
        mViewCommunication.setClient(client); // Set the client to be able to cancel request

        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, "{}");

        switch (searchType) {
            case 1:
                mUrl = getURL() + getPopularMovies() + "?page=" + mMoviesCurrentPage + "&language="
                        + getLanguage() + "&api_key=" + getApiKey();
                break;
            case 2:
                mUrl = getURL() + getSearchKeyword() + "?api_key=" + getApiKey() + "&query="
                        + mQuery + "&page=" + mKeywordCurrentPage;
                break;
            case 3:
                mUrl = getURL() + getMoviesByKeywordPart1() + "/" + mQuery
                        + getMoviesByKeywordPart2() + "?api_key=" + getApiKey()
                        + "&language=" + getLanguage() + "&include_adult=false"
                        + "&page=" + mMoviesCurrentPage;
                break;
        }

        Request request = new Request.Builder().url(mUrl).get().tag("TAG").build();

        // Asynchronous calls
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // If we don't need to get keywordsId
                            if (mPreviouSearchType != 2) {
                                parseJSON(response.body().string());
                            } else {
                                parseIdsJSON(response.body().string());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * Parse the JSON object to save them on the model
     */
    public void parseJSON(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);

            // Get the important values once
            if (mMoviesCurrentPage == 1) {
                mTotalMoviePages = Integer.valueOf(jsonObject.getString("total_pages"));
                mTotalMovies = Integer.valueOf(jsonObject.getString("total_results"));
            }

            JSONArray movies = jsonObject.getJSONArray("results");

            for (int i = 0; i < movies.length(); i++) {

                Movie movie = new Movie();
                JSONObject object = movies.getJSONObject(i);

                movie.setImagePath("https://image.tmdb.org/t/p/w500" + object.getString("poster_path"));
                movie.setTitle(object.getString("title"));
                movie.setYear(object.getString("release_date").substring(0, 4));
                movie.setOverview(object.getString("overview"));
                movie.setKeyword(object.getString("id"));

                mMovies.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mViewCommunication.updateList(mMovies);

        if (mTotalMovies == 1) {
            getNextMovies();
        }
    }

    /**
     * If I got one movie as total amount, fetch the next movies because scrollListener
     * can not be called
     */
    public void getNextMovies() {
        if (mAreThereMoreKeywords) {
            try {
                mLastTotalMovies += mTotalMovies; // Update the movie amount
                fetchingMovies(2, mKeywordSearched);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mViewCommunication.showMessage(R.string.no_movies);
        }
    }

    /**
     * Controls when the list reach the end of the page.
     *
     * @param rv  The current RecyclerView
     * @param llm The LinearLayoutManager to get the elements
     * @return The current RecyclerView updated
     */
    @Override
    public RecyclerView scrollListener(RecyclerView rv, final LinearLayoutManager llm) {

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int pastVisibleMovies, visibleMoviesCount, totalMovieCount;

                // When the user scrolls down
                if (dy > 0) {
                    visibleMoviesCount = llm.getChildCount();
                    totalMovieCount = llm.getItemCount();
                    pastVisibleMovies = llm.findFirstVisibleItemPosition();

                    // And the app is not loading extra movies, the user reach the RecyclerView
                    // bottom and it isn't displayed all movies
                    if (!mLoading
                            && ((visibleMoviesCount + pastVisibleMovies) >= totalMovieCount)
                            && (totalMovieCount < (mTotalMovies + mLastTotalMovies))) {
                        mLoading = true;

                        try {
                            // Increment the page number to load the next movies
                            mMoviesCurrentPage++;
                            fetchingMovies(mPreviouSearchType, mQuery);
                            mViewCommunication.manageProgressBar(true);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mLoading = false;
                                    mViewCommunication.manageProgressBar(false);
                                }
                            }, 1500);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // If there are more keywords and no more movies to show, request it
                    } else if ((totalMovieCount == (mTotalMovies + mLastTotalMovies)) && mAreThereMoreKeywords) {
                        try {
                            mLastTotalMovies += mTotalMovies; // Update the movie amount
                            fetchingMovies(2, mKeywordSearched);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // If there are no more keywords and no more movies to show, show a message
                    } else if ((totalMovieCount == mTotalMovies)) {
                        mViewCommunication.showMessage(R.string.no_movies);
                    }

                }

            }
        });

        return rv;
    }

    /**
     * Parse the keyword Ids from the JSON object
     */
    public void parseIdsJSON(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);

            // Get the important data once
            if (mCurrentKeywordPosition == -1) {
                //mKeywordCurrentPage = Integer.valueOf(jsonObject.getString("page"));
                mTotalKeywordPages = Integer.valueOf(jsonObject.getString("total_pages"));
                mTotalKeywords = Integer.valueOf(jsonObject.getString("total_results"));
                mKeywordSearched = mQuery;
            }
            mCurrentKeywordPosition++;

            JSONArray keywordResults = jsonObject.getJSONArray("results");
            JSONObject object = keywordResults.getJSONObject(mCurrentKeywordPosition);
            fetchingMovies(3, object.getString("id"));

            // If I have keywords to search
            if ((mCurrentKeywordPosition != keywordResults.length() - 1)) {
                mAreThereMoreKeywords = true;
            } else {
                if (mKeywordCurrentPage < mTotalKeywordPages) {
                    mCurrentKeywordPosition = -1;
                    mKeywordCurrentPage++;
                    mAreThereMoreKeywords = true;
                } else {
                    mAreThereMoreKeywords = false;
                }
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancel the current calls when the user is tipping.
     * @param client OkHttpClient object to cancel it
     * @param tag A identifying tag
     */
    @Override
    public void cancelCall(OkHttpClient client, String tag) {
        for (Call call : client.dispatcher().queuedCalls()) {
            if (call.request().tag().equals(tag))
                call.cancel();
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (call.request().tag().equals(tag))
                call.cancel();
        }
    }

}
