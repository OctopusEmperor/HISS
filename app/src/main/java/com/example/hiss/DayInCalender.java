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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DayInCalender extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    ImageButton exitBtn;
    TextView monthDayTV, pendingTasksTV, allDayTV, errorMsg;
    Dialog d;
    EditText titleET, descriptionET;
    SwitchCompat switchCompat;
    TimePicker tp;
    Button saveBtn;
    String title, description, time, day, month, year;
    boolean[] statuses;
    ArrayList<ArrayList<Event>> arrayListEvents;
    TextView tv00, tv01, tv02, tv03, tv04, tv05, tv06, tv07, tv08, tv09, tv10, tv11, tv12, tv13, tv14, tv15, tv16, tv17, tv18, tv19, tv20, tv21, tv22, tv23, tv24;
    DatabaseReference myRef;
    DayStatus dayStatus;
    List<DayStatus> dayStatuses;
    int index;

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

        Intent intent = getIntent();
        day = intent.getStringExtra("day");
        month = intent.getStringExtra("month");
        year = intent.getStringExtra("year");
        monthDayTV.setText(month + " " + day + " |");

        dayStatuses = new ArrayList<>();

        FirebaseUser firebaseUser = getIntent().getParcelableExtra("user");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user/"+firebaseUser.getUid()+"/daystatus/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        DayStatus dayStatus = ds.getValue(DayStatus.class);
                        dayStatuses.add(dayStatus);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Failed to read value: " + databaseError);
            }
        });
        index = -1;
        for (int i=0; i<dayStatuses.size(); i++){
            DayStatus ds = dayStatuses.get(i);
            if (ds.getYear()==Integer.parseInt(year) && ds.getMonth()==monthFromString(month) && ds.getDay()==Integer.parseInt(day)){
                dayStatus = dayStatuses.get(i);
                index = i;
            }
        }

        initDayInCalender();

        statuses = new boolean[25];
        arrayListEvents = new ArrayList<ArrayList<Event>>(25);

        for (int i = 0; i<25; i++){
            boolean status=false;
            statuses[i] = status;

            if (dayStatus==null) {
                ArrayList<Event> eventNodes = new ArrayList<Event>();
                arrayListEvents.add(eventNodes);
            }
        }
        if (dayStatus!=null){
            arrayListEvents = dayStatus.getEvents();
        }
    }

    public boolean checkDayStatus(ArrayList<ArrayList<Event>> a){
        if (!a.isEmpty()) {
            for (int i = 0; i < a.size(); i++) {
                if (a.get(i).size() > 0) {
                    return true;
                }
            }
        }
        return false;
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

    @Override
    public void onClick(View v) {
        if (exitBtn==v){
            finish();
            Intent intent = new Intent(DayInCalender.this, MainMenuActivity.class);
            startActivity(intent);
        }

        if (saveBtn==v) {
            title = this.titleET.getText().toString();
            description = this.descriptionET.getText().toString();
            int hour;
            if (!switchCompat.isChecked()){
                if (tp.getHour() < 10){
                  time = "0" + tp.getHour() + ":" + tp.getMinute();
                }
                else {
                    time = tp.getHour() + ":" + tp.getMinute();
                }
                hour = Integer.parseInt(time.substring(0, 2));
                if (checkTitleValidity(arrayListEvents.get(hour),title,time)){
                    Event event = new Event(title, description, time);

                    for (int i = 0; i < 25; i++){
                        if (i==hour){
                            int id;
                            if (i<10){
                                id = getId("tv0"+String.valueOf(i), R.id.class);
                            }
                            else {
                                id = getId("tv"+String.valueOf(i), R.id.class);
                            }
                            TextView tv = (TextView) findViewById(id);
                            if (arrayListEvents.get(i).size()>0){
                                tv.setText(tv.getText().toString()+" | "+title);
                            }
                            else {
                                tv.setText(title);
                            }
                            statuses[i]=true;
                            arrayListEvents.get(i).add(event);

                            FirebaseUser firebaseUser = getIntent().getParcelableExtra("user");
                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users/"+firebaseUser.getUid()+"/daystatus/");
                            DayStatus dayStatus1 = new DayStatus(Integer.parseInt(day), monthFromString(month), Integer.parseInt(year), true, arrayListEvents);
                            if (index ==-1) {
                                myRef.child(String.valueOf(dayStatuses.size())).setValue(dayStatus1);
                            }
                            else {
                                myRef.child(String.valueOf(index)).setValue(dayStatus1);
                            }
                        }
                    }
                    d.dismiss();
                }
                else {
                    errorMsg.setVisibility(View.VISIBLE);
                }
            }
            else {
                time = "all-day";
                allDayTV.setText(title);
                allDayTV.setVisibility(View.VISIBLE);
                Event event = new Event(title, description, time);
                arrayListEvents.get(24).add(event);
                d.dismiss();
            }
        }

        if (tv00 == v){
            ArrayList<Event> array = arrayListEvents.get(0);
            if (statuses[0]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv01 == v){
            ArrayList<Event> array = arrayListEvents.get(1);
            if (statuses[1]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv02 == v){
            ArrayList<Event> array = arrayListEvents.get(2);
            if (statuses[2]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv03 == v){
            ArrayList<Event> array = arrayListEvents.get(3);
            if (statuses[3]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv04 == v){
            ArrayList<Event> array = arrayListEvents.get(4);
            if (statuses[4]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv05 == v){
            ArrayList<Event> array = arrayListEvents.get(5);
            if (statuses[5]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv06 == v){
            ArrayList<Event> array = arrayListEvents.get(6);
            if (statuses[6]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv07 == v){
            ArrayList<Event> array = arrayListEvents.get(7);
            if (statuses[7]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv08 == v){
            ArrayList<Event> array = arrayListEvents.get(8);
            if (statuses[8]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv09 == v){
            ArrayList<Event> array = arrayListEvents.get(9);
            if (statuses[9]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv10 == v){
            ArrayList<Event> array = arrayListEvents.get(10);
            if (statuses[10]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv11 == v){
            ArrayList<Event> array = arrayListEvents.get(11);
            if (statuses[11]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv12 == v){
            ArrayList<Event> array = arrayListEvents.get(12);
            if (statuses[12]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv13 == v){
            ArrayList<Event> array = arrayListEvents.get(13);
            if (statuses[13]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv14 == v){
            ArrayList<Event> array = arrayListEvents.get(14);
            if (statuses[14]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv15 == v){
            ArrayList<Event> array = arrayListEvents.get(15);
            if (statuses[15]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv16 == v){
            ArrayList<Event> array = arrayListEvents.get(16);
            if (statuses[16]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv17 == v){
            ArrayList<Event> array = arrayListEvents.get(17);
            if (statuses[17]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv18 == v){
            ArrayList<Event> array = arrayListEvents.get(18);
            if (statuses[18]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv19 == v){
            ArrayList<Event> array = arrayListEvents.get(19);
            if (statuses[19]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv20 == v){
            ArrayList<Event> array = arrayListEvents.get(20);
            if (statuses[20]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv21 == v) {
            ArrayList<Event> array = arrayListEvents.get(21);
            if (statuses[21]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv22 == v) {
            ArrayList<Event> array = arrayListEvents.get(22);
            if (statuses[22]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv23 == v) {
            ArrayList<Event> array = arrayListEvents.get(23);
            if (statuses[23]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
        if (tv24 == v){
            ArrayList<Event> array = arrayListEvents.get(24);
            if (statuses[24]==false){
                createEventDialog();
            }
            else if (array.size() <= 1){
                editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
            }
        }
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

    public void editEventDialog(String title, String description, String time){
        d = new Dialog(this);
        d.setContentView(R.layout.event_dialog);
        d.setTitle("Edit Event");
        d.setCancelable(true);

        titleET = (EditText) d.findViewById(R.id.titleET);
        descriptionET = (EditText) d.findViewById(R.id.descriptionET);
        switchCompat = (SwitchCompat) d.findViewById(R.id.switchCompat);
        errorMsg = (TextView) d.findViewById(R.id.errorMsg);
        switchCompat.setOnCheckedChangeListener(this);
        tp = (TimePicker) d.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        saveBtn = (Button) d.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        titleET.setText(title);
        descriptionET.setText(description);
        tp.setHour(Integer.valueOf(time.substring(0, 1)));
        tp.setMinute(Integer.valueOf(time.substring(3, 4)));
        if (time.equals("all-day")){
            switchCompat.setChecked(true);
        }
        else {
            switchCompat.setChecked(false);
        }

        d.show();
    }



    public void initDayInCalender(){
        tv00 = (TextView) findViewById(R.id.tv00);
        tv01 = (TextView) findViewById(R.id.tv01);
        tv02 = (TextView) findViewById(R.id.tv02);
        tv03 = (TextView) findViewById(R.id.tv03);
        tv04 = (TextView) findViewById(R.id.tv04);
        tv05 = (TextView) findViewById(R.id.tv05);
        tv06 = (TextView) findViewById(R.id.tv06);
        tv07 = (TextView) findViewById(R.id.tv07);
        tv08 = (TextView) findViewById(R.id.tv08);
        tv09 = (TextView) findViewById(R.id.tv09);
        tv10 = (TextView) findViewById(R.id.tv10);
        tv11 = (TextView) findViewById(R.id.tv11);
        tv12 = (TextView) findViewById(R.id.tv12);
        tv13 = (TextView) findViewById(R.id.tv13);
        tv14 = (TextView) findViewById(R.id.tv14);
        tv15 = (TextView) findViewById(R.id.tv15);
        tv16 = (TextView) findViewById(R.id.tv16);
        tv17 = (TextView) findViewById(R.id.tv17);
        tv18 = (TextView) findViewById(R.id.tv18);
        tv19 = (TextView) findViewById(R.id.tv19);
        tv20 = (TextView) findViewById(R.id.tv20);
        tv21 = (TextView) findViewById(R.id.tv21);
        tv22 = (TextView) findViewById(R.id.tv22);
        tv23 = (TextView) findViewById(R.id.tv23);
        tv24 = (TextView) findViewById(R.id.tv24);

        tv00.setOnClickListener(this);
        tv01.setOnClickListener(this);
        tv02.setOnClickListener(this);
        tv03.setOnClickListener(this);
        tv04.setOnClickListener(this);
        tv05.setOnClickListener(this);
        tv06.setOnClickListener(this);
        tv07.setOnClickListener(this);
        tv08.setOnClickListener(this);
        tv09.setOnClickListener(this);
        tv10.setOnClickListener(this);
        tv11.setOnClickListener(this);
        tv12.setOnClickListener(this);
        tv13.setOnClickListener(this);
        tv14.setOnClickListener(this);
        tv15.setOnClickListener(this);
        tv16.setOnClickListener(this);
        tv17.setOnClickListener(this);
        tv18.setOnClickListener(this);
        tv19.setOnClickListener(this);
        tv20.setOnClickListener(this);
        tv21.setOnClickListener(this);
        tv22.setOnClickListener(this);
        tv23.setOnClickListener(this);
        tv24.setOnClickListener(this);

        Log.d(TAG, "Initialized day in calender");
        if (dayStatus!=null){
        Log.d(TAG, "dayStatus: " + dayStatus);
            ArrayList<ArrayList<Event>> events = dayStatus.getEvents();
            for (int i=0; i<25; i++){
                if (!events.get(i).isEmpty()) {
                    for (int j = 0; j < events.get(i).size(); j++) {
                        Event event = events.get(i).get(j);
                        if (event.getTime().equals("all-day")) {
                            if (allDayTV.getText().toString().equals("")) {
                                allDayTV.setText(event.getTitle());
                            }
                            else {
                                allDayTV.setText(allDayTV.getText().toString() + " | " + event.getTitle());
                            }
                        }
                        else {
                            int id;
                            if (i < 10) {
                                id = getId("tv0" + String.valueOf(i), R.id.class);
                            }
                            else {
                                id = getId("tv" + String.valueOf(i), R.id.class);
                            }
                            TextView tv = (TextView) findViewById(id);
                            if (tv.getText().toString().equals("")){
                                tv.setText(event.getTitle());
                            }
                            else {
                                tv.setText(tv.getText().toString() + " | " + event.getTitle());
                            }
                        }
                    }
                }
            }
        }
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