package com.example.hiss;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TopicSubjectsTab extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView subjectRecyclerView;
    private SubjectAdapter subjectAdapter;
    private List<String> subjectList;
    ImageButton exitBtb;
    Dialog d;
    EditText titleET;
    Button saveBtn, addSubjectsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_subjects);

        exitBtb = findViewById(R.id.exitBtn);
        exitBtb.setOnClickListener(this);
        addSubjectsBtn = findViewById(R.id.addSubjectBtn);
        addSubjectsBtn.setOnClickListener(this);
        subjectRecyclerView = findViewById(R.id.subjectRecyclerView);
        subjectRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        subjectList = new ArrayList<>();
        subjectAdapter = new SubjectAdapter(subjectList, new SubjectAdapter.ItemClickListener() {
            @Override
            public void onItemClick(String title) {

            }
        });
        subjectRecyclerView.setAdapter(subjectAdapter);
    }

    @Override
    public void onClick(View v) {
        if (exitBtb==v){
            finish();
            Intent intent = new Intent(TopicSubjectsTab.this, MainActivity.class);
            startActivity(intent);
        }

        if (addSubjectsBtn==v){
            createSubjectDialog();
        }

        if (saveBtn==v){
            Log.d(TAG, "onClick: "+titleET.getText().toString());
            String title = titleET.getText().toString();
            subjectList.add(title);
            subjectAdapter.notifyItemInserted(subjectList.size()-1);
            d.dismiss();
            Log.d(TAG, "onClick: "+titleET.getText().toString());
        }
    }

    public void createSubjectDialog(){
        d = new Dialog(this);
        d.setContentView(R.layout.subject_dialog);
        d.setTitle("New Subject");
        d.setCancelable(true);

        titleET = d.findViewById(R.id.titleET);
        saveBtn = d.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        d.show();
    }
}
