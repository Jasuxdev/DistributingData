package com.example.distributingdata;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class ModelDetailPage extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Bitmap> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.model_detail_page);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        TextView bookTitle = findViewById(R.id.book_title_text);
        TextView bookAuthors = findViewById(R.id.book_author);
        TextView bookPublisher = findViewById(R.id.book_publisher);
        ImageView largeImage = findViewById(R.id.book_image);
        TextView bookDescription = findViewById(R.id.book_description);
        TextView bookCategory = findViewById(R.id.book_category);


        if (extras != null) {

            GoogleBookModel item = (GoogleBookModel) extras.getSerializable(ItemAdapter.EXTRA_NAME);

            bookTitle.setText(item.getTitle());
            bookAuthors.setText(item.getAuthors());
            bookPublisher.setText(getString(R.string.publisher) + " " + item.getPublisher());
            bookDescription.setText(item.getDescription());
            bookCategory.setText(getString(R.string.category) + " " + item.getCategories());

            Bundle bundle = new Bundle();
            bundle.putString("url", item.getLargeImage());
            getSupportLoaderManager().initLoader(0, bundle, this);
        }
    }

    @NonNull
    @Override
    public Loader<Bitmap> onCreateLoader(int id, @Nullable Bundle args) {
        String url = args.getString("url");
        return new AsyncImageLoader(this, url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Bitmap> loader, Bitmap data) {
        ImageView imgView = findViewById(R.id.book_image);
        imgView.setImageBitmap(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Bitmap> loader) {

    }
}
