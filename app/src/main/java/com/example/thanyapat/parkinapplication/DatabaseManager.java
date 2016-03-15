package com.example.thanyapat.parkinapplication;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
                }
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
