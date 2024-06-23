package com.example.hiss;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TDL (To-Do List) activity that displays and manages a list of tasks.
 */
public class TDL extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView tdlRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    Button addTaskBtn, saveTaskBtn;
    Dialog d;
    EditText titleET,descriptionET;
    RadioButton rdUrgent, rdNotUrgent, rdImportant, rdNotImportant;
    RadioGroup radioGroup;
    TextView dateTV, pendingTasksTV;
    ImageButton exitBtn;
    FirebaseUser firebaseUser;
    DatabaseReference myRef;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    /**
     * Called when the activity is first created. Initializes views,
     Firebase, and sets up the RecyclerView.
     *
     * @param savedInstanceState If the activity is being re-initialized
    after previously being shut down, this Bundle contains the data it most
    recently supplied in {@link #onSaveInstanceState}. <b><i>Note: Otherwise it
    is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tdl_layout);

        sp = getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = sp.edit();

        tdlRecyclerView = findViewById(R.id.tdlRecyclerView);
        tdlRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addTaskBtn = findViewById(R.id.addTaskBtn);
        addTaskBtn.setOnClickListener(this);
        dateTV = findViewById(R.id.dateTv);
        exitBtn = findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(this);
        firebaseUser = getIntent().getParcelableExtra("user");

        dateTV.setText(LocalDate.now().toString());

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(getApplicationContext(), taskList, firebaseUser, new TaskAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                int position = taskList.indexOf(task);

                DatabaseReference delRef = FirebaseDatabase.getInstance().getReference("users/" + firebaseUser.getUid() + "/tasks/" + position);
                delRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (position<5){
                            editor.putString("task" + position+1, "");
                            editor.apply();
                        }
                        taskList.remove(position);

                        ArrayList<Integer> indexWithEvents = new ArrayList<>();
                        for (int i = 0; i < taskList.size(); i++){
                            if (taskList.get(i) != null){
                                indexWithEvents.add(i);
                            }
                        }

                        for (int i = 0; i < taskList.size(); i++) {
                            taskList.set(i, taskList.get(indexWithEvents.get(i)));
                        }
                        delRef.setValue(taskList);
                        taskAdapter.notifyItemRemoved(position);
                    }
                });
            }
        });
        tdlRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        tdlRecyclerView.setAdapter(taskAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/"+firebaseUser.getUid()+"/tasks");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Attempting to read value: " + dataSnapshot);
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        Task task = ds.getValue(Task.class);
                        taskList.add(task);
                        taskAdapter.notifyItemInserted(taskList.size()-1);
                    }
                    Log.d(TAG, "Successfully read value: " + taskList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Failed to read value: " + databaseError);
            }
        });
    }

    /**
     * Handles click events for various UI components.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v==addTaskBtn){
            createTaskDialog();
        }

        if (saveTaskBtn==v){
            taskList.add(new Task(titleET.getText().toString(), descriptionET.getText().toString(), getUrgency(radioGroup.getCheckedRadioButtonId()), false));
            taskAdapter.notifyItemInserted(taskList.size()-1);
            for (int i = 0; i < taskList.size() && i<5; i++){
                editor.putString("task" + (i+1), taskList.get(i).getTitle());
            }
            editor.apply();
            updateTDL(new Task(titleET.getText().toString(), descriptionET.getText().toString(), getUrgency(radioGroup.getCheckedRadioButtonId()), false));
            d.dismiss();
        }

        if (exitBtn==v){
            finish();
        }
    }

    /**
     * Updates the to-do list in the Firebase Realtime Database with a new task.
     *
     * @param newTask The new task to add.
     */
    public void updateTDL(Task newTask){
        DatabaseReference tdlRef = FirebaseDatabase.getInstance().getReference("users/" + firebaseUser.getUid() + "/tasks");
        tdlRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    List<Task> tasks = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        Task task = ds.getValue(Task.class);
                        tasks.add(task);
                    }
                    tasks.add(newTask);
                    tdlRef.setValue(tasks);
                }
                else {
                    tdlRef.setValue(taskList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled", "Failed to get reference" + tdlRef);
            }
        });
    }

    /**
     * Creates a dialog for adding a new task.
     */
    public void createTaskDialog() {
        d = new Dialog(this);
        d.setContentView(R.layout.task_dialog);
        d.setTitle("Add Event");
        d.setCancelable(true);

        titleET = d.findViewById(R.id.titleET);
        descriptionET = d.findViewById(R.id.descriptionET);
        radioGroup = d.findViewById(R.id.radioGroup);
        rdUrgent = d.findViewById(R.id.rdUrgent);
        rdNotUrgent = d.findViewById(R.id.rdNotUrgent);
        rdImportant = d.findViewById(R.id.rdImportant);
        rdNotImportant = d.findViewById(R.id.rdNotImportant);
        saveTaskBtn = d.findViewById(R.id.saveBtn);
        saveTaskBtn.setOnClickListener(this);

        d.show();
    }

    /**
     * Returns the urgency level based on the given radio button ID.
     *
     * @param id The ID of the selected radio button.
     * @return The urgency level as a string.
     */
    public String getUrgency(int id){
        if (id==rdNotUrgent.getId()){
            return rdNotUrgent.getText().toString();
        }
        if (id==rdImportant.getId()) {
            return rdImportant.getText().toString();
        }
        if (rdNotImportant.getId()==id){
            return rdNotImportant.getText().toString();
        }
        return rdUrgent.getText().toString();
    }
}
