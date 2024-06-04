package com.example.hiss;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        subjectRecyclerView.setLayoutManager(layoutManager);

        subjectList = new ArrayList<>();
        subjectAdapter = new SubjectAdapter(subjectList, new SubjectAdapter.ItemClickListener() {
            @Override
            public void onItemClick(String title) {

            }
        });
        subjectRecyclerView.setAdapter(subjectAdapter);
        subjectList.add("boolbool");
        subjectAdapter.notifyItemInserted(subjectList.size()-1);
        Log.d(TAG, "Added boolbool");
    }

    @Override
    public void onClick(View v) {
        if (exitBtb==v){
            finish();
        }

        if (addSubjectsBtn==v){
            createSubjectDialog();
        }

        if (saveBtn==v){
            String title = titleET.getText().toString();
            subjectList.add(title);
            subjectAdapter.notifyItemInserted(subjectList.size()-1);
            d.dismiss();
        }
    }

    public void createSubjectDialog(){
        d = new Dialog(this);
        d.setContentView(R.layout.subject_dialog);
        d.setTitle("New Subject");
        d.setCancelable(true);

        titleET = (EditText) d.findViewById(R.id.titleET);
        saveBtn = (Button) d.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        d.show();
    }
}
