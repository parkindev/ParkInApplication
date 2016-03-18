package com.example.thanyapat.parkinapplication;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.thanyapat.parkinapplication.History.HistoryContent;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks {

    public static Map<String, Fragment> fragmentList = new HashMap<>();
    public static FragmentManager fragmentManager;
    private static GoogleApiClient googleApiClient;
    protected static List<ParkingArea> areaList = new LinkedList<>();
    protected LatLng userPosition;
    public static NavigationView navigationView;
    public static String name;
    public static String email;
    public static Bitmap profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addFragmentToList();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new MyConnectionFailedListener())
                .build();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);
        fragmentManager = getSupportFragmentManager();
        // initialize the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_container, fragmentList.get("map")).commit();
            Log.w("MainActivity", "First time enter the Application");
        }
        getUserDetailsFromFB();
    }

    private void checkForAction(String action) {
        if (action.toUpperCase().equalsIgnoreCase("NAV_TO_TIMER")) {
            if (fragmentList.get("timer") != null) {
                Log.w("MainActivity", "TimerFragment is in memory");
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frame_container, fragmentList.get("timer"))
                        .commit();
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.frame_container, fragmentList.get("timer"))
                    .commit();
        }
    }

    private void addFragmentToList() {
        fragmentList.put("map", new MapFragment());
        fragmentList.put("memo", new MemoFragment());
        fragmentList.put("home", new HomeFragment());
        fragmentList.put("history", new HistoryFragment());
        fragmentList.put("issue-report", new IssueReportFragment());
        fragmentList.put("info-report", new InfoReportFragment());
        fragmentList.put("settings", new SettingsFragment());

    }


    public void putList(List<ParseObject> list) {
        for (ParseObject object : list) {
            areaList.add(new ParkingArea(object));
        }
    }

    public List<ParkingArea> getAreaList() {
        return areaList;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public void issueNotification(String action, String message) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(action);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        resultIntent.putExtra("Notification", "Test");
        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        // Make this unique ID to make sure there is not generated just a brand new intent with new extra values:
        int requestID = (int) System.currentTimeMillis();

        // Pass the unique ID to the resultPendingIntent:

        // mId allows
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_app)
                        .setContentTitle("ParkIn")
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setCategory(Notification.CATEGORY_ALARM)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setContentIntent(PendingIntent.getActivity(this, requestID, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        int mNotificationId = 001; // Sets an ID for the notification
        // Gets an instance of the NotificationManager service to builds the notification and issues it.
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(mNotificationId, mBuilder.build());
        Log.w("TimerFragment", "Issue Notification");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("MainActivity", "Configuration Changed");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        return displayView(item.getItemId());
    }


    private boolean displayView(int id) {
        // update the main content by replacing fragments
        String fragmentName = "";
        if (id == R.id.nav_home) {
            fragmentName = "map";
        } else if (id == R.id.nav_timer) {
            if (userPosition != null) {
                if (fragmentList.get("timer") == null) {
                    Log.w("MainActivity", "Assign new location for timer");
                    fragmentList.put("timer", TimerFragment.newInstance(userPosition));
                } else if (((TimerFragment) fragmentList.get("timer")).getArea() != null) {
                    Log.w("MainActivity", "Open timer");
                }
                fragmentName = "timer";
            } else {
                Log.w("MainActivity", "userPosition = null");
            }
        } else if (id == R.id.nav_memo) {
            fragmentName = "memo";
        } else if (id == R.id.nav_history) {
            fragmentName = "history";
        } else if (id == R.id.nav_report) {
            showReportDialog();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_settings) {
            fragmentName = "settings";
        }

        if (!fragmentName.equals("")) {
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_container, fragmentList.get(fragmentName)).commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }


    public void showReportDialog() {
        String[] EDIT_CHOICE = {"Parking Area Info", "Technical Issue"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What to report?")
                .setItems(EDIT_CHOICE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_container, fragmentList.get("info-report")).commit();
                                break;
                            case 1:
                                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame_container, fragmentList.get("issue-report")).commit();
                                break;
                        }
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });
        builder.create().show();
    }


    public void setUserPosition(LatLng userPos) {
        userPosition = userPos;
    }

    @Override
    protected void onResume() {
        Log.e("Status", "App resumed");
        super.onResume();
        HistoryContent.init(this);
        try {
            String action = getIntent().getAction();
            if (action != null) {
                checkForAction(action);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Problem consuming action from intent", e);
        }
        if (this.getIntent().getStringExtra("Notification") != null) {
            Log.w("onResume", this.getIntent().getStringExtra("Notification"));
        }

    }

    @Override
    protected void onStart() {
        Log.e("Status", "App started");
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        Log.e("Status", "App stopped");
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            // Disconnect Google API Client if available and connected
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        Log.e("Status", "App paused");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.e("Status", "App destroyed");
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Place request for permission here when targetSdk = 23
            return;
        }
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            // Call Location Services
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(1000)
                    .setFastestInterval(200);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, ((MapFragment) fragmentList.get("map")).getLocationListener());
        } else {
            Log.e("Location Request", "FAILED");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private class MyConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            // some error massage
            Log.e("MainActivity", "Connection failed " + connectionResult.getErrorMessage());
        }
    }

    private void getUserDetailsFromFB() {

        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture");


        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {

                            SettingsFragment.response = response;
                            Log.w("Response", response.getRawResponse());

                            email = response.getJSONObject().getString("email");
                            //mEmail.setText(email);
                            Log.w("ParseUser", email);

                            name = response.getJSONObject().getString("name");
                            //mName.setText(name);
                            Log.w("ParseUser", name);

                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");

                            if(data.getBoolean("is_silhouette")){
                                // TODO: set a silhouette image instead
                            }else{
                                String pictureUrl = "https://graph.facebook.com/" + response.getJSONObject().getString("id") + "/picture?type=large";

                                Log.w("Profile pic", "url: " + pictureUrl);

                                new ProfilePhotoAsync(pictureUrl).execute();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }

    public static Bitmap DownloadImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("IMAGE", "Error getting bitmap", e);
        }
        return bm;
    }

    class ProfilePhotoAsync extends AsyncTask<String, String, String> {
        String url;

        public ProfilePhotoAsync(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            // Fetching data from URI and storing in bitmap
            profile = DownloadImageBitmap(url);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //mProfile.setImageBitmap(bitmap);
            //saveNewUser();
        }
    }
}