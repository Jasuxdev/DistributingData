package com.example.distributingdata;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private static final String LOG_TAG = ItemAdapter.class.getSimpleName();
    private ArrayList<GoogleBookModel> mGoogleBookModelList;
    private LoaderManager mLoaderManager;
    public final static String EXTRA_NAME = "me.jas.itemsrecyclerview.EXTRA.NAME";

    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(GoogleBookModel model, View itemView); //added parameter of type view
    }

    ItemAdapter(ArrayList<GoogleBookModel> googleBookModels, LoaderManager loaderManager, ListItemClickListener listener) {
        mGoogleBookModelList = googleBookModels;
        mLoaderManager = loaderManager;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.model_list_item, parent, false);
        return new ItemViewHolder(itemView, this, mLoaderManager);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(mGoogleBookModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return mGoogleBookModelList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, LoaderManager.LoaderCallbacks<Bitmap>  {
        final TextView titleTextView;
        final TextView authorsTextView;
        //        final TextView linkTextView;
        final ImageView mImageView;
        final ItemAdapter mAdapter;
        final LoaderManager mLoaderManager;

        public ItemViewHolder(View itemView, ItemAdapter adapter, LoaderManager loaderManager) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleText);
            authorsTextView = itemView.findViewById(R.id.authorText);
//            linkTextView = itemView.findViewById(R.id.selfLink);
            mImageView = itemView.findViewById(R.id.book_image_small);
            mAdapter = adapter;
            mLoaderManager = loaderManager;

            itemView.setOnClickListener(this);
        }

        void bind(GoogleBookModel model) {
            titleTextView.setText(model.getTitle());
            authorsTextView.setText(model.getAuthors());
//            linkTextView.setText(model.getLink());

            Bundle bundle = new Bundle();
            bundle.putString("url", model.getSmallImage());
            mLoaderManager.restartLoader(0, bundle, this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            GoogleBookModel element = mAdapter.mGoogleBookModelList.get(clickedPosition);
            mOnClickListener.onListItemClick(element, view);
        }

        @NonNull
        @Override
        public Loader<Bitmap> onCreateLoader(int id, @Nullable Bundle args) {
            String url = args.getString("url");
            return new AsyncImageLoader(itemView.getContext(), url);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Bitmap> loader, Bitmap data) {
            mImageView.setImageBitmap(data);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Bitmap> loader) {

        }
    }
}
