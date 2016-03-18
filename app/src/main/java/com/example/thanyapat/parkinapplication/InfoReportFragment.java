package com.example.thanyapat.parkinapplication;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class InfoReportFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private View rootView;
    private boolean isSomethingChange = false;
    private EditText openEdit;
    private EditText capacityEdit;
    private EditText feeEdit;
    private String editedOpenHours = "";
    private int editedCapacity = -1;
    private int editedFee = -1;
    private String availability = "";
    private String selectedLocation = "";

    public InfoReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_info_report, container, false);

        MainActivity.navigationView.getMenu().getItem(4).setChecked(true);

        // Spinner element
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner_location);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> list = new ArrayList<String>();
        for(ParkingArea area : MainActivity.areaList){
            list.add(area.getName());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, list);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        //spinner.setSelection();

        // get Text if text is changed
        openEdit = (EditText) rootView.findViewById(R.id.editText_open);
        capacityEdit = (EditText) rootView.findViewById(R.id.editText_capacity);
        feeEdit = (EditText) rootView.findViewById(R.id.editText_fee);
        openEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editedOpenHours = s.toString();
                isSomethingChange = true;
            }
        });
        capacityEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editedCapacity = getInputFormat(s.toString());
                isSomethingChange = true;
            }
        });
        feeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editedFee = getInputFormat(s.toString());
                isSomethingChange = true;
            }
        });

        // init RadioButtons
        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.radio_opened) {
                    availability = "Opened";
                } else if (checkedId == R.id.radio_close_permanent) {
                    availability = "Close (Permanently)";
                } else if (checkedId == R.id.radio_close_renovation) {
                    availability = "Close (Renovation)";
                }
            }
        });

        // add submit button listener
        Button submitBtn = (Button) rootView.findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        String username = ParseUser.getCurrentUser() != null ? ParseUser.getCurrentUser().getUsername() : "null";
        if (isSomethingChange) {
            DatabaseManager.putDataReport(selectedLocation, editedOpenHours, availability, editedCapacity, editedFee, username);
        }
        resetData();
        Snackbar.make(rootView
                , "Thank You for your suggestion!"
                , Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        selectedLocation = parent.getItemAtPosition(position).toString();
        for(ParkingArea area : MainActivity.areaList){
            if(area.getName().equals(selectedLocation)){
                fillData(area);
            }
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {

    }

    private void fillData(ParkingArea area){
        openEdit.setText(ApplicationUtils.getOpenTime(area));
        capacityEdit.setText(""+area.getCapacity());
        // TODO: Parking Fee will be a preview like TimerFragment
        // feeEdit.setText(ParkingAreaUtils.durationToPrice());
        RadioButton openRadio = (RadioButton) rootView.findViewById(R.id.radio_opened);
        openRadio.setChecked(true);
    }

    private int getInputFormat(String text){
        int number;
        try{
            number = Integer.parseInt(text);
        }catch(NumberFormatException e){
            number = -1;
            e.printStackTrace();
        }
        return number;
    }

    private void resetData(){
        openEdit.setText("");
        capacityEdit.setText("");
        feeEdit.setText("");
    }

}
