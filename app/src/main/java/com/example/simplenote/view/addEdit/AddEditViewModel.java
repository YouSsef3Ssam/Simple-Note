package com.example.simplenote.view.addEdit;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.simplenote.local.models.Note;
import com.example.simplenote.network.repositories.FirebaseRepository;
import com.example.simplenote.local.database.NoteRepository;

public class AddEditViewModel extends AndroidViewModel {

    private NoteRepository noteRepository;
    private FirebaseRepository firebaseRepository;

    public AddEditViewModel(@NonNull Application application) {
        super(application);
        noteRepository = new NoteRepository(application);
        firebaseRepository = new FirebaseRepository(application);
    }

    public long insert(Note note){
        return noteRepository.insert(note);
    }

    public void update(Note note){
        noteRepository.update(note);
    }

    public void insertWithFirebase(Note note, String userId){
        firebaseRepository.insert(note, userId);
    }

    public void updateWithFirebase(Note note, String userId){
        firebaseRepository.update(note, userId);
    }
}