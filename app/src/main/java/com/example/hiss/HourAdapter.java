package com.example.hiss;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {

    // List of events for each hour
    private ArrayList<ArrayList<Event>> eventList;
    // Click listener for items
    private HourAdapter.ItemClickListener mItemListener;
    // Context for the adapter
    Context context;
    // Day, month, and year for the events
    private String day, month, year;
    // Firebase user reference
    private FirebaseUser firebaseUser;



    // Constructor to initialize HourAdapter with necessary data
    public HourAdapter(Context context, ArrayList<ArrayList<Event>> events, String day, String month, String year, FirebaseUser firebaseUser, ItemClickListener itemClickListener) {
        this.eventList = events;
        this.context = context;
        this.mItemListener = itemClickListener;
        this.day = day;
        this.month = month;
        this.year = year;
        this.firebaseUser = firebaseUser;
    }

    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // Inflate the layout for each hour cell
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_cell, parent, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
        // Bind the event data to the ViewHolder for each hour
        ArrayList<Event> events = eventList.get(position);
        String hour;
        if (position < 10) {
            hour = "0" + position;
        } else {
            hour = String.valueOf(position);
        }
        holder.timeTV.setText(hour + ":00");
        holder.itemView.setOnClickListener(view -> {
            if (mItemListener != null)
                mItemListener.onItemClick(position);
        });

        for (int j=0; j<3; j++){
            int index = j;
            holder.events[j].setOnLongClickListener(view -> {
                eventList.get(position).remove(index);
                HourAdapter hourAdapter = new HourAdapter(context, eventList, day, month, year, firebaseUser, mItemListener);
                hourAdapter.notifyItemChanged(position);
                holder.events[index].setVisibility(View.GONE);
                int newPosition = position;

                for (int k=0; k<eventList.size(); k++) {
                    if (eventList.get(k).size() == 0) {
                        DatabaseReference dateRef = FirebaseDatabase.getInstance().getReference("users/" + firebaseUser.getUid() + "/datewithevent");
                        dateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    List<Date> dates = new ArrayList<>();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Date date = snapshot.getValue(Date.class);
                                        dates.add(date);
                                    }

                                    for (int i = 0; i < dates.size(); i++) {
                                        if (dates.get(i).getDay() == Integer.parseInt(day) && dates.get(i).getMonth().equals(month) && dates.get(i).getYear() == Integer.parseInt(year)) {
                                            int dateIndex = i;
                                            dateRef.child("/" + i).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    dates.remove(dateIndex);
                                                }
                                            });
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("ContentValues", "Failed to read value.", databaseError.toException());
                            }
                        });
                    }
                }

                DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("users/"+firebaseUser.getUid()+"/daystatuses");
                eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            List<DayStatus> dayStatusList = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                DayStatus dayStatus = snapshot.getValue(DayStatus.class);
                                dayStatusList.add(dayStatus);
                            }
                            for (int i = 0; i < dayStatusList.size(); i++) {
                                if (dayStatusList.get(i).getDay()==Integer.parseInt(day) && dayStatusList.get(i).getMonth()==monthFromString(month) && dayStatusList.get(i).getYear()==Integer.parseInt(year)) {
                                    int eventIndex=i;
                                    eventRef.child("/" + String.valueOf(i) + "/events/" + index).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            ArrayList<Event> events1 =dayStatusList.get(eventIndex).getEvents();
                                            events1.remove(index);
                                            for (int i = 0; i < holder.events.length; i++) {
                                                if (i+1 < holder.events.length) {
                                                    if (holder.events[i] != null) {
                                                        holder.events[i] = holder.events[i+1];
                                                        holder.events[i+1] = null;
                                                    }
                                                }
                                            }

                                            ArrayList<Integer> indexWithEvents = new ArrayList<>();
                                            for (int i = 0; i < events1.size(); i++){
                                                if (events1.get(i) != null){
                                                    indexWithEvents.add(i);
                                                }
                                            }

                                            for (int i = 0; i < events1.size(); i++) {
                                                events1.set(i, events1.get(indexWithEvents.get(i)));
                                            }

                                            dayStatusList.get(eventIndex).setEvents(events1);
                                            eventRef.setValue(dayStatusList);
                                            eventList.set(newPosition, events1);
                                            HourAdapter hourAdapter = new HourAdapter(context, eventList, day, month, year, firebaseUser, mItemListener);
                                            hourAdapter.notifyItemChanged(newPosition);
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ContentValues", "Failed to read value.", databaseError.toException());
                    }
                });

                return true;
            });
        }

        int i=0;
        for (; i < events.size() && i < 3; i++) {
            holder.events[i].setText(events.get(i).getTitle());
            holder.events[i].setVisibility(View.VISIBLE);
        }
        for (; i < 3; i++) {
            holder.events[i].setVisibility(View.GONE);
        }
        holder.events[0].setOnClickListener(view -> {
            Event event = events.get(0);
            editEventDialog(event.getTitle(), event.getDescription(), event.getTime(), position, 0, holder);
        });
        holder.events[1].setOnClickListener(view -> {
            Event event = events.get(1);
            editEventDialog(event.getTitle(), event.getDescription(), event.getTime(), position, 1, holder);
        });
        holder.events[2].setOnClickListener(view -> {
            Event event = events.get(2);
            editEventDialog(event.getTitle(), event.getDescription(), event.getTime(), position, 2, holder);
        });
    }

    @Override
    public int getItemCount() {
        // Return the number of hours in the list
        return 24;
    }

    public int monthFromString(String month){
        if (month.equals("January")){
            return 1;
        }
        if (month.equals("February")){
            return 2;
        }
        if (month.equals("March")){
            return 3;
        }
        if (month.equals("April")){
            return 4;
        }
        if (month.equals("May")){
            return 5;
        }
        if (month.equals("June")){
            return 6;
        }
        if (month.equals("July")) {
            return 7;
        }
        if (month.equals("August")){
            return 8;
        }
        if (month.equals("September")) {
            return 9;
        }
        if (month.equals("October")){
            return 10;
        }
        if (month.equals("November")) {
            return 11;
        }
        if (month.equals("December")){
            return 12;
        }
        return 0;
    }

    public interface ItemClickListener {
        // Interface for item click listener
        void onItemClick(int position);
    }

    public void editEventDialog (String title, String description, String time,int position, int i, HourViewHolder holder){
        Dialog d;
        EditText titleET, descriptionET;
        SwitchCompat switchCompat;
        TimePicker tp;
        Button saveBtn;

        d = new Dialog(context);
        d.setContentView(R.layout.event_dialog);
        d.setTitle("Edit Event");
        d.setCancelable(true);

        titleET = (EditText) d.findViewById(R.id.titleET);
        descriptionET = (EditText) d.findViewById(R.id.descriptionET);
        switchCompat = (SwitchCompat) d.findViewById(R.id.switchCompat);
        tp = (TimePicker) d.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        tp.setHour(Integer.parseInt(time.substring(0,2)));
        tp.setMinute(Integer.parseInt(time.substring(3,5)));
        tp.setVisibility(View.GONE);
        switchCompat.setVisibility(View.GONE);
        saveBtn = (Button) d.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(view -> {
            Event event = new Event(titleET.getText().toString(), descriptionET.getText().toString(), tp.getHour() + ":" + tp.getMinute());
            eventList.get(position).set(i, event);
            holder.events[i].setText(event.getTitle());
            editCalender(new Event(titleET.getText().toString(), descriptionET.getText().toString(), time));
            d.dismiss();
        });

        titleET.setText(title);
        descriptionET.setText(description);

        d.show();
    }

    public void editCalender(Event event){
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("users/"+firebaseUser.getUid()+"/daystatuses");
        List<DayStatus> dayStatusList = new ArrayList<>();
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    DayStatus oldDayStatus;
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        DayStatus dayStatus = ds.getValue(DayStatus.class);
                        dayStatusList.add(dayStatus);
                    }
                    for (int i=0; i<dayStatusList.size(); i++){
                        if (dayStatusList.get(i).getDay()==Integer.parseInt(day) && dayStatusList.get(i).getMonth()==monthFromString(month) && dayStatusList.get(i).getYear()==Integer.parseInt(year)){
                            oldDayStatus = dayStatusList.get(i);
                            ArrayList<Event> eventList1 = dayStatusList.get(i).getEvents();
                            for (int j=0; j<eventList1.size(); j++){
                                if (eventList1.get(j).getTime().equals(event.getTime())){
                                    eventList1.set(j, event);
                                    break;
                                }
                            }
                            oldDayStatus.setEvents(eventList1);
                            dayStatusList.set(i, oldDayStatus);
                            eventRef.setValue(dayStatusList);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Failed to read value: " + databaseError);
            }
        });
    }

    public class HourViewHolder extends RecyclerView.ViewHolder {
        TextView timeTV;
        Button[] events;

        public HourViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize buttons for events in the hour
            events = new Button[3];
            events[0] = itemView.findViewById(R.id.event1);
            events[1] = itemView.findViewById(R.id.event2);
            events[2] = itemView.findViewById(R.id.event3);
            // Initialize TextView for the hour
            timeTV = itemView.findViewById(R.id.timeTV);
        }
    }
}


