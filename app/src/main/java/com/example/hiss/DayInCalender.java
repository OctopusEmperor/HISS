package com.example.hiss;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class DayInCalender extends AppCompatActivity implements View.OnClickListener, DayAdapter.OnItemListener {

    ImageButton exitBtn;
    TextView monthDayTV, pendingTasksTV;
    Dialog d;
    RecyclerView dayRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_calender);

        monthDayTV = (TextView) findViewById(R.id.monthDayTV);
        pendingTasksTV = (TextView) findViewById(R.id.pendingTasksTV);
        exitBtn = (ImageButton) findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(this);
        initDayInCalender();
        initWidgets();
    }

    @Override
    public void onClick(View v) {
        if (exitBtn==v){
            finish();
            Intent intent = new Intent(DayInCalender.this, MainMenuActivity.class);
            startActivity(intent);
        }
    }

    public void initWidgets() {
        dayRecyclerView = (RecyclerView) findViewById(R.id.dayRecyclerView);
    }

    public void times(){

    }

    public void createEventDialog() {
        d = new Dialog(this);
        d.setContentView(R.layout.event_dialog);
        d.setTitle("Add Event");
        d.setCancelable(true);

        d.show();
    }

    public void initDayInCalender(){
        Intent intent = getIntent();
        String day = intent.getStringExtra("day");
        String month = intent.getStringExtra("month");
        monthDayTV.setText(month + " " + day);
    }

    @Override
    public void onItemClick(int position, String event, String time){
         if (!event.equals("")){

         }
    }
}
