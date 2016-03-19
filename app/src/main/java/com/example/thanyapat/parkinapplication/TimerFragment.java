package com.example.thanyapat.parkinapplication;

import android.app.TimePickerDialog;
import android.content.Context;
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

import com.example.thanyapat.parkinapplication.History.HistoryContent;
import com.google.android.gms.maps.model.LatLng;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;

import org.json.JSONException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.thanyapat.parkinapplication.History.HistoryContent.HistoryItem;
import com.parse.ParseUser;

import me.grantland.widget.AutofitTextView;

public class TimerFragment extends StatedFragment {

    private static final String USER_LOCATION = "user-location";
    private static final String TAG = "TimerFragment";

    
    private View rootView;
    private Button startBtn;
    private TextView arrivalTextView;
    private AutofitTextView locationTextView;
    private TextView parkingTextView;
    private static ParkingArea area = null;
    private TextView location_label;
    private boolean isTimeSet = false;
    private static boolean isCounting = false;
    private static int hour = 0;
    private static int min = 0;
    private static int sec = 0;
    private Timer timer = null;
    private static String timerElapsed = "" + hour + "H " + min + "M " + sec + "S ";
    private Handler handler;
    private static String arrivalTime = "";
    private final static int COLOR_GREEN = Color.rgb(0, 136, 43);
    private final static int COLOR_RED = Color.rgb(255, 0, 0);
    private int priceSum = 0;
    private boolean isLocationChanged;

    public TimerFragment() {
        // Required empty public constructor
    }

    public static TimerFragment newInstance(LatLng userPosition) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putParcelable(USER_LOCATION, userPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate");

        Bundle bundle = getArguments();
        if (bundle != null) {
            area = ApplicationUtils.getClosest((LatLng) bundle.getParcelable(USER_LOCATION));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setActionBarTitle("TIMER");
        MainActivity.navigationView.getMenu().getItem(1).setChecked(true);
        getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString(SettingsFragment.CURRENT_FRAGMENT, TAG).commit();
        ((MainActivity)getActivity()).changeMenuIcon(R.drawable.edit_icon);

        rootView = inflater.inflate(R.layout.fragment_timer, container, false);
        return rootView;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (area == null) {
            Log.w(TAG, "area = null");
            return;
        }
        initiateAttributes();
    }

    private void initiateAttributes() {
        handler = new TimeChangeHandler();
        isLocationChanged = false;

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

    public void edit(){
        String[] EDIT_CHOICE = {"Arrival Time", "Location"};
        Log.w(TAG, "EditButton clicked");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please Select")
                .setItems(EDIT_CHOICE, new DialogClickListener());
        builder.create().show();
    }

    public void onResume() {
        super.onResume();
        if (isCounting) {
            startBtn.setBackgroundColor(COLOR_RED);
            startBtn.setText("STOP");
            location_label.setText("Duration");
            rootView.findViewById(R.id.view_after).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.view_before).setVisibility(View.GONE);
            parkingTextView.setText(String.valueOf(priceSum));
            arrivalTextView.setText(arrivalTime);
        } else if (!isCounting) { // if Timer not started --> arrival time = current time
            setCurrentTime();
            arrivalTextView.setText(arrivalTime);
            Log.w(TAG, "Timer for " + area.getName() + ", arrival time is " + arrivalTime);
        } else if (isTimeSet) {
            arrivalTextView.setText(arrivalTime);
            Log.w(TAG, "Timer for " + area.getName() + ", arrival time is " + arrivalTime);
        } else if (!isTimeSet) {
            setCurrentTime();
        }
    }

    private void setCurrentTime() {
        Calendar c = Calendar.getInstance();
        if (c.get(Calendar.MINUTE) < 10) {
            arrivalTime = "" + c.get(Calendar.HOUR_OF_DAY) + ":0" + c.get(Calendar.MINUTE);
        } else {
            arrivalTime = "" + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        }
        arrivalTextView.setText(arrivalTime);
        Log.w(TAG, "Set arrival time to current time : " + arrivalTime);
    }

    public ParkingArea getArea() {
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

    class TimerButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isCounting) { // if Timer is counting --> stop it
                switchUI();
                isCounting = false;
                location_label.setText("Location");
                timerStartStop(isCounting);

                setCurrentTime();
            } else { // if Timer is not counting --> start it
                hour = 0;
                min = 0;
                sec = 0;
                timerElapsed = hour + "H " + min + "M " + sec + "S";
                locationTextView.setText(timerElapsed);
                switchUI();
                isCounting = true;
                location_label.setText("Duration");
                // TODO: update History when user press timer or when timer notify user?
                String username;
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    username = currentUser.getUsername();
                } else {
                    username = "null";
                }
                DatabaseManager.putHistory(area.getName(), (LatLng) getArguments().getParcelable(USER_LOCATION), isLocationChanged, username);
                HistoryContent.addItem(new HistoryItem(area.getName(), new LatLng(area.getLat(), area.getLong())));
                try {
                    // Save the list of entries to internal storage
                    InternalStorage.writeHistoryObject(TimerFragment.this.getContext());
                } catch (IOException e) {
                    Log.e("TimerFragment", e.getMessage());
                }
                timerStartStop(isCounting);
            }
        }

        public void timerStartStop(boolean isCounting) {
            if (isCounting) {
                timer = new Timer();
                locationTextView.setText(timerElapsed);
                // wait one minute and then update the TextView
                Log.w("Timer", "Initiate timer");
                timer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        handler.obtainMessage(1).sendToTarget();
                    }
                }, 1000, 1000);

            } else {
                timer.cancel();
                locationTextView.setText(area.getName());
                locationTextView.setMaxLines(1);
                locationTextView.setTextSize(500);
            }
        }

        public void switchUI() {
            if (isCounting) {
                parkingTextView.setText("0");
                startBtn.setBackgroundColor(COLOR_GREEN);
                startBtn.setText("START");
                rootView.findViewById(R.id.view_after).setVisibility(View.GONE);
                rootView.findViewById(R.id.view_before).setVisibility(View.VISIBLE);
            } else {
                startBtn.setBackgroundColor(COLOR_RED);
                startBtn.setText("STOP");
                rootView.findViewById(R.id.view_after).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.view_before).setVisibility(View.GONE);
            }
        }

    }

    class DialogClickListener implements DialogInterface.OnClickListener {
        private List<ParkingArea> areaList = ((MainActivity) TimerFragment.this.getActivity()).getAreaList();
        private String[] places = new String[areaList.size()];

        public void onClick(DialogInterface dialog, int which) {
            // The 'which' argument contains the index position
            // of the selected item
            switch (which) {
                case 0:
                    showTimePicker();
                    break;
                case 1:
                    showItemDialog();
                    break;
            }
        }

        private void showTimePicker() {
            isTimeSet = true;
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(TimerFragment.this.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    if (selectedMinute < 10) {
                        arrivalTime = "" + selectedHour + ":0" + selectedMinute;
                    } else {
                        arrivalTime = "" + selectedHour + ":" + selectedMinute;
                    }
                    arrivalTextView.setText(arrivalTime);
                    isTimeSet = true;
                }
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }

        private void showItemDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Pick your parking place")
                    .setItems(getPlacesArray(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position of the selected item
                            isLocationChanged = true;
                            setArea(areaList.get(which));
                            locationTextView.setText(area.getName());
                        }
                    });
            builder.create().show();
        }

        public void setArea(ParkingArea a) {
            area = a;
        }

        private String[] getPlacesArray() {

            int i = 0;
            for (ParkingArea a : areaList) {
                places[i] = a.getName();
                i++;
            }
            return places;
        }
    }

    class TimeChangeHandler extends Handler {
        public void handleMessage(Message msg) {
            updateTimer();
        }

        public void updateTimer() {
            sec++;
            if (sec == 60) {
                min++;
                if (min == 60) {
                    hour++;
                    min = 0;
                    //updateFee();
                }
                if (area.getPrice() != null) {
                    priceSum = ApplicationUtils.durationToPrice(area, min);
                    parkingTextView.setText(String.valueOf(priceSum));
                }
                if(getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(SettingsFragment.IS_NOTIFICATION_ENABLED, true)) {
                    Log.w(TAG, "isNotificationEnabled = " + getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean("isNotificationEnabled", true));
                    ((MainActivity) getActivity()).issueNotification("NAV_TO_TIMER", "Total fee : " + priceSum + " Baht");
                }
                sec = 0;
            }
            Log.w("Timer", "parking duration is " + hour + "H " + min + "M " + sec + "S");
            timerElapsed = hour + "H " + min + "M " + sec + "S";
            locationTextView.setText(timerElapsed);
        }
    }

}


