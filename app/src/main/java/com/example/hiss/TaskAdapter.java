package com.example.hiss;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for displaying tasks in a RecyclerView.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    private List<Task> taskList;
    private ItemClickListener mItemListener;
    private Context context;
    private FirebaseUser firebaseUser;
    int position;

    /**
     * Constructs a TaskAdapter with the specified list of tasks and item
     click listener.
     *
     * @param taskList The list of tasks.
     * @param itemClickListener The listener for item click events.
     */
    public TaskAdapter(Context context, List<Task> taskList, FirebaseUser firebaseUser,ItemClickListener itemClickListener) {
        this.context = context;
        this.taskList = taskList;
        this.mItemListener = itemClickListener;
        this.firebaseUser = firebaseUser;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param i The view type of the new View.
     * @return A new TaskViewHolder that holds a View of the given view
    type.
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_cell, parent, false);
        return new TaskViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified
     position.
     * This method updates the contents of the ViewHolder to reflect the
     item at the given position.
     *
     * @param holder The ViewHolder which should be updated to represent
    the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data
    set.
     */
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.titleTv.setText(task.getTitle());
        holder.descriptionTv.setText(task.getDescription());
        holder.urgency.setText(task.getUrgency());
        holder.status.setChecked(task.isStatus());
        if (task.isStatus()) {
            holder.status.setText("Completed");
        } else{
            holder.status.setText("Incomplete");
        }
        holder.status.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setStatus(isChecked);
            if (task.isStatus()) {
                holder.status.setText("Completed");
            } else{
                holder.status.setText("Incomplete");
            }
        });
        holder.editBtn.setOnClickListener(view -> {
            editTask(holder);
        });
        holder.itemView.setOnLongClickListener(view -> {
            mItemListener.onItemClick(taskList.get(position));
            return true;
        });
    }

    /**
     * Edits the selected task in the dialog.
     *
     * @param holder The ViewHolder of the task being edited.
     */
    public void editTask(TaskViewHolder holder){
        Dialog d;
        EditText titleET,descriptionET;
        RadioButton rdUrgent, rdNotUrgent, rdImportant, rdNotImportant;
        RadioGroup radioGroup;
        Button saveTaskBtn;

        SharedPreferences sp = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        d = new Dialog(holder.editBtn.getContext());
        d.setContentView(R.layout.task_dialog);
        d.setTitle("Edit Event");
        d.setCancelable(true);


        for (int i =0; i < taskList.size(); i++){
            if (taskList.get(i).getTitle().equals(holder.titleTv.getText()) && holder.descriptionTv.getText().equals(taskList.get(i).getDescription()) && holder.urgency.getText().equals(taskList.get(i).getUrgency())){
                position = i;
            }
        }

        titleET = d.findViewById(R.id.titleET);
        descriptionET = d.findViewById(R.id.descriptionET);
        radioGroup = d.findViewById(R.id.radioGroup);
        rdUrgent = d.findViewById(R.id.rdUrgent);
        rdNotUrgent = d.findViewById(R.id.rdNotUrgent);
        rdImportant = d.findViewById(R.id.rdImportant);
        rdNotImportant = d.findViewById(R.id.rdNotImportant);
        saveTaskBtn = d.findViewById(R.id.saveBtn);

        titleET.setText(holder.titleTv.getText());
        descriptionET.setText(holder.descriptionTv.getText());
        radioGroup.check(setUrgency(holder.urgency.getText().toString(), rdUrgent, rdNotUrgent, rdImportant, rdNotImportant));


        saveTaskBtn.setOnClickListener(view -> {
            int x = position;
            holder.titleTv.setText(titleET.getText());
            holder.descriptionTv.setText(descriptionET.getText());
            holder.urgency.setText(getUrgency(radioGroup.getCheckedRadioButtonId(), rdUrgent, rdNotUrgent, rdImportant, rdNotImportant));
            updateTDL(new Task(holder.titleTv.getText().toString(), holder.descriptionTv.getText().toString(), holder.urgency.getText().toString(), holder.status.isChecked()), position);
            for (int i = 0; i < taskList.size() && i<5; i++){
                editor.putString("task" + (i+1), taskList.get(i).getTitle());
            }
            editor.apply();
            d.dismiss();
        });

        d.show();
    }

    public void updateTDL(Task task, int position){
        SharedPreferences sp = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
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
                    tasks.set(position, task);
                    tdlRef.setValue(tasks);
                    TaskAdapter taskAdapter = new TaskAdapter(context, tasks, firebaseUser, mItemListener);
                    taskAdapter.notifyItemChanged(taskList.size()-1);
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
     * Returns the urgency text based on the given radio button ID.
     *
     * @param id The ID of the selected radio button.
     * @return The urgency text.
     */
    public String getUrgency(int id, RadioButton rdUrgent, RadioButton rdNotUrgent, RadioButton rdImportant, RadioButton rdNotImportant){
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

    /**
     * Returns the radio button ID based on the given urgency text.
     *
     * @param urgency The urgency text.
     * @return The radio button ID.
     */
    public int setUrgency(String urgency, RadioButton rdUrgent, RadioButton rdNotUrgent, RadioButton rdImportant, RadioButton rdNotImportant){
        if (urgency.equals(rdUrgent.getText().toString())){
            return rdUrgent.getId();
        }
        if (urgency.equals(rdImportant.getText().toString())){
            return rdImportant.getId();
        }
        if (urgency.equals(rdNotImportant.getText().toString())) {
            return rdNotImportant.getId();
        }
        return rdNotUrgent.getId();
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * Interface for handling item click events.
     */
    public interface ItemClickListener{
        /**
         * Called when an item is clicked.
         *
         * @param task The task that was clicked.
         */
        void onItemClick(Task task);
    }
}
