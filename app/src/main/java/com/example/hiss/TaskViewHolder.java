package com.example.hiss;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    public TextView titleTv;
    public TextView descriptionTv;
    public TextView urgency;
    public CheckBox status;
    public Button editBtn;

    public TaskViewHolder(View itemView) {
        super(itemView);
        titleTv = (TextView) itemView.findViewById(R.id.title);
        descriptionTv = (TextView) itemView.findViewById(R.id.description);
        urgency = (TextView) itemView.findViewById(R.id.urgency);
        status = (CheckBox) itemView.findViewById(R.id.status);
        editBtn = (Button) itemView.findViewById(R.id.editTaskBtn);
    }
}
