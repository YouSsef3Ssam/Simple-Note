package com.example.simplenote.network.repositories;

import android.app.Application;
import com.example.simplenote.network.client.FirebaseDB;
import com.example.simplenote.local.models.Note;
import com.example.simplenote.R;
import com.google.firebase.database.DatabaseReference;

public class FirebaseRepository {

    private DatabaseReference reference;
    private Application application;

    public FirebaseRepository(Application application){
        reference = FirebaseDB.getInstance();
        this.application = application;

    }

    public void insert(Note note, String userId) {
        reference.child(application.getString(R.string.notes)).child(userId).child(String.valueOf(note.getId())).setValue(note);
    }

    public void update(Note note, String userId) {
        reference.child(application.getString(R.string.notes)).child(userId).child(String.valueOf(note.getId())).setValue(note);
    }

    public void delete(Note note, String userId) {
        reference.child(application.getString(R.string.notes)).child(userId).child(String.valueOf(note.getId())).removeValue();
    }

    public void deleteAll(String userId) {
        reference.child(application.getString(R.string.notes)).child(userId).removeValue();
    }
}