package com.example.simplenote.view.notes;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.simplenote.local.models.Note;
import com.example.simplenote.network.repositories.FirebaseRepository;
import com.example.simplenote.local.database.NoteRepository;
import java.util.List;

public class NotesViewModel extends AndroidViewModel {

    private NoteRepository noteRepository;
    private FirebaseRepository firebaseRepository;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        noteRepository = new NoteRepository(application);
        firebaseRepository = new FirebaseRepository(application);
    }

    public void insertNotesFromFirebase(Note note){
        noteRepository.insert(note);
    }

    public void delete(Note note){
        noteRepository.delete(note);
    }

    public void deleteAll(){
        noteRepository.deleteAll();
    }

    public LiveData<List<Note>> getNotes(int pageNumber, int pageSize){
        return noteRepository.getNotes(pageNumber, pageSize);
    }

    ////////////////////////////////////////////////////////////////// Firebase ////////////////////////////////////////////////////////////////

    public void deleteWithFirebase(Note note, String deviceId) {
        firebaseRepository.delete(note, deviceId);
    }

    public void deleteAllWithFirebase(String deviceId) {
        firebaseRepository.deleteAll(deviceId);
    }
}