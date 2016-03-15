package com.example.thanyapat.parkinapplication;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class IssueReportFragment extends Fragment {

    private View rootView;
    private Button submitBtn;
    public IssueReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_issue_report, container, false);

        submitBtn = (Button) rootView.findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Snackbar.make(rootView.findViewById(R.id.view_issue_report)
                            , "Thank You for your suggestion!"
                            , Snackbar.LENGTH_LONG)
                            .show();
                }else{
                    Snackbar.make(rootView.findViewById(R.id.view_info_report)
                            , "Please fill both fields."
                            , Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });

        return rootView;
    }

}
