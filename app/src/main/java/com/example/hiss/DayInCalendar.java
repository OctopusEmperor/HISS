package com.example.hiss;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DayInCalendar extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    ImageButton exitBtn;
    TextView monthDayTV, pendingTasksTV, allDayTV, errorMsg;
    Dialog d;
    EditText titleET, descriptionET;
    SwitchCompat switchCompat;
    TimePicker tp;
    Button saveBtn;
    String title, description, time, day, month, year;
    DatabaseReference myRef;
    List<DayStatus> dayStatuses;
    RecyclerView recyclerView;
    ArrayList<ArrayList<Event>> eventList;
    HourAdapter hourAdapter;
    Event allDayEvent;
    FirebaseUser firebaseUser;
    ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_calender);

        monthDayTV = (TextView) findViewById(R.id.monthDayTV);
        pendingTasksTV = (TextView) findViewById(R.id.pendingTasksTV);
        allDayTV = (TextView) findViewById(R.id.allDayTV);
        allDayTV.setOnClickListener(this);
        exitBtn = (ImageButton) findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(this);
        recyclerView = findViewById(R.id.dayRecyclerView);
        eventList = new ArrayList<>(24);
        events = new ArrayList<>();

        Intent intent = getIntent();
        day = intent.getStringExtra("day");
        month = intent.getStringExtra("month");
        year = intent.getStringExtra("year");
        firebaseUser = intent.getParcelableExtra("user");
        monthDayTV.setText(month + " " + day + " |");

        for (int i=0; i<24; i++){
            eventList.add(new ArrayList<>());
        }

        hourAdapter = new HourAdapter(this, eventList, day, month , year, firebaseUser, new HourAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                createEventDialog();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(hourAdapter);
        recyclerView.setOnClickListener(this);

        dayStatuses = new ArrayList<>();

        FirebaseUser firebaseUser = getIntent().getParcelableExtra("user");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/"+firebaseUser.getUid()+"/daystatuses");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Attempting to read value: " + dataSnapshot);
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        DayStatus dayStatus = ds.getValue(DayStatus.class);
                        dayStatuses.add(dayStatus);
                    }
                    Log.d(TAG, "Successfully read value: " + dayStatuses);

                    for (int i=0; i<dayStatuses.size(); i++){
                        DayStatus dayStatus = dayStatuses.get(i);
                        if (dayStatus.getDay()==Integer.parseInt(day) && dayStatus.getMonth()==monthFromString(month)){
                            ArrayList<Event> events1 = dayStatus.getEvents();
                            pendingTasksTV.setText(String.valueOf(events1.size()));
                            Log.d(TAG, "Attempting to add events to view");
                            for (int j=0; j<events1.size(); j++){
                                Log.d(TAG, "Attempting to add event");
                                Event event = events1.get(j);
                                int hour = Integer.parseInt(event.getTime().substring(0, 2));
                                eventList.get(hour).add(event);
                                hourAdapter.notifyItemChanged(hour);
                            }
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

    @Override
    public void onClick(View v) {
        if (exitBtn==v){
            finish();
            Intent intent = new Intent(DayInCalendar.this, MainMenuActivity.class);
            startActivity(intent);
        }

        if (recyclerView==v){
            createEventDialog();
        }

        if (saveBtn==v) {
            title = titleET.getText().toString();
            description = descriptionET.getText().toString();
            if (switchCompat.isChecked()){
                time = "all-day";
                if (allDayEvent==null) {
                    allDayTV.setText(title);
                    allDayEvent = new Event(title, description, time);
                    events.add(allDayEvent);
                    pendingTasksTV.setText(String.valueOf(events.size()));
                    d.dismiss();
                }
                else{
                    errorMsg.setText("You have already added an event for all day");
                    errorMsg.setVisibility(View.VISIBLE);
                }
            }
            else {
                if (tp.getHour()<10) {
                    if (tp.getMinute()<10) {
                        time = "0" + tp.getHour() + ":0" + tp.getMinute();
                    }
                    else {
                        time = "0" + tp.getHour() + ":" + tp.getMinute();
                    }
                }
                else {
                    if (tp.getMinute()<10) {
                        time = tp.getHour() + ":0" + tp.getMinute();
                    }
                    else {
                        time = tp.getHour() + ":" + tp.getMinute();
                    }
                }

                int hour = Integer.parseInt(time.substring(0, 2));
                eventList.get(hour).add(new Event(title, description, time));
                hourAdapter.notifyItemChanged(hour);
                events.add(new Event(title, description, time));
                pendingTasksTV.setText(String.valueOf(events.size()));
                updateCalender(time);
                d.dismiss();
            }
        }
    }

    public void updateCalender(String time){
        Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
        List<Date> datesCheck = new ArrayList<>();
        DatabaseReference dateRef= FirebaseDatabase.getInstance().getReference("users/"+firebaseUser.getUid()+"/datewithevent");
        dateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "ValueEventListener initialized");
                if (dataSnapshot.exists()){
                    boolean alreadyExist=false;
                    List<Date> dates = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        Date date = ds.getValue(Date.class);
                        dates.add(date);
                        datesCheck.add(date);
                    }
                    for (int i=0; i<dates.size(); i++){
                        if (dates.get(i).getDay()==date.getDay() && dates.get(i).getMonth().equals(date.getMonth()) && dates.get(i).getYear()==date.getYear()){
                            alreadyExist=true;
                        }
                    }
                    if (!alreadyExist){
                        dates.add(date);
                        datesCheck.add(date);
                        dateRef.setValue(dates);
                    }
                }
                else {
                    ArrayList<Date> dates = new ArrayList<>();
                    dates.add(date);
                    datesCheck.add(date);
                    dateRef.setValue(dates);
                    Log.d(TAG, "Date added" + date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Failed to read value: " + databaseError);
            }
        });

        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("users/"+firebaseUser.getUid()+"/daystatuses");
        List<DayStatus> dayStatusList = new ArrayList<>();
        DayStatus ds = new DayStatus(date.getDay(), monthFromString(date.getMonth()), date.getYear(), events);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    boolean check = true;
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        DayStatus dayStatus = ds.getValue(DayStatus.class);
                        dayStatusList.add(dayStatus);
                    }
                    for (int i=0; i<dayStatusList.size(); i++){
                        if (dayStatusList.get(i).getDay()==ds.getDay() && dayStatusList.get(i).getMonth()==ds.getMonth() && dayStatusList.get(i).getYear()==ds.getYear()){
                            ArrayList<Event> eventList1 = dayStatusList.get(i).getEvents();
                            for (int j=0; j<eventList1.size()-1; j++){
                                if (time.equals(eventList1.get(j).getTime())){
                                    check=false;
                                    events.remove(events.size()-1);
                                    eventList.get(Integer.parseInt(time.substring(0, 2))).remove(events.size()-1);
                                    hourAdapter.notifyItemRemoved(Integer.parseInt(time.substring(0, 2)));
                                    for (int k=0; k<datesCheck.size(); k++){
                                        if (datesCheck.get(k).getDay()==date.getDay() && datesCheck.get(i).getMonth().equals(date.getMonth()) && datesCheck.get(k).getYear()==date.getYear()){
                                            if (events.isEmpty()){
                                                datesCheck.remove(k);
                                            }
                                        }
                                    }
                                    Toast.makeText(getApplicationContext(), "You have already added an event for this time", Toast.LENGTH_LONG).show();
                                }
                            }
                            if (check){
                                DayStatus newDayStatus = new DayStatus(ds.getDay(), ds.getMonth(), ds.getYear(), events);
                                dayStatusList.set(i, newDayStatus);
                                eventRef.setValue(dayStatusList);
                            }
                        }
                        else {
                            dayStatusList.add(ds);
                            eventRef.setValue(dayStatusList);
                        }
                    }
                }
                else {
                    dayStatusList.add(ds);
                    eventRef.setValue(dayStatusList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Failed to read value: " + databaseError);
            }
        });
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

    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }

    public boolean checkTitleValidity(ArrayList<Event> array, String title, String time){
        for (int i=0; i<array.size(); i++){
            if (array.get(i).getTitle().equals(title)){
                if (array.get(i).getTime().substring(0,2).equals(time.substring(0,2))){
                    return false;
                }
            }
        }
        return true;
    }

    public void createEventDialog() {
        d = new Dialog(this);
        d.setContentView(R.layout.event_dialog);
        d.setTitle("Add Event");
        d.setCancelable(true);

        titleET = (EditText) d.findViewById(R.id.titleET);
        descriptionET = (EditText) d.findViewById(R.id.descriptionET);
        errorMsg = (TextView) d.findViewById(R.id.errorMsg);
        switchCompat = (SwitchCompat) d.findViewById(R.id.switchCompat);
        switchCompat.setOnCheckedChangeListener(this);
        tp = (TimePicker) d.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        saveBtn = (Button) d.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        d.show();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (switchCompat==buttonView) {
            if (isChecked == true) {
                tp.setVisibility(View.GONE);
            } else if (isChecked==false) {
                tp.setVisibility(View.VISIBLE);
            }
        }
    }
}