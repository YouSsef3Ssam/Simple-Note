package com.example.simplenote.view.notes;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simplenote.R;

public class NoteHolder extends RecyclerView.ViewHolder {

    public TextView titleTV;
    public TextView descriptionTV;
    public TextView dateTV;

    public NoteHolder(@NonNull View v) {
        super(v);

        descriptionTV = v.findViewById(R.id.note_description);
        dateTV = v.findViewById(R.id.note_date);
        titleTV = v.findViewById(R.id.note_title);
    }
}