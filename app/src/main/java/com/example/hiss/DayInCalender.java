package com.example.hiss;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DayInCalender extends AppCompatActivity implements View.OnClickListener, DayAdapter.OnItemListener, CompoundButton.OnCheckedChangeListener {

    ImageButton exitBtn;
    TextView monthDayTV, pendingTasksTV;
    Dialog d;
    RecyclerView dayRecyclerView;
    EditText titleET, descriptionET;
    SwitchCompat switchCompat;
    TimePicker tp;
    Button saveBtn, eventBttn;
    String title, description, time;
    ArrayList<String> events, times;
    int position=0, lastPosition=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_calender);

        dayRecyclerView = (RecyclerView) findViewById(R.id.dayRecyclerView);
        monthDayTV = (TextView) findViewById(R.id.monthDayTV);
        pendingTasksTV = (TextView) findViewById(R.id.pendingTasksTV);
        exitBtn = (ImageButton) findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(this);
        initDayInCalender();
        setTimesAndEvents();
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
            position++;
            times();
            events.add(title);
            eventBttn.setVisibility(View.VISIBLE);
            d.dismiss();
        }
    }


    public void setTimesAndEvents(){
        ArrayList<String> timesArrayList = times();
        events = new ArrayList<>();

        DayAdapter dayAdapter = new DayAdapter(timesArrayList, events, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        dayRecyclerView.setLayoutManager(layoutManager);
        dayRecyclerView.setAdapter(dayAdapter);
    }

    public ArrayList<String> times(){
        times = new ArrayList<>();

        if (position-1==lastPosition){
            times.add(title);
            lastPosition++;
        }
        return times;
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

    public void initDayInCalender(){
        Intent intent = getIntent();
        String day = intent.getStringExtra("day");
        String month = intent.getStringExtra("month");
        monthDayTV.setText(month + " " + day);
    }

    @Override
    public void onItemClick(int position, String event, String time, Button eventBtn){
         if (!event.equals("")){
             createEventDialog();
             eventBttn = eventBtn;
         }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (switchCompat.isChecked()==true) {
            tp.setVisibility(View.GONE);
        }
    }
}
