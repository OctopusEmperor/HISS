package com.example.hiss;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>  {

    // List of days in the month
    private final ArrayList<String> daysOfMonth;
    // Listener for item clicks
    private final OnItemListener onItemListener;
    // Statuses indicating whether each day has events
    private ArrayList<Boolean> statuses;

    // Constructor for CalendarAdapter
    public CalendarAdapter(ArrayList<String> daysOfMonth, ArrayList<Boolean> statuses,OnItemListener onItemListener)
    {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.statuses = statuses;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // Inflate the calendar cell layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams ();
        // Set the height of each cell to be 1/6th of the parent height
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        // Set the text for the day of the month
        holder.dayOfMonth.setText(daysOfMonth.get(position));
        // Check if the day has events
        if (statuses.get(position)) {
            Log.d("ContentValues", "Events on day " + daysOfMonth.get(position));
            // Set the background for days with events
            holder.dayOfMonth.setBackgroundResource(R.drawable.daywithevent);
        }
    }

    @Override
    public int getItemCount() {
        // Return the number of days in the month
        return daysOfMonth.size();
    }

    // Interface for item click listener
    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }
}
