package com.example.distributingdata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements ItemAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<String> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ItemAdapter mAdapter;
    private EditText mBookInput;
    private TextView mErrorMessage;

    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private ArrayList<GoogleBookModel> mBookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBookInput = (EditText) findViewById(R.id.bookInput);
        mErrorMessage = (TextView) findViewById(R.id.main_error_message);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.dd_loading_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        if(savedInstanceState != null){
            getSupportLoaderManager().initLoader(1, null,this);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

    }

    public void searchBooks(View view) {
        // Get the search string from the input field
        String queryString = mBookInput.getText().toString();

        // Hide keyboard when user taps the button
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        // Manage the network state and the empty search field case
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected() && queryString.length() != 0) {
//            new FetchBook(mTitleText, mAuthorText).execute(queryString);
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);
            getSupportLoaderManager().restartLoader(1, queryBundle, this);

            mRecyclerView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            if (queryString.length() == 0) {
                mErrorMessage.setText(R.string.no_search_term);
            } else {
                mErrorMessage.setText(R.string.no_network);
            }
            mRecyclerView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";

        if (args != null) {
            queryString = args.getString("queryString");
        }
        return new BookLoader(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            String title = null;
            String authors = null;
            String publisher = null;
            String imageLargeThumb = null;
            String imageSmallThumb = null;
            String description = null;
            String categories = null;

            mBookList = new ArrayList<GoogleBookModel>();

            for (int i=0; i < itemsArray.length(); i++) {
                // Get the current item information
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");


                Log.d(LOG_TAG, volumeInfo.toString());

                // Try to get the author and title from the current item, catch if
                // either field is empty and move on
                try {
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");

                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                    publisher = volumeInfo.getString("publisher");
                    imageLargeThumb = imageLinks.getString("thumbnail");
                    imageSmallThumb = imageLinks.getString("smallThumbnail");
                    description = volumeInfo.getString("description");
                    categories = volumeInfo.getString("categories");

                    GoogleBookModel bookModel = new GoogleBookModel();
                    bookModel.addTitle(title);
                    bookModel.addAuthors(authors.replaceAll("\\[|\\]|\\\"", "").replaceAll(",",", "));
                    bookModel.addPublisher(publisher);
                    bookModel.addLargeImage(imageLargeThumb);
                    bookModel.addSmallImage(imageSmallThumb);
                    bookModel.addDescription(description);
                    bookModel.addCategories(categories.replaceAll("\\[|\\]|\\\"", "").replaceAll(",",", "));

                    Log.d(LOG_TAG, bookModel.getTitle());

                    mBookList.add(bookModel);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mErrorMessage.setVisibility(View.INVISIBLE);
            ItemAdapter mAdapter = new ItemAdapter(mBookList, getSupportLoaderManager(), this);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
            // If both are found, display the result
//            if (title != null && authors != null) {
//                mLoadingIndicator.setVisibility(View.INVISIBLE);
//                mTitleText.setText(title);
//                mAuthorText.setText(authors);
//            } else {
//                // If none are found, update the UI to show failed results set loader to invisible
//                mLoadingIndicator.setVisibility(View.INVISIBLE);
//                mTitleText.setText(R.string.no_results);
//                mAuthorText.setText("");
//            }
        } catch (Exception e) {
            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results
            mErrorMessage.setText(R.string.no_results);
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessage.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }

        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            /*
             * Restart recycler view all over
             */
            case R.id.action_refresh:
                if (mRecyclerView.getVisibility() == View.VISIBLE ) {
                    mAdapter = new ItemAdapter(mBookList, getSupportLoaderManager(), this);
                    mRecyclerView.setAdapter(mAdapter);

                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *  Detects item clicked in the recycler view
     * @param element
     * @param itemView
     */
    @Override
    public void onListItemClick(GoogleBookModel element, View itemView) {
        Intent intent = new Intent(itemView.getContext(), ModelDetailPage.class);
        Bundle extra = new Bundle();
        extra.putSerializable(ItemAdapter.EXTRA_NAME, element);
        intent.putExtras(extra);
        itemView.getContext().startActivity(intent);
    }
}