package com.example.hiss;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TDL extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView tdlRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    Button addTaskBtn, saveTaskBtn;
    Dialog d;
    EditText titleET,descriptionET;
    RadioButton rdUrgent, rdNotUrgent, rdImportant, rdNotImportant;
    RadioGroup radioGroup;
    TextView dateTV;
    ImageButton exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tdl_layout);

        tdlRecyclerView = findViewById(R.id.tdlRecyclerView);
        tdlRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addTaskBtn = findViewById(R.id.addTaskBtn);
        addTaskBtn.setOnClickListener(this);
        dateTV = findViewById(R.id.dateTv);
        exitBtn = findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(this);

        dateTV.setText(LocalDate.now().toString());

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, new TaskAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                int position = taskList.indexOf(task);
                taskList.remove(position);
                taskAdapter.notifyItemRemoved(position);
            }
        });
        tdlRecyclerView.setAdapter(taskAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v==addTaskBtn){
            createTaskDialog();
        }

        if (saveTaskBtn==v){
            taskList.add(new Task(titleET.getText().toString(), descriptionET.getText().toString(), getUrgency(radioGroup.getCheckedRadioButtonId()), false));
            taskAdapter.notifyItemInserted(taskList.size()-1);
            d.dismiss();
        }

        if (exitBtn==v){
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

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
