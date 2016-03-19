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


public class HistoryFragment extends Fragment implements SwipeableRecyclerViewTouchListener.SwipeListener {
    private static final String TAG = "HistoryFragment";

    private List<HistoryContent.HistoryItem> items;
    private MyHistoryRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_list, container, false);
        MainActivity.navigationView.getMenu().getItem(3).setChecked(true);
        ((MainActivity)getActivity()).setActionBarTitle("HISTORY");
        getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString(SettingsFragment.CURRENT_FRAGMENT, TAG).commit();
        ((MainActivity)getActivity()).changeMenuIcon(R.drawable.clear_icon);

        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        items = HistoryContent.ITEMS;
        adapter = new MyHistoryRecyclerViewAdapter(items, (MainActivity) this.getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new SwipeableRecyclerViewTouchListener(recyclerView, this));

        return rootView;
    }

    public void clear(){
        Log.w(TAG, "Clear Button Clicked");
        if (!items.isEmpty()) {
            new AlertDialog.Builder(HistoryFragment.this.getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Clearing All History")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.w(TAG, "size before delete = " + items.size());
                            HistoryContent.removeAllItem();
                            adapter.notifyItemRangeRemoved(0, items.size());
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

    private void dismissAndRefresh(int[] reverseSortedPositions) {
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
