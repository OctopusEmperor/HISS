package com.example.hiss;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TopicSubjectsTab extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView subjectRecyclerView;
    private SubjectAdapter subjectAdapter;
    List<String> subjectList;
    ImageButton exitBtb;
    Dialog d;
    EditText titleET;
    Button saveBtn, addSubjectsBtn;
    FirebaseUser firebaseUser;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_subjects);

        exitBtb = findViewById(R.id.exitBtn);
        exitBtb.setOnClickListener(this);
        addSubjectsBtn = findViewById(R.id.addSubjectBtn);
        addSubjectsBtn.setOnClickListener(this);
        subjectRecyclerView = findViewById(R.id.subjectRecyclerView);
        subjectRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        firebaseUser = getIntent().getParcelableExtra("user");

        subjectList = new ArrayList<>();
        subjectAdapter = new SubjectAdapter(subjectList, firebaseUser, new SubjectAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                editSubjectDialog(subjectList.get(position), position);
            }
        });
        subjectRecyclerView.setAdapter(subjectAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/"+firebaseUser.getUid()+"/subjects");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Attempting to read value: " + dataSnapshot);
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        String string = ds.getValue(String.class);
                        subjectList.add(string);
                        subjectAdapter.notifyItemInserted(subjectList.size()-1);
                    }
                    Log.d(TAG, "Successfully read value: " + subjectList);
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
        if (exitBtb==v){
            finish();
        }

        if (addSubjectsBtn==v){
            createSubjectDialog();
        }

        if (saveBtn==v){
            String title = titleET.getText().toString();
            subjectList.add(title);
            subjectAdapter.notifyItemInserted(subjectList.size());
            updateTDL(title);
            d.dismiss();
        }
    }

    public void updateTDL(String newSubject){
        DatabaseReference subjectRef = FirebaseDatabase.getInstance().getReference("users/" + firebaseUser.getUid() + "/subjects");
        subjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    List<String> strings = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        String string = ds.getValue(String.class);
                        strings.add(string);
                    }
                    strings.add(newSubject);
                    subjectRef.setValue(strings);
                }
                else {
                    subjectRef.setValue(subjectList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled", "Failed to get reference" + subjectRef);
            }
        });
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

    public void editSubjectDialog(String title, int index){
        d = new Dialog(this);
        d.setContentView(R.layout.subject_dialog);
        d.setTitle("New Subject");
        d.setCancelable(true);

        titleET = (EditText) d.findViewById(R.id.titleET);
        Button saveEditBtn = (Button) d.findViewById(R.id.saveBtn);
        saveEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subjectList.set(index, titleET.getText().toString());
                subjectAdapter.notifyItemChanged(index);
                DatabaseReference subjectRef = FirebaseDatabase.getInstance().getReference("users/" + firebaseUser.getUid() + "/subjects");
                subjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            List<String> strings = new ArrayList<>();
                            for (DataSnapshot ds : dataSnapshot.getChildren()){
                                String string = ds.getValue(String.class);
                                strings.add(string);
                            }
                            strings.set(index,titleET.getText().toString());
                            subjectRef.setValue(strings);
                        }
                        else {
                            subjectRef.setValue(subjectList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("onCancelled", "Failed to get reference" + subjectRef);
                    }
                });
                d.dismiss();
            }
        });

        titleET.setText(title);

        d.show();
    }
}
