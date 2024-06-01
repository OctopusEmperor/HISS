package com.example.hiss;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> implements ValueEventListener {

    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;
    DatabaseReference myRef;
    ArrayList<DayStatus> dayStatusList;

    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener)
    {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("DayStatus");
        myRef.addValueEventListener(this);

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
        if (dayStatusList!=null && isDayWithEvent(daysOfMonth.get(position))){
            holder.dayOfMonth.setBackgroundResource(R.drawable.daywithevent);
        }
    }

    public boolean isDayWithEvent(String day){
        for (int j=0; j<dayStatusList.size(); j++){
            if (dayStatusList.get(j).getDay()==Integer.parseInt(day)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount()
    {
        return daysOfMonth.size();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        dayStatusList = (ArrayList<DayStatus>) dataSnapshot.getValue();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.w(TAG, "Failed to read value.", databaseError.toException());
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, String dayText);
    }
}
