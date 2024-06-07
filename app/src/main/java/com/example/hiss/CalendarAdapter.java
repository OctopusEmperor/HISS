package com.example.hiss;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>  {

    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;
    private ArrayList<Boolean> statuses;
    DatabaseReference myRef;

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
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams ();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        holder.dayOfMonth.setText(daysOfMonth.get(position));
        if (statuses.get(position))
        {
            Log.d("ContentValues", "Events on day " +  daysOfMonth.get(position));
            holder.dayOfMonth.setBackgroundResource(R.drawable.daywithevent);
        }
        else {
            Log.d("ContentValues", "No events on day " +  daysOfMonth.get(position));
            holder.dayOfMonth.setBackgroundResource(R.drawable.calenderdayborder);
        }
    }

    @Override
    public int getItemCount()
    {
        return daysOfMonth.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, String dayText);
    }
}
