package com.example.distributingdata.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    // Base URL for Books API
    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    // Parameter for the search string
    private static final String QUERY_PARAM = "q";
    // Parameter that limits search results
    private static final String MAX_RESULTS = "maxResults";
    // Parameter to filter by print type
    private static final String PRINT_TYPE = "printType";
    // Code Challenge of Code Lab 7.2 - Parameter that finds downloadable books
    private static final String DOWNLOADABLE = "download";

    public static String getBookInfo(String queryString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, queryString)
                    .appendQueryParameter(MAX_RESULTS, "10")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .appendQueryParameter(DOWNLOADABLE, "epub") //Code Challenge CodeLab 7.2
                    .build();
            URL requestURL = new URL(builtURI.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Get the InputStream
            InputStream inputStream = urlConnection.getInputStream();

            // Create a buffered reader from that input stream
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Use a StringBuilder to hold the incoming response
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing) but
                // it does make debugging a *lot* easier if you print out the completed buffer for debugging
                builder.append("\n");
            }

            if (builder.length() == 0) {
                // Stream was empty. No point in parsing
                return null;
            }

            bookJSONString = builder.toString();
            Log.d(LOG_TAG, bookJSONString);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the connection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            // Close the BufferReader
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bookJSONString;
    }
}
