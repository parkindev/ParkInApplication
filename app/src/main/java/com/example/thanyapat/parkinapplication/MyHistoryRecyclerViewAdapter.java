package com.example.thanyapat.parkinapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thanyapat.parkinapplication.History.HistoryContent.HistoryItem;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import me.grantland.widget.AutofitTextView;

public class MyHistoryRecyclerViewAdapter extends RecyclerView.Adapter<MyHistoryRecyclerViewAdapter.ViewHolder> {

    private final List<HistoryItem> mValues;
    private MainActivity main;

    public MyHistoryRecyclerViewAdapter(List<HistoryItem> items, MainActivity main) {
        mValues = items;
        this.main = main;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).date);
        holder.mContentView.setText(mValues.get(position).name);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected
                MainActivity.fragmentList.put("map", MapFragment.newInstance(new LatLng(mValues.get(position).latitude,mValues.get(position).longitude)));
                main.getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frame_container, MainActivity.fragmentList.get("map"))
                        .commit();
           }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final AutofitTextView mContentView;
        public HistoryItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.date);
            mContentView = (AutofitTextView) view.findViewById(R.id.place);

            mContentView.setMaxLines(1);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
