package com.example.thanyapat.parkinapplication;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tong on 9/11/2015. Edited by Thanyapat on 14/02/2016
 */
public class ParkingArea implements Parcelable {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String address;
    private JSONObject price;
    private int capacity;


    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(price!=null) {
            dest.writeStringArray(new String[]{this.id,
                    this.name,
                    "" + this.latitude,
                    "" + this.longitude,
                    this.address,
                    this.price.toString(),
                    "" + this.capacity});
        }else{
            dest.writeStringArray(new String[]{this.id,
                    this.name,
                    "" + this.latitude,
                    "" + this.longitude,
                    this.address,
                    "",
                    "" + this.capacity});
        }
    }
    public ParkingArea(Parcel in) {
        String[] data = new String[7];

        in.readStringArray(data);
        this.id = data[0];
        this.name = data[1];
        this.latitude = Double.parseDouble(data[2]);
        this.longitude = Double.parseDouble(data[3]);
        this.address = data[4];
        try {
            this.price = data[5].equals("") ? null : new JSONObject(data[5]);
        }catch(JSONException e ){
            Log.e("String_to_JSONObject", e.getMessage());
        }
        this.capacity = Integer.parseInt(data[6]);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ParkingArea createFromParcel(Parcel in) {
            return new ParkingArea(in);
        }

        public ParkingArea[] newArray(int size) {
            return new ParkingArea[size];
        }
    };

    public ParkingArea(String name, String address , double latitude, double longitude){
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public ParkingArea(ParseObject object){
        this.id = object.getObjectId();
        this.name = object.getString("name");
        this.latitude = object.getParseGeoPoint("coordinate").getLatitude();
        this.longitude = object.getParseGeoPoint("coordinate").getLongitude();
        //get price if not null
        this.price = object.getJSONObject("price") != null ? object.getJSONObject("price") : null;
        //get address if not null
        this.address = object.getString("address")!= null ? object.getString("address"):null;
        //get capacity if not null
        this.capacity = object.getInt("capacity");

    }

    public String getName(){
        return name;
    }

    public JSONObject getPrice(){
        return price;
    }

    public String getAddress(){
        return address;
    }

    public int getCapacity(){
        return capacity;
    }

    public double getLat(){
        return latitude;
    }

    public double getLong(){
        return  longitude;
    }

}
