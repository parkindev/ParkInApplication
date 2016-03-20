package com.example.thanyapat.parkinapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.GraphResponse;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.parse.ParseUser;

public class SettingsFragment extends StatedFragment {

    public static String WILL_IMAGE_SAVED = "willImageSaved";
    public static String IS_NOTIFICATION_ENABLED = "isNotificationEnabled";
    public static String CURRENT_FRAGMENT = "current-fragment";
    private static final String TAG = "SettingsFragment";

    public GraphResponse response;
    private View rootView;
    private static TextView mEmail;
    private static TextView mName;
    private static CircularImageView mProfile;


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setActionBarTitle("SETTINGS");
        getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString(SettingsFragment.CURRENT_FRAGMENT, TAG).commit();
        ((MainActivity)getActivity()).changeMenuIcon(R.drawable.blank_icon);

        mEmail = (TextView)rootView.findViewById(R.id.textView_email);
        mEmail.setText(MainActivity.email);
        mName = (TextView)rootView.findViewById(R.id.textView_name);
        mName.setText(MainActivity.name);
        mProfile = (CircularImageView) rootView.findViewById(R.id.imgView_profile);
        mProfile.setImageBitmap(MainActivity.profile);

        Switch notiSw = (Switch) rootView.findViewById(R.id.switch_notification);
        notiSw.setChecked(getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(IS_NOTIFICATION_ENABLED,true));
        notiSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getActivity().getPreferences(Context.MODE_PRIVATE).edit().putBoolean(IS_NOTIFICATION_ENABLED, isChecked).commit();
                Log.w(TAG, "isNotificationEnabled set = "
                        + getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(IS_NOTIFICATION_ENABLED, true));
            }
        });
        Switch imgSw = (Switch) rootView.findViewById(R.id.switch_save_image);
        imgSw.setChecked(getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(WILL_IMAGE_SAVED, true));
        imgSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getActivity().getPreferences(Context.MODE_PRIVATE).edit().putBoolean(WILL_IMAGE_SAVED, isChecked).commit();
                Log.w(TAG, "willImageSaved set = "
                        + getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(WILL_IMAGE_SAVED, true));
            }
        });

        (rootView.findViewById(R.id.textView_sign_out)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsFragment.this.getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Log Out")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ParseUser.logOut();
                            Intent intent = new Intent(getActivity(), FacebookLoginActivity.class);
                            intent.putExtra(FacebookLoginActivity.IS_LOGOUT, true);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            }
        });
        return rootView;
    }

    public void setResponse(GraphResponse response){
        this.response = response;
    }

    public GraphResponse getResponse(){
        return this.response;
    }
}
