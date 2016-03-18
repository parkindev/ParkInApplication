package com.example.thanyapat.parkinapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class SettingsFragment extends StatedFragment {

    public static GraphResponse response;
    private View rootView;
    private static TextView mEmail;
    private static TextView mName;
    private static CircularImageView mProfile;


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        mEmail = (TextView)rootView.findViewById(R.id.textView_email);
        mEmail.setText(MainActivity.email);
        mName = (TextView)rootView.findViewById(R.id.textView_name);
        mName.setText(MainActivity.name);
        mProfile = (CircularImageView) rootView.findViewById(R.id.imgView_profile);
        mProfile.setImageBitmap(MainActivity.profile);



        return rootView;
    }

    protected void onFirstTimeLaunched() {
    }


}
