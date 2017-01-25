package com.ja.cobos.obrero.themoviedatabaseapp;

/**
 * Created by Juan Antonio Cobos Obrero on 21/01/17.
 */

public class Movie {

    private String mTitle;
    private String mYear;
    private String mOverview;
    private String mImagePath;
    private String mKeyword;

    public Movie() {
        // Empty constructor
    }

    /**
     * Use this constructor to create a new movie
     *
     * @param title
     * @param year
     * @param overview
     * @param imagePath
     * @param keyWord
     */
    public Movie(String title, String year, String overview, String imagePath, String keyWord) {
        mTitle = title;
        mYear = year;
        mOverview = overview;
        mImagePath = imagePath;
        mKeyword = keyWord;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    public String getKeyword() {
        return mKeyword;
    }

    public void setKeyword(String keyword) {
        this.mKeyword = keyword;
    }
}
