package com.example.thanyapat.parkinapplication.History;

import android.content.Context;
import android.util.Log;

import com.example.thanyapat.parkinapplication.InternalStorage;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryContent implements Serializable{

    public static List<HistoryItem> ITEMS;

    public static void init(Context context){
        try {
            ITEMS = InternalStorage.readHistoryObject(context) == null ? new ArrayList<HistoryItem>() : (ArrayList<HistoryContent.HistoryItem>) InternalStorage.readHistoryObject(context);
        } catch (IOException e) {
            Log.e("HistoryFragment", e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("HistoryFragment", e.getMessage());
        }
    }

    public static void addItem(HistoryItem item) {
        ITEMS.add(item);
    }

    public static void removeItem(int position){
        ITEMS.remove(position);
    }

    public static void removeAllItem(){
        ITEMS.clear();
    }

    /**
     * A History item representing a piece of content.
     */
    public static class HistoryItem implements Serializable{
        public final String date;
        public final String name;
        public final double latitude;
        public final double longitude;

        public HistoryItem(String name, LatLng latLng) {
            this.date = getCurrentDate();
            this.name = name;
            this.latitude = latLng.latitude;
            this.longitude = latLng.longitude;
        }

        @Override
        public String toString() {
            return name;
        }

        private String getCurrentDate(){
            Calendar c = Calendar.getInstance();
            return getMonthForInt(c.get(Calendar.MONTH))+" "+c.get(Calendar.DAY_OF_MONTH);
        }

        String getMonthForInt(int num) {
            String month = "";
            switch(num){
                case 0:
                    month = "JAN";
                    break;
                case 1:
                    month = "FEB";
                    break;
                case 2:
                    month = "MAR";
                    break;
                case 3:
                    month = "APR";
                    break;
                case 4:
                    month = "MAY";
                    break;
                case 5:
                    month = "JUN";
                    break;
                case 6:
                    month = "JUL";
                    break;
                case 7:
                    month = "AUG";
                    break;
                case 8:
                    month = "SEP";
                    break;
                case 9:
                    month = "OCT";
                    break;
                case 10:
                    month = "NOV";
                    break;
                case 11:
                    month = "DEC";
                    break;
            }
            return month;
        }
    }
}
