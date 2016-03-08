package com.example.thanyapat.parkinapplication;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by Tong on 9/14/2015.
 */
public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("Application", "onCreate");
        Log.e("Status", "INITIALIZING PARSE.COM");
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "fGnIrUAQfSdY4guwK7t3WdWZ9J2huaPepf6j9SUZ", "y89G7XylNrXXbdOUO7GbM9g7I1axzDb0m77ACgus");
        ParseFacebookUtils.initialize(getApplicationContext());

    }
}
