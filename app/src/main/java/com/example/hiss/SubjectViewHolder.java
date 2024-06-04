package com.example.hiss;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class SubjectViewHolder extends RecyclerView.ViewHolder {
    public TextView titleTV;

    public SubjectViewHolder(View itemView) {
        super(itemView);
        titleTV = (TextView) itemView.findViewById(R.id.titleTV);
    }
}
