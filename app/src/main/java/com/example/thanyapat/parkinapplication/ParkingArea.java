package com.example.thanyapat.parkinapplication;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Tong on 9/11/2015. Edited by Thanyapat on 14/02/2016
 */
public class ParkingArea implements Parcelable, Serializable {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String address;
    private JSONObject price;
    private int capacity;
    private JSONArray openTime;
    private JSONArray closeTime;
    private String picUrl;
    private int freeTimeInMinute;

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(
                new String[]{this.id
                        , this.name
                        , "" + this.latitude
                        , "" + this.longitude
                        , this.address
                        , this.price!=null ? this.price.toString() : ""
                        , "" + this.capacity
                        , this.openTime!=null ? this.openTime.toString() : ""
                        , this.closeTime!=null ? this.closeTime.toString() : ""
                        , this.picUrl
                        , ""+freeTimeInMinute});
    }

    public ParkingArea(Parcel in) {
        String[] data = new String[11];
        try {
            in.readStringArray(data);
            this.id = data[0];
            this.name = data[1];
            this.latitude = Double.parseDouble(data[2]);
            this.longitude = Double.parseDouble(data[3]);
            this.address = data[4];
            this.price = data[5].equals("") ? null : new JSONObject(data[5]);
            this.capacity = Integer.parseInt(data[6]);
            this.openTime =  data[7].equals("") ? null : new JSONArray(data[7]);
            this.closeTime = data[8].equals("") ? null : new JSONArray(data[8]);
            this.picUrl = data[9];
            this.freeTimeInMinute = Integer.parseInt(data[10]);
        }catch(JSONException e ){
            Log.e("String_to_JSONObject", e.getMessage());
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ParkingArea createFromParcel(Parcel in) {
            return new ParkingArea(in);
        }

        public ParkingArea[] newArray(int size) {
            return new ParkingArea[size];
        }
    };

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
        this.openTime = object.getJSONArray("openTime");
        this.closeTime = object.getJSONArray("closeTime");
        this.picUrl = object.getString("picURL");
        this.freeTimeInMinute = object.getInt("freeTimeInMinute");
    }

    public String getId(){ return id;}

    public String getName(){ return name; }

    public JSONObject getPrice(){ return price; }

    public String getAddress(){ return address; }

    public int getCapacity(){ return capacity; }

    public double getLat(){ return latitude; }

    public double getLong(){ return  longitude; }

    public JSONArray getOpenTime(){ return openTime; }

    public JSONArray getCloseTime(){ return closeTime; }

    public String getPicURL(){ return picUrl; }

    public int getFreeHour(){ return freeTimeInMinute; }

}
