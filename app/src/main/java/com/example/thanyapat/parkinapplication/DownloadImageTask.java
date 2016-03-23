package com.example.thanyapat.parkinapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Tong on 2/16/2016.
 */
class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    public ImageView bmImage;
    private HashMap<String , Bitmap> map;
    private ProgressBar progressBar;

    public DownloadImageTask(ImageView bmImage, HashMap<String, Bitmap> map , ProgressBar progressIm) {
        this.bmImage = bmImage;
        this.map = map;
        this.progressBar = progressIm;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];

        if(map.containsKey(urldisplay)){
            Log.e("Load Image" , "Cache from map");
            return map.get(urldisplay);
        }

        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        Log.e("Load Image" , "Image finished loading");
        map.put(urldisplay , mIcon11);
        return mIcon11;
    }

    protected  void onPreExecute(){
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void onPostExecute(Bitmap result) {
        if(result==null) {
            Log.e("DLoad Image" , "Null result");
            return;
        }
        progressBar.setVisibility(View.INVISIBLE);
        bmImage.setImageBitmap(result);
    }
}
