package com.example.thanyapat.parkinapplication;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.Calendar;


public class ApplicationUtils {

    public static int durationToPrice(ParkingArea area, int hours) {
        int priceSum = 0;
        try {
            for(int j = 0; j < hours ; j++){
                priceSum += (int)area.getPrice().getJSONArray
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

    public static String getOpenTime(ParkingArea area){
        String openTime = "";
        JSONArray jsonArray = area.getOpenTime();
        try {
            openTime = jsonArray==null ? "" : jsonArray.getString(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return openTime;
    }

    public static ParkingArea getClosest(LatLng userPos){
        ParkingArea closest=null;
        for(ParkingArea p : MainActivity.areaList) {
            if (closest == null) {
                closest = p;
            } else if(CalculationByDistance(new LatLng(p.getLat()      , p.getLong()     ), userPos)
                    < CalculationByDistance(new LatLng(closest.getLat(),closest.getLong()),userPos)){
                closest = p;
            }
        }
        return closest;
    }

    public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

}
