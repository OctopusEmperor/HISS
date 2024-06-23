package com.example.hiss;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectViewHolder> {

    private List<String> subjectList;
    private ItemClickListener mItemListener;
    private FirebaseUser firebaseUser;
    private Context context;

    public SubjectAdapter(Context context, List<String> subjectList, FirebaseUser firebaseUser, ItemClickListener itemClickListener) {
        this.subjectList = subjectList;
        this.mItemListener = itemClickListener;
        this.firebaseUser = firebaseUser;
        this.context = context;
    }
    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_cell, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int i) {
        String title = subjectList.get(i);
        holder.titleTV.setText(title);
        holder.itemView.setOnClickListener(view -> {
            mItemListener.onItemClick(i);
        });
        int position = i;
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SubjectAdapter subjectAdapter = new SubjectAdapter(context, subjectList, firebaseUser, mItemListener);

                DatabaseReference subjectRef = FirebaseDatabase.getInstance().getReference("users/" + firebaseUser.getUid() + "/subjects");
                subjectRef.child("/" + position).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        SharedPreferences sp = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        if (position<5){
                            editor.putString("task" + position+1, "");
                            editor.apply();
                        }
                        subjectList.remove(position);

                        Log.d("ContentValues", "Successfully removed subject from realtime database");

                        ArrayList<Integer> indexWithEvents = new ArrayList<>();
                        for (int i = 0; i < subjectList.size(); i++){
                            if (subjectList.get(i) != null){
                                indexWithEvents.add(i);
                            }
                        }

                        for (int i = 0; i < subjectList.size(); i++) {
                            subjectList.set(i, subjectList.get(indexWithEvents.get(i)));
                        }

                        subjectRef.setValue(subjectList);
                        subjectAdapter.notifyItemRemoved(position);
                        Log.d("ContentValues", "Successfully removed subject from recycler view");
                    }
                });
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public interface ItemClickListener{
        void onItemClick(int position);
    }
}
