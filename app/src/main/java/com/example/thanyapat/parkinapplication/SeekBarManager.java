package com.example.thanyapat.parkinapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Thanyapat on 1/24/2016 AD.
 */
public class SeekBarManager {

    private MapFragment main;
    private View view;
    private Bitmap bitmap;
    private int seekBarValue;
    protected SeekBar seekbar;

    public SeekBarManager(MapFragment m, View v){
        main = m;
        view = v;
        initialize();
    }

    private void initialize(){
        seekbar = (SeekBar) view.findViewById(R.id.seekBar);
        seekbar.setMax(40);
        seekbar.setProgress(seekbar.getMax() / 2);
        Log.w("SeekBar", "Progress = " + seekbar.getProgress());
        seekBarValue = (seekbar.getProgress() / 10) + 1;
        Log.w("SeekBar", "Value = " + seekBarValue);

        bitmap = BitmapFactory.decodeResource(main.getResources(), R.drawable.thumb_large);
        changeThumb(Integer.toString((seekbar.getProgress() / 10) + 1), "Hour");
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value;
            int prev = 1;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = seekBar.getProgress() % 10 > 5 ? (int) Math.ceil((double) seekBar.getProgress() / 10.0) : (int) Math.floor((double) seekBar.getProgress() / 10.0);
                if (value != prev) {
                    if (value > 0)
                        changeThumb(Integer.toString(value + 1), "Hours");
                    else
                        changeThumb(Integer.toString(value + 1), "Hour");
                    prev = value;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.w("SeekBar Value", "" + (value + 1));
                seekBar.setProgress(value * 10);
                seekBarValue = value + 1;
                //map.showMarkersInBound();
                main.markAll();
            }
        });
    }
    public void updateSeekBar(int newValue){
        seekBarValue = newValue;
        if (seekBarValue > 0)
            changeThumb(""+seekBarValue, "Hours");
        else
            changeThumb(""+seekBarValue, "Hour");
    }


    public int durationToPrice(JSONObject p) {
        int priceSum = 0;
        try {
            for(int j = 0; j < seekBarValue ; j++){
                priceSum += (int)p.getJSONArray
                        ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                                || (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                                ? "weekEndPrice" : "weekDayPrice" )
                        .getJSONArray(Calendar.HOUR_OF_DAY)
                        .get(j);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return priceSum;
    }

    protected int getValue(){
        return seekBarValue;
    }

    private void changeThumb(String num,String text){
        Bitmap bmp = getResizedBitmap(bitmap,150,150).copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(bmp);
        Paint p = new Paint();
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setTextSize(80);
        p.setColor(Color.BLACK);
        int width_num = (int) p.measureText(num);
        int yPos_num = (int) ((c.getHeight() / 2) - ((p.descent() + p.ascent()) / 2)-15);
        c.drawText(num, (bmp.getWidth() - width_num) / 2, yPos_num, p);
        p.setTextSize(30);
        int width_text = (int) p.measureText(text);
        int yPos_text = (int) ((c.getHeight() / 2) - ((p.descent() + p.ascent()) / 2)+32);
        c.drawText(text, (bmp.getWidth() - width_text) / 2, yPos_text, p);
        seekbar.setThumb(new BitmapDrawable(main.getResources(), bmp));
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

}
