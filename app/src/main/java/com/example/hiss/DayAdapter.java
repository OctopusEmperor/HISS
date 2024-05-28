package com.example.hiss;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DayAdapter extends RecyclerView.Adapter<DayViewHolder>{

    private final ArrayList<String> times;
    private final ArrayList<String> events;
    private final OnItemListener onItemListener;

    public DayAdapter(ArrayList<String> times, ArrayList<String> events, OnItemListener onItemListener) {
        this.times = times;
        this.events = events;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.time_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new DayViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        holder.time.setText(times.get(position));
        holder.event.setText(events.get(position));
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public interface OnItemListener{
        void onItemClick(int position, String event, String time, Button eventBtn);
    }
}
