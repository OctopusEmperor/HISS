package com.example.hiss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;

import android.os.Bundle;
import android.widget.CalendarView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener {

    androidx.credentials.CredentialManager credMan;

    private FirebaseAuth firebaseAuth;
    CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        credMan = CredentialManager.create(this);
        firebaseAuth = FirebaseAuth.getInstance();

        calendar = (CalendarView) findViewById(R.id.calendar);
        calendar.setOnDateChangeListener(this);
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

    }
}