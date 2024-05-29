package com.example.hiss;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class DayInCalender extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    ImageButton exitBtn;
    TextView monthDayTV, pendingTasksTV, allDayTV, errorMsg;
    Dialog d;
    EditText titleET, descriptionET;
    SwitchCompat switchCompat;
    TimePicker tp;
    Button saveBtn;
    String title, description, time;
    boolean[] statuses;
    ArrayList<ArrayList<Event>> arrayListEvents;
    Event events;
    TextView tv00, tv01, tv02, tv03, tv04, tv05, tv06, tv07, tv08, tv09, tv10, tv11, tv12, tv13, tv14, tv15, tv16, tv17, tv18, tv19, tv20, tv21, tv22, tv23, tv24;
    ViewGroup view;

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
        initDayInCalender();
        statuses = new boolean[25];
        arrayListEvents = new ArrayList<ArrayList<Event>>(25);

        for (int i = 0; i<25; i++){
            boolean status=false;
            statuses[i] = status;

            ArrayList<Event> eventNodes = new ArrayList<Event>();
            arrayListEvents.add(eventNodes);
        }
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
            if (switchCompat.isChecked()==false){
                time = tp.getHour() + ":" + tp.getMinute();
            }
            else {
                time = "all-day";
            }

            int hour = Integer.valueOf(time.substring(0, 1));

            if (checkTitleValidity(arrayListEvents.get(hour),title,time)){
                Event event = new Event(title, description, time);

                if (time.equals("all-day")){
                    allDayTV.setText(title);
                }
                else {
                    for (int i = 0; i < 25; i++){
                        if (i==Integer.valueOf(time.substring(0, 1))){
                            int id;
                            if (i<10){
                                id = getId("tv0"+String.valueOf(i), R.id.class);
                            }
                            else {
                                id = getId("tv"+String.valueOf(i), R.id.class);
                            }
                            TextView tv = (TextView) findViewById(id);
                            tv.setText(tv.getText().toString()+" | "+title);
                            statuses[i]=true;
                            arrayListEvents.get(i).add(event);
                        }
                    }
                }

                d.dismiss();
            }
            else {
                errorMsg.setVisibility(View.VISIBLE);
            }
        }

        if (tv00 == v){
            ArrayList<Event> array = arrayListEvents.get(0);
            if (statuses[0]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv01 == v){
            ArrayList<Event> array = arrayListEvents.get(1);
            if (statuses[1]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv02 == v){
            ArrayList<Event> array = arrayListEvents.get(2);
            if (statuses[2]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv03 == v){
            ArrayList<Event> array = arrayListEvents.get(3);
            if (statuses[3]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv04 == v){
            ArrayList<Event> array = arrayListEvents.get(4);
            if (statuses[4]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv05 == v){
            ArrayList<Event> array = arrayListEvents.get(5);
            if (statuses[5]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv06 == v){
            ArrayList<Event> array = arrayListEvents.get(6);
            if (statuses[6]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv07 == v){
            ArrayList<Event> array = arrayListEvents.get(7);
            if (statuses[7]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv08 == v){
            ArrayList<Event> array = arrayListEvents.get(8);
            if (statuses[8]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv09 == v){
            ArrayList<Event> array = arrayListEvents.get(9);
            if (statuses[9]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv10 == v){
            ArrayList<Event> array = arrayListEvents.get(10);
            if (statuses[10]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv11 == v){
            ArrayList<Event> array = arrayListEvents.get(11);
            if (statuses[11]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv12 == v){
            ArrayList<Event> array = arrayListEvents.get(12);
            if (statuses[12]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv13 == v){
            ArrayList<Event> array = arrayListEvents.get(13);
            if (statuses[13]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv14 == v){
            ArrayList<Event> array = arrayListEvents.get(14);
            if (statuses[14]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv15 == v){
            ArrayList<Event> array = arrayListEvents.get(15);
            if (statuses[15]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv16 == v){
            ArrayList<Event> array = arrayListEvents.get(16);
            if (statuses[16]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv17 == v){
            ArrayList<Event> array = arrayListEvents.get(17);
            if (statuses[17]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv18 == v){
            ArrayList<Event> array = arrayListEvents.get(18);
            if (statuses[18]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv19 == v){
            ArrayList<Event> array = arrayListEvents.get(19);
            if (statuses[19]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv20 == v){
            ArrayList<Event> array = arrayListEvents.get(20);
            if (statuses[20]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv21 == v) {
            ArrayList<Event> array = arrayListEvents.get(21);
            if (statuses[21]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv22 == v) {
            ArrayList<Event> array = arrayListEvents.get(22);
            if (statuses[22]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv23 == v) {
            ArrayList<Event> array = arrayListEvents.get(23);
            if (statuses[23]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
            }
        }
        if (tv24 == v){
            ArrayList<Event> array = arrayListEvents.get(24);
            if (statuses[24]==false){
                createEventDialog();
            }
            else {
                if (array.size() <= 1){
                    editEventDialog(array.get(0).getTitle(), array.get(0).getDescription(), array.get(0).getTime());
                }
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
                if (array.get(i).getTime()==time){
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
        Intent intent = getIntent();
        String day = intent.getStringExtra("day");
        String month = intent.getStringExtra("month");
        monthDayTV.setText(month + " " + day + " |");

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

        int x=0;
        tv00.setTag(String.valueOf(x));
        x++;
        tv01.setTag(String.valueOf(x));
        x++;
        tv02.setTag(String.valueOf(x));
        x++;
        tv03.setTag(String.valueOf(x));
        x++;
        tv04.setTag(String.valueOf(x));
        x++;
        tv05.setTag(String.valueOf(x));
        x++;
        tv06.setTag(String.valueOf(x));
        x++;
        tv07.setTag(String.valueOf(x));
        x++;
        tv08.setTag(String.valueOf(x));
        x++;
        tv09.setTag(String.valueOf(x));
        x++;
        tv10.setTag(String.valueOf(x));
        x++;
        tv11.setTag(String.valueOf(x));
        x++;
        tv12.setTag(String.valueOf(x));
        x++;
        tv13.setTag(String.valueOf(x));
        x++;
        tv14.setTag(String.valueOf(x));
        x++;
        tv15.setTag(String.valueOf(x));
        x++;
        tv16.setTag(String.valueOf(x));
        x++;
        tv17.setTag(String.valueOf(x));
        x++;
        tv18.setTag(String.valueOf(x));
        x++;
        tv19.setTag(String.valueOf(x));
        x++;
        tv20.setTag(String.valueOf(x));
        x++;
        tv21.setTag(String.valueOf(x));
        x++;
        tv22.setTag(String.valueOf(x));
        x++;
        tv23.setTag(String.valueOf(x));
        x++;
        tv24.setTag(String.valueOf(x));
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