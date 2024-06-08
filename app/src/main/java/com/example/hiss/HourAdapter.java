package com.example.hiss;

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

    private ArrayList<ArrayList<Event>> eventList;
    private HourAdapter.ItemClickListener mItemListener;
    Context context;
    Dialog d;
    EditText titleET, descriptionET;
    SwitchCompat switchCompat;
    TimePicker tp;
    Button saveBtn, e1, e2, e3;
    TextView errorMsg;
    private String day,month,year;
    private  FirebaseUser firebaseUser;
    int datePosition, eventPosition;



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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_cell, parent, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
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
                                            dayStatusList.get(eventIndex).getEvents().remove(index);
                                        }
                                    });
                                    eventPosition=i;
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
        void onItemClick(int position);
    }

    public void editEventDialog (String title, String description, String time,int position,
    int i, HourViewHolder holder){
        d = new Dialog(context);
        d.setContentView(R.layout.event_dialog);
        d.setTitle("Edit Event");
        d.setCancelable(true);

        titleET = (EditText) d.findViewById(R.id.titleET);
        descriptionET = (EditText) d.findViewById(R.id.descriptionET);
        switchCompat = (SwitchCompat) d.findViewById(R.id.switchCompat);
        errorMsg = (TextView) d.findViewById(R.id.errorMsg);
        tp = (TimePicker) d.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        tp.setVisibility(View.GONE);
        switchCompat.setVisibility(View.GONE);
        saveBtn = (Button) d.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(view -> {
            Event event = new Event(titleET.getText().toString(), descriptionET.getText().toString(), tp.getHour() + ":" + tp.getMinute());
            eventList.get(position).set(i, event);
            holder.events[i].setText(event.getTitle());
            d.dismiss();
        });

        titleET.setText(title);
        descriptionET.setText(description);

        d.show();
    }

    public class HourViewHolder extends RecyclerView.ViewHolder {
        TextView timeTV;
        Button[] events;

        public HourViewHolder(@NonNull View itemView) {
            super(itemView);
            events = new Button[3];
            events[0] = itemView.findViewById(R.id.event1);
            events[1] = itemView.findViewById(R.id.event2);
            events[2] = itemView.findViewById(R.id.event3);
            timeTV = itemView.findViewById(R.id.timeTV);
        }
    }
}

