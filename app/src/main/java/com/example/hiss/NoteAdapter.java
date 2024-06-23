package com.example.hiss;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter class for displaying notes in a RecyclerView.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notesList;
    private List<String> uriList;
    private NoteAdapter.ItemClickListener mItemListener;
    Context context;


    /**
     * Constructs a NoteAdapter with the specified context, list of notes,
     list of URIs, and item click listener.
     *
     * @param context The context in which the adapter is used.
     * @param notesList The list of notes.
     * @param uriList The list of URIs corresponding to images for
    each note.
     * @param mItemListener The listener for item click events.
     */
    public NoteAdapter(Context context, List<Note> notesList, List<String> uriList, ItemClickListener mItemListener) {
        this.context = context;
        this.notesList = notesList;
        this.uriList = uriList;
        this.mItemListener = mItemListener;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to
     represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added
    after it is bound to an adapter position.
     * @param i The view type of the new View.
     * @return A new NoteViewHolder that holds a View of the given view
    type.
     */
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_cell, parent, false);
        return new NoteAdapter.NoteViewHolder(view);
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
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.titleTV.setText(note.getTitle());
        holder.descriptionTV.setText(note.getDescription());

        Log.d("TAG", "Binding view holder at position " + position);

        if (uriList != null && uriList.size() == notesList.size()) {
            String uri = uriList.get(position);
            if (uri != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
                storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    Picasso.get().load(uri1).into(holder.img);
                }).addOnFailureListener(exception -> {
                    Log.e("TAG", "Failed to get download URL: " + exception.getMessage());
                });
            } else {
                Log.e("TAG", "Uri at position " + position + " is null.");
            }
        } else {
            Log.e("TAG", "uriList is null or sizes do not match.");
        }

        holder.itemView.setOnClickListener(view -> {
            if (mItemListener != null)
                mItemListener.onItemClick(position);
        });

    }

    /**
     * Returns the total number of items in the data set held by the
     adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {return notesList.size();}

    /**
     * Interface for handling item click events.
     */
    public interface ItemClickListener {
        /**
         * Called when an item is clicked.
         *
         * @param position The position of the item clicked.
         */
        void onItemClick(int position);
    }

    /**
     * ViewHolder class for the NoteAdapter, used to display individual
     notes.
     */
    public class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV, descriptionTV;
        ImageView img;

        /**
         * Constructs a NoteViewHolder with the specified item view.
         *
         * @param itemView The view of the individual note cell.
         */
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.titleTV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            img = itemView.findViewById(R.id.img);
        }
    }
}
