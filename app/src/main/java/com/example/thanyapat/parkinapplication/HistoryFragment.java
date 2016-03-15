package com.example.thanyapat.parkinapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.thanyapat.parkinapplication.History.HistoryContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment implements SwipeableRecyclerViewTouchListener.SwipeListener{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private List<HistoryContent.HistoryItem> items;
    private MyHistoryRecyclerViewAdapter adapter;
    private Button clearBtn;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryFragment() {
    }
    @SuppressWarnings("unused")
    public static HistoryFragment newInstance(int columnCount) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("HistoryFragment", "onCreate");
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_list, container, false);
        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        items = HistoryContent.ITEMS;
        adapter = new MyHistoryRecyclerViewAdapter(items,(MainActivity)this.getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new SwipeableRecyclerViewTouchListener(recyclerView, this));
        clearBtn = (Button) rootView.findViewById(R.id.clear_btn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("HistoryFragment", "Clear Button Clicked");
                new AlertDialog.Builder(HistoryFragment.this.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Clearing All History")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int listSize = HistoryContent.ITEMS.size();
                                Log.w("HistoryFragment", "size before delete = " + listSize);
                                HistoryContent.removeAllItem();
                                adapter.notifyItemRangeRemoved(0, listSize);
                                adapter.notifyDataSetChanged();
                                try {
                                    // Save the list of entries to internal storage
                                    InternalStorage.writeHistoryObject(HistoryFragment.this.getContext());
                                } catch (IOException e) {
                                    Log.e("TimerFragment", e.getMessage());
                                }
                                // refresh fragment
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .remove(HistoryFragment.this)
                                        .addToBackStack(null)
                                        .replace(R.id.frame_container, MainActivity.fragmentList.get("history"))
                                        .commit();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean canSwipeLeft(int position) {
        return true;
    }

    @Override
    public boolean canSwipeRight(int position) {
        return true;
    }

    @Override
    public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
        dismissAndRefresh(reverseSortedPositions);
    }

    @Override
    public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
        dismissAndRefresh(reverseSortedPositions);
    }

    private void dismissAndRefresh(int[] reverseSortedPositions){
        for (int position : reverseSortedPositions) {
            HistoryContent.removeItem(position);
            adapter.notifyItemRemoved(position);
        }
        adapter.notifyDataSetChanged();
        try {
            // Save the list of entries to internal storage
            InternalStorage.writeHistoryObject(HistoryFragment.this.getContext());
        } catch (IOException e) {
            Log.e("TimerFragment", e.getMessage());
        }
        // refresh fragment
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(HistoryFragment.this)
                .addToBackStack(null)
                .replace(R.id.frame_container, MainActivity.fragmentList.get("history"))
                .commit();
    }
}
