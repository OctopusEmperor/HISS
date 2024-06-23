package com.example.hiss;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder class for the TaskAdapter, used to display individual tasks.
 */
public class TaskViewHolder extends RecyclerView.ViewHolder {
    public TextView titleTv;
    public TextView descriptionTv;
    public TextView urgency;
    public CheckBox status;
    public Button editBtn;

    /**
     * Constructs a TaskViewHolder with the specified item view.
     *
     * @param itemView The view of the individual task cell.
     */
    public TaskViewHolder(View itemView) {
        super(itemView);
        titleTv = (TextView) itemView.findViewById(R.id.title);
        descriptionTv = (TextView) itemView.findViewById(R.id.description);
        urgency = (TextView) itemView.findViewById(R.id.urgency);
        status = (CheckBox) itemView.findViewById(R.id.status);
        editBtn = (Button) itemView.findViewById(R.id.editTaskBtn);
    }
}
