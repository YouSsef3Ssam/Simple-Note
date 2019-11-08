package com.example.simplenote.view.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simplenote.local.models.Note;
import com.example.simplenote.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteHolder> {

    private OnNoteClickListner listner;
    private List<Note> notes = new ArrayList<>();

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NoteHolder holder, int position) {
        Note note = notes.get(position);
        if (note != null){
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd  HH:mm");
            String date = format.format(new Date(System.currentTimeMillis()));

            holder.titleTV.setText(note.getTitle());
            holder.descriptionTV.setText(note.getDescription());
            holder.dateTV.setText(date);

            holder.itemView.setOnClickListener(view -> {
                int position1 = holder.getAdapterPosition();
                if (listner != null && position1 != RecyclerView.NO_POSITION) {
                    listner.onNoteClicked(notes.get(position1));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public Note getNote(int position){
        return notes.get(position);
    }

    public void setNotes(List<Note> notes){
        this.notes.addAll(notes);
        notifyDataSetChanged();
    }

    public void reset(){
        this.notes = new ArrayList<>();
    }

    public interface OnNoteClickListner{
        void onNoteClicked(Note note);
    }

    public void setOnNoteClickListner(OnNoteClickListner listner){
        this.listner = listner;
    }
}