package com.example.hiss;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DayAdapter extends RecyclerView.Adapter<DayViewHolder>{

    private final String[] times;
    private final String[] events;
    private final OnItemListener onItemListener;

    public DayAdapter(String[] times, String[] events, OnItemListener onItemListener) {
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
        holder.time.setText(times[position]);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface OnItemListener{
        void onItemClick(int position, String event, String time);
    }
}
