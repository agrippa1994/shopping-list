package com.example.shoppinglist.ui.data_access;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

    static Map<String, Bitmap> cache = new HashMap<>();

    public interface Listener{

        void onImageLoaded(Bitmap bitmap);
        void onError();
    }

    public LoadImageTask(Listener listener) {
        mListener = listener;
    }

    private Listener mListener;
    @Override
    protected Bitmap doInBackground(String... args) {
        if (cache.containsKey(args[0])) {
            return cache.get(args[0]);
        }

        try {

            Bitmap map = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
            if(map != null) {
                cache.put(args[0], map);
            }
            return map;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (bitmap != null) {

            mListener.onImageLoaded(bitmap);

        } else {

            mListener.onError();
        }
    }
}