package com.example.hiss;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener, CalendarAdapter.OnItemListener {

    TextView welcometv;
    Button signOutButton;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    ArrayList<DayStatus> dayStatuses;
    Drawable dayWithEvent;
    FirebaseDatabase database;
    DatabaseReference myRef;
    LinearLayout taskLL, topicLL;
    ImageButton notesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        welcometv = (TextView) findViewById(R.id.welcometv);
        signOutButton = (Button) findViewById(R.id.signOutBtn);
        signOutButton.setOnClickListener(this);
        notesBtn = findViewById(R.id.notesBtm);
        notesBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            welcometv.setText("Welcome " + firebaseUser.getDisplayName());
        }
        dayStatuses = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/" + firebaseUser.getUid()+"/daystatus");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dss : snapshot.getChildren()){
                        DayStatus dayStatus = dss.getValue(DayStatus.class);
                        dayStatuses.add(dayStatus);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });

        initWidgets();
        selectedDate = LocalDate.now();
        setMonthView();
        dayWithEvent = ResourcesCompat.getDrawable(getResources(), R.drawable.daywithevent, null);

        taskLL = findViewById(R.id.taskLL);
        taskLL.setOnClickListener(this);
        topicLL = findViewById(R.id.topicLL);
        topicLL.setOnClickListener(this);
    }

    public void saveListToDatabase(View v){

    }

    @Override
    public void onClick(View v) {
        if (v == signOutButton) {
            signOut();
        }

        if (taskLL == v) {
            Intent intent = new Intent(MainMenuActivity.this, TDL.class);
            startActivity(intent);
        }

        if (topicLL == v) {
            Intent intent = new Intent(MainMenuActivity.this, TopicSubjectsTab.class);
            startActivity(intent);
        }
    }

    void signOut() {
        mAuth.signOut();
        startActivity(new Intent(MainMenuActivity.this, MainActivity.class));
    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    public String monthOfSelectedDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
        return date.format(formatter);
    }

    public String yearOfSelectedDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        return date.format(formatter);
    }


    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(MainMenuActivity.this, DayInCalender.class);
            intent.putExtra("day", dayText);
            intent.putExtra("month", monthOfSelectedDate(selectedDate));
            intent.putExtra("year", yearOfSelectedDate(selectedDate));
            intent.putExtra("user", firebaseUser);
            startActivity(intent);

        }
    }
}