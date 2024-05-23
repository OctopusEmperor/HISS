package com.example.hiss;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DayInCalender extends AppCompatActivity implements View.OnClickListener {

    ImageButton exitBtn;
    private TextView monthDayTV, pendingTasksTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_calender);

        monthDayTV = (TextView) findViewById(R.id.monthDayTV);
        pendingTasksTV = (TextView) findViewById(R.id.pendingTasksTV);
        exitBtn = (ImageButton) findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (exitBtn==v){
            finish();
            Intent intent = new Intent(DayInCalender.this, MainMenuActivity.class);
            startActivity(intent);
        }
    }

    public void initDayInCalender(String day, String month){
        DayInCalender dayInCalender = new DayInCalender();
        TextView tv = dayInCalender.getMonthDayTV();
        tv.setText(month + " " + day);
    }

    public void initThings(){

    }

    public TextView getPendingTasksTV() {
        return pendingTasksTV;
    }

    public void setPendingTasksTV(TextView pendingTasksTV) {
        this.pendingTasksTV = pendingTasksTV;
    }

    public TextView getMonthDayTV() {
        return monthDayTV;
    }

    public void setMonthDayTV(TextView monthDayTV) {
        this.monthDayTV = monthDayTV;
    }
}
