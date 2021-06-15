package com.example.distributingdata;

import android.graphics.Bitmap;

import java.net.URL;

public class GoogleBookModel implements java.io.Serializable {
    private String mTitle;
    private String mAuthors;
    private String mPublisher;
    private String mLargeImage;
    private String mSmallImage;
    private String mDescription;
    private String mCategories;

    public String getTitle() {
        return mTitle;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public String getLargeImage() { return mLargeImage; }

    public String getSmallImage() { return mSmallImage; }

    public String getDescription() { return mDescription; }

    public String getPublisher() {return mPublisher;}

    public String getCategories() {return mCategories;}


    public void addTitle(String title) {
         mTitle = title;
    }

    public void addAuthors(String author) {
        mAuthors = author;
    }

    public void addLargeImage(String largeImage) { mLargeImage = largeImage; }

    public void addSmallImage(String smallImage) { mSmallImage = smallImage; }

    public void addDescription(String description) { mDescription = description; }

    public void addPublisher(String publisher) { mPublisher = publisher; }

    public void addCategories(String categories) { mCategories = categories; }

}
