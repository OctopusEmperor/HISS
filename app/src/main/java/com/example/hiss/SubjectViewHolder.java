package com.example.hiss;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubjectViewHolder extends RecyclerView.ViewHolder {
    public TextView titleTV;

    public SubjectViewHolder(@NonNull View itemView) {
        super(itemView);
        titleTV = itemView.findViewById(R.id.titleTV);
    }
}
