package com.example.thanyapat.parkinapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.Collection;

public class FacebookLoginActivity extends AppCompatActivity {

    private ImageButton facebookBtn;
    private SplashScreenFragment splashScreen = new SplashScreenFragment();
    Handler handler;
    Runnable runnable;
    long delay_time;
    long time = 1500L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        handler = new Handler();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, splashScreen).commit();

        runnable = new Runnable() {
            public void run() {
                Log.e("Status", "Stop Splashing");
                if (isOnline()) {
                    if (ParseUser.getCurrentUser() != null) {
                        // Start an intent for the logged in activity
                        Log.e("FacebookLogin", "already has user session");
                        startActivity(new Intent(FacebookLoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Log.e("FacebookLogin", "initiate login UI");
                        getSupportFragmentManager().beginTransaction().remove(splashScreen).commit();
                        initLoginButton();
                    }
                } else {
                    Snackbar.make(FacebookLoginActivity.this.findViewById(R.id.view_facebook_login), "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = FacebookLoginActivity.this.getIntent();
                                    finish();
                                    FacebookLoginActivity.this.startActivity(intent);
                                }
                            })
                            .show();
                }
            }
        };
    }

    public void onResume() {
        super.onResume();
        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
    }

    protected void initLoginButton() {
        facebookBtn = (ImageButton) findViewById(R.id.facebook_imgBtn);
        facebookBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                loginFacebook();
            }

        });
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    protected void loginFacebook() {
        Collection<String> permissions = Arrays.asList("publish_actions");
        ParseFacebookUtils.logInWithPublishPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                    Intent intent = new Intent(FacebookLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

}
