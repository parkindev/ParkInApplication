package com.example.thanyapat.parkinapplication;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class IssueReportFragment extends Fragment {
    private static final String TAG = "IssueReportFragment";

    private View rootView;
    public IssueReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_issue_report, container, false);
        MainActivity.navigationView.getMenu().getItem(4).setChecked(true);
        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setActionBarTitle("REPORT");
        getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString(SettingsFragment.CURRENT_FRAGMENT, TAG).commit();
        ((MainActivity)getActivity()).changeMenuIcon(R.drawable.submit_icon);

        return rootView;
    }

    public void submit(){
        String username;
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            username = currentUser.getUsername();
        } else {
            username = "null";
        }
        EditText typeEdit = (EditText)rootView.findViewById(R.id.editText_type);
        EditText problemEdit = (EditText)rootView.findViewById(R.id.editText_problem);
        if(!typeEdit.getText().toString().equals("") && !problemEdit.getText().toString().equals("")){
            DatabaseManager.putIssueReport(typeEdit.getText().toString(),problemEdit.getText().toString(),username);
            typeEdit.setText("");
            problemEdit.setText("");
            Snackbar.make(rootView
                    , "Thank You for your suggestion!"
                    , Snackbar.LENGTH_LONG)
                    .show();
        }else{
            Snackbar.make(rootView
                    , "Please fill both fields."
                    , Snackbar.LENGTH_LONG)
                    .show();
        }
    }

}
