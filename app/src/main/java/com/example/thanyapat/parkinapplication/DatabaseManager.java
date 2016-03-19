package com.example.thanyapat.parkinapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class DatabaseManager {

    private MapFragment main;

    public DatabaseManager(MapFragment main){
        this.main = main;
    }

    public void queryAll(String dbName){
        if(((MainActivity)main.getActivity()).getAreaList().size()!=0){
            main.markAll();
            return;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(dbName);
        query.whereExists("coordinate");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> recieveList, ParseException e) {
                if (e == null) {
                    Log.w("Status", "Retrieved " + recieveList.size() + " Parking areas");
                    ((MainActivity)main.getActivity()).putList(recieveList);
                    main.markAll();
                } else {
                    Log.e("Query result", "Error: " + e.getMessage());
                    switch (e.getCode()) {
                        case ParseException.INVALID_SESSION_TOKEN:
                            handleInvalidSessionToken();
                            break;
                        // Other Parse API errors that you want to explicitly handle
                    }
                }
            }
            private void handleInvalidSessionToken() {
                new AlertDialog.Builder(main.getActivity())
                        .setMessage("Session is no longer valid, please log out and log in again.")
                   .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
                        Intent intent = new Intent(main.getActivity(), FacebookLoginActivity.class);
                        intent.putExtra(FacebookLoginActivity.IS_LOGOUT, true);
                        main.getActivity().startActivity(intent);
                        main.getActivity().finish();
                    }
                }).create().show();

                //--------------------------------------
                // Option #2: Show login screen so user can re-authenticate.
                //--------------------------------------
                // You may want this if the logout button could be inaccessible in the UI.
                //
                // startActivityForResult(new ParseLoginBuilder(getActivity()).build(), 0);
            }
        });
    }

    public static void putHistory(String areaName, LatLng userLocation, boolean isLocationChanged, String username){
        ParseObject history = new ParseObject("History");
        history.put("userLocation", new ParseGeoPoint(userLocation.latitude,userLocation.longitude));
        history.put("areaName", areaName);
        history.put("isLocationChanged", isLocationChanged);
        history.put("username", username);
        history.saveInBackground();
        Log.w("DatabaseManager", "user press timer at location: " + areaName + " at " + userLocation.toString());
    }

    public static void putDataReport(String location, String openHours, String availability,int capacity,int parkingFee, String username){
        ParseObject report = new ParseObject("DataReport");
        report.put("Location",location);
        report.put("openHours", openHours);
        report.put("Capacity", capacity);
        report.put("parkingFee", parkingFee);
        report.put("Availability", availability);
        report.put("username", username);
        report.saveInBackground();
    }

    public static void putIssueReport(String issueName, String issueDescription, String username){
        ParseObject report = new ParseObject("IssueReport");
        report.put("issueName", issueName);
        report.put("issueDescription", issueDescription);
        report.put("username", username);
        report.saveInBackground();
    }


}
