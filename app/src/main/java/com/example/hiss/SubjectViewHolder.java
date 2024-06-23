package com.example.hiss;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder class for the SubjectAdapter, used to display individual
 subjects.
 */
public class SubjectViewHolder extends RecyclerView.ViewHolder {
    public TextView titleTV;

    /**
     * Constructs a SubjectViewHolder with the specified item view.
     *
     * @param itemView The view of the individual subject cell.
     */
    public SubjectViewHolder(View itemView) {
        super(itemView);
        titleTV = (TextView) itemView.findViewById(R.id.titleTV);
    }
}
