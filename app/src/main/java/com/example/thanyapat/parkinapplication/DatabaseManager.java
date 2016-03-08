package com.example.thanyapat.parkinapplication;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Thanyapat on 1/24/2016 AD.
 */
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
}
