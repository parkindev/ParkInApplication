package com.example.thanyapat.parkinapplication;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.TimePicker;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;

import org.json.JSONException;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.grantland.widget.AutofitTextView;

public class TimerFragment extends StatedFragment {
   private static final String KEY_STRING = "PARKING_AREA";

    private View rootView;
    private Button startBtn;
    private TextView arrivalTextView;
    private AutofitTextView locationTextView;
    private TextView parkingTextView;
    private static ParkingArea area=null;
    private TextView location_label;
    private TextView editBtn;
    private boolean isTimeSet=false;
    private static boolean isCounting=false;
    private static int hour = 0 ;
    private static int min = 0 ;
    private static int sec = 0 ;
    private Timer timer=null;
    private static String timerElapsed=""+hour + "H " + min + "M " + sec + "S ";
    private Handler handler;
    private static String arrivalTime ="";
    private final static int COLOR_GREEN = Color.rgb(0,136,43);
    private final static int COLOR_RED = Color.rgb(255,0,0);
    private int priceSum;


    public TimerFragment() {
        // Required empty public constructor
    }

    public static TimerFragment newInstance(Parcelable area) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_STRING, area);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("TimerFragment", "onCreate");

        Bundle bundle = getArguments();
        if(bundle != null) {
            area = bundle.getParcelable(KEY_STRING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_timer, container, false);
        return rootView;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(area==null){
            Log.w("TimerFragment","area = null");
            return;
        }
        initiateAttributes();
    }

    private void initiateAttributes(){
        handler = new TimeChangeHandler();

        editBtn = (TextView) rootView.findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                String[] EDIT_CHOICE = {"Arrival Time", "Location"};
                Log.w("TimerFragment", "EditButton clicked");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Please Select")
                        .setItems(EDIT_CHOICE, new DialogClickListener());
                builder.create().show();
            }
        });

        arrivalTextView = (TextView) rootView.findViewById(R.id.tv_arrival);

        locationTextView = (AutofitTextView) rootView.findViewById(R.id.tv_location);
        locationTextView.setMaxLines(1);
        locationTextView.setTextSize(500);
        locationTextView.setText(area.getName());
        location_label = (TextView) rootView.findViewById(R.id.tv_location_label);

        parkingTextView = (TextView) rootView.findViewById(R.id.tv_cumulative_fee);

        startBtn = (Button) rootView.findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new TimerButtonClickListener());
    }

    public void onResume(){
        super.onResume();
        if(isCounting){ //
            startBtn.setBackgroundColor(COLOR_RED);
            startBtn.setText("STOP");
            location_label.setText("Duration");
            rootView.findViewById(R.id.view_after).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.view_before).setVisibility(View.GONE);
            parkingTextView.setText(String.valueOf(priceSum));
            arrivalTextView.setText(arrivalTime);
        } else if(!isCounting){ // if Timer not started --> arrival time = current time
            setCurrentTime();
            arrivalTextView.setText(arrivalTime);
            Log.w("TimerFragment", "Timer for " + area.getName() + ", arrival time is " + arrivalTime);
        } else if(isTimeSet){
            arrivalTextView.setText(arrivalTime);
            Log.w("TimerFragment", "Timer for " + area.getName() + ", arrival time is " + arrivalTime);
        } else if(!isTimeSet){
            setCurrentTime();
        }
    }

    private void setCurrentTime(){
        Calendar c = Calendar.getInstance();
        if(c.get(Calendar.MINUTE) < 10){
            arrivalTime = ""+c.get(Calendar.HOUR_OF_DAY)+":0"+c.get(Calendar.MINUTE);
        }else{
            arrivalTime = ""+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
        }
        arrivalTextView.setText(arrivalTime);
        Log.w("TimerFragment", "Set arrival time to current time : " + arrivalTime);
    }

    public void updateFee() {
        // fetch the price and increment number in attribute [parkingFee]
        priceSum = 0;
        if (area.getPrice() != null) {
                for (int j = 0; j < min; j++) {
                    priceSum +=  getPrice(j);
                }
            Log.w("TimerFragment", "Set cumulative fee to " + priceSum + " Baht");
            parkingTextView.setText(String.valueOf(priceSum));
        }else{
            Log.w("TimerFragment", "There's no pricing information for " + area.getName() + " parking area");
        }
        ((MainActivity)getActivity()).issueNotification("NAV_TO_TIMER", "Total fee : " + priceSum + " Baht"); // move into if when finish
    }

    private int getPrice(int index){
        try {
            return (int) area.getPrice().getJSONArray
                    ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                            || (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                            ? "weekEndPrice" : "weekDayPrice")
                    .getJSONArray(Integer.parseInt(arrivalTime.substring(0, arrivalTime.indexOf(":"))))
                    .get(index);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

   public ParkingArea getArea(){
        return area;
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        // For example:
        //outState.putString("text", tvSample.getText().toString());
        //outState.putParcelable("Location",area);
        //outState.putString("Time", arrivalTime);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        // For example:
        //tvSample.setText(savedInstanceState.getString("text"));
    }

    class TimerButtonClickListener implements  View.OnClickListener {
            @Override
            public void onClick(View v) {
                if(isCounting){ // if Timer is counting --> stop it
                    switchUI();
                    isCounting = false;
                    location_label.setText("Location");
                    timerStartStop(isCounting);

                    setCurrentTime();
                }else { // if Timer is not counting --> start it
                    hour=0;min=0;sec=0;
                    timerElapsed = hour+"H "+min+"M "+sec+"S";
                    locationTextView.setText(timerElapsed);
                    switchUI();
                    isCounting = true;
                    location_label.setText("Duration");
                    timerStartStop(isCounting);
                }
            }
        public void timerStartStop(boolean isCounting){
            if(isCounting){
                timer = new Timer();
                locationTextView.setText(timerElapsed);
                // wait one minute and then update the TextView
                Log.w("Timer", "Initiate timer");
                timer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        handler.obtainMessage(1).sendToTarget();
                    }
                }, 1000, 1000);

            }else{
                timer.cancel();
                locationTextView.setText(area.getName());
                locationTextView.setMaxLines(1);
                locationTextView.setTextSize(500);
            }
        }
        public void switchUI(){
            if(isCounting){
                parkingTextView.setText("0");
                editBtn.setEnabled(true);
                startBtn.setBackgroundColor(COLOR_GREEN);
                startBtn.setText("START");
                rootView.findViewById(R.id.view_after).setVisibility(View.GONE);
                rootView.findViewById(R.id.view_before).setVisibility(View.VISIBLE);
            }else {
                editBtn.setEnabled(false);
                startBtn.setBackgroundColor(COLOR_RED);
                startBtn.setText("STOP");
                rootView.findViewById(R.id.view_after).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.view_before).setVisibility(View.GONE);
            }
        }

    }

    class DialogClickListener implements DialogInterface.OnClickListener{
        private List<ParkingArea> areaList = ((MainActivity)TimerFragment.this.getActivity()).getAreaList();
        private String[] places = new String[areaList.size()];

        public void onClick(DialogInterface dialog, int which) {
            // The 'which' argument contains the index position
            // of the selected item
            switch(which){
                case 0:
                    showTimePicker();
                    break;
                case 1:
                    showItemDialog();
                    break;
            }
        }
        private void showTimePicker(){
            isTimeSet = true;
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(TimerFragment.this.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    if(selectedMinute < 10){
                        arrivalTime = ""+selectedHour + ":0" + selectedMinute;
                    }else{
                        arrivalTime = ""+selectedHour + ":" + selectedMinute;
                    }
                    arrivalTextView.setText(arrivalTime);
                    isTimeSet = true;
                }
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
        private void showItemDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Pick your parking place")
                    .setItems(getPlacesArray(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position of the selected item
                            // TODO: send new area to backend
                            setArea(areaList.get(which));
                            locationTextView.setText(area.getName());
                        }
                    });
            builder.create().show();
        }

        public void setArea(ParkingArea a){
            area = a;
        }

        private String[] getPlacesArray(){

            int i=0;
            for (ParkingArea a : areaList){
                places[i] = a.getName();
                i++;
            }
            return places;
        }
    }

    class TimeChangeHandler extends Handler{
        public void handleMessage(Message msg) {
            updateTimer();
        }
        public void updateTimer(){
            sec++;
            if (sec == 60) {
                min++;
                if(min == 60){
                    hour++;
                    min=0;
                    //updateFee();
                }
                updateFee();
                sec=0;
            }
            Log.w("Timer","parking duration is "+hour+"H "+min+"M "+sec+"S");
            timerElapsed = hour+"H "+min+"M "+sec+"S";
            locationTextView.setText(timerElapsed);
        }
    }

}


